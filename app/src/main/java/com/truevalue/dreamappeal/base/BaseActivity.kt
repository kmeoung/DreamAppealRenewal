package com.truevalue.dreamappeal.base

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity

open class BaseActivity : AppCompatActivity(){

    /**
     * Fragment Replace
     */
    fun replaceFragment(container : Int, fragment : Fragment, addToBack : Boolean){
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(container, fragment!!)
        if(addToBack){
            ft.addToBackStack("")
        }
        ft.commit()
    }
}