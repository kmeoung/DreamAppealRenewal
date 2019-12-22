package com.truevalue.dreamappeal.base

import android.app.Application
import com.truevalue.dreamappeal.utils.Comm_Prefs
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

class BaseApplication :Application(){

    override fun onCreate() {
        super.onCreate()
        val prefs = Comm_Prefs
        prefs.init(applicationContext)

        Fabric.with(this, Crashlytics())
    }
}