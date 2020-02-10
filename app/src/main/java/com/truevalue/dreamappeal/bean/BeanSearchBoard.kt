package com.truevalue.dreamappeal.bean

import java.io.Serializable

data class BeanSearchBoard(var idx : Int,
                           val profile_idx : Int,
                           var object_name : String?,
                           var thumbnail_image : String?,
                           var value_style : String?,
                           var job : String?,
                           var tags : String?,
                           var post_type : Int?,
                           var content : String?) : Serializable