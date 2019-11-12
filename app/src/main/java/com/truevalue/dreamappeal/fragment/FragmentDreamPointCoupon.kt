package com.truevalue.dreamappeal.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityDreamPoint
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_add_coupon.*
import okhttp3.Call

class FragmentDreamPointCoupon : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_coupon, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View Init
        initView()
        // OnClickListener
        onClickView()
    }

    /**
     * View Init
     */
    private fun initView() {
        // Action Bar 설정
        (activity as ActivityDreamPoint).iv_back_black.visibility = View.GONE
        (activity as ActivityDreamPoint).iv_back_blue.visibility = View.GONE
        (activity as ActivityDreamPoint).iv_close.visibility = View.VISIBLE
        (activity as ActivityDreamPoint).iv_check.visibility = View.GONE
        (activity as ActivityDreamPoint).tv_title.text = getString(R.string.str_add_coupon)

    }

    /**
     * View Click Listener
     */
    private fun onClickView(){
        val listener = View.OnClickListener{
            when(it){
                (activity as ActivityDreamPoint).iv_close-> activity!!.onBackPressed()
                btn_input->{
                    addDreamPointCoupon()
                }
            }
        }

        (activity as ActivityDreamPoint).iv_close.setOnClickListener(listener)
        btn_input.setOnClickListener(listener)
    }

    /**
     * Http
     * 드림 포인트 쿠폰 사용하기
     */
    private fun addDreamPointCoupon(){
        // todo : 글자수는 무조건 16자리입니다
        val coupon = et_coupon.text.toString()
        if (coupon.length == 16) {
            DAClient.addDreamPointCoupon(coupon,
                object : DAHttpCallback{
                    override fun onResponse(
                        call: Call,
                        serverCode: Int,
                        body: String,
                        code: String,
                        message: String
                    ) {
                        if(context != null){
                            val context = context!!
                            Toast.makeText(context.applicationContext,message,Toast.LENGTH_SHORT).show()

                            if(code == DAClient.SUCCESS){
                                activity!!.onBackPressed()
                            }
                        }
                    }
                })
        }
    }
}