package com.truevalue.dreamappeal.base

import android.app.Application
import com.truevalue.dreamappeal.utils.Comm_Prefs

class BaseApplication :Application(){


    override fun onCreate() {
        super.onCreate()
        val prefs = Comm_Prefs
        prefs.mContext = applicationContext
    }
}