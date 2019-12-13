package com.truevalue.dreamappeal.bean

data class BeanGalleryInfo(
    var bucketName: String,
    var bucketId: String,
    var imagePath: String?,
    var imageCheck : Boolean,
    var imageSelectedIdx : Int
){
    init {
        imageCheck = false
    }
}