package com.truevalue.dreamappeal.bean

data class BeanNotification(
    val code: String,
    val following_items: List<Item>,
    val message: String,
    val private_items: List<Item>
)

data class Item(
    val present_idx: Int?,
    val achievement_idx: Int?,
    val action_idx: Int?,
    val checked: Int,
    val code: String,
    val concern_idx: Int?,
    val contents_bold: String?,
    val contents_regular: String?,
    val image: String?,
    val item_idx: Int,
    val job: String?,
    val nickname: String?,
    val post_type: Int,
    val profile_idx: Int,
    val reg_date: String,
    val source_idx: Int,
    val thumbnail_image: String?,
    val type: Int,
    val user_idx: Int,
    val value_style: String?
)
