package com.truevalue.dreamappeal.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

object Comm_Prefs {
    var mContext: Context? = null
    private var prefs: SharedPreferences?
    var isLogin : Boolean

    init {
        prefs = null
        isLogin = false
    }

    fun init(context: Context){
        mContext = context
        prefs = mContext!!.getSharedPreferences(Comm_Param.APP_NAME, MODE_PRIVATE)
        isLogin = false
    }

}