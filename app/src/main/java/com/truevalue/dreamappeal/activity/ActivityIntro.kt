package com.truevalue.dreamappeal.activity
;

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.utils.Comm_Prefs

class ActivityIntro : BaseActivity() {
    val DELAY: Long = 1000 * 1

    val handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)

            val cameraCheck = ContextCompat.checkSelfPermission(this@ActivityIntro, Manifest.permission.CAMERA)
            val writeStorage =
                ContextCompat.checkSelfPermission(this@ActivityIntro, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val readStorage =
                ContextCompat.checkSelfPermission(this@ActivityIntro, Manifest.permission.READ_EXTERNAL_STORAGE)

            if (cameraCheck == PackageManager.PERMISSION_GRANTED
                && writeStorage == PackageManager.PERMISSION_GRANTED
                && readStorage == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(this@ActivityIntro, ActivityMain::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this@ActivityIntro, ActivityMain::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

//    private val handler = object : Handler() {
//        override fun handleMessage(msg: Message) {
//            super.handleMessage(msg)
//
//            val cameraCheck = ContextCompat.checkSelfPermission(this@ActivityIntro, Manifest.permission.CAMERA)
//            val writeStorageCheck =
//                ContextCompat.checkSelfPermission(this@ActivityIntro, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            val readStorageCheck =
//                ContextCompat.checkSelfPermission(this@ActivityIntro, Manifest.permission.READ_EXTERNAL_STORAGE)
//            val intent: Intent
//            if (cameraCheck == PackageManager.PERMISSION_GRANTED
//                && writeStorageCheck == PackageManager.PERMISSION_GRANTED
//                && readStorageCheck == PackageManager.PERMISSION_GRANTED
//            ) {
//                // 권한 있음
//                val prefs = Comm_Prefs(this@ActivityIntro)
//
//                if (prefs.isLogin() && prefs.getMyProfileIndex() !== -1) { // 바로 메인
//                    intent = Intent(this@ActivityIntro, ActivityMain::class.java)
//                } else { // 로그인 페이지
//                    intent = Intent(this@ActivityIntro, ActivityLogin::class.java)
//                }
//                // todo : 이부분 확인 필요
//                // 앱 껏다 켰을 시 다시 내 profile index로 설정
//                if (prefs.getMyProfileIndex() !== prefs.getProfileIndex()) {
//                    prefs.setProfileIndex(prefs.getMyProfileIndex(), true)
//
//                }
//
//            } else {
//                // 권한 없음
//                intent = Intent(this@ActivityIntro, ActivityPermission::class.java)
//            }
//
//            startActivity(intent)
//
//            // TODO : Activity 애니메이션 없애기
//            //            overridePendingTransition(0, 0);
//            finish()
//        }
//    }


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