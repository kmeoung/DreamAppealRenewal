package com.truevalue.dreamappeal.bean

data class BeanConcernVote(
    val code: String,
    val concern_idx: Int,
    val message: String,
    val status: Boolean,
    val vote_side: String,
    val votes: String
)