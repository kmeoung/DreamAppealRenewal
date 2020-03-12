package com.truevalue.dreamappeal.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.fragment.dream_point.FragmentDreamPoint

class ActivityDreamPoint : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        onAction()
    }

    /**
     * Normal Action
     */
    private fun onAction(){
        replaceFragment(R.id.base_container,
            FragmentDreamPoint(),false)
    }

    /**
     * Fragment에서 접근하는 Fragment 변경
     */
    fun replaceFragment(fragment: Fragment, addToBack: Boolean) {
        replaceFragment(R.id.base_container, fragment, addToBack)
    }
}