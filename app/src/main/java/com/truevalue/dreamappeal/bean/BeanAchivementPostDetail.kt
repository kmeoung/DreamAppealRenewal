package com.truevalue.dreamappeal.bean

data class BeanAchivementPostDetail(var idx : Int,
                                    var profile_idx : Int,
                                    var title : String,
                                    var content : String,
                                    var tags : String,
                                    var register_date : String,
                                    var comment_count : Int,
                                    var like_count : Int,
                                    var Images : ArrayList<BeanImages>,
                                    var status : Boolean)