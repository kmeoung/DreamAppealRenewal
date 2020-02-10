package com.truevalue.dreamappeal.bean

data class BeanActionPostDetail(var idx : Int,
                                var profile_idx : Int,
                                var object_idx : Int,
                                var step_idx : Int,
                                var content : String,
                                var thumbnail_image : String?,
                                var tags : String?,
                                var profile_image : String?,
                                var register_date : String,
                                var object_name : String?,
                                var step_name : String?,
                                var comment_count : Int,
                                var like_count : Int,
                                val link : String?,
                                var status : Boolean)
