package com.truevalue.dreamappeal.fragment.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.OnSingleClick
import com.truevalue.dreamappeal.utils.Comm_Param
import kotlinx.android.synthetic.main.action_bar_profile_other.*
import kotlinx.android.synthetic.main.fragment_normal_login.*
import kotlinx.android.synthetic.main.fragment_register.et_id
import kotlinx.android.synthetic.main.fragment_register.et_password

class FragmentLogin : BaseFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_normal_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 기본 View 초기화
        initView()
        // View Click Listener
        onClickView()
    }


    /**
     * Init View
     */
    private fun initView() {
        // Action Bar 설정
        tv_title.text = getString(R.string.str_login)
        iv_back.visibility = VISIBLE
        iv_menu.visibility = GONE
        iv_search.visibility = INVISIBLE

        if (!Comm_Param.REAL) {
            et_id.setText("test@gmail.com")
            et_password.setText("test")
        }
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = object : OnSingleClick() {
            override fun onSingleClick(v: View?) {
                when (v) {
                    iv_back -> activity?.onBackPressed()
                    btn_login -> {
                        val intent = Intent(context, ActivityMain::class.java)
                        activity?.startActivity(intent)
                        activity?.finish()
                    }
                }
            }
        }

        btn_login.setOnClickListener(listener)
        iv_back.setOnClickListener(listener)
    }
}