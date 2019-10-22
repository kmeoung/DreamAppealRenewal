package com.truevalue.dreamappeal.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

object Comm_Prefs {

    private var mContext: Context? = null
    var prefs: SharedPreferences?
    var isLogin : Boolean

    init {
        prefs = null
        isLogin = false
    }

    /**
     * App 실행 시 한번만 실행
     */
    fun init(context: Context){
        mContext = context
        prefs = mContext!!.getSharedPreferences(Comm_Param.APP_NAME, MODE_PRIVATE)
    }

}