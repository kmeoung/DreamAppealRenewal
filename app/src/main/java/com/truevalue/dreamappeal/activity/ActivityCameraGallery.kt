package com.truevalue.dreamappeal.activity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.AccessControlList
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.PutObjectRequest
import com.bumptech.glide.Glide
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.base.BaseImageUploader
import com.truevalue.dreamappeal.bean.BeanGalleryInfo
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_gallery.*
import kotlinx.android.synthetic.main.activity_camera_gallery.*
import java.io.File
import java.io.FileOutputStream
import java.util.*


class ActivityCameraGallery : BaseActivity() {

    private val AWS_LOG = "AWS_LOG"

    private var mOldPath: ArrayList<BeanGalleryInfo>? = null
    private var mItemPath: ArrayList<BeanGalleryInfo>? = null
    private var mBucked: ArrayList<BeanGalleryInfo>? = null
    private var mImageTest : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_gallery)

        // Init Aws
//        AWSMobileClient.getInstance().initialize(this) {
//            Log.d(AWS_LOG, "AWSMobileClient is initialized")
//        }.execute()
        // todo : TEST
        initAdapter()
    }

    /**
     * Upload
     */
    fun uploadTransferUtility(imagePath: String?) {
        val transferUtility = TransferUtility.builder()
            .context(applicationContext)
            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .s3Client(AmazonS3Client(AWSMobileClient.getInstance().credentialsProvider))
            .build()

        val extStorageDirectory = Environment.getExternalStorageDirectory().toString()
        val file = File(imagePath)

        val uploadObserver = transferUtility.upload(
            "test.png",
            file
        )

        uploadObserver.setTransferListener(object : TransferListener {
            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                val done = (((bytesCurrent.toDouble() / bytesTotal) * 100.0).toInt())
                Log.d(AWS_LOG, "UPLOAD - - ID: $id, percent done = $done")
            }

            override fun onStateChanged(id: Int, state: TransferState?) {
                if (state == TransferState.COMPLETED) {
                    // Handle a completed upload
                }
            }

            override fun onError(id: Int, ex: Exception?) {
                Log.d(AWS_LOG, "UPLOAD ERROR - - ID: $id - - EX: ${ex!!.message.toString()}")
            }


        })

        // If you prefer to long-poll for updates
        if (uploadObserver.state == TransferState.COMPLETED) {
            /* Handle completion */
        }

        val bytesTransferred = uploadObserver.bytesTransferred
    }

    private fun initAdapter() {
        mOldPath = ArrayList()
        mBucked = ArrayList()
        mItemPath = ArrayList()

        var firstImage = false
        applicationContext
        val (bucketNameList, bucketIdList, beanImageInfoList) = Utils.getImageFilePath(
            applicationContext!!
        )

        val strBucketNameList = ArrayList<String>()
        for (i in bucketNameList.indices) {
            val title = bucketNameList[i]
            val id = bucketIdList[i]
            mBucked!!.add(BeanGalleryInfo(title, id, null))
            strBucketNameList.add(title)
        }

        val titleSpinner = sp_title

        val arrayAdapter = ArrayAdapter(
            applicationContext,
            R.layout.support_simple_spinner_dropdown_item,
            strBucketNameList
        )
        titleSpinner.setAdapter(arrayAdapter)

        for (i in beanImageInfoList.indices) {
            val (bucketName, bucketId, imagePath) = beanImageInfoList[i]
            mOldPath!!.add(BeanGalleryInfo(bucketName, bucketId, imagePath))
            mItemPath!!.add(BeanGalleryInfo(bucketName, bucketId, imagePath))

            if (!firstImage) {
                Glide.with(applicationContext!!)
                    .load(mItemPath!!.get(0).imagePath)
                    .into(iv_select_image)

//                iv_select_image.setmImageFile(File(mItemPath!!.get(0).imagePath))
                firstImage = true
            }
        }
        val mGridAdapter = GridAdapter(applicationContext, mItemPath!!)
        gv_gallery.adapter = mGridAdapter

        mImageTest = mItemPath!![0].imagePath

        gv_gallery.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, i, l ->
            Glide.with(applicationContext!!)
                .load(mItemPath!!.get(i).imagePath)
                .into(iv_select_image)

            mImageTest = mItemPath!![i].imagePath
//            (getActivity() as ActivityGalleryCamera).setmImageFile(File(mItemPath!!.get(i).imagePath))
        })

        titleSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val bean = mBucked!!.get(position)
                mItemPath!!.clear()
                if (TextUtils.equals(bean.bucketId, "All")) {
                    mItemPath!!.addAll(mOldPath!!)
                } else {
                    for (i in mOldPath!!.indices) {
                        val oldBean = mOldPath!!.get(i)
                        if (TextUtils.equals(bean.bucketId, oldBean.bucketId)) {
                            mItemPath!!.add(oldBean)
                        }
                    }
                }
                // 이미지뷰 초기화
                if (mItemPath!!.size > 0) {
                    Glide.with(applicationContext!!)
                        .load(mItemPath!!.get(0).imagePath)
                        .into(iv_select_image)

//                    (getActivity() as ActivityGalleryCamera).setmImageFile(File(mItemPath!!.get(0).imagePath))
                }

                mGridAdapter.notifyDataSetChanged()

                tv_camera.setOnClickListener(View.OnClickListener {
//                                        uploadTransferUtility(mItemPath!!.get(0).imagePath)
                    val file = File(mImageTest!!)


                    val thread = object : Thread(){
                        override fun run() {
                            super.run()
                            val uploader = BaseImageUploader()
                            uploader.uploadFile(file)
                        }
                    }
                    thread.start()

                })
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        })
    }


    /**
     * GridAdapter
     */
    internal inner class GridAdapter(
        private val mContext: Context,
        private val pictureList: ArrayList<BeanGalleryInfo>
    ) :
        BaseAdapter() {
        private val inflater: LayoutInflater

        init {
            inflater =
                applicationContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        override fun getCount(): Int {
            return pictureList.size
        }

        override fun getItem(position: Int): Any? {
            return null
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.listitem_gallery, parent, false)
            }
            val imageView = convertView!!.findViewById<ImageView>(R.id.iv_image)

            //onCreate에서 정해준 크기로 이미지를 붙인다.
            Glide.with(mContext)
                .load(pictureList[position].imagePath)
                .placeholder(R.drawable.ic_image_black)
                .into(imageView)

            return convertView
        }
    }

}