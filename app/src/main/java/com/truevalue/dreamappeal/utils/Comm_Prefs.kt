package com.truevalue.dreamappeal.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

object Comm_Prefs {

    private var mContext: Context? = null
    private var prefs: SharedPreferences?

    init {
        prefs = null
    }

    /**
     * App 실행 시 한번만 실행
     */
    fun init(context: Context) {
        mContext = context
        prefs = mContext!!.getSharedPreferences(Comm_Param.APP_NAME, MODE_PRIVATE)
    }

    /**
     * Token
     */
    fun setToken(token : String?){
        prefs!!.edit().putString(Comm_Prefs_Param.PREFS_USER_TOKEN,token).commit()
    }

    fun getToken() : String?{
        return prefs!!.getString(Comm_Prefs_Param.PREFS_USER_TOKEN,null)
    }

    /**
     * Profile Index
     */
    fun setUserProfileIndex(idx : Int){
        prefs!!.edit().putInt(Comm_Prefs_Param.PREFS_USER_PROFILE_INDEX,idx).commit()
    }

    fun getUserProfileIndex() : Int{
        return prefs!!.getInt(Comm_Prefs_Param.PREFS_USER_PROFILE_INDEX,-1)
    }

}