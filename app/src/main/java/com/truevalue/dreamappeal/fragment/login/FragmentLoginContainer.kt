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
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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

    // google login result
    private var RC_SIGN_IN = 9001
    // google api client
    private var googleSignInClient : GoogleSignInClient?
    // firebase auth
    private var firebaseAuth : FirebaseAuth?

    companion object{
        private const val TAG = "MainActivity"

    }

    init {
        callback = SessionCallback()
        googleSignInClient = null
        firebaseAuth = null
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

        callback = SessionCallback()
        Session.getCurrentSession().addCallback(callback)

        /** 토큰 만료시 갱신을 시켜준다**/
        if (Session.getCurrentSession().isOpenable) {
            Session.getCurrentSession().checkAndImplicitOpen()
        }

        Toast.makeText(context!!.applicationContext,
            "시작!",Toast.LENGTH_SHORT).show()
        Log.e(TAG, "해시 : " + getHashKey(context!!))
        Log.e(TAG, "토큰 : " + Session.getCurrentSession().tokenInfo.accessToken)
        Log.e(TAG, "토큰 리프레쉬토큰 : " + Session.getCurrentSession().tokenInfo.refreshToken)
        Log.e(TAG, "토큰 파이어데이트 : " + Session.getCurrentSession().tokenInfo.remainingExpireTime)

        setGoogleApi()
    }

    /**
     * Google 설정
     */
    private fun setGoogleApi(){
        // config signin
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context!!,gso)

        firebaseAuth = FirebaseAuth.getInstance()

        login_google.setOnClickListener {
            val signInIntent = googleSignInClient?.signInIntent
            startActivityForResult(signInIntent,RC_SIGN_IN)
        }

    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(activity!!) {
                // 성공여부
                if (it.isSuccessful) {

                    val user = firebaseAuth?.currentUser
                    Toast.makeText(context!!.applicationContext, "로그인 성공", Toast.LENGTH_SHORT).show()

                } else {

                    Toast.makeText(context!!.applicationContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
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

        // Google 로그인 인텐트 응답
        if (requestCode === RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    firebaseAuthWithGoogle(it)
                }

            } catch (e: ApiException) {
                e.printStackTrace()
            }
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
                    (activity as ActivityLoginContainer).replaceFragment(
                        FragmentRegister(),
                        true
                    )
//                    UserManagement.getInstance().requestLogout(object : LogoutResponseCallback(){
//                        override fun onCompleteLogout() {
//
//                        }
//                    })
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


    private inner class SessionCallback : ISessionCallback {

        override fun onSessionOpenFailed(exception: KakaoException?) {
            Log.e(TAG, "Session Call back :: onSessionOpenFailed: ${exception?.message}")
            Toast.makeText(context!!.applicationContext,
                "카카오 깡 실패",Toast.LENGTH_SHORT).show()
        }

        override fun onSessionOpened() {
            Log.e(TAG, "카카오 로그인 성공 ")
            Toast.makeText(context!!.applicationContext,
                "카카오 로그인 성공",Toast.LENGTH_SHORT).show()
            /** 사용자에 대한 정보를 가져온다 **/
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
                    Log.e(TAG, "Session Call back :: on failed ${errorResult?.errorMessage}")
                    Toast.makeText(context!!.applicationContext,
                        "카카오 사용자 정보 가져오기 실패",Toast.LENGTH_SHORT).show()
                }

                override fun onSessionClosed(errorResult: ErrorResult?) {
                    Log.e(TAG, "Session Call back :: onSessionClosed ${errorResult?.errorMessage}")
                    Toast.makeText(context!!.applicationContext,
                        "카카오 세션 닫힘",Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(result: MeV2Response?) {
                    Toast.makeText(context!!.applicationContext,
                        "카카오 사용자 정보 가져오기 성공",Toast.LENGTH_SHORT).show()
                    // register or login
                    Log.e(TAG,result.toString())
                }

            })
        }
    }
}