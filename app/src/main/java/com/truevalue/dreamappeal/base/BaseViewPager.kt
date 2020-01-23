package com.truevalue.dreamappeal.base

import android.content.Context
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import java.lang.Exception

class BaseViewPager(context : Context) : ViewPager(context){

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        try{
            return super.onTouchEvent(ev)
        }catch (e: Exception){
            e.printStackTrace()
        }
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        try{
            return super.onInterceptTouchEvent(ev)
        }catch (e : Exception){
            e.printStackTrace()
        }
        return false
    }
}