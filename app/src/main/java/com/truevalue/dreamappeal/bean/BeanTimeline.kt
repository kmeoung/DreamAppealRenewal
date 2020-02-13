package com.truevalue.dreamappeal.bean

data class BeanTimeline(
    val achievement_idx: Int?,
    val action_idx: Int?,
    val code: String?,
    val comment_count: Int?,
    val concern_idx: Int?,
    val content: String?,
    val contents_bold: String?,
    val title : String?,
    val contents_regular: String?,
    val idx: Int?,
    val images: List<TimelineImage>?,
    val item_idx: Int?,
    val job: String?,
    val link : String?,
    var like_count: Int?,
    val nickname: String?,
    val object_title: String?,
    val post_type: Int?,
    val copied : Int?,
    val present_idx: Int?,
    val profile_idx: Int?,
    val profile_image: String?,
    val register_date: String?,
    val source_idx: Int?,
    var status: Boolean?,
    val step_title: String?,
    val tags: String?,
    val type: Int?,
    val unconfirmed: Int?,
    val user_idx: Int?,
    val value_style: String?,
    val origin_post_writer : originPostWriter?
)

data class TimelineImage(
    val url: String?
)

data class originPostWriter(val value_style:String?,
                              val job:String?,
                              val nickname:String?)