package com.truevalue.dreamappeal.bean

data class BeanScrapMember(
    val code: String,
    val message: String,
    val scrap_targets: List<ScrapTarget>
)

data class ScrapTarget(
    val idx: Int,
    val image: String,
    val job: String,
    val nickname: String,
    val value_style: String
)