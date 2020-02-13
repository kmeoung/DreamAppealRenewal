package com.truevalue.dreamappeal.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityIntro
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_confirm_leave.*
import okhttp3.Call

class FragmentConfirmLeave : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_confirm_leave, container, false)

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
     * Http
     * 탈퇴 취소
     */
    private fun cancelLeaveMember(){
        DAClient.cancelLeaveMember(object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if(code == DAClient.SUCCESS){
                    (activity as ActivityMain).initProfileView()
                }
                context?.let {
                    Toast.makeText(it.applicationContext,message, Toast.LENGTH_SHORT).show()
                }
            }
        })
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
                tv_cancel_leave_member->{
                    val builder =
                        AlertDialog.Builder(context!!)
                            .setMessage(getString(R.string.str_cancel_leave_contents))
                            .setPositiveButton(
                                getString(R.string.str_yes)
                            ) { dialog, _ ->
                                cancelLeaveMember()
                                dialog.dismiss()
                            }
                            .setNegativeButton(
                                getString(R.string.str_no)
                            ) { dialog, _ -> dialog.dismiss() }
                    val dialog = builder.create()
                    dialog.show()
                }
                tv_go_login->{
                    ActivityCompat.finishAffinity(activity!!)
                    val intent =
                        Intent(context!!, ActivityIntro::class.java)
                    Comm_Prefs.allReset()
                    startActivity(intent)
                }
            }
        }
        iv_back_black.setOnClickListener(listener)
        tv_cancel_leave_member.setOnClickListener(listener)
        tv_go_login.setOnClickListener(listener)
    }


}