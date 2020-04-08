package com.truevalue.dreamappeal.base_new.fragment

import com.example.stackoverflowuser.base.viewmodel.BaseViewModel

abstract class BaseTabFragment<T : BaseViewModel> : BaseFragment<T>() {

    companion object{
        const val RESULT_CODE = 1004
        const val RESULT_REPLACE_USER_IDX = "RESULT_REPLACE_USER_IDX"
    }

    override fun init() {
        checkLoaded()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.isVisibleToUser = isVisibleToUser
        checkLoaded()
    }

    private fun checkLoaded() {
        if (firstVisible && view != null && isVisibleToUser) {
            firstVisible = false
            onFirstRender()
        }
    }

    private var firstVisible = true
    private var isVisibleToUser = false

    abstract fun onFirstRender()
}
