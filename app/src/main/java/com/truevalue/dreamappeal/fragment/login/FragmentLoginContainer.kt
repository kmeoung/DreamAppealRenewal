package com.truevalue.dreamappeal.fragment.login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Base64.NO_WRAP
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import com.kakao.util.helper.Utility.getPackageInfo
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityLoginContainer
import com.truevalue.dreamappeal.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_login_container.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException




class FragmentLoginContainer : BaseFragment() {

    private var callback: SessionCallback

    init {
        callback = SessionCallback()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_login_container, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Click View Listener
        onClickView()

        Session.getCurrentSession().addCallback(callback)
        Session.getCurrentSession().checkAndImplicitOpen()
    }

    /**
     * View Destroyed
     */
    override fun onDestroyView() {
        super.onDestroyView()
        //Session.getCurrentSession().removeCallback(callback)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            Log.d(REQUEST_TAG,"session get current session")
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                btn_login -> (activity as ActivityLoginContainer).replaceFragment(
                    FragmentLogin(),
                    true
                )
                btn_register -> {
//                    (activity as ActivityLoginContainer).replaceFragment(
//                        FragmentRegister(),
//                        true
//                    )
                    UserManagement.getInstance().requestLogout(object : LogoutResponseCallback(){
                        override fun onCompleteLogout() {

                        }
                    })
                }
            }
        }
        btn_login.setOnClickListener(listener)
        btn_register.setOnClickListener(listener)
    }

    val REQUEST_TAG = "REQUEST_TAG"
    /**
     * Kakao 사용자 정보 가져오기
     */
    private fun getHashKey(context : Context) : String? {
        try{
            if (Build.VERSION.SDK_INT >= 28) {
                val packageInfo = getPackageInfo(context, PackageManager.GET_SIGNING_CERTIFICATES)
                val signatures = packageInfo.signingInfo.apkContentsSigners
                val md = MessageDigest.getInstance("SHA")
                for (signature in signatures) {
                    md.update(signature.toByteArray())
                    return String(Base64.encode(md.digest(), NO_WRAP))
                }
            } else {
                val packageInfo =
                    getPackageInfo(context, PackageManager.GET_SIGNATURES) ?: return null

                for (signature in packageInfo!!.signatures) {
                    try {
                        val md = MessageDigest.getInstance("SHA")
                        md.update(signature.toByteArray())
                        return Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                    } catch (e: NoSuchAlgorithmException) {
                        Log.d(REQUEST_TAG,"Unable to get MessageDigest. signature=$signature")
                    }
                }
            }
        }catch (e: PackageManager.NameNotFoundException){
            e.printStackTrace()
        }catch (e: NoSuchAlgorithmException){
            e.printStackTrace()
        }
        return null
    }


    private class SessionCallback : ISessionCallback {

        private val TAG = "SessionCallback"

        override fun onSessionOpenFailed(exception: KakaoException?) {
            Log.d(TAG, "Session Call back :: onSessionOpenFailed: ${exception?.message}")
        }

        override fun onSessionOpened() {
            val keys = ArrayList<String>()
            keys.add("emailNeedsAgreement")
            keys.add("ageRangeNeedsAgreement")
            keys.add("birthdayNeedsAgreement")
            keys.add("genderNeedsAgreement")

            keys.add("properties.nickname")
            keys.add("properties.profile_image")
            keys.add("kakao_account.email")
            keys.add("kakao_account.age_range")
            keys.add("kakao_account.birthday")
            keys.add("kakao_account.gender")

            UserManagement.getInstance().me(keys,object : MeV2ResponseCallback() {

                override fun onFailure(errorResult: ErrorResult?) {
                    Log.d(TAG, "Session Call back :: on failed ${errorResult?.errorMessage}")
                }

                override fun onSessionClosed(errorResult: ErrorResult?) {
                    Log.d(TAG, "Session Call back :: onSessionClosed ${errorResult?.errorMessage}")

                }

                override fun onSuccess(result: MeV2Response?) {
                    checkNotNull(result) { "session response null" }
                    // register or login
                    Log.d(TAG,result.toString())
                }

            })
        }
    }
}