package com.truevalue.dreamappeal.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
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
    private val mMultiImage: ArrayList<BeanGalleryInfo>?
    private var mCurrentViewImage: File? = null
    private var mGridAdapter: GridAdapter? = null

    private var mSelectType: String?
    private var mViewType: String?

    val REQUEST_IMAGE_CAPTURE = 1004

    val REQUEST_ADD_ACTION_POST = 1005

    init {
        mMultiImage = ArrayList()
        // 기본 싱글
        mSelectType = EXTRA_IMAGE_SINGLE_SELECT
        // 기본 Achivement Post
        mViewType = EXTRA_ACHIVEMENT_POST
    }

    companion object {
        val EXTRA_IMAGE_SINGLE_SELECT = "EXTRA_IMAGE_SINGLE_SELECT"
        val EXTRA_IMAGE_MULTI_SELECT = "EXTRA_IMAGE_MULTI_SELECT"
        val SELECT_TYPE = "SELECT_TYPE"
        val VIEW_TYPE = "VIEW_TYPE"
        val REQUEST_IMAGE_FILES = "REQUEST_IMAGE_FILES"
        val EXTRA_ACTION_POST = "EXTRA_ACTION_POST"
        val EXTRA_ACHIVEMENT_POST = "EXTRA_ACHIVEMENT_POST"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_gallery)

        // View 초기화
        initView()
        // Adapter 초기화
        initAdapter()
        // View OnClick Listener
        onClickView()
    }

    /**
     * View Init
     */
    private fun initView() {
        iv_check.isSelected = true

        if (!intent.getStringExtra(SELECT_TYPE).isNullOrEmpty()) {
            mSelectType = intent.getStringExtra(SELECT_TYPE)
        }

        if (!intent.getStringExtra(VIEW_TYPE).isNullOrEmpty()) {
            mViewType = intent.getStringExtra(VIEW_TYPE)
        }

        when (mSelectType) {
            EXTRA_IMAGE_SINGLE_SELECT -> {
                btn_multi_select.visibility = GONE
                isMultiMode = false
            }
            EXTRA_IMAGE_MULTI_SELECT -> {
                btn_multi_select.visibility = VISIBLE
            }
        }
        iv_close.visibility = VISIBLE
        iv_back_black.visibility = GONE
    }

    /**
     * View OnClick Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_check -> {
                    val array = ArrayList<File>()
                    if (mViewType.equals(EXTRA_ACHIVEMENT_POST)) {
                        val intent = Intent()
                        if (isMultiMode) {
                            for (i in 0 until mMultiImage!!.size) {
                                array.add(File(mMultiImage[i].imagePath))
                            }
                        } else {
                            if (mCurrentViewImage != null) {
                                val intent = Intent()
                                array.add(mCurrentViewImage!!)
                            }
                        }
                        intent.putExtra(REQUEST_IMAGE_FILES, array)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else if (mViewType.equals(EXTRA_ACTION_POST)) {
                        if (isMultiMode) {
                            // Action Post 이미지 추가로 이동
                            for (i in 0 until mMultiImage!!.size) {
                                array.add(File(mMultiImage[i].imagePath))
                            }
                        }else{
                            if(mCurrentViewImage != null) array.add(mCurrentViewImage!!)
                        }
                        val intent =
                            Intent(this@ActivityCameraGallery, ActivityAddActionPost::class.java)
                        intent.putExtra(REQUEST_IMAGE_FILES, array)
                        startActivityForResult(intent, REQUEST_ADD_ACTION_POST)
                    }
                }
                btn_multi_select -> {
                    isMultiMode = when (isMultiMode) {
                        true -> {
                            mMultiImage!!.clear()
                            false
                        }
                        else -> true
                    }
                    mGridAdapter!!.notifyDataSetChanged()

                }
                iv_close -> {
                    finish()
                }
            }
        }
        iv_close.setOnClickListener(listener)
        iv_check.setOnClickListener(listener)
        btn_multi_select.setOnClickListener(listener)
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
            mBucked!!.add(BeanGalleryInfo(title, id, null, false, -1))
            strBucketNameList.add(title)
        }

        val titleSpinner = sp_title

        val arrayAdapter = ArrayAdapter(
            applicationContext,
            R.layout.spinner_text,
            strBucketNameList
        )

        titleSpinner.adapter = arrayAdapter
        Utils.setDropDownHeight(sp_title, 500)

        for (i in beanImageInfoList.indices) {
            val (bucketName, bucketId, imagePath) = beanImageInfoList[i]
            mOldPath!!.add(BeanGalleryInfo(bucketName, bucketId, imagePath, false, -1))
            mItemPath!!.add(BeanGalleryInfo(bucketName, bucketId, imagePath, false, -1))

            if (!firstImage) {
                Glide.with(applicationContext!!)
                    .load(mItemPath!![0].imagePath)
                    .into(iv_select_image)

//                iv_select_image.setmImageFile(File(mItemPath!!.get(0).imagePath))
                firstImage = true
            }
        }
        mGridAdapter = GridAdapter(applicationContext, mItemPath!!)
        gv_gallery.adapter = mGridAdapter

        if (mItemPath!!.size < 1) return

        mCurrentViewImage = File(mItemPath!![0].imagePath)

        gv_gallery.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            Glide.with(applicationContext!!)
                .load(mItemPath!![i].imagePath)
                .into(iv_select_image)

            mCurrentViewImage = File(mItemPath!![i].imagePath)
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
                }

                mGridAdapter!!.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        sp_title.onItemSelectedListener = spinnerListener

        tv_camera.setOnClickListener(View.OnClickListener {
            // 카메라 처리
            onClickedCamera()
        })
    }

    /**
     * 사진찍어서 가져오기
     */
    fun onClickedCamera() {
        val intent = Intent()
        intent.action = MediaStore.ACTION_IMAGE_CAPTURE
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                REQUEST_IMAGE_CAPTURE-> {
                    var extras: Bundle? = null
                    try {
                        extras = data!!.extras
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    var uri: Uri? = null
                    try {
                        uri = data!!.data
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    var file: File? = null
                    if (extras != null) {
                        val photo = extras.getParcelable<Bitmap>("data")
                        val filePath = Environment.getExternalStorageDirectory().path + "/"
                        val fileName: String
                        fileName = Date().time.toString() + ".jpeg"
                        Utils.SaveBitmapToFileCache(photo!!, filePath, fileName)
                        file = File(filePath + fileName)
                    }
                    if (uri != null) {
                        uri = data!!.data
                        file = File(Utils.getRealPathFromURI(this@ActivityCameraGallery, uri!!))
                    }

                    val array = ArrayList<File>()
                    array.add(file!!)
                    // todo : 이미지
                    val intent = Intent()
                    intent.putExtra(REQUEST_IMAGE_FILES, array)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                REQUEST_ADD_ACTION_POST-> {
                    // todo : 여기에는 확인이 필요합니다.
                    finish()
                }

            }

        }
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

            val bean = pictureList[position]

            val imageView = convertView!!.findViewById<ImageView>(R.id.iv_image)
            val multiCheck = convertView!!.findViewById<ImageView>(R.id.iv_multi)
            val tvMulti = convertView!!.findViewById<TextView>(R.id.tv_multi)
            val rlMulti = convertView!!.findViewById<RelativeLayout>(R.id.rl_multi)

            if (isMultiMode) {
                rlMulti.visibility = VISIBLE
                multiCheck.isSelected = bean.imageCheck
                if (bean.imageCheck) {
                    tvMulti.text = (mMultiImage!!.indexOf(bean) + 1).toString()
                } else {
                    tvMulti.text = ""
                }
            } else {
                rlMulti.visibility = GONE
                pictureList[position].imageCheck = false
            }

            rlMulti.setOnClickListener(View.OnClickListener {
                // todo : 멀티 버튼 테스트
                if (bean.imageCheck) {
                    pictureList[position].imageCheck = false
                    mMultiImage!!.remove(bean)
//                    pictureList[position].imageSelectedIdx = -1
                } else {
                    if (mMultiImage!!.size < 10) {
                        pictureList[position].imageCheck = true
                        mMultiImage!!.add(bean)
//                        pictureList[position].imageSelectedIdx = mMultiImage!!.indexOf(bean) + 1
                    }
                }
                mGridAdapter!!.notifyDataSetChanged()
            })

            //onCreate에서 정해준 크기로 이미지를 붙인다.
            Glide.with(mContext)
                .load(pictureList[position].imagePath)
                .placeholder(R.drawable.ic_image_black)
                .into(imageView)

            return convertView
        }
    }

}