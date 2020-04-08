package com.example.stackoverflowuser.base.fragment

import com.example.stackoverflowuser.base.viewmodel.BaseViewModel

abstract class BaseTabFragment<T : BaseViewModel> : BaseFragment<T>() {

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
