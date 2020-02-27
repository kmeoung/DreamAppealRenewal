package com.truevalue.dreamappeal.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.OptionalBoolean
import com.kakao.util.exception.KakaoException
import com.kakao.util.helper.Utility.getPackageInfo
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.fragment.login.FragmentLoginContainer
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import okhttp3.Call
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class ActivityLoginContainer : BaseActivity() {

    companion object {
        private const val TAG = "ActivityLoginContainer"
    }

    lateinit var kakaoCallback : SessionCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setKakaoApi()

        Log.d("KaKaoHash",getKeyHash())
        // Action
        onAction()
    }

    private fun setKakaoApi() {
        // add kakao callback
        kakaoCallback = SessionCallback()
        Session.getCurrentSession().addCallback(kakaoCallback)
//        Session.getCurrentSession().checkAndImplicitOpen()
    }


    private fun onAction() {
        // 처음 로그인 컨테이너로 이동
        replaceFragment(R.id.base_container, FragmentLoginContainer(), false)
    }

    fun getKeyHash(): String? {
        val packageInfo = getPackageInfo(this@ActivityLoginContainer, PackageManager.GET_SIGNATURES) ?: return null

        for (signature in packageInfo!!.signatures) {
            try {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP)
            } catch (e: NoSuchAlgorithmException) {
                Log.w("KaKaoHashKey", "Unable to get MessageDigest. signature=$signature", e)
            }

        }
        return null
    }



    /**
     * Fragment에서 접근하는 Fragment 변경
     */
    fun replaceFragment(fragment: Fragment, addToBack: Boolean) {
        replaceFragment(R.id.base_container, fragment, addToBack)
    }

    /**
     * Fragment 초기화
     */
    fun initFragment(){
        val fm = supportFragmentManager

        for (i in 0 .. fm.backStackEntryCount){
            fm.popBackStack()
        }
    }

    /**
     * Fragment 초기화 후 이동
     */
    fun initFragment(fragment: Fragment){
        val fm = supportFragmentManager

        for (i in 0 .. fm.backStackEntryCount){
            fm.popBackStack()
        }
        replaceFragment(fragment,true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // 카카오 로그인 인텐트 응답
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(kakaoCallback)
    }

    /**
     * KAKAO 콜백
     */
    inner class SessionCallback : ISessionCallback {

        override fun onSessionOpenFailed(exception: KakaoException?) {
            Toast.makeText(applicationContext, getString(R.string.str_login_failed), Toast.LENGTH_SHORT).show()
            exception?.let {
                Log.d(TAG, exception.toString())
            }
        }

        override fun onSessionOpened() {
            Toast.makeText(applicationContext, getString(R.string.str_wait_login), Toast.LENGTH_SHORT).show()
            requestMe()
        }
    }

    private fun requestMe() {
        UserManagement.getInstance().me(object : MeV2ResponseCallback() {
            override fun onFailure(errorResult: ErrorResult?) {
                Toast.makeText(applicationContext, getString(R.string.str_login_failed), Toast.LENGTH_SHORT).show()
                val message = "failed to get user info. msg=" + errorResult!!
                Log.d(TAG,message)
            }

            override fun onSessionClosed(errorResult: ErrorResult) {
                //
            }

            override fun onSuccess(response: MeV2Response) {
                Log.d(TAG,"user id : " + response.id)

                val kakaoAccount = response.kakaoAccount
                if (kakaoAccount != null) {
                    val email = kakaoAccount.email
                    if (email != null) {
                        Log.d(TAG,"email: $email")
                    } else if (kakaoAccount.emailNeedsAgreement() == OptionalBoolean.TRUE) {
                        // 동의 요청 후 이메일 획득 가능
                        // 단, 선택 동의로 설정되어 있다면 서비스 이용 시나리오 상에서 반드시 필요한 경우에만 요청해야 합니다.
                    } else {
                        // 이메일 획득 불가
                    }

                    val profile = kakaoAccount.profile
                    if (profile != null) {
                        Log.d(TAG,"nickname: " + profile.nickname)
                        Log.d(TAG,"profile image: " + profile.profileImageUrl)
                        Log.d(TAG,"thumbnail image: " + profile.thumbnailImageUrl)
                    } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                        // 동의 요청 후 프로필 정보 획득 가능

                    } else {
                        // 프로필 획득 불가
                    }

                    DAClient.kakaoLogin(profile.nickname,email,Session.getCurrentSession().tokenInfo.accessToken,object :DAHttpCallback{
                        override fun onResponse(
                            call: Call,
                            serverCode: Int,
                            body: String,
                            code: String,
                            message: String
                        ) {
                            if (code == DAClient.SUCCESS) {
                                val json = JSONObject(body)
                                val token = json.getString("token")
                                val profileIdx = json.getInt("profile_idx")
                                Comm_Prefs.setToken(token)
                                Comm_Prefs.setUserProfileIndex(profileIdx)

                                val intent = Intent(applicationContext, ActivityMain::class.java)
                                startActivity(intent)
                                finish()

                            } else {
                                AccessToken.setCurrentAccessToken(null)
                                LoginManager.getInstance().logOut()
                                FirebaseAuth.getInstance().signOut()

                                Toast.makeText(
                                    applicationContext,
                                    message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    })

                }

            }

        })
    }

}