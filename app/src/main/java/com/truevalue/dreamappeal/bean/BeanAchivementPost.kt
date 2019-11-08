package com.truevalue.dreamappeal.bean

data class BeanAchivementPost(var idx : Int,
                              var profile_idx : Int,
                              var title : String,
                              var content : String,
                              var thumbnail_image : String,
                              var register_date : String,
                              var comment_count : Int,
                              var like_count : Int,
                              var status : Boolean)
