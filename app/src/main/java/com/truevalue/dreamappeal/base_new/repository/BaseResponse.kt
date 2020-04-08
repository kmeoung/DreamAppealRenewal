package com.example.stackoverflowuser.base.repository

import com.google.gson.Gson

open class BaseResponse<T> {
    var msg: String? = ""
    var path: String? = null
    var status: Int? = 0
    var exist: Boolean? = false
    var next: Boolean? = false
    var data: T? = null
    override fun toString(): String {
        return Gson().toJson(this)
    }
}