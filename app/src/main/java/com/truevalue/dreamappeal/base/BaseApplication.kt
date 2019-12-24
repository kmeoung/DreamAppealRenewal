package com.truevalue.dreamappeal.base

import android.app.Application
import com.truevalue.dreamappeal.utils.Comm_Prefs
import com.crashlytics.android.Crashlytics;
import com.truevalue.dreamappeal.http.BaseOkhttpClient
import io.fabric.sdk.android.Fabric;

class BaseApplication :Application(){

    override fun onCreate() {
        super.onCreate()
        val prefs = Comm_Prefs
        prefs.init(applicationContext)

        Fabric.with(this, Crashlytics())
        // TW : 이 방법은 되도록 사용하지 않는게 좋습니다
        BaseOkhttpClient.mContext = applicationContext
    }
}