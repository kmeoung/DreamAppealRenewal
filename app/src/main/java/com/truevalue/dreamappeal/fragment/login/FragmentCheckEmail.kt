package com.truevalue.dreamappeal.fragment.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityLoginContainer
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_login.*
import kotlinx.android.synthetic.main.fragment_check_email.*

class FragmentCheckEmail : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_check_email, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        initView()
        // View Click Listener
        onClickView()
    }

    /**
     * View Init
     */
    private fun initView() {
        // 인증 이메일 하이라이트 설정
        tv_send_auth_mail.text =
            Utils.replaceTextColor(context, tv_send_auth_mail, getString(R.string.str_auth_email))
    }

    /**
     * OnClickListener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                btn_resend_email -> {
                    activity!!.onBackPressed()
                }
                btn_success_register -> {
                    (activity as ActivityLoginContainer).initFragment()
                }
                iv_back->{
                    activity!!.onBackPressed()
                }
            }
        }
        btn_resend_email.setOnClickListener(listener)
        btn_success_register.setOnClickListener(listener)
        iv_back.setOnClickListener(listener)
    }
}