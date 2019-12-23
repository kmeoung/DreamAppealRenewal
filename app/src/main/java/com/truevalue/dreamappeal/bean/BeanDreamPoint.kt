package com.truevalue.dreamappeal.bean

data class BeanDreamPoint(
    var idx: Int,
    var missionName: String?,
    var limit: Int,
    var curState : Int,
    var point: Int,
    var status: Int
)

/**
 * 0 Disable
 * 1
 */