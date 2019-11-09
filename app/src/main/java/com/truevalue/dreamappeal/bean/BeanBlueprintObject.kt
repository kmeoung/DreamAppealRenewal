package com.truevalue.dreamappeal.bean

data class BeanBlueprintObject(
    var idx: Int,
    var profile_idx: Int,
    var object_name: String?,
    var thumbnail_image: String?,
    var complete: Int, // 0 아님 1
    var complete_date: String?,
    var total_action_post_count: Int
)