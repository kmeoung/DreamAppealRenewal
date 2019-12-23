package com.truevalue.dreamappeal.bean

data class BeanProfileGroup(
    var idx: Int,
    var group_idx: Int,
    var groupName: String?,
    var Class: Int, //
    var position: String?,
    var description: String?,
    var start_date: String?,
    var end_date: String?
)
/**
 * class 각 숫자의 의미
 *  0 : 직장
 *  1 : 학교
 *  2 : 동아리
 *  3 : 단체
 */