package com.truevalue.dreamappeal.fragment.dream_board

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.bumptech.glide.Glide
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityCameraGallery
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.bean.BeanConcernDetail
import com.truevalue.dreamappeal.bean.BeanWish
import com.truevalue.dreamappeal.bean.BeanWishPost
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_board.*
import okhttp3.Call
import org.json.JSONObject
import java.io.File
import java.io.IOException

class FragmentAddBoard : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter? = null
    private lateinit var mDialog: ProgressDialog

    private var mViewType: String?
    private var mBean: Any?

    init {
        mViewType = TYPE_ADD_WISH
        mBean = null
    }

    companion object {
        private const val REQUEST_ADD_IMAGES = 1003
        const val TYPE_ADD_WISH = "TYPE_ADD_WISH"
        const val TYPE_ADD_CONCERN = "TYPE_ADD_CONCERN"

        const val TYPE_EDIT_WISH = "TYPE_EDIT_WISH"
        const val TYPE_EDIT_CONCERN = "TYPE_EDIT_CONCERN"

        fun newInstance(view_type: String, bean: Any? = null): FragmentAddBoard {
            val fragment = FragmentAddBoard()
            fragment.mViewType = view_type
            fragment.mBean = bean
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_board, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // View 초기화
        initView()
        // bind init Data
        bindInitData()
        // Adapter 초기화
        initAdapter()
        // View Click 초기화
        onClickView()
    }

    override fun onResume() {
        super.onResume()
        (activity as ActivityMain).bottom_view.visibility = GONE
    }

    override fun onPause() {
        super.onPause()
        (activity as ActivityMain).bottom_view.visibility = VISIBLE
    }

    /**
     * View 초기화
     */
    private fun initView() {
        mDialog = ProgressDialog(context!!)
        mDialog.setCancelable(false)

        when (mViewType) {
            TYPE_ADD_WISH, TYPE_EDIT_WISH -> {
                tv_title.text = getString(R.string.str_wish_add_title)
            }
            TYPE_ADD_CONCERN, TYPE_EDIT_CONCERN -> {
                tv_title.text = getString(R.string.str_concern_add_title)
            }
        }
        iv_check.visibility = VISIBLE

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                iv_check.isSelected = check()
            }
        }

        et_title.addTextChangedListener(watcher)
        et_contents.addTextChangedListener(watcher)
    }

    /**
     * Check 버튼 설정
     */
    private fun check(): Boolean {
        return (!et_title.text.toString().isNullOrEmpty()) &&
                (!et_contents.text.toString().isNullOrEmpty())
    }

    /**
     * 초기 데이터 셋팅
     */
    private fun bindInitData() {
        mBean?.let {
            when (mViewType) {
                TYPE_EDIT_CONCERN -> {
                    val bean = mBean as BeanConcernDetail
                    et_title.setText(bean.post.title)
                    et_contents.setText(bean.post.content)
                }
                TYPE_EDIT_WISH -> {
                    val bean = mBean as BeanWishPost
                    et_title.setText(bean.title)
                    et_contents.setText(bean.content)
                }
            }
        }
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvImageListener)
        rv_board_img.adapter = mAdapter
        rv_board_img.layoutManager =
            LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
    }

    /**
     * View Click 초기화
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_add_photo -> {
                    val intent = Intent(context!!, ActivityCameraGallery::class.java)
                    intent.putExtra(
                        ActivityCameraGallery.SELECT_TYPE,
                        ActivityCameraGallery.EXTRA_IMAGE_MULTI_SELECT
                    )
                    intent.putExtra(
                        ActivityCameraGallery.VIEW_TYPE,
                        ActivityCameraGallery.EXTRA_BOARD
                    )
                    startActivityForResult(intent, REQUEST_ADD_IMAGES)
                }
                iv_check -> {
                    if (iv_check.isSelected) {
                        when (mViewType) {
                            TYPE_ADD_WISH -> {
                                DAClient.addWish(
                                    et_title.text.toString(),
                                    et_contents.text.toString(),
                                    checkCallbackListener
                                )
                            }
                            TYPE_ADD_CONCERN -> {
                                DAClient.addConcern(
                                    et_title.text.toString(),
                                    et_contents.text.toString(),
                                    checkCallbackListener
                                )
                            }
                            TYPE_EDIT_WISH -> {
                                val bean = mBean as BeanWishPost
                                DAClient.updateWish(
                                    bean.idx, et_title.text.toString(),
                                    et_contents.text.toString(),
                                    checkCallbackListener
                                )
                            }
                            TYPE_EDIT_CONCERN -> {
                                val bean = mBean as BeanConcernDetail
                                DAClient.updateConcern(
                                    bean.post.idx, et_title.text.toString(),
                                    et_contents.text.toString(),
                                    checkCallbackListener
                                )
                            }
                        }
                    }
                }
                iv_back_black -> {
                    (activity as ActivityMain).onBackPressed(false)
                }
            }
        }
        iv_add_photo.setOnClickListener(listener)
        iv_check.setOnClickListener(listener)
        iv_back_black.setOnClickListener(listener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_ADD_IMAGES) {
                val fileArray =
                    data!!.getSerializableExtra(ActivityCameraGallery.REQUEST_IMAGE_FILES) as ArrayList<File>
                fileArray?.let { files ->
                    if (files.size > 0) {
                        mAdapter?.let { adapter ->
                            for (file in files) {
                                adapter.add(file)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Check Callback Listener
     */
    private val checkCallbackListener = object : DAHttpCallback {
        override fun onResponse(
            call: Call,
            serverCode: Int,
            body: String,
            code: String,
            message: String
        ) {
            if (code == DAClient.SUCCESS) {
                if(mAdapter!!.size() > 0) {
                    if (mViewType == TYPE_ADD_CONCERN) {
                        val json = JSONObject(body)
                        val insertId = json.getInt("concern_idx")
                        mDialog.show()
                        uploadImage(insertId)
                    } else if (mViewType == TYPE_ADD_WISH) {
                        val json = JSONObject(body)
                        val insertId = json.getInt("post_idx")
                        mDialog.show()
                        uploadImage(insertId)
                    } else {
                        (activity as ActivityMain).onBackPressed(false)
                    }
                }else{
                    (activity as ActivityMain).onBackPressed(false)
                }
            } else {
                context?.let {
                    Toast.makeText(it.applicationContext, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Http
     * 이미지 업로드
     */
    private fun uploadImage(idx: Int) {
        val type: String = when (mViewType) {
            TYPE_ADD_WISH -> {
                DAClient.IMAGE_TYPE_WISH
            }
            TYPE_ADD_CONCERN -> {
                DAClient.IMAGE_TYPE_CONCERN
            }
            else -> ""
        }
        var isCalled = false

        mAdapter?.let { adapter ->
            val imgs = (adapter.mArray as ArrayList<File>)
            Utils.multiUploadWithTransferUtility(
                context!!.applicationContext,
                imgs,
                "$type/$idx",
                object :
                    IOS3ImageUploaderListener {
                    override fun onMutiStateCompleted(adressList: ArrayList<String>) {
                        super.onMutiStateCompleted(adressList)
                        if (!isCalled) {
                            updateProfileImage(idx, type, adressList)
                            isCalled = true
                        }
                    }

                    override fun onStateCompleted(
                        id: Int,
                        state: TransferState,
                        imageBucketAddress: String
                    ) {

                    }

                    override fun onError(id: Int, ex: java.lang.Exception?) {
                        if (mDialog.isShowing) mDialog.dismiss()
                    }
                })
        }
    }

    /**
     * Http
     * Image Update
     */
    private fun updateProfileImage(idx: Int, type: String, url: ArrayList<String>) {
        val list = ArrayList<String>()
        for (s in url) {
            list.add(s)
        }
        DAClient.uploadsImage(idx, type, list, object : DAHttpCallback {
            override fun onFailure(call: Call, e: IOException) {
                super.onFailure(call, e)
                if (mDialog.isShowing) mDialog.dismiss()
            }

            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (mDialog.isShowing) mDialog.dismiss()
                if (context != null) {
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()

                    if (code == DAClient.SUCCESS) {
                        (activity as ActivityMain).onBackPressed(false)
                    }
                }
            }
        })
    }

    /**
     * RecyclerView 이미지 Listener
     */
    private val rvImageListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = mAdapter?.size() ?: 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_achivement_list, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            val file = mAdapter?.get(i) as File
            val image = h.getItemView<ImageView>(R.id.iv_achivement)
            val delete = h.getItemView<ImageView>(R.id.iv_delete)
            delete.visibility = GONE
            Glide.with(context!!)
                .load(file)
                .placeholder(R.drawable.ic_image_gray)
                .centerCrop()
                .into(image)
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}