package com.example.stackoverflowuser.base.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.truevalue.dreamappeal.base_new.repository.BaseResponse
import com.truevalue.dreamappeal.base_new.repository.BuilderRepository
import com.truevalue.dreamappeal.base_new.repository.ErrorModel

open class BaseViewModel : ViewModel() {
    private var error = MutableLiveData<ErrorModel>()
    private var loading = MutableLiveData<Boolean>()
    val builderRepository = BuilderRepository()

    fun error() = error
    fun loading() = loading

    init {
        builderRepository.run {
            setScope(viewModelScope)
            notifyLoading(loading)
            notifyError(error)
        }
    }

    protected fun <T> executeRoot(call: suspend () -> T, result: MutableLiveData<T>?) {
        builderRepository.executeRoot(call, result)
    }

    protected fun <T> execute(call: suspend () -> BaseResponse<T>, result: MutableLiveData<T>?) {
        builderRepository.execute(call, result)
    }
}