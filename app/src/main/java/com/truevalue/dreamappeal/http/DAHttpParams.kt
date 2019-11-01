package com.truevalue.dreamappeal.http

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class DAHttpParams {

    val JSON = "application/json; charset=utf-8".toMediaType()

    private val map: LinkedHashMap<String, Any>

    init {
        map = LinkedHashMap()
    }

    fun put(key: Any, value: Any) {
        map.put(key.toString(), value)
    }

    fun urlParams(): String {
        var urlParam: String = ""
        if (map.size > 0) {
            var i = 0
            for (key: String in map.keys) {
                urlParam = when (i) {
                    0 -> urlParam + "?" + key + "=" + map[key]
                    else -> {
                        i++
                        urlParam + "&" + key + "=" + map[key]
                    }
                }
            }
        }
        return urlParam
    }

    fun bodyParams(): RequestBody {
        val json = JSONObject()
        if (map.size > 0) {
            for (key: String in map.keys) {
                json.put(key, map[key])
            }
        }
        return json.toString().toRequestBody(JSON)
    }

}