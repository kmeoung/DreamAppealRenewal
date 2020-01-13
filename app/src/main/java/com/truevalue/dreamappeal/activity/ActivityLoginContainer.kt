package com.truevalue.dreamappeal.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.fragment.login.FragmentLoginContainer

class ActivityLoginContainer : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Action
        onAction()
    }

    private fun onAction() {
        // 처음 로그인 컨테이너로 이동
        replaceFragment(R.id.base_container, FragmentLoginContainer(), false)
    }

    /**
     * Fragment에서 접근하는 Fragment 변경
     */
    fun replaceFragment(fragment: Fragment, addToBack: Boolean) {
        replaceFragment(R.id.base_container, fragment, addToBack)
    }

    /**
     * Fragment 초기화
     */
    fun initFragment(){
        val fm = supportFragmentManager

        for (i in 0 .. fm.backStackEntryCount){
            fm.popBackStack()
        }
    }

    /**
     * Fragment 초기화 후 이동
     */
    fun initFragment(fragment: Fragment){
        val fm = supportFragmentManager

        for (i in 0 .. fm.backStackEntryCount){
            fm.popBackStack()
        }
        replaceFragment(fragment,true)
    }
}