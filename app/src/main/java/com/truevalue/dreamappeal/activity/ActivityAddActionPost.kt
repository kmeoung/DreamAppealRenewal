package com.truevalue.dreamappeal.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity

class ActivityAddActionPost : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_action_post)
    }

    private fun initView(){

    }

    /**
     * Fragment에서 접근하는 Fragment 변경
     */
    fun replaceFragment(fragment: Fragment, addToBack: Boolean) {
        replaceFragment(R.id.action_container, fragment, addToBack)
    }


}