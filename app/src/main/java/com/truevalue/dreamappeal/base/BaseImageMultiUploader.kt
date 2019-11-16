package com.truevalue.dreamappeal.base

import com.amazonaws.AmazonServiceException
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.PutObjectRequest
import java.io.File

class BaseImageMultiUploader {
    val clientRegion = Regions.AP_NORTHEAST_2;
    val bucketName = "*** Bucket name ***";
    val keyName = "*** Object key ***";
    val filePath = "*** Path for file to upload ***";

//    fun uploadFile(file : File) {
//        try {
//            val s3Client = Amazons3c.standard()
//                .withRegion(clientRegion)
//                .withCredentials(new ProfileCredentialsProvider())
//                .build();
//            TransferManager tm = TransferManagerBuilder.standard()
//                .withS3Client(s3Client)
//                .build();
//
//            // TransferManager processes all transfers asynchronously,
//            // so this call returns immediately.
//            Upload upload = tm.upload(bucketName, keyName, new File(filePath));
//            System.out.println("Object upload started");
//
//            // Optionally, wait for the upload to finish before continuing.
//            upload.waitForCompletion();
//            System.out.println("Object upload complete");
//        } catch (e : AmazonServiceException) {
//            // The call was transmitted successfully, but Amazon S3 couldn't process
//            // it, so it returned an error response.
//            e.printStackTrace();
//        } catch (e : SdkClientException) {
//            // Amazon S3 couldn't be contacted for a response, or the client
//            // couldn't parse the response from Amazon S3.
//            e.printStackTrace();
//        }
//    }


}