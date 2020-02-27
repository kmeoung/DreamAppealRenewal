package com.truevalue.dreamappeal.activity

import android.os.Bundle
import android.widget.Toast
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.activity_credits.*
import okhttp3.Call
import org.json.JSONObject

class ActivityCredits : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)
        tv_title.text = "credits / description"
        getCredits()
        onClickView()
    }

    private fun onClickView(){
        iv_back_black.setOnClickListener {
            finish()
        }
    }

    private fun getCredits(){
        DAClient.getCredits(object : DAHttpCallback{
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if(code == DAClient.SUCCESS){
                    val json = JSONObject(body)
                    val credits = json.getJSONArray("credits")

                    var text = ""
                    for(i in 0 until credits.length()){
                        val credit = credits.getString(i)
                        text = "${text}\n${credit}"
                    }
                    tv_credits.text = text
                }else{
                    Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}