package com.truevalue.dreamappeal.bean

data class BeanConcernDetail(
    val adopted_re_post: AdoptedRePost?,
    val images: List<Image>?,
    val post: Post,
    val post_writer: PostWriter,
    val re_posts: List<RePost>?
)

data class AdoptedRePost(
    val adopted: Int,
    val content: String,
    val idx: Int,
    val job: String,
    val nickname: String,
    val register_date: String,
    val reputation: String,
    val value_style: String,
    val votes: String
)

data class Image(
    val concern_idx: Int,
    val idx: Int,
    val image_url: String
)

data class Post(
    val content: String,
    val idx: Int,
    val profile_idx: Int,
    val register_date: String,
    val title: String,
    val votes: String
)

data class PostWriter(
    val job: String,
    val nickname: String,
    val reputation: String,
    val value_style: String
)

data class RePost(
    val adopted: Int,
    val content: String,
    val idx: Int,
    val job: String,
    val nickname: String,
    val register_date: String,
    val reputation: String,
    val value_style: String,
    val votes: String
)