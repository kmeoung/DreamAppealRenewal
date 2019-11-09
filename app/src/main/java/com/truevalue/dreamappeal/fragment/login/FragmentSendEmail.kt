package com.truevalue.dreamappeal.fragment.login

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityLoginContainer
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.bean.BeanFindPassword
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_login.*
import kotlinx.android.synthetic.main.fragment_send_email.*
import okhttp3.Call

class FragmentSendEmail : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_send_email, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // View 초기화
        initView()
        // OnClickLinstener
        onClickView()
    }

    /**
     * Init View
     */
    private fun initView() {
        // 상단 타이틀 설정
        tv_title.text = getString(R.string.str_reset_password)
    }

    /**
     * On Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                btn_send_code -> {
                    sendEmail() // 이메일 전송
                }
                iv_back_blue -> activity!!.onBackPressed()
            }
        }
        btn_send_code.setOnClickListener(listener)
        iv_back_blue.setOnClickListener(listener)
    }

    /**
     * Email 인증코드 전송
     */
    private fun sendEmail() {
        var email = et_email.text.toString().trim()
        email = email.replace(" ","")

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(
                context!!.applicationContext,
                getString(R.string.str_input_email),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (!Utils.isEmailValid(email)) {
            Toast.makeText(
                context!!.applicationContext,
                getString(R.string.str_input_email_type),
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }

        val bean = BeanFindPassword(email,null)
        bean.email = email

        DAClient.recoverInitiate(email,
            object : DAHttpCallback {
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    if (context != null) {
                        Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
                            .show()

                        if (code == DAClient.SUCCESS) {
                            (activity as ActivityLoginContainer).replaceFragment(
                                FragmentCheckEmail.newInstance(bean),
                                true
                            )
                        }
                    }
                }
            })
    }
}