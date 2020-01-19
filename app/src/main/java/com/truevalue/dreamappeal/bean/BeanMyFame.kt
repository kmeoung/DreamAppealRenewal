package com.truevalue.dreamappeal.bean

data class BeanMyFame(
    val concern_history: List<ConcernHistory>,
    val re_concern_history: List<ReConcernHistory>,
    val user: User
)

data class ConcernHistory(
    val idx: Int,
    val count: Int,
    val adopted : Int,
    val title: String,
    val votes: String
)

data class ReConcernHistory(
    val idx: Int,
    val count: Int,
    val adopted : Int,
    val title: String,
    val votes: String
)

data class User(
    val image: String?,
    val nickname: String,
    val reputation: Int,
    val user_idx: Int
)

