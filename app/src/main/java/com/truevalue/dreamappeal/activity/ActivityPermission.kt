package com.truevalue.dreamappeal.activity

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.utils.Comm_Prefs
import kotlinx.android.synthetic.main.activity_permission.*

class ActivityPermission : BaseActivity() {

    companion object {
        private const val REQUEST_PERMISSION_GRANT = 3000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        btn_permission.setOnClickListener {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                REQUEST_PERMISSION_GRANT
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_GRANT) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 허가
                // 해당 권한을 사용해서 작업을 진행할 수 있습니다
//                val prefs = Comm_Prefs
//                val intent: Intent = if (prefs.getUserProfileIndex() > -1) { // 바로 메인
//                    Intent(this@ActivityPermission, ActivityMain::class.java)
//                } else { // 로그인 페이지
//                }
                Comm_Prefs.allReset()
                val intent = Intent(this@ActivityPermission, ActivityLoginContainer::class.java)
                startActivity(intent)
                finish()
            } else run {
                // 권한 거부
                // 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다
                //                CustomToast.makeText(this, "권한을 허용해주셔야 테스트가 가능합니다.", CustomToast.LENGTH_SHORT).show();
                val builder = AlertDialog.Builder(this@ActivityPermission)
                builder.setMessage("권한을 허용해주셔야 정상적으로 앱 이용이 가능합니다.")
                builder.setPositiveButton(
                    "확인"
                ) { dialog, _ -> dialog.dismiss() }
                builder.show()
            }

        }
    }
}
