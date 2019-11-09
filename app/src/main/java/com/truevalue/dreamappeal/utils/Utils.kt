package com.truevalue.dreamappeal.utils

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import com.truevalue.dreamappeal.R
import java.util.*
import java.util.regex.Pattern

object Utils {

    /**
     * replace Text Color
     */
    fun replaceTextColor(
        context: Context?,
        tv: TextView,
        changeText: String
    ): SpannableStringBuilder {
        if (context == null) return SpannableStringBuilder()
        val str = tv.text.toString()
        val first = str.indexOf(changeText)
        val last = str.lastIndexOf(changeText) + changeText.length
        val ssb = SpannableStringBuilder(str)
        ssb.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.main_blue)),
            first,
            last,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return ssb
    }

    /**
     * replace Text Color
     */
    fun replaceTextColor(
        context: Context?,
        str: String,
        changeText: String
    ): SpannableStringBuilder {
        if (context == null) return SpannableStringBuilder()
        val first = str.indexOf(changeText)
        val last = str.lastIndexOf(changeText) + changeText.length
        val ssb = SpannableStringBuilder(str)
        ssb.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.main_blue)),
            first,
            last,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return ssb
    }

    /**
     * replace Text Color
     */
    fun replaceTextColor(
        context: Context?,
        str: String,
        color: Int,
        changeText: String
    ): SpannableStringBuilder {
        if (context == null) return SpannableStringBuilder()
        val first = str.indexOf(changeText)
        val last = str.lastIndexOf(changeText) + changeText.length
        val ssb = SpannableStringBuilder(str)
        ssb.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, color)),
            first,
            last,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return ssb
    }

    /**
     * 문자열이 Email 방식인지 인지 확인
     */
    fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    /**
     * RefreshView 설정
     */
    fun setSwipeRefreshLayout(
        srl: SwipeRefreshLayout,
        listener: SwipeRefreshLayout.OnRefreshListener
    ) {
        srl.setOnRefreshListener(listener)
        srl.setColorSchemeResources(R.color.main_blue)
    }

    /**
     * 나이 계산하기
     */
    fun dateToAge(date : Date) : Int{
        val cal = Calendar.getInstance()
        val curYear = cal.get(Calendar.YEAR)
        cal.time = date
        val inputYear = cal.get(Calendar.YEAR)
        return curYear - inputYear + 1
    }
}