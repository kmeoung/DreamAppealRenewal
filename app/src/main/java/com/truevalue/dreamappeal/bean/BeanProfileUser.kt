package com.truevalue.dreamappeal.bean

data class BeanProfileUser(
    var idx: Int,
    var name: String,
    var nickname: String,
    var gender: Int,
    var address: String,
    var email: String,
    var birth: String,
    var private : BeanProfileUserPrivates
)