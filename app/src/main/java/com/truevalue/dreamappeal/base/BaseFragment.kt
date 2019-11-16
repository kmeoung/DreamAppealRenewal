package com.truevalue.dreamappeal.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

open class BaseFragment : Fragment(){

    /**
     * Fragment Replace
     */
    fun replaceFragment(container: Int, fragment: Fragment, addToStack: Boolean) {
        val fm = fragmentManager as FragmentManager
        val ft = fm.beginTransaction()

        if (addToStack) {
            ft.addToBackStack(null)
        }
        ft.add(container, fragment)
        ft.commit()
    }

    /**
     * Refresh Data
     */
    open fun OnServerRefresh(){}
}