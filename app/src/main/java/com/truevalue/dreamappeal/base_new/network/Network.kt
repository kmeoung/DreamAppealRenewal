package com.example.stackoverflowuser.network

import android.util.Log
import com.truevalue.dreamappeal.base.BaseApplication
import kotlinx.coroutines.*
import java.io.IOException

/**
 * Created by tho nguyen on 2019-05-15.
 */
object Network {
    const val NO_INTERNET = "No internet access"
    fun <T> request(
        scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
        call: suspend () -> T,
        success: ((response: T?) -> Unit)?,
        error: ((throwable: Throwable) -> Unit)? = null,
        doOnSubscribe: (() -> Unit)? = null,
        doOnTerminate: (() -> Unit)? = null
    ) {
        doOnSubscribe?.invoke()
        val IOContext = Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            error?.invoke(throwable)
            doOnTerminate?.invoke()
        }
        scope.launch(IOContext) {
            if (!BaseApplication.isConnectInternet()) {
                throw IOException(NO_INTERNET)
            }
            success?.invoke(call.invoke())
            doOnTerminate?.invoke()
        }
    }

    fun <T> multipleRequest(
        scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
        calls: List<suspend () -> T>,
        success: ((responses: List<T>?) -> Unit)?,
        error: ((throwable: Throwable) -> Unit)? = null,
        doOnSubscribe: (() -> Unit)? = null,
        doOnTerminate: (() -> Unit)? = null
    ) {
        doOnSubscribe?.invoke()
        val IOContext = Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            error?.invoke(throwable)
            doOnTerminate?.invoke()
        }
        scope.launch(IOContext) {
            if (!BaseApplication.isConnectInternet()) {
                throw IOException(NO_INTERNET)
            }
            val results = calls.map { async(IOContext) { it.invoke() } }.map { it.await() }.toList()
            success?.invoke(results)
            doOnTerminate?.invoke()
        }
    }
}