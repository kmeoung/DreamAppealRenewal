package com.truevalue.dreamappeal.fragment.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityLoginContainer
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_login_container.*

class FragmentLoginContainer : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_login_container, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Click View Listener
        onClickView()
    }

    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                btn_login -> (activity as ActivityLoginContainer).replaceFragment(
                    FragmentLogin(),
                    true
                )
                btn_register -> (activity as ActivityLoginContainer).replaceFragment(
                    FragmentRegister(),
                    true
                )
            }
        }
        btn_login.setOnClickListener(listener)
        btn_register.setOnClickListener(listener)
    }
}