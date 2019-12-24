package com.truevalue.dreamappeal.bean


data class BeanAnotherProfile(
    var idx : Int,
    var name : String?,
    var nickname : String?,
    var gender : String?,
    var address : String?,
    var email : String?,
    var group: ArrayList<BeanAnotherProfileGroup>?,
    var private : BeanProfileUserPrivates?,
    var birth : String?
)

