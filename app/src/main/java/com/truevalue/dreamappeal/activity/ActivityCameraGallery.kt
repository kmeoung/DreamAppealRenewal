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


    private var mOldPath: ArrayList<BeanGalleryInfo>?
    private var mItemPath: ArrayList<BeanGalleryInfo>?
    private var mBucked: ArrayList<BeanGalleryInfo>?
    private var isMultiMode: Boolean
    private val mMultiImage: ArrayList<BeanGalleryInfo>?
    private var mCurrentViewImage: File?
    private var mGridAdapter: GridAdapter?

    private var mSelectType: String?
    private var mViewType: String?
    private var mBestIdx: Int
    private var mPopupMenu : PopupMenu?

    init {
        mOldPath = null
        mItemPath = null
        mBucked = null
        isMultiMode = false
        mCurrentViewImage = null
        mGridAdapter = null
        mPopupMenu = null
        mMultiImage = ArrayList()
        // 기본 싱글
        mSelectType = EXTRA_IMAGE_SINGLE_SELECT
        // 기본 Achivement Post
        mViewType = EXTRA_ACHIVEMENT_POST

        mBestIdx = -1
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1004

        const val REQUEST_ADD_ACTION_POST = 1005
        const val REQUEST_ADD_ACHIEVEMENT_POST = 1006

        const val SELECT_TYPE = "SELECT_TYPE"
        const val VIEW_TYPE = "VIEW_TYPE"
        const val ACHIEVEMENT_POST_BEST_IDX = "ACHIEVEMENT_POST_BEST_IDX"

        const val EXTRA_IMAGE_SINGLE_SELECT = "EXTRA_IMAGE_SINGLE_SELECT"
        const val EXTRA_IMAGE_MULTI_SELECT = "EXTRA_IMAGE_MULTI_SELECT"
        const val EXTRA_ACTION_POST = "EXTRA_ACTION_POST"
        const val EXTRA_ACHIVEMENT_POST = "EXTRA_ACHIVEMENT_POST"
        const val EXTRA_BOARD = "EXTRA_BOARD"
        const val EXTRA_DREAM_PROFILE = "EXTRA_DREAM_PROFILE"
        const val REQUEST_IMAGE_FILES = "REQUEST_IMAGE_FILES"
        const val REQUEST_BEST_IDX = "REQUEST_BEST_IDX"
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

        if (mViewType == EXTRA_ACHIVEMENT_POST) {
            mBestIdx = intent.getIntExtra(ACHIEVEMENT_POST_BEST_IDX, -1)
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
    }

    /**
     * View OnClick Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_check -> {
                    val array = ArrayList<File>()
                    when (mViewType) {
                        EXTRA_ACHIVEMENT_POST -> {
                            if (isMultiMode) {
                                // Achievement Post 이미지 추가로 이동
                                for (i in 0 until mMultiImage!!.size) {
                                    array.add(File(mMultiImage[i].imagePath))
                                }
                            } else {
                                if (mCurrentViewImage != null) array.add(mCurrentViewImage!!)
                            }
                            val intent =
                                Intent(this@ActivityCameraGallery, ActivityAddPost::class.java)
                            intent.putExtra(REQUEST_IMAGE_FILES, array)
                            intent.putExtra(REQUEST_BEST_IDX, mBestIdx)
                            intent.putExtra(VIEW_TYPE, EXTRA_ACHIVEMENT_POST)
                            startActivityForResult(intent, REQUEST_ADD_ACHIEVEMENT_POST)
                        }
                        EXTRA_ACTION_POST -> {
                            if (isMultiMode) {
                                // Action Post 이미지 추가로 이동
                                for (i in 0 until mMultiImage!!.size) {
                                    array.add(File(mMultiImage[i].imagePath))
                                }
                            } else {
                                if (mCurrentViewImage != null) array.add(mCurrentViewImage!!)
                            }
                            val intent =
                                Intent(this@ActivityCameraGallery, ActivityAddPost::class.java)
                            intent.putExtra(VIEW_TYPE, EXTRA_ACTION_POST)
                            intent.putExtra(REQUEST_IMAGE_FILES, array)
                            startActivityForResult(intent, REQUEST_ADD_ACTION_POST)
                        }
                        EXTRA_DREAM_PROFILE -> {
                            if (isMultiMode) {
                                // Action Post 이미지 추가로 이동
                                for (i in 0 until mMultiImage!!.size) {
                                    array.add(File(mMultiImage[i].imagePath))
                                }
                            } else {
                                if (mCurrentViewImage != null) array.add(mCurrentViewImage!!)
                            }

                            val intent = Intent()
                            intent.putExtra(REQUEST_IMAGE_FILES, array)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                        EXTRA_BOARD -> {
                            if (isMultiMode) {
                                // Action Post 이미지 추가로 이동
                                for (i in 0 until mMultiImage!!.size) {
                                    array.add(File(mMultiImage[i].imagePath))
                                }
                            } else {
                                if (mCurrentViewImage != null) array.add(mCurrentViewImage!!)
                            }

                            val intent = Intent()
                            intent.putExtra(REQUEST_IMAGE_FILES, array)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
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
                    btn_multi_select.isSelected = isMultiMode
                    mGridAdapter?.let { adapter -> adapter.notifyDataSetChanged() }
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

    /**
     * 어뎁터 초기화
     */
    private fun initAdapter() {
        mOldPath = ArrayList()
        mBucked = ArrayList()
        mItemPath = ArrayList()

        var firstImage = false
        applicationContext
        val (bucketNameList, bucketIdList, beanImageInfoList) = Utils.getImageFilePath(
            applicationContext!!
        )

        mPopupMenu = PopupMenu(applicationContext, ll_title)
        val strBucketNameList = ArrayList<String>()
        for (i in bucketNameList.indices) {
            val title = bucketNameList[i]
            mPopupMenu!!.menu.add(title)
            val id = bucketIdList[i]
            mBucked!!.add(BeanGalleryInfo(title, id, null, false, -1))
            strBucketNameList.add(title)
        }
        tv_title.text = strBucketNameList[0]

        for (i in beanImageInfoList.indices) {
            val (bucketName, bucketId, imagePath) = beanImageInfoList[i]
            mOldPath!!.add(BeanGalleryInfo(bucketName, bucketId, imagePath, false, -1))
            mItemPath!!.add(BeanGalleryInfo(bucketName, bucketId, imagePath, false, -1))

            if (!firstImage) {
                Glide.with(applicationContext!!)
                    .load(mItemPath!![0].imagePath)
                    .into(iv_select_image)
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

        mPopupMenu!!.setOnMenuItemClickListener {
            mItemPath!!.clear()
            if (TextUtils.equals(it.title, "All")) {
                mItemPath!!.addAll(mOldPath!!)
            } else {
                for (i in mOldPath!!.indices) {
                    val oldBean = mOldPath!![i]
                    if (TextUtils.equals(it.title, oldBean.bucketName)) {
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
            tv_title.text = it.title
            false
        }

        tv_camera.setOnClickListener{
            // 카메라 처리
            onClickedCamera()
        }

        ll_title.setOnClickListener {
            mPopupMenu!!.show()
        }
    }

    /**
     * 사진찍어서 가져오기
     */
    private fun onClickedCamera() {
        val intent = Intent()
        intent.action = MediaStore.ACTION_IMAGE_CAPTURE
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
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
                        val fileName = Date().time.toString() + ".jpeg"
                        Utils.saveBitmapToFileCache(photo!!, filePath, fileName)
                        file = File(filePath + fileName)
                    }
                    if (uri != null) {
                        uri = data!!.data
                        file = File(Utils.getRealPathFromURI(this@ActivityCameraGallery, uri!!))
                    }

                    val array = ArrayList<File>()
                    array.add(file!!)

                    when(mViewType){
                        EXTRA_ACHIVEMENT_POST -> {
                            val intent =
                                Intent(this@ActivityCameraGallery, ActivityAddPost::class.java)
                            intent.putExtra(REQUEST_IMAGE_FILES, array)
                            intent.putExtra(REQUEST_BEST_IDX, mBestIdx)
                            intent.putExtra(VIEW_TYPE, EXTRA_ACHIVEMENT_POST)
                            startActivityForResult(intent, REQUEST_ADD_ACHIEVEMENT_POST)
                        }
                        EXTRA_ACTION_POST -> {
                            val intent =
                                Intent(this@ActivityCameraGallery, ActivityAddPost::class.java)
                            intent.putExtra(VIEW_TYPE, EXTRA_ACTION_POST)
                            intent.putExtra(REQUEST_IMAGE_FILES, array)
                            startActivityForResult(intent, REQUEST_ADD_ACTION_POST)
                        }
                        else->{
                            // : 이미지
                            val intent = Intent()
                            intent.putExtra(REQUEST_IMAGE_FILES, array)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    }
                }
                REQUEST_ADD_ACTION_POST, REQUEST_ADD_ACHIEVEMENT_POST -> {
                    setResult(RESULT_OK)
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
            val convertView =
                convertView ?: inflater.inflate(R.layout.listitem_gallery, parent, false)

            val bean = pictureList[position]

            val imageView = convertView.findViewById<ImageView>(R.id.iv_image)
            val multiCheck = convertView.findViewById<ImageView>(R.id.iv_multi)
            val tvMulti = convertView.findViewById<TextView>(R.id.tv_multi)
            val rlMulti = convertView.findViewById<RelativeLayout>(R.id.rl_multi)

            val mMultiImage = mMultiImage!!

            if (isMultiMode) {
                rlMulti.visibility = VISIBLE
                multiCheck.isSelected = bean.imageCheck
                tvMulti.text =
                    if (bean.imageCheck) (mMultiImage.indexOf(bean) + 1).toString() else ""
            } else {
                rlMulti.visibility = GONE
                pictureList[position].imageCheck = false
            }

            rlMulti.setOnClickListener {
                if (bean.imageCheck) {
                    pictureList[position].imageCheck = false
                    mMultiImage.remove(bean)
                } else {
                    if (mMultiImage.size < 10) {
                        pictureList[position].imageCheck = true
                        mMultiImage.add(bean)
                    }
                }
                notifyDataSetChanged()
            }

            //onCreate에서 정해준 크기로 이미지를 붙인다.
            Glide.with(mContext)
                .load(pictureList[position].imagePath)
                .placeholder(R.drawable.ic_image_black)
                .into(imageView)

            return convertView
        }
    }

}