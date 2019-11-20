package com.truevalue.dreamappeal.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.PutObjectRequest
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.IOS3ImageUploaderListener
import com.truevalue.dreamappeal.bean.BeanGalleryInfo
import com.truevalue.dreamappeal.bean.BeanGalleryInfoList
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList


object Utils {

    /**
     * replace Text Color
     */
    fun replaceTextColor(
        context: Context?,
        tv: TextView,
        changeText: String
    ): SpannableStringBuilder {
        if (context == null) return SpannableStringBuilder()
        val str = tv.text.toString()
        val first = str.indexOf(changeText)
        val last = str.lastIndexOf(changeText) + changeText.length
        val ssb = SpannableStringBuilder(str)
        ssb.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.main_blue)),
            first,
            last,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return ssb
    }

    /**
     * replace Text Color
     */
    fun replaceTextColor(
        context: Context?,
        str: String,
        changeText: String
    ): SpannableStringBuilder {
        if (context == null) return SpannableStringBuilder()
        val first = str.indexOf(changeText)
        val last = str.lastIndexOf(changeText) + changeText.length
        val ssb = SpannableStringBuilder(str)
        ssb.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.main_blue)),
            first,
            last,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return ssb
    }

    /**
     * replace Text Color
     */
    fun replaceTextColor(
        context: Context?,
        str: String,
        color: Int,
        changeText: String
    ): SpannableStringBuilder {
        if (context == null) return SpannableStringBuilder()
        val first = str.indexOf(changeText)
        val last = str.lastIndexOf(changeText) + changeText.length
        val ssb = SpannableStringBuilder(str)
        ssb.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, color)),
            first,
            last,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return ssb
    }

    /**
     * 문자열이 Email 방식인지 인지 확인
     */
    fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    /**
     * RefreshView 설정
     */
    fun setSwipeRefreshLayout(
        srl: SwipeRefreshLayout,
        listener: SwipeRefreshLayout.OnRefreshListener
    ) {
        srl.setOnRefreshListener(listener)
        srl.setColorSchemeResources(R.color.main_blue)
    }

    /**
     * 나이 계산하기
     */
    fun dateToAge(date: Date): Int {
        val cal = Calendar.getInstance()
        val curYear = cal.get(Calendar.YEAR)
        cal.time = date
        val inputYear = cal.get(Calendar.YEAR)
        return curYear - inputYear + 1
    }

    /**
     * Spinner Dropdown 크기 조정
     */
    fun setDropDownHeight(spinner: Spinner, size: Int) {
        try {
            val popup = Spinner::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true

            // Get private mPopup member variable and try cast to ListPopupWindow
            val popupWindow = popup.get(spinner) as android.widget.ListPopupWindow

            // Set popupWindow height to 500px
            popupWindow.height = size
        } catch (e: NoClassDefFoundError) {
            // silently fail...
        } catch (e: ClassCastException) {
        } catch (e: NoSuchFieldException) {
        } catch (e: IllegalAccessException) {
        }

    }

    /**
     * image RealPath
     *
     * @param context
     * @param contentUri
     * @return
     */
    fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        var result: String? = null

        val cursor = context.contentResolver.query(contentUri, null, null, null, null)

        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentUri.path
        } else {
            if (cursor.moveToFirst()) {
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                result = cursor.getString(idx)
            }
            cursor.close()
        }
        return result

    }

    /**
     * 비트맵 파일 변환
     *
     * @param bitmap
     * @param strFilePath
     * @param filename
     */
    fun SaveBitmapToFileCache(
        bitmap: Bitmap, strFilePath: String,
        filename: String
    ): File {

        val file = File(strFilePath)

        // If no folders
        if (!file.exists()) {
            file.mkdirs()
            // Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }

        val fileCacheItem = File(strFilePath + filename)
        var out: OutputStream? = null

        try {
            fileCacheItem.createNewFile()
            out = FileOutputStream(fileCacheItem)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                out!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return file
    }

    /**
     * 휴대전화 이미지 가져오기
     *
     * @param context
     * @return
     */
    fun getImageFilePath(context: Context): BeanGalleryInfoList {


        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        val cursor = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            MediaStore.MediaColumns.DATE_ADDED + " desc"
        )
        val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        val columnDisplayname = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
        val columnBucketID = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
        val columnBucketName =
            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        val bucketNameList = ArrayList<String>()
        val bucketIdList = ArrayList<String>()
        val imageInfoList = ArrayList<BeanGalleryInfo>()

        // init 설정
        bucketNameList.add("All")
        bucketIdList.add("All")

        var lastIndex: Int
        while (cursor.moveToNext()) {
            val absolutePathOfImage = cursor.getString(columnIndex)
            val nameOfFile = cursor.getString(columnDisplayname)
            val bucketName = cursor.getString(columnBucketName)
            val bucketId = cursor.getString(columnBucketID)

            var equal = false

            for (i in bucketNameList.indices) {
                val name = bucketNameList.get(i)
                if (TextUtils.equals(name, bucketName)) {
                    equal = true
                }
            }

            if (!equal) {
                bucketNameList.add(bucketName)
                bucketIdList.add(bucketId)
            }

            lastIndex = absolutePathOfImage.lastIndexOf(nameOfFile)
            lastIndex = if (lastIndex >= 0) lastIndex else nameOfFile.length - 1

            if (!TextUtils.isEmpty(absolutePathOfImage)) {

                val info = BeanGalleryInfo(bucketName, bucketId, absolutePathOfImage)
                imageInfoList.add(info)
            }
        }

        return BeanGalleryInfoList(bucketNameList, bucketIdList, imageInfoList)
    }

    /**
     * 타이머 남은 시간 계산
     */
    fun getTimerTime(endDate: String): String {
        var strDate = ""
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val endDate = sdf.parse(endDate)
        val curDate = Date()

        var time: Long = endDate.time - curDate.time
        val day : Int = (time / (24 * 60 * 60 * 1000)).toInt()
        time -= day * (24 * 60 * 60 * 1000)
        val hour : Int = (time / (60 * 60 * 1000)).toInt()
        time -= hour * (60 * 60 * 1000)
        val min : Int = (time / (60 * 1000)).toInt()
        time -= min * (60 * 1000)
        val sec : Int = (time / 1000).toInt()

        strDate = if (day > 0) {
            String.format("%d일 %d시간 %d분", day, hour, min)
        } else {
            String.format("%d시간 %d분 %d초", hour, min, sec)
        }
        return strDate
    }


    fun convertFromDate(strPostDate: String): String {

        var strDate = ""
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sdf2 = SimpleDateFormat("yyyy. MM. dd")
        val cal = Calendar.getInstance()

        val nowHour = cal.get(Calendar.HOUR)
        val nowMinute = cal.get(Calendar.MINUTE)
        val nowSeconds = cal.get(Calendar.SECOND)
        try {
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)

            val nowDate = cal.time

            val parseDate = sdf.parse(strPostDate)
            cal.time = parseDate

            val postHour = cal.get(Calendar.HOUR_OF_DAY)
            val postMinute = cal.get(Calendar.MINUTE)
            val postSeconds = cal.get(Calendar.SECOND)

            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)

            val postDate = cal.time

            if (nowDate.compareTo(postDate) > 0) {
                val viewSdf = SimpleDateFormat("yy. MM. dd")
                strDate = viewSdf.format(postDate)
            } else {
                if (postHour < nowHour) {
                    strDate = String.format("%d시간전", nowHour - postHour)
                } else {
                    if (postMinute < nowMinute) {
                        strDate = String.format("%d분전", nowMinute - postMinute)
                    } else {
                        var second = nowSeconds - postSeconds
                        if (second < 0) second = 0
                        strDate = String.format("%d초전", second)
                    }
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return strDate
    }

    /**
     * 단일 이미지 업로드
     * AWS ImageUploader
     */
    fun uploadWithTransferUtility(context : Context, file: File,subBucket : String,listener : IOS3ImageUploaderListener) {
        val AWS_LOG = "AWS_LOG"
        val transferUtility = TransferUtility.builder()
            .context(context)
            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .s3Client(AmazonS3Client(AWSMobileClient.getInstance().credentialsProvider))
            .build()

        val other = if(subBucket.isNullOrEmpty()) "" else "$subBucket/"
        val KEY = "public/$other"

        val date = Date()
        val pos = file.name.lastIndexOf(".")
        val ext = file.name.substring(pos + 1)

        val fileName = "${date.time}.$ext"
        val uploadObserver = transferUtility.upload(KEY + fileName, file)
        Log.d(AWS_LOG, "UPLOAD - - It Is a Key: ${uploadObserver.key}")
        // Attach a listener to the observer
        uploadObserver.setTransferListener(object : TransferListener{
            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                val done = (((bytesCurrent.toDouble() / bytesTotal) * 100.0).toInt())
                Log.d("AWS_LOG", "UPLOAD - - ID: $id, percent done = $done")
            }

            override fun onStateChanged(id: Int, state: TransferState?) {
                if(state == TransferState.COMPLETED){
                    listener.onStateCompleted(id,state,uploadObserver.key)
                }
            }

            override fun onError(id: Int, ex: java.lang.Exception?) {
                Log.d("AWS_LOG", "UPLOAD ERROR - - ID: $id - - EX: ${ex!!.message.toString()}")
                listener.onError(id,ex)
            }
        })

        // If you prefer to long-poll for updates
        if (uploadObserver.state == TransferState.COMPLETED) {
            /* Handle completion */
        }

        val bytesTransferred = uploadObserver.bytesTransferred
    }

    /**
     * 다중 이미지 업로드
     */
    fun multiUploadWithTransferUtility(context : Context, file: ArrayList<File>,subBucket : String,listener : IOS3ImageUploaderListener) {
        val AWS_LOG = "AWS_LOG"
        val transferUtility = TransferUtility.builder()
            .context(context)
            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .s3Client(AmazonS3Client(AWSMobileClient.getInstance().credentialsProvider))
            .build()

        val other = if(subBucket.isNullOrEmpty()) "" else "$subBucket/"
        val KEY = "public/$other"
        var completeFile = 0
        var addressList = ArrayList<String>()
        for (i in 0 until file.size) {

            val date = Date()
            val pos = file[i].name.lastIndexOf(".")
            val ext = file[i].name.substring(pos + 1)

            val fileName = "${date.time}_$i.$ext"
            val uploadObserver = transferUtility.upload(KEY + fileName, file[i])
            Log.d(AWS_LOG, "UPLOAD - - It Is a Key: ${uploadObserver.key}")

            addressList.add(uploadObserver.key)

            // Attach a listener to the observer
            uploadObserver.setTransferListener(object : TransferListener {
                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                    val done = (((bytesCurrent.toDouble() / bytesTotal) * 100.0).toInt())
                    Log.d("AWS_LOG", "UPLOAD - - ID: $id, percent done = $done")
                }

                override fun onStateChanged(id: Int, state: TransferState?) {
                    if (state == TransferState.COMPLETED) {
                        listener.onStateCompleted(id, state, uploadObserver.key)
                        // todo : 모든 이미지가 성공했을 시 업로드
                        if(++completeFile >= file.size) listener.onMutiStateCompleted(addressList)
                    }
                }

                override fun onError(id: Int, ex: java.lang.Exception?) {
                    Log.d("AWS_LOG", "UPLOAD ERROR - - ID: $id - - EX: ${ex!!.message.toString()}")
                    listener.onError(id, ex)
                }
            })

            // If you prefer to long-poll for updates
            if (uploadObserver.state == TransferState.COMPLETED) {
                /* Handle completion */
            }

            val bytesTransferred = uploadObserver.bytesTransferred
        }
    }


}