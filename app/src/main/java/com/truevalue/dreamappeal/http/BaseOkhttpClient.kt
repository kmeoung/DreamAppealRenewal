package com.truevalue.dreamappeal.http

import android.os.Handler
import android.util.Log
import com.truevalue.dreamappeal.utils.Comm_Param
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


object BaseOkhttpClient : OkHttpClient() {

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
                if(callback != null){
                    callback.onFailure(call, e)
                } else return
            }

            override fun onResponse(call: Call, response: Response) {

                val strBody = response.body!!.string()
                if (!Comm_Param.REAL) Log.d("SERVER BODY", strBody)
                var json = JSONObject(strBody)
                val code: String? = json.getString("code")
                val message: String? = json.getString("message")
                if (callback != null) {
                    if (!code.isNullOrEmpty() && !message.isNullOrEmpty()) {
                        handler.post(Runnable { callback.onResponse(call, response.code, strBody, code, message) })
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