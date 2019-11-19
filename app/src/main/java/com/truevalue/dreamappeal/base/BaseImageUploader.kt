package com.truevalue.dreamappeal.base

import com.amazonaws.AmazonServiceException
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.PutObjectRequest
import java.io.File

class BaseImageUploader{
    val ACCESS_KEY = "AKIA22CFAVT2BRQ7BCXX"
    val SECRET_KEY = "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"
    val BUCKET_NAME = "dreamappeal-dev"
    val SUB_BUCKET_NAME = "images/profiles/"
    var amazonS3 : AmazonS3Client? = null
    init {
        val awsCredentials = BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)
        amazonS3 = AmazonS3Client(awsCredentials)
    }

    fun uploadFile(file : File) {
        if (amazonS3 != null) {
            try {
                val putObjectRequest = PutObjectRequest(BUCKET_NAME/*sub directory*/,SUB_BUCKET_NAME +  file.name, file);
                putObjectRequest.cannedAcl = CannedAccessControlList.PublicRead // file permission
                amazonS3!!.putObject(putObjectRequest); // upload file

            } catch (ase : AmazonServiceException) {
                ase.printStackTrace();
            } finally {
                amazonS3 = null;
            }
        }
    }
}