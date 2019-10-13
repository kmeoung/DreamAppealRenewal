package com.truevalue.dreamappeal.base

import android.view.View
import android.os.SystemClock


/**
 * 중복 클릭 방지
 */
abstract class OnSingleClick : View.OnClickListener {

    // 중복 클릭 방지 시간 설정
    private val MIN_CLICK_INTERVAL: Long = 600
    private var mLastClickTime: Long = 0

    abstract fun onSingleClick(v : View?);

    override fun onClick(v: View?) {
        val currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - mLastClickTime
        mLastClickTime = currentClickTime

        // 중복 클릭인 경우
        if (elapsedTime <= MIN_CLICK_INTERVAL) {
            return
        }

        // 중복 클릭이 아니라면 추상함수 호출
        onSingleClick(v)
    }
}