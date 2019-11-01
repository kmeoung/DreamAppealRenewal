package com.truevalue.dreamappeal.http

import com.bumptech.glide.RequestBuilder
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class DAHttpHeader {

    private val map: LinkedHashMap<String, Any>

    init {
        map = LinkedHashMap()
    }

    fun put(key: Any, value: Any) {
        map.put(key.toString(), value)
    }

    /**
     * RequestBuilder
     */
    fun getBuilder(): Request.Builder {
        val builder = Request.Builder()
        if(map.size > 0) {
            for (key: String in map.keys) {
                builder.addHeader(key, map[key].toString())
            }
        }
        return builder
    }

}