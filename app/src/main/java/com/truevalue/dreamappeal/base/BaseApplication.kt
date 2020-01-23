package com.truevalue.dreamappeal.base

import android.app.Application
import com.kakao.auth.KakaoSDK
import com.truevalue.dreamappeal.utils.Comm_Prefs

class BaseApplication :Application(){

    override fun onCreate() {
        super.onCreate()
        val prefs = Comm_Prefs

        prefs.init(applicationContext)
        // Kakao Login Sdk 설정
        KakaoSDK.init(KakaoSDKAdapter(applicationContext))
    }
}