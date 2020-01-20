package com.truevalue.dreamappeal.bean

data class BeanConcernDetail(
    val adopted_re_post: AdoptedRePost,
    val code: String,
    val images: List<Image>,
    val message: String,
    val post: Post,
    val post_writer: PostWriter,
    val re_posts: List<RePost>
)

data class AdoptedRePost(
    val adopted: Int,
    val auth: Boolean,
    val content: String,
    val idx: Int?,
    val image: String,
    val job: String,
    val nickname: String,
    val register_date: String,
    val reputation: String,
    val status: Boolean,
    val value_style: String,
    val vote_type: String,
    val votes: String
)

data class Image(
    val concern_idx: Int,
    val idx: Int,
    val image_url: String
)

data class Post(
    val auth: Boolean,
    val content: String,
    val idx: Int,
    val profile_idx: Int,
    val register_date: String,
    val status: Boolean,
    val title: String,
    val vote_type: String,
    val votes: String
)

data class PostWriter(
    val image: String,
    val job: String,
    val nickname: String,
    val reputation: String,
    val value_style: String
)

data class RePost(
    val adopted: Int,
    val auth: Boolean,
    val content: String,
    val idx: Int,
    val image: String,
    val job: String,
    val nickname: String,
    val register_date: String,
    val reputation: String,
    val status: Boolean,
    val value_style: String,
    val vote_type: String,
    val votes: String
)