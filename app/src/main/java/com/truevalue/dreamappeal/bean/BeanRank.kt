package com.truevalue.dreamappeal.bean

data class BeanRank(
    val code: String,
    val high_rank: List<Rank>,
    val message: String,
    val my_range: List<Rank>,
    val my_rank: Rank
)

data class Rank(
    val action_post_init: Int,
    val exp: Int,
    val idx: Int,
    val image: String?,
    val job: String,
    val level: Int,
    val nickname: String,
    val profile_init: Int,
    val ranking: Int,
    val row_num: Int,
    val value_style: String?,
    val count: Int,
    val user_idx : Int?,
    val point : Int?
)

