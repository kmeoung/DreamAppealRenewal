package com.truevalue.dreamappeal.fragment.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityLoginContainer
import com.truevalue.dreamappeal.base.BaseFragment
import kotlinx.android.synthetic.main.action_bar_login.*
import kotlinx.android.synthetic.main.fragment_change_password.*

class FragmentChangePassword : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_change_password, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init View
        initView()
        // onClickListener
        onClickView()
    }

    /**
     * View Init
     */
    private fun initView(){
        // 상단 타이틀 설정
        tv_title.text = getString(R.string.str_reset_password)
    }

    /**
     * View OnClick Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                btn_change_password -> (activity as ActivityLoginContainer).initFragment()
                iv_back_blue -> activity?.onBackPressed()
            }
        }
        iv_back_blue.setOnClickListener(listener)
        btn_change_password.setOnClickListener(listener)

    }


}