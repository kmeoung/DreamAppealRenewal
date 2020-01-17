package com.truevalue.dreamappeal.bean

data class BeanMyFame(
    val concern_history: List<ConcernHistory>,
    val re_concern_history: List<ReConcernHistory>,
    val user: User
)

data class ConcernHistory(
    val count: Int,
    val idx: Int,
    val title: String,
    val votes: String
)

data class ReConcernHistory(
    val count: Int,
    val idx: Int,
    val title: String,
    val votes: String
)

data class User(
    val idx: Int,
    val nickname: String,
    val point: Int,
    val user_idx: Int
)