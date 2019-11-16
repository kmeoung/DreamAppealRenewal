package com.truevalue.dreamappeal.base

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import com.amazonaws.mobile.client.AWSMobileClient
import com.truevalue.dreamappeal.utils.Comm_Prefs

class BaseApplication :Application(){

    override fun onCreate() {
        super.onCreate()
        val prefs = Comm_Prefs
        prefs.init(applicationContext)


    }
}