package com.truevalue.dreamappeal.base

import android.util.Log
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState

interface IOS3ImageUploaderListener {

    fun onStateCompleted(id: Int, state: TransferState,imageBucketAddress : String)
}