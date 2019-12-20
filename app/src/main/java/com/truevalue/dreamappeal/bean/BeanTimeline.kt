package com.truevalue.dreamappeal.bean

data class BeanTimeline(var idx : Int,
                        var profile_idx : Int,
                        var object_title : String?,
                        var step_title : String?,
                        var content : String,
                        var tags : String?,
                        var thumbnail_image : String?,
                        var post_type : Int,
                        var value_style : String?,
                        var job : String?,
                        var nickname : String,
                        var profile_image : String?,
                        var register_date : String,
                        var comment_count : Int,
                        var like_count : Int,
                        var status : Boolean)
