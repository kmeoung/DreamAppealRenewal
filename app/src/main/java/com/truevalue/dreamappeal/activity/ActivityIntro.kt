package com.truevalue.dreamappeal.activity
;

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import androidx.core.content.ContextCompat
import com.kakao.util.helper.Utility.getPackageInfo
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.service.ServiceFirebaseMsg
import com.truevalue.dreamappeal.utils.Comm_Param
import com.truevalue.dreamappeal.utils.Comm_Prefs
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class ActivityIntro : BaseActivity() {

    companion object{
        private const val TAG = "ActivityIntro"
        private const val DELAY: Long = 1000 * 1
    }

    private val handler = Handler(Handler.Callback {

        val cameraCheck =
            ContextCompat.checkSelfPermission(this@ActivityIntro, Manifest.permission.CAMERA)
        val writeStorage =
            ContextCompat.checkSelfPermission(
                this@ActivityIntro,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        val readStorage =
            ContextCompat.checkSelfPermission(
                this@ActivityIntro,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        var intent: Intent
        if (cameraCheck == PackageManager.PERMISSION_GRANTED
            && writeStorage == PackageManager.PERMISSION_GRANTED
            && readStorage == PackageManager.PERMISSION_GRANTED
        ) {
            val prefs = Comm_Prefs
            intent = if (prefs.getUserProfileIndex() > -1) { // 바로 메인
                Intent(this@ActivityIntro, ActivityMain::class.java)
            } else { // 로그인 페이지
                Intent(this@ActivityIntro, ActivityLoginContainer::class.java)
            }
        } else {
            intent = Intent(this@ActivityIntro, ActivityPermission::class.java)
        }

        getIntent().getStringExtra(ServiceFirebaseMsg.TYPE)?.let{
            intent.putExtra(ServiceFirebaseMsg.TYPE, it)
        }

        startActivity(intent)

        finish()
        // TODO : Activity 애니메이션 없애기
//            overridePendingTransition(0, 0);
        false
    })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
    }

    override fun onResume() {
        super.onResume()
        handler.sendEmptyMessageDelayed(0, DELAY)
    }

    override fun onPause() {
        super.onPause()
        handler.removeMessages(0)
    }
}