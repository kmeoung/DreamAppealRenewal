package com.truevalue.dreamappeal.bean

data class BeanCommentDetail(var idx : Int,
                             var profile_idx : Int,
                             var writer_idx : Int,
                             var parent_idx : Int,
                             var content : String?,
                             var like_count : Int,
                             var register_date : String,
                             var tmp : Int,
                             var value_style : String?,
                             val tag_profile_idx : Int?,
                             val tag_name : String?,
                             var job : String?,
                             var image : String?,
                             var name : String?,
                             var status : Boolean,
                             var parent_name : String?)
