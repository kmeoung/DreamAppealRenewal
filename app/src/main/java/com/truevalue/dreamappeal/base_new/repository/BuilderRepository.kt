package com.truevalue.dreamappeal.base_new.repository

import androidx.lifecycle.MutableLiveData
import com.truevalue.dreamappeal.base_new.network.Network
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class BuilderRepository {
    private var loading: MutableLiveData<Boolean>? = null
    private var error: MutableLiveData<ErrorModel>? = null
    private var isNotHideLoading = false
    private var isShowLoading = true
    private var isShowError = true
    private var scope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    fun notifyLoading(loading: MutableLiveData<Boolean>?) {
        this.loading = loading
    }

    fun notifyError(errorDefault: MutableLiveData<ErrorModel>?) {
        this.error = errorDefault
    }


    fun isNotHideLoading(value: Boolean) {
        isNotHideLoading = value
    }

    fun isShowLoading(value: Boolean) {
        isShowLoading = value
    }

    fun isShowError(value: Boolean) {
        isShowError = value
    }

    fun setScope(scope: CoroutineScope) {
        this.scope = scope
    }

    private fun <T> execute(call: suspend () -> T, onResult: (T?) -> Unit) {
        Network.request(
            scope = scope,
            call = call,
            success = {
                onResult.invoke(it)
            },
            doOnTerminate = {
                if (!isNotHideLoading && isShowLoading) {
                    loading?.postValue(false)
                }
            },
            doOnSubscribe = {
                if (isShowLoading) {
                    loading?.postValue(true)
                }
            },
            error = {
                if (isShowError) {
                    error?.postValue(
                        ErrorModel(
                            "",
                            it.message
                        )
                    )
                }
            }
        )
    }

    fun <T> execute(call: suspend () -> BaseResponse<T>, result: MutableLiveData<T>?) {
        execute(call) {
            result?.postValue(it?.data)
        }
    }

    fun <T> executeRoot(call: suspend () -> T, result: MutableLiveData<T>?) {
        execute(call) {
            result?.postValue(it)
        }
    }

}