package com.truevalue.dreamappeal.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.Uri
import android.provider.MediaStore
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.IOS3ImageUploaderListener
import com.truevalue.dreamappeal.bean.BeanGalleryInfo
import com.truevalue.dreamappeal.bean.BeanGalleryInfoList
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

        context?.let {
            val str = tv.text.toString()
            val first = str.indexOf(changeText)
            val last = str.lastIndexOf(changeText) + changeText.length
            val ssb = SpannableStringBuilder(str)
            ssb.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(it, R.color.main_blue)),
                first,
                last,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return ssb
        } ?: kotlin.run {
            return SpannableStringBuilder()
        }
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
        context?.let {
            val first = str.indexOf(changeText)
            val last = str.lastIndexOf(changeText) + changeText.length
            val ssb = SpannableStringBuilder(str)
            ssb.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(it, color)),
                first,
                last,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return ssb
        } ?: kotlin.run {
            return SpannableStringBuilder()
        }
    }

    /**
     * replace Text Type
     */
    fun replaceTextType(
        context: Context?,
        tv: TextView,
        changeText: String
    ): SpannableStringBuilder {
        context?.let {
            val str = tv.text.toString()
            val first = str.indexOf(changeText)
            val last = str.lastIndexOf(changeText) + changeText.length
            val ssb = SpannableStringBuilder(str)
            ssb.setSpan(
                StyleSpan(Typeface.BOLD),
                first,
                last,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return ssb
        } ?: kotlin.run {
            return SpannableStringBuilder()
        }
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

        cursor?.let {
            if (cursor.moveToFirst()) {
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                result = cursor.getString(idx)
            }
            cursor.close()
        } ?: kotlin.run {
            result = contentUri.path
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
    fun saveBitmapToFileCache(
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
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
//        val columnDisplayname = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
        val columnBucketID = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
        val columnBucketName =
            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        val bucketNameList = ArrayList<String>()
        val bucketIdList = ArrayList<String>()
        val imageInfoList = ArrayList<BeanGalleryInfo>()

        // init 설정
        bucketNameList.add("All")
        bucketIdList.add("All")

//        var lastIndex: Int
        while (cursor.moveToNext()) {
            val absolutePathOfImage: String? = cursor.getString(columnIndex)
//            val nameOfFile = cursor.getString(columnDisplayname)
            val bucketName: String? = cursor.getString(columnBucketName)
            val bucketId: String? = cursor.getString(columnBucketID)

            var equal = false

            for (i in 0 until bucketNameList.size) {
                val name = bucketNameList[i]
                if (name == bucketName) {
                    equal = true
                }
            }

            if (!bucketName.isNullOrEmpty() && !bucketId.isNullOrEmpty()) {

                if (!equal) {
                    bucketNameList.add(bucketName)
                    bucketIdList.add(bucketId)
                }

//            lastIndex = absolutePathOfImage.lastIndexOf(nameOfFile)
//            lastIndex = if (lastIndex >= 0) lastIndex else nameOfFile.length - 1
                if (!absolutePathOfImage.isNullOrEmpty()) {
                    val info = BeanGalleryInfo(bucketName, bucketId, absolutePathOfImage, false, -1)
                    imageInfoList.add(info)
                }
            }
        }

        return BeanGalleryInfoList(bucketNameList, bucketIdList, imageInfoList)
    }

    /**
     * 타이머 남은 시간 계산
     */
    fun getTimerTime(endDate: String?): String {

        if (endDate.isNullOrEmpty()) return ""

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val endDate = sdf.parse(endDate)
        val curDate = Date()

        var time: Long = endDate.time - curDate.time
        val day: Long = (time / (24 * 60 * 60 * 1000))
        time -= day * (24 * 60 * 60 * 1000)
        val hour: Long = (time / (60 * 60 * 1000))
        time -= hour * (60 * 60 * 1000)
        val min: Long = (time / (60 * 1000))
        time -= min * (60 * 1000)
        val sec: Long = (time / 1000)

        return if (day > 0) {
            "${day}일 ${hour}시간 ${min}분"
        } else {
            "${hour}시간 ${min}분 ${sec}초"
        }
    }


    /**
     * 시간 변경
     */
    fun convertFromDate(strPostDate: String?): String {

        var strDate = ""
        strPostDate?.let { strPostDate ->
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        val sdf2 = SimpleDateFormat("yyyy. MM. dd")
            val cal = Calendar.getInstance()

            val nowHour = cal.get(Calendar.HOUR_OF_DAY)
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

                if (nowDate > postDate) {
                    val viewSdf = SimpleDateFormat("yy. MM. dd")
                    strDate = viewSdf.format(postDate)
                } else {
                    if (postHour < nowHour) {
                        strDate = "${nowHour - postHour}시간전"
                    } else {
                        if (postMinute < nowMinute) {
                            strDate = "${nowMinute - postMinute}분전"
                        } else {
                            var second = nowSeconds - postSeconds
                            if (second < 0) second = 0
                            strDate = "${second}초전"
                        }
                    }
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }

        return strDate
    }

    /**
     * 단일 이미지 업로드
     * AWS ImageUploader
     */
    fun uploadWithTransferUtility(
        context: Context,
        file: File,
        subBucket: String,
        listener: IOS3ImageUploaderListener
    ) {
        val transferUtility = TransferUtility.builder()
            .context(context)
            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .s3Client(AmazonS3Client(AWSMobileClient.getInstance().credentialsProvider))
            .build()

        val other = if (subBucket.isNullOrEmpty()) "" else "$subBucket/"
        val KEY = if (Comm_Param.REAL) "public/images/$other" else "public/devImages/$other"

        val date = Date()
        val pos = file.name.lastIndexOf(".")
        val ext = file.name.substring(pos + 1)

        val fileName = "${date.time}.$ext"
        val uploadObserver = transferUtility.upload(KEY + fileName, file)
        // Attach a listener to the observer
        uploadObserver.setTransferListener(object : TransferListener {
            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
//                val done = (((bytesCurrent.toDouble() / bytesTotal) * 100.0).toInt())
            }

            override fun onStateChanged(id: Int, state: TransferState?) {
                if (state == TransferState.COMPLETED) {
                    listener.onStateCompleted(id, state, uploadObserver.key)
                }
            }

            override fun onError(id: Int, ex: java.lang.Exception?) {
                listener.onError(id, ex)
            }
        })

        // If you prefer to long-poll for updates
        if (uploadObserver.state == TransferState.COMPLETED) {
            /* Handle completion */
        }

//        val bytesTransferred = uploadObserver.bytesTransferred
    }

    /**
     * 다중 이미지 업로드
     */
    fun multiUploadWithTransferUtility(
        context: Context,
        file: ArrayList<File>,
        subBucket: String,
        listener: IOS3ImageUploaderListener
    ) {
        val transferUtility = TransferUtility.builder()
            .context(context)
            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .s3Client(AmazonS3Client(AWSMobileClient.getInstance().credentialsProvider))
            .build()

        val other = if (subBucket.isNullOrEmpty()) "" else "$subBucket/"
        val KEY = if (Comm_Param.REAL) "public/images/$other" else "public/devImages/$other"
        var completeFile = 1
        var addressList = ArrayList<String>()

        for (i in 0 until file.size) {

            val date = Date()
            val pos = file[i].name.lastIndexOf(".")
            val ext = file[i].name.substring(pos + 1)

            val fileName = "${date.time}_$i.$ext"
            val uploadObserver = transferUtility.upload(KEY + fileName, file[i])

            addressList.add(uploadObserver.key)

            // Attach a listener to the observer
            uploadObserver.setTransferListener(object : TransferListener {
                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
//                    val done = (((bytesCurrent.toDouble() / bytesTotal) * 100.0).toInt())
                }

                override fun onStateChanged(id: Int, state: TransferState?) {
                    if (state == TransferState.COMPLETED) {
                        listener.onStateCompleted(id, state, uploadObserver.key)
                        // todo : 모든 이미지가 성공했을 시 업로드
                        if (++completeFile >= file.size) listener.onMutiStateCompleted(addressList)
                    }
                }

                override fun onError(id: Int, ex: java.lang.Exception?) {
                    listener.onError(id, ex)
                }
            })

            // If you prefer to long-poll for updates
            if (uploadObserver.state == TransferState.COMPLETED) {
                /* Handle completion */
            }

//            val bytesTransferred = uploadObserver.bytesTransferred
        }
    }

    /**
     * 이미지 뷰 정사각형 처리
     */
    fun setImageViewSquare(context: Context?, view: View) {
        context?.let {
            val params = view.layoutParams
            val display = it.resources.displayMetrics
            val swidth = display.widthPixels
            params.width = swidth
            params.height = swidth
            view.layoutParams = params
        }
    }

    /**
     * 이미지 뷰 크기 처리
     */
    fun setImageViewSquare(context: Context?, view: View, column: Int, row: Int) {
        context?.let {
            val params = view.layoutParams
            val display = it.resources.displayMetrics
            val swidth = display.widthPixels
            params.width = swidth
            val standard = (swidth / column) * row
            params.height = standard
            view.layoutParams = params
        }
    }

    /**
     * 이미지 아이템 뷰 정사각형 처리
     */
    fun setImageItemViewSquare(context: Context?, view: View) {
        context?.let {
            val params = view.layoutParams
            val display = it.resources.displayMetrics
            val swidth = display.widthPixels
            params.width = swidth / 3 + swidth % 3
            params.height = swidth / 3 + swidth % 3
            view.layoutParams = params
        }
    }

    /**
     * replace Comment Count text
     */
    fun getCommentView(count: Int): String {
        var strCommentCount: String
        strCommentCount = if (count < 1000) {
            count.toString()
        } else {
            val k = count / 1000
            if (k < 1000) {
                "${k}K"
            } else {
                val m = k / 1000
                "${m}M"
            }
        }
        return strCommentCount
    }

    /**
     * SNS 더보기 설정
     *
     * @param view
     * @param text
     * @param maxLine
     */
    fun setReadMore(view: TextView, text: String, maxLine: Int) {
        val context = view.context
        val expanedText = " ... 더보기"
        if (view.tag != null && view.tag == text) { //Tag로 전값 의 text를 비교하여똑같으면 실행하지 않음.
            return
        }
        view.tag = text //Tag에 text 저장
        view.text = text // setText를 미리 하셔야  getLineCount()를 호출가능
        view.post {
            if (view.lineCount >= maxLine) { //Line Count가 설정한 MaxLine의 값보다 크다면 처리시작
                val lineEndIndex =
                    view.layout.getLineVisibleEnd(maxLine - 1) //Max Line 까지의 text length
                val split =
                    text.split("\n").toTypedArray() //text를 자름
                var splitLength = 0
                var lessText = ""
                for (item in split) {
                    splitLength += item.length + 1
                    if (splitLength >= lineEndIndex) { //마지막 줄일때!
                        lessText += if (item.length >= expanedText.length) {
                            item.substring(
                                0,
                                item.length - expanedText.length
                            ) + expanedText
                        } else {
                            item + expanedText
                        }
                        break //종료
                    }
                    lessText += item + "\n"
                }
                val spannableString = SpannableString(lessText)
                spannableString.setSpan(
                    object : ClickableSpan() {
                        //클릭이벤트
                        override fun onClick(v: View) {
                            view.text = text
                        }

                        override fun updateDrawState(ds: TextPaint) { //컬러 처리
                            ds.color = ContextCompat.getColor(
                                context,
                                R.color.tab_none_select_gray
                            )
                        }
                    },
                    spannableString.length - expanedText.length,
                    spannableString.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                view.text = spannableString
                view.movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    /**
     * dpToPixel 코드
     *
     * @param context
     * @param DP
     * @return
     */
    fun dpToPixel(context: Context, DP: Float): Int {
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, DP, context.resources
                .displayMetrics
        )
        return px.toInt()
    }

    /**
     * Keyboard 내리기
     * Activity 에서 키보드 내리기
     */
    fun downKeyBoard(activity: Activity) {
//        val inputMethodManager =
//            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)

        val imm =
            activity.applicationContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
    }

}