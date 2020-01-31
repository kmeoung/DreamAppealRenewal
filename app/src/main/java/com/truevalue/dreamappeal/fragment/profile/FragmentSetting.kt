package com.truevalue.dreamappeal.fragment.profile

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
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.utils.Comm_Param
import com.truevalue.dreamappeal.utils.Comm_Prefs
import com.truevalue.dreamappeal.utils.Comm_Prefs_Param
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_setting.*
import java.lang.Exception

class FragmentSetting : BaseFragment() {
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

                }
            }
        }
        iv_back_blue.setOnClickListener(listener)
        tv_terms_of_use.setOnClickListener(listener)
        tv_privacy_policy.setOnClickListener(listener)
        tv_leave_member.setOnClickListener(listener)
    }
}