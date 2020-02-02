package com.truevalue.dreamappeal.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth

object Comm_Prefs {

    private lateinit var mContext: Context
    private lateinit var prefs: SharedPreferences

    /**
     * 전부 초기화
     */
    fun allReset(){
        setUserProfileIndex(-1)
        setToken(null)
        setPushToken(null)
        setNotification(true)
        // Sns 로그인 로그아웃
        FirebaseAuth.getInstance().signOut()
        LoginManager.getInstance().logOut()
    }

    /**
     * App 실행 시 한번만 실행
     */
    fun init(context: Context) {
        mContext = context
        prefs = mContext.getSharedPreferences(Comm_Param.APP_NAME, MODE_PRIVATE)
    }

    /**
     * Token
     */
    fun setToken(token: String?) {
        prefs.edit().putString(Comm_Prefs_Param.PREFS_USER_TOKEN, token).commit()
    }

    fun getToken(): String? {
        return prefs.getString(Comm_Prefs_Param.PREFS_USER_TOKEN, null)
    }

    /**
     * Push Token
     */
    fun setPushToken(token: String?) {
        prefs.edit().putString(Comm_Prefs_Param.PREFS_PUSH_TOKEN, token).commit()
    }

    fun getPushToken(): String? {
        return prefs.getString(Comm_Prefs_Param.PREFS_PUSH_TOKEN, null)
    }

    /**
     * Profile Index
     */
    fun setUserProfileIndex(idx: Int) {
        prefs.edit().putInt(Comm_Prefs_Param.PREFS_USER_PROFILE_INDEX, idx).commit()
    }

    fun getUserProfileIndex(): Int {
        return prefs.getInt(Comm_Prefs_Param.PREFS_USER_PROFILE_INDEX, -1)
    }

    /**
     * Notification 알림 여부 설정
     */
    fun setNotification(use : Boolean){
        prefs.edit().putBoolean(Comm_Prefs_Param.PREFS_IS_NOTIFICATION,use).commit()
    }

    fun isNotification() : Boolean{
        return prefs.getBoolean(Comm_Prefs_Param.PREFS_IS_NOTIFICATION,true)
    }

}