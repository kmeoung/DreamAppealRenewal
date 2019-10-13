package com.truevalue.dreamappeal.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity(){

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