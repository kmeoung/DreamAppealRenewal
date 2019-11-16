package com.truevalue.dreamappeal.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.bean.BeanGalleryInfo
import com.truevalue.dreamappeal.bean.BeanGalleryInfoList
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import java.util.regex.Pattern


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
    fun dateToAge(date : Date) : Int{
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

                val info = BeanGalleryInfo(bucketName,bucketId,absolutePathOfImage)
                imageInfoList.add(info)
            }
        }

        return BeanGalleryInfoList(bucketNameList,bucketIdList,imageInfoList)
    }
}