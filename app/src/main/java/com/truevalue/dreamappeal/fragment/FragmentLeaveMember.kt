package com.truevalue.dreamappeal.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_leave_member.*
import okhttp3.Call


class FragmentLeaveMember : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_leave_member, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        onClickView()
    }

    /**
     * View Init
     */
    private fun initView() {
        // Action Bar 설정
        iv_back_black.visibility = View.VISIBLE
        tv_title.text = "탈퇴 신청"
    }

    /**
     * View Click Listener
     */
    private fun onClickView(){
        val listener = View.OnClickListener{
            when(it){
                iv_back_black->{
                    (activity as ActivityMain).onBackPressed(false)
                }
                tv_leave_member->{
                    val builder =
                        AlertDialog.Builder(context!!)
                            .setMessage(getString(R.string.str_really_leave_contents))
                            .setPositiveButton(
                                getString(R.string.str_yes)
                            ) { dialog, _ ->
                                confirmLeaveMember()
                                dialog.dismiss()
                            }
                            .setNegativeButton(
                                getString(R.string.str_no)
                            ) { dialog, _ -> dialog.dismiss() }
                    val dialog = builder.create()
                    dialog.show()

                }
                cb_agree->{
                    tv_leave_member.visibility = if(cb_agree.isChecked) VISIBLE else GONE
                }
            }
        }
        iv_back_black.setOnClickListener(listener)
        tv_leave_member.setOnClickListener(listener)
        cb_agree.setOnClickListener(listener)
    }

    /**
     * Http
     * 탈퇴 처리
     */
    private fun confirmLeaveMember(){
        DAClient.confirmLeaveMember("",object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {

                context?.let {
                    Toast.makeText(it.applicationContext,message, Toast.LENGTH_SHORT).show()
                }
                if(code == DAClient.SUCCESS){
                    (activity as ActivityMain).replaceFragment(FragmentConfirmLeave(),addToBack = true,isMainRefresh = false)
                }
            }
        })
    }


}