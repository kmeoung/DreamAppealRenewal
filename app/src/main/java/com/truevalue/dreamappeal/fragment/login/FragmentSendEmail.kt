package com.truevalue.dreamappeal.fragment.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityLoginContainer
import com.truevalue.dreamappeal.base.BaseFragment
import kotlinx.android.synthetic.main.action_bar_login.*
import kotlinx.android.synthetic.main.fragment_send_email.*

class FragmentSendEmail : BaseFragment() {

    private var mViewType = -1

    companion object{
        val VIEW_TYPE_REGISTER = 0
        val VIEW_TYPE_FIND_PASSWORD = 1

        fun newInstance(view_type : Int) : FragmentSendEmail{
            val fragment = FragmentSendEmail()
            fragment.mViewType = view_type
            return fragment
        }
    }

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
    private fun initView(){
        // 상단 타이틀 설정
        tv_title.text = when(mViewType){
            VIEW_TYPE_REGISTER->getString(R.string.str_register)
            VIEW_TYPE_FIND_PASSWORD->getString(R.string.str_reset_password)
            else->""
        }
    }

    /**
     * On Click Listener
     */
    private fun onClickView(){
        val listener = View.OnClickListener{
            when(it){
                btn_send_code->(activity as ActivityLoginContainer).replaceFragment(FragmentCheckEmail(),true)
                iv_back->activity!!.onBackPressed()
            }
        }
        btn_send_code.setOnClickListener(listener)
        iv_back.setOnClickListener(listener)
    }
}