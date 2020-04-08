package com.example.stackoverflowuser.base.repository

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

class ExtensionUtils
fun Context.isNetworkStatusAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    return connectivityManager?.activeNetworkInfo?.isConnected ?: false
}
