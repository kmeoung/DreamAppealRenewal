package com.truevalue.dreamappeal.fragment.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityLoginContainer
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.OnSingleClick
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Param
import com.truevalue.dreamappeal.utils.Comm_Prefs
import kotlinx.android.synthetic.main.action_bar_login.*
import kotlinx.android.synthetic.main.fragment_normal_login.*
import okhttp3.Call
import org.json.JSONObject

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
        tv_title.text = ""
        iv_back_blue.visibility = VISIBLE

        if (!Comm_Param.REAL) {
            et_id.setText(Comm_Param.DEBUG_EMAIL)
            et_password.setText(Comm_Param.DEBUG_PASSWORD)
        }
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = object : OnSingleClick() {
            override fun onSingleClick(v: View?) {
                when (v) {
                    iv_back_blue -> activity?.onBackPressed()
                    btn_login -> {
                        checkLogin() // 로그인
                    }
                    tv_forgot_password -> {
                        (activity as ActivityLoginContainer).replaceFragment(
                            FragmentSendEmail(), true
                        )
                    }
                }
            }
        }

        btn_login.setOnClickListener(listener)
        iv_back_blue.setOnClickListener(listener)
        tv_forgot_password.setOnClickListener(listener)
    }

    /**
     * 로그인
     */
    private fun checkLogin() {
        var email = et_id.text.toString().trim()
        email = email.replace(" ","")
        val password = et_password.text.toString()

        if (email.isNullOrEmpty()) {
            Toast.makeText(
                context!!.applicationContext,
                getString(R.string.str_input_email),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (password.isNullOrEmpty()) {
            Toast.makeText(
                context!!.applicationContext,
                getString(R.string.str_input_password),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        DAClient.login(email, password, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (context != null) {
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()

                    if (code == DAClient.SUCCESS) {

                        val json = JSONObject(body)
                        val token = json.getString("token")
                        val profileIdx = json.getInt("profile_idx")

                        Comm_Prefs.setToken(token)
                        Comm_Prefs.setUserProfileIndex(profileIdx)

                        val intent = Intent(context, ActivityMain::class.java)
                        activity?.startActivity(intent)
                        activity?.finish()
                    }
                }
            }
        })
    }
}