package com.truevalue.dreamappeal.fragment.dream_point

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter.AllCaps
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.util.Log
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
        et_coupon.filters = arrayOf(AllCaps(), LengthFilter(19)) // 소문자로 입력된 값을 대문자로 바꿔줌.

        et_coupon.addTextChangedListener(object : TextWatcher {
            private var beforeLength = 0
            private var afterLength = 0

            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.isNullOrEmpty()) {
                    beforeLength = 0
                } else beforeLength = p0!!.length
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (s == null || s.isEmpty()) {
                    Log.d(
                        "addTextChangedListener",
                        "onTextChanged: Intput text is wrong (Type : Length)"
                    )
                    return
                }
                val inputChar = s[s.length - 1]
                if (inputChar != '-' &&
                    (inputChar == 'O' || inputChar == 'o') &&
                    ((inputChar < '1' || inputChar > '9')
                            && (inputChar < 'A' || inputChar > 'Z'))) {
                    et_coupon.text.delete(s.length - 1, s.length)
                    return
                }

                afterLength = s.length

                // 삭제 중
                if (beforeLength > afterLength) {
                    // 삭제 중에 마지막에 -는 자동으로 지우기
                    if (s.toString().endsWith("-")) {
                        et_coupon.setText(s.toString().substring(0, s.length - 1))
                    }
                } else if (beforeLength < afterLength) {
                    if (afterLength === 5 && s.toString().indexOf("-") < 0) {
                        et_coupon.setText("${s.toString().subSequence(0, 4)}-${s.toString().substring(4, s.length)}")
                    } else if (afterLength === 10) {
                        et_coupon.setText("${s.toString().subSequence(0, 9)}-${s.toString().substring(9, s.length)}")
                    } else if (afterLength === 15) {
                        et_coupon.setText("${s.toString().subSequence(0, 14)}-${s.toString().substring(14, s.length)}")
                    }
                }// 입력 중
                et_coupon.setSelection(et_coupon.length())
            }
        })
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                (activity as ActivityDreamPoint).iv_close -> activity!!.onBackPressed()
                btn_input -> {
                    addDreamPointCoupon()
                }
                tv_old_coupon->{
                    (activity as ActivityDreamPoint).replaceFragment(FragmentDreamPointOldCoupon(),true)
                }
            }
        }

        (activity as ActivityDreamPoint).iv_close.setOnClickListener(listener)
        btn_input.setOnClickListener(listener)
        tv_old_coupon.setOnClickListener(listener)
    }

    /**
     * Http
     * 드림 포인트 쿠폰 사용하기
     */
    private fun addDreamPointCoupon() {
        // todo : 글자수는 무조건 16자리입니다
        val coupon = et_coupon.text.toString().replace("-","")
        if (coupon.length == 16) {
            DAClient.addDreamPointCoupon(coupon,
                object : DAHttpCallback {
                    override fun onResponse(
                        call: Call,
                        serverCode: Int,
                        body: String,
                        code: String,
                        message: String
                    ) {
                        if (context != null) {
                            val context = context!!
                            Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT)
                                .show()

                            if (code == DAClient.SUCCESS) {
                                activity!!.onBackPressed()
                            }
                        }
                    }
                })
        }
    }
}