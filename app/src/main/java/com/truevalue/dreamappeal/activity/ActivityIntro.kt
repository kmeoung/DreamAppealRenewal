package com.truevalue.dreamappeal.activity
;

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.utils.Comm_Prefs

class ActivityIntro : BaseActivity() {
    val DELAY: Long = 1000 * 1

    val handler = Handler(Handler.Callback {

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
            if (prefs.isLogin) { // 바로 메인
                intent = Intent(this@ActivityIntro, ActivityMain::class.java)
            } else { // 로그인 페이지
                // todo : 로그인 지정이 필요합니다
                intent = Intent(this@ActivityIntro, ActivityLoginContainer::class.java)
            }
        } else {
            intent = Intent(this@ActivityIntro, ActivityPermission::class.java)
        }

        startActivity(intent)
        // TODO : Activity 애니메이션 없애기
//            overridePendingTransition(0, 0);
        finish()
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