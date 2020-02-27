package com.truevalue.dreamappeal.fragment.login

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import com.kakao.util.exception.KakaoException
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityLoginContainer
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.http.BaseOkhttpClient
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Param
import com.truevalue.dreamappeal.utils.Comm_Prefs
import kotlinx.android.synthetic.main.fragment_login_container.*
import okhttp3.Call
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class FragmentLoginContainer : BaseFragment() {

    // google login result
    private var RC_SIGN_IN = 9001
    // google api client
    private lateinit var googleSignInClient: GoogleSignInClient
    // firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var callbackManager: CallbackManager

//    private lateinit var kakaoCallback: SessionCallback

    companion object {
        private const val TAG = "FragmentLoginContainer"
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = firebaseAuth.currentUser
//        updateUI(currentUser)
//        FirebaseAuth.getInstance().signOut()
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

        getHashKey()

        firebaseAuth = FirebaseAuth.getInstance()
        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()
        setFacebookApi()

        setGoogleApi()

//        setKakaoApi()
    }


    private fun getHashKey() {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo =
                activity!!.packageManager.getPackageInfo(
                    activity!!.packageName,
                    PackageManager.GET_SIGNATURES
                )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (packageInfo == null) Log.e("KeyHash", "KeyHash:null")
        for (signature in packageInfo!!.signatures) {
            try {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            } catch (e: NoSuchAlgorithmException) {
                Log.e(
                    "KeyHash",
                    "Unable to get MessageDigest. signature=$signature",
                    e
                )
            }
        }
    }

    /**
     * Facebook 설정
     */
    private fun setFacebookApi() {
        val listener = object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                //login success
                result?.let { result ->
                    handleFacebookAccessToken(result.accessToken)
                }
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                //login cancelled by user

            }

            override fun onError(error: FacebookException?) {
                Log.d(TAG, "facebook:onError", error)
                //login error handle exception
            }

        }

        AccessToken.setCurrentAccessToken(null)
        LoginManager.getInstance().logOut()
        firebaseAuth.signOut()

        login_facebook.setPermissions(Arrays.asList("email", "public_profile"))
        login_facebook.fragment = this
        login_facebook.registerCallback(callbackManager, listener)// ...
    }

    private fun handleFacebookAccessToken(token: AccessToken) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity!!) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }

                // ...
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        user?.let { user ->
            Toast.makeText(context!!.applicationContext, getString(R.string.str_wait_login), Toast.LENGTH_SHORT)
                .show()

            user.getIdToken(true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val idToken = task.result?.token ?: ""
                        Comm_Prefs.setSNSToken(idToken)
                        DAClient.snsLogin(idToken, object : DAHttpCallback {

                            override fun onResponse(
                                call: Call,
                                serverCode: Int,
                                body: String,
                                code: String,
                                message: String
                            ) {
                                context?.let { context ->

                                    if (code == DAClient.SUCCESS) {
                                        val json = JSONObject(body)
                                        val token = json.getString("token")
                                        val profileIdx = json.getInt("profile_idx")
                                        Comm_Prefs.setToken(token)
                                        Comm_Prefs.setUserProfileIndex(profileIdx)

                                        val intent = Intent(context, ActivityMain::class.java)
                                        activity?.startActivity(intent)
                                        activity?.finish()

                                    } else {
                                        AccessToken.setCurrentAccessToken(null)
                                        LoginManager.getInstance().logOut()
                                        FirebaseAuth.getInstance().signOut()

                                        Toast.makeText(
                                            context.applicationContext,
                                            message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                            }
                        })
                    } else {
                        // Handle error -> task.getException();
                    }
                }


        } ?: kotlin.run {
            Toast.makeText(context!!.applicationContext,getString(R.string.str_login_failed), Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * Google 설정
     */
    private fun setGoogleApi() {
        // config signin
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context!!, gso)

        login_google.setOnClickListener {
            googleSignInClient.signOut()
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

    }

    /**
     * Firebase Auth With Google
     */
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity!!) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }

                // ...
            }
    }

    /**
     * View Destroyed
     */
    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Facebook 로그인 인텐트 응답
        callbackManager.onActivityResult(requestCode, resultCode, data)

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
                }
            }
        }
        btn_login.setOnClickListener(listener)
        btn_register.setOnClickListener(listener)
    }

}