package com.truevalue.dreamappeal.fragment.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityLoginContainer
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.bean.BeanFindPassword
import com.truevalue.dreamappeal.bean.BeanRegister
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_login.*
import kotlinx.android.synthetic.main.fragment_check_email.*
import okhttp3.Call

class FragmentCheckEmail : BaseFragment() {

    private var mViewType = -1
    private var mBean: Any?

    init {
        mBean = null
    }

    companion object {
        val VIEW_TYPE_REGISTER = 0
        val VIEW_TYPE_FIND_PASSWORD = 1

        fun newInstance(bean: Any): FragmentCheckEmail {
            val fragment = FragmentCheckEmail()
            fragment.mBean = when (bean) {
                is BeanRegister -> {
                    fragment.mViewType = VIEW_TYPE_REGISTER
                    bean
                }
                is BeanFindPassword -> {
                    fragment.mViewType = VIEW_TYPE_FIND_PASSWORD
                    bean
                }
                else -> bean
            }

            return fragment
        }
    }

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
        // 상단 타이틀 설정
        tv_title.text = when (mViewType) {
            VIEW_TYPE_REGISTER -> {
                btn_success_register.setText(R.string.str_success_register)
                getString(R.string.str_register)
            }

            VIEW_TYPE_FIND_PASSWORD -> {
                btn_success_register.setText(R.string.str_confirm)
                getString(R.string.str_reset_password)
            }
            else -> ""
        }

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
                    resendEmail() // 인증 이메일 재전송
                }
                btn_success_register -> {
                    checkEmail() // 이메일 인증코드 확인
                }
                iv_back_blue -> {
                    activity!!.onBackPressed()
                }
            }
        }
        btn_resend_email.setOnClickListener(listener)
        btn_success_register.setOnClickListener(listener)
        iv_back_blue.setOnClickListener(listener)
    }

    /**
     * 인증 이메일 재전송
     */
    private fun resendEmail() {
        when (mViewType) {
            VIEW_TYPE_REGISTER -> {
                if (mBean != null) {
                    DAClient.sendEmail(
                        (mBean as BeanRegister).email,
                        (mBean as BeanRegister).name,
                        object : DAHttpCallback {
                            override fun onResponse(
                                call: Call,
                                serverCode: Int,
                                body: String,
                                code: String,
                                message: String
                            ) {
                                if (context != null) {
                                    Toast.makeText(
                                        context!!.applicationContext,
                                        message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        })
                } else {
                    activity!!.onBackPressed()
                }
            }
            VIEW_TYPE_FIND_PASSWORD -> {
                if (mBean != null) {
                    if (!(mBean as BeanFindPassword).email.isNullOrEmpty()) {
                        DAClient.recoverInitiate((mBean as BeanFindPassword).email!!,
                            object : DAHttpCallback {
                                override fun onResponse(
                                    call: Call,
                                    serverCode: Int,
                                    body: String,
                                    code: String,
                                    message: String
                                ) {
                                    if (context != null) {
                                        Toast.makeText(
                                            context!!.applicationContext,
                                            message,
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                }
                            })
                    }
                }
            }
        }
    }

    /**
     * 이메일 인증코드 확인
     */
    private fun checkEmail() {
        val auth_code = et_auth_code.text.toString()

        if (auth_code.isNullOrEmpty()) {
            Toast.makeText(
                context!!.applicationContext,
                getString(R.string.str_input_auth_code),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        when (mViewType) {
            VIEW_TYPE_REGISTER -> {
                if (mBean != null) {
                    DAClient.emailVerify(
                        (mBean as BeanRegister).email,
                        auth_code,
                        object : DAHttpCallback {
                            override fun onResponse(
                                call: Call,
                                serverCode: Int,
                                body: String,
                                code: String,
                                message: String
                            ) {
                                if (context != null) {
                                    Toast.makeText(
                                        context!!.applicationContext,
                                        message,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    if (code == DAClient.SUCCESS) {
                                        register((mBean as BeanRegister))
                                    }
                                }
                            }
                        })
                } else {
                    activity!!.onBackPressed()
                }
            }
            VIEW_TYPE_FIND_PASSWORD -> {
                if (mBean != null) {
                    if (!(mBean as BeanFindPassword).email.isNullOrEmpty()) {
                        (mBean as BeanFindPassword).verify_code = auth_code

                        DAClient.recoverCode(
                            (mBean as BeanFindPassword).email!!,
                            auth_code,
                            object : DAHttpCallback {
                                override fun onResponse(
                                    call: Call,
                                    serverCode: Int,
                                    body: String,
                                    code: String,
                                    message: String
                                ) {
                                    if(code == DAClient.SUCCESS) {
                                        (activity as ActivityLoginContainer).replaceFragment(
                                            FragmentChangePassword.newInstance((mBean as BeanFindPassword)),
                                            true
                                        )
                                    }
                                }
                            })
                    }
                }

            }
        }
    }

    /**
     * 회원가입
     */
    private fun register(bean: BeanRegister) {
        DAClient.register(
            bean.email,
            bean.password,
            bean.name,
            bean.isGender,
            bean.birth,
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
                            (activity as ActivityLoginContainer).initFragment()
                        }
                    }
                }
            })
    }
}