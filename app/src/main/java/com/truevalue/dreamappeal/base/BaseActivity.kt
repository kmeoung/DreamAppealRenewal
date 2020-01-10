package com.truevalue.dreamappeal.base

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

open class BaseActivity : AppCompatActivity(){

    companion object{
        const val RESULT_CODE = 1004
        const val RESULT_REPLACE_USER_IDX = "RESULT_REPLACE_USER_IDX"
    }

    /**
     * Fragment Replace
     */
    fun replaceFragment(container : Int, fragment : Fragment, addToBack : Boolean){
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(container, fragment)
        if(addToBack){
            ft.addToBackStack("")
        }
        ft.commit()
    }
}