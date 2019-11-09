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
import kotlinx.android.synthetic.main.action_bar_login.*
import kotlinx.android.synthetic.main.fragment_change_password.*
import okhttp3.Call

class FragmentChangePassword : BaseFragment() {

    private var mBean : BeanFindPassword?

    init {
        mBean = null
    }

    companion object{

        fun newInstance(bean : BeanFindPassword) : FragmentChangePassword{
            val fragment = FragmentChangePassword()
            fragment.mBean = bean
            return fragment
        }

    }

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
                btn_change_password -> recoverPassword() // 비밀번호 변경
                iv_back_blue -> activity?.onBackPressed()
            }
        }
        iv_back_blue.setOnClickListener(listener)
        btn_change_password.setOnClickListener(listener)
    }

    /**
     * 비밀번호 변경
     */
    private fun recoverPassword(){
        val password = et_password.text.toString()
        val rePassword = et_check_password.text.toString()

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(
                context!!.applicationContext,
                getString(R.string.str_input_password),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if(password.length < 8){
            Toast.makeText(
                context!!.applicationContext,
                getString(R.string.str_check_password_min_length),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (!TextUtils.equals(password, rePassword)) {
            Toast.makeText(
                context!!.applicationContext,
                getString(R.string.str_plz_match_password),
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }

        if(mBean != null){
            DAClient.recoverPassword(mBean!!.email!!,mBean!!.verify_code!!,password,object : DAHttpCallback{
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    if(context != null){
                        Toast.makeText(context!!.applicationContext,message,Toast.LENGTH_SHORT).show()

                        if(code == DAClient.SUCCESS){
                            (activity as ActivityLoginContainer).initFragment()
                        }
                    }
                }
            })
        }

    }


}