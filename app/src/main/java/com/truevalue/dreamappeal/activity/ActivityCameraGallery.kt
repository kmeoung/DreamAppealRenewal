package com.truevalue.dreamappeal.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.bumptech.glide.Glide
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.bean.BeanGalleryInfo
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_gallery.*
import kotlinx.android.synthetic.main.activity_camera_gallery.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class ActivityCameraGallery : BaseActivity() {



    private var mOldPath: ArrayList<BeanGalleryInfo>? = null
    private var mItemPath: ArrayList<BeanGalleryInfo>? = null
    private var mBucked: ArrayList<BeanGalleryInfo>? = null
    private var isMultiMode = false
    private val mArrayImage : ArrayList<File>?
    private var mCurrentViewImage : File? = null

    init {
        mArrayImage = ArrayList()
    }

    companion object{
        val REQUEST_IMAGE_FILES = "REQUEST_IMAGE_FILES"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_gallery)

        initAdapter()

        // View OnClick Listener
        onClickView()
    }

    /**
     * View OnClick Listener
     */
    private fun onClickView(){
        val listener = View.OnClickListener{
            when(it){
                iv_check->{
                    if(mArrayImage != null && mArrayImage.size > 0) {
                        val intent = Intent()
                        intent.putExtra(REQUEST_IMAGE_FILES, mArrayImage)
                        setResult(Activity.RESULT_OK, intent)
                    }
                    finish()
                }
            }
        }
        iv_check.setOnClickListener(listener)
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

        titleSpinner.adapter = arrayAdapter
        Utils.setDropDownHeight(sp_title,500)

        for (i in beanImageInfoList.indices) {
            val (bucketName, bucketId, imagePath) = beanImageInfoList[i]
            mOldPath!!.add(BeanGalleryInfo(bucketName, bucketId, imagePath))
            mItemPath!!.add(BeanGalleryInfo(bucketName, bucketId, imagePath))

            if (!firstImage) {
                Glide.with(applicationContext!!)
                    .load(mItemPath!![0].imagePath)
                    .into(iv_select_image)

//                iv_select_image.setmImageFile(File(mItemPath!!.get(0).imagePath))
                firstImage = true
            }
        }
        val mGridAdapter = GridAdapter(applicationContext, mItemPath!!)
        gv_gallery.adapter = mGridAdapter

        if(mItemPath!!.size < 1) return

        mCurrentViewImage = File(mItemPath!![0].imagePath)

        if(mArrayImage!!.size < 1 || isMultiMode){
            mArrayImage!!.add(mCurrentViewImage!!)
        }else{
            mArrayImage!![0] = mCurrentViewImage!!
        }

        gv_gallery.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            Glide.with(applicationContext!!)
                .load(mItemPath!![i].imagePath)
                .into(iv_select_image)

            mCurrentViewImage = File(mItemPath!![i].imagePath)

            if(mArrayImage!!.size < 1){
                mArrayImage!!.add(mCurrentViewImage!!)
            }else{
                mArrayImage!![0] = mCurrentViewImage!!
            }
        }

        // todo : 여기에 멀티 셀렉트 모드 추가 Listener 만들어야 함

        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val bean = mBucked!![position]
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

                    mCurrentViewImage = File(mItemPath!![0].imagePath)

                    Glide.with(applicationContext!!)
                        .load(mItemPath!![0].imagePath)
                        .into(iv_select_image)
//                    (getActivity() as ActivityGalleryCamera).setmImageFile(File(mItemPath!!.get(0).imagePath))
                }

                mGridAdapter.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        sp_title.onItemSelectedListener = spinnerListener

        tv_camera.setOnClickListener(View.OnClickListener {
            //                                        uploadTransferUtility(mItemPath!!.get(0).imagePath)

            // todo : 여기서 카메라 처리
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