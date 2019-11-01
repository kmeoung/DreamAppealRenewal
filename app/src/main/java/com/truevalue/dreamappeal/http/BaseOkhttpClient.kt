package com.truevalue.dreamappeal.http

import okhttp3.*
import org.json.JSONObject
import java.io.IOException

object BaseOkhttpClient : OkHttpClient() {

    private var client: OkHttpClient

    init {
        client = OkHttpClient()
    }

    fun request(
        http_type: Int,
        url: String,
        header: DAHttpHeader,
        params: DAHttpParams,
        callback: DAHttpCallback
    ) {
        val clientRequest = when (http_type) {
            HttpType.POST -> post(url, header, params)
            HttpType.GET -> get(url, header, params)
            HttpType.PATCH -> patch(url, header, params)
            HttpType.DELETE -> delete(url, header, params)
            else -> post(url, header, params)
        }

        val call = client.newCall(clientRequest)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailure(call, e)
            }

            override fun onResponse(call: Call, response: Response) {

                val strBody = response.body.toString()
                var json = JSONObject(strBody)
                val code = json.getString("code")
                val message = json.getString("message")

                callback.onResponse(call, response.code, strBody, code, message)

            }
        })
    }

    /**
     * Http GET
     */
    fun get(
        url: String,
        header: DAHttpHeader,
        params: DAHttpParams
    ): Request {
        var requestUrl = url + params.urlParams()
        val builder = header.getBuilder()
        return builder.url(requestUrl)
            .get()
            .build()
    }

    /**
     * Http POST
     */
    private fun post(
        url: String,
        header: DAHttpHeader,
        params: DAHttpParams
    ): Request {
        val builder = header.getBuilder()
        return builder.url(url)
            .post(params.bodyParams())
            .build()
    }

    /**
     * Http PATCH
     */
    private fun patch(
        url: String,
        header: DAHttpHeader,
        params: DAHttpParams
    ): Request {
        val builder = header.getBuilder()
        return builder.url(url)
            .patch(params.bodyParams())
            .build()
    }

    /**
     * Http DELETE
     */
    fun delete(
        url: String,
        header: DAHttpHeader,
        params: DAHttpParams
    ): Request {
        val builder = header.getBuilder()
        return builder.url(url)
            .delete(params.bodyParams())
            .build()
    }

}