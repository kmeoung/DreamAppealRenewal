package com.truevalue.dreamappeal.fragment.profile

import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.fragment.FragmentConfirmLeave
import com.truevalue.dreamappeal.fragment.FragmentLeaveMember
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Param
import com.truevalue.dreamappeal.utils.Comm_Prefs
import com.truevalue.dreamappeal.utils.Comm_Prefs_Param
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_setting.*
import okhttp3.Call
import org.json.JSONObject
import java.lang.Exception

class FragmentSetting : BaseFragment() {
    private var isLeave = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_setting, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // view 초기화
        initView()
        // View Click Listener
        onClickView()

        checkLeaveMember()
    }

    /**
     * Http
     * 탈퇴 회원 확인
     */
    private fun checkLeaveMember(){
        DAClient.checkLeaveMember(object : DAHttpCallback{
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if(code == DAClient.SUCCESS){
                    val json = JSONObject(body)
                    val status = json.getBoolean("status")
                    isLeave = status

                    tv_leave_member.text = if(isLeave) getString(R.string.str_cancel_leave) else getString(R.string.str_leave_member)
                }else{
                    context?.let {
                        Toast.makeText(it.applicationContext,message,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    /**
     * Http
     * 탈퇴 취소
     */
    private fun cancelLeaveMember(){
        DAClient.cancelLeaveMember(object : DAHttpCallback{
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                context?.let {
                    Toast.makeText(it.applicationContext,message,Toast.LENGTH_SHORT).show()
                }
                if(code == DAClient.SUCCESS) (activity as ActivityMain).onBackPressed(false)
            }
        })
    }

    /**
     * View 초기화
     */
    private fun initView() {
        tv_title.text = getString(R.string.str_setting)
        iv_back_black.visibility = GONE
        iv_back_blue.visibility = VISIBLE

        sw_notification.isChecked = Comm_Prefs.isNotification()

        sw_notification.setOnCheckedChangeListener { _, isChecked ->
            Comm_Prefs.setNotification(isChecked)
        }

        try{
            val pi = activity!!.packageManager.getPackageInfo(activity!!.packageName,0)
            tv_version.text = "${getString(R.string.str_version)} ${pi.versionName}"
        }catch (e : Exception){

        }
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when(it){
                iv_back_blue->{ (activity as ActivityMain).onBackPressed(false) }
                tv_terms_of_use->{
                    val intent = Intent(ACTION_VIEW, Uri.parse(Comm_Param.URL_DOCS_TERMS))
                    startActivity(intent)
                }
                tv_privacy_policy->{
                    val intent = Intent(ACTION_VIEW, Uri.parse(Comm_Param.URL_DOCS_PRIVACY))
                    startActivity(intent)
                }
                tv_leave_member->{
                    if(isLeave){
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
                    }else (activity as ActivityMain).replaceFragment(FragmentLeaveMember(),addToBack = true,isMainRefresh = false)
                }
                tv_site->{
                    val intent = Intent(ACTION_VIEW, Uri.parse("http://${tv_site.text}"))
                    startActivity(intent)
                }
                tv_license_1->{
                    val intent = Intent(ACTION_VIEW, Uri.parse("https://creativecommons.org/licenses/by/3.0/"))
                    startActivity(intent)
                }
                tv_license_2->{
                    val intent = Intent(ACTION_VIEW, Uri.parse("https://icons8.com/vue-static/landings/pricing/icons8-license.pdf"))
                    startActivity(intent)
                }
            }
        }
        iv_back_blue.setOnClickListener(listener)
        tv_terms_of_use.setOnClickListener(listener)
        tv_privacy_policy.setOnClickListener(listener)
        tv_leave_member.setOnClickListener(listener)
        tv_site.setOnClickListener(listener)
        tv_license_1.setOnClickListener(listener)
        tv_license_2.setOnClickListener(listener)
    }
}