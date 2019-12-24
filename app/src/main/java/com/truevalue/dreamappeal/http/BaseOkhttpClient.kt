package com.truevalue.dreamappeal.http

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityLoginContainer
import com.truevalue.dreamappeal.utils.Comm_Param
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


object BaseOkhttpClient : OkHttpClient() {

    var mContext: Context? = null

    private val client: OkHttpClient
    private val handler: Handler

    init {
        client = OkHttpClient()
        handler = Handler()
    }

    fun request(
        http_type: Int,
        url: String,
        header: DAHttpHeader?,
        params: DAHttpParams?,
        callback: DAHttpCallback?
    ) {
        val clientRequest = when (http_type) {
            HttpType.POST -> post(url, header, params)
            HttpType.GET -> get(url, header, params)
            HttpType.PATCH -> patch(url, header, params)
            HttpType.DELETE -> delete(url, header, params)
            else -> post(url, header, params)
        }

        val call = client.newCall(clientRequest)
        if (!Comm_Param.REAL) Log.d("SERVER REQUEST URL", call.request().url.toString())
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (callback != null) {
                    callback.onFailure(call, e)
                } else return
            }

            override fun onResponse(call: Call, response: Response) {
                // GET 방식에 Parameter가 들어가게 될 경우 떄문에 처리
                if (call.request().url.toUrl().toString().split("?")[0] == url) {
                    val strBody = response.body!!.string()
                    if (!Comm_Param.REAL) Log.d("SERVER BODY", strBody)
                    try {
                        var json = JSONObject(strBody)
                        val code: String? = json.getString("code")
                        val message: String? = json.getString("message")
                        if (callback != null) {
                            if (!code.isNullOrEmpty() && !message.isNullOrEmpty()) {
                                handler.post(Runnable {
                                    callback.onResponse(
                                        call,
                                        response.code,
                                        strBody,
                                        code,
                                        message
                                    )
                                })
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()

                        if (mContext != null) {
                            handler.post(Runnable {
                                callback!!.onResponse(
                                    call,
                                    response.code,
                                    strBody,
                                    DAClient.FAIL,
                                    mContext!!.getString(R.string.str_error_server)
                                )
                            })
                        }
                    }
                }

            }
        })
    }

    /**
     * Http GET
     */
    private fun get(
        url: String,
        header: DAHttpHeader?,
        params: DAHttpParams?
    ): Request {
        var requestUrl = url
        if (params != null) {
            requestUrl += params.urlParams()
        }
        var builder: Request.Builder = Request.Builder()
        if (header != null)
            builder = header.getBuilder()

        return builder.url(requestUrl)
            .get()
            .build()
    }

    /**
     * Http POST
     */
    private fun post(
        url: String,
        header: DAHttpHeader?,
        params: DAHttpParams?
    ): Request {
        var requestBody: RequestBody = DAHttpParams.toRequestType(JSONObject())
        if (params != null) {
            requestBody = params.bodyParams()
        }

        var builder: Request.Builder = Request.Builder()
        if (header != null)
            builder = header.getBuilder()

        return builder.url(url)
            .post(requestBody)
            .build()
    }

    /**
     * Http PATCH
     */
    private fun patch(
        url: String,
        header: DAHttpHeader?,
        params: DAHttpParams?
    ): Request {
        var requestBody: RequestBody = DAHttpParams.toRequestType(JSONObject())
        if (params != null) {
            requestBody = params.bodyParams()
        }

        var builder: Request.Builder = Request.Builder()
        if (header != null)
            builder = header.getBuilder()

        return builder.url(url)
            .patch(requestBody)
            .build()
    }

    /**
     * Http DELETE
     */
    private fun delete(
        url: String,
        header: DAHttpHeader?,
        params: DAHttpParams?
    ): Request {
        var requestBody: RequestBody = DAHttpParams.toRequestType(JSONObject())
        if (params != null) {
            requestBody = params.bodyParams()
        }

        var builder: Request.Builder = Request.Builder()
        if (header != null)
            builder = header.getBuilder()

        return builder.url(url)
            .delete(requestBody)
            .build()
    }

}