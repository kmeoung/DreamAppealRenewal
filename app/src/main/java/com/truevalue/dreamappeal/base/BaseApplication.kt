package com.truevalue.dreamappeal.base

import android.app.Application
import android.content.Context
import com.truevalue.dreamappeal.utils.Comm_Prefs
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.example.stackoverflowuser.base.repository.isNetworkStatusAvailable
import com.kakao.auth.*
import com.truevalue.dreamappeal.utils.value


class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val prefs = Comm_Prefs

        prefs.init(applicationContext)

        KakaoSDK.init(KakaoSDKAdapter())

        instance = this
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

    companion object {
        private var instance: BaseApplication? = null
        fun isConnectInternet() = this.instance?.isNetworkStatusAvailable().value()
    }
}