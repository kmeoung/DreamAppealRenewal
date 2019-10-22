package com.truevalue.dreamappeal.base

interface IOActionBarListener {

    fun onClickMenu() {}
    fun onClickBack() {}
    fun onClickClose() {}
    val title: String
    fun onClickSearch() {}
    fun onClickText(): String = ""

}