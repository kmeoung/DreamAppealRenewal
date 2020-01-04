package com.truevalue.dreamappeal.activity

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.widget.ScrollView
import android.widget.TextView
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.http.*
import com.truevalue.dreamappeal.utils.Comm_Param
import kotlinx.android.synthetic.main.activity_address_search.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_search.btn_cancel
import kotlinx.android.synthetic.main.activity_search.et_search
import kotlinx.android.synthetic.main.activity_search.iv_cancel
import kotlinx.android.synthetic.main.fragment_add_action_post.*
import okhttp3.Call
import java.io.IOException

class ActivityAddrSearch : BaseActivity() {

    private val SEARCH_DELAY = 1000L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_search)

        // View Init
        initView()
        // view click listener
        onClickView()
    }

    /**
     * Init View
     */
    private fun initView() {
        iv_cancel.visibility = GONE

        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (et_search.text.toString().isNullOrEmpty()) {
                    iv_cancel.visibility = GONE
                } else iv_cancel.visibility = VISIBLE
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                btn_cancel -> finish()
                iv_cancel -> {
                    et_search.setText("")
                }
            }
        }
        btn_cancel.setOnClickListener(listener)
        iv_cancel.setOnClickListener(listener)

        et_search.setOnEditorActionListener(TextView.OnEditorActionListener { _, i, _ ->
            if(i == EditorInfo.IME_ACTION_SEARCH){
                if(!et_search.text.toString().isNullOrEmpty()){
                    getAddrPost(et_search.text.toString())
                    et_search.setText("")
                }
            }else
                false
            true
        })

    }

    private fun getAddrPost(addr : String){
        val header = DAHttpHeader()
        header.put("Authorization"," KakaoAK ${getString(R.string.kakao_rest_api_key)}")
        val params = DAHttpParams()
        params.put("query",addr)
        BaseOkhttpClient.request(
            HttpType.GET,
            Comm_Param.KAKAO_ADDRESS_API,
            header,
            params,
            object : DAHttpCallback{
                override fun onFailure(call: Call, e: IOException) {
                    super.onFailure(call, e)
                    e.printStackTrace()
                }

                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    Log.d("TEST ADDR",body)
                }
            }
        )
    }

}