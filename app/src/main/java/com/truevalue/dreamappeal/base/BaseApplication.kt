package com.truevalue.dreamappeal.base

import android.app.Application
import android.content.Context
import com.truevalue.dreamappeal.utils.Comm_Prefs
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.kakao.auth.*


class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val prefs = Comm_Prefs

        prefs.init(applicationContext)

        KakaoSDK.init(KakaoSDKAdapter())
    }

    private inner class KakaoSDKAdapter : KakaoAdapter() {

        /**
         * Session Config에 대해서는 default값들이 존재한다.
         * 필요한 상황에서만 override해서 사용하면 됨.
         * @return Session의 설정값.
         */
        override fun getSessionConfig(): ISessionConfig {
            return object : ISessionConfig {
                override fun getAuthTypes(): Array<AuthType> {
                    return arrayOf(AuthType.KAKAO_LOGIN_ALL)
                }

                override fun isUsingWebviewTimer(): Boolean {
                    return false
                }

                override fun isSecureMode(): Boolean {
                    return false
                }

                override fun getApprovalType(): ApprovalType? {
                    return ApprovalType.INDIVIDUAL
                }

                override fun isSaveFormData(): Boolean {
                    return true
                }
            }
        }

        override fun getApplicationConfig(): IApplicationConfig {
            return IApplicationConfig { applicationContext }
        }
    }
}