package com.truevalue.dreamappeal.fragment.profile.performance

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.bumptech.glide.Glide
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityCameraGallery
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_main.*
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_add_achivement.*
import kotlinx.android.synthetic.main.fragment_add_action_post.*
import kotlinx.android.synthetic.main.fragment_add_action_post.btn_edit
import kotlinx.android.synthetic.main.fragment_add_action_post.iv_add_img
import okhttp3.Call
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FragmentAddAchivementPost : BaseFragment() {

    val REQUEST_ADD_IMAGES = 1003
    private var mAdapter: BaseRecyclerViewAdapter? = null
    private var isEdit = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_achivement, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View Init
        initView()
        // Recyclerview Adapter 초기화
        initAdapter()
        // view click
        onClickView()
    }

    private fun initView() {
        iv_check.visibility = View.VISIBLE

        et_title.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                initRightText()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        et_contents.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                initRightText()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun initRightText() {
        iv_check.isSelected =
            !et_title.text.toString().isNullOrEmpty() && !et_contents.text.toString().isNullOrEmpty()
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(recyclerViewListener)
        rv_achivement_img.adapter = mAdapter
        rv_achivement_img.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_back_black -> (activity as ActivityMain).onBackPressed(false)
                iv_add_img -> {
                    val intent = Intent(context!!, ActivityCameraGallery::class.java)
                    intent.putExtra(
                        ActivityCameraGallery.SELECT_TYPE,
                        ActivityCameraGallery.EXTRA_IMAGE_MULTI_SELECT
                    )
                    startActivityForResult(intent, REQUEST_ADD_IMAGES)
                }
                btn_edit -> {
                    isEdit = !isEdit
                    mAdapter!!.notifyDataSetChanged()
                }
                iv_check -> {
                    if (iv_check.isSelected) addAchivementPost()
                }
            }
        }
        iv_add_img.setOnClickListener(listener)
        btn_edit.setOnClickListener(listener)
        iv_check.setOnClickListener(listener)
        iv_back_black.setOnClickListener(listener)
    }

    private fun addAchivementPost() {
        val title = et_title.text.toString()
        val contents = et_contents.text.toString()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val reg_date = sdf.format(Date())
        val tags = ""
        DAClient.addAchivementPost(title, contents, reg_date, tags, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (context != null) {
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()

                    if (code == DAClient.SUCCESS) {
                        val json = JSONObject(body)
                        val result = json.getJSONObject("result")
                        val insertId = result.getInt("insertId")
                        uploadImage(insertId)
                    }
                }
            }
        })
    }

    /**
     * Http
     * 이미지 업로드
     */
    private fun uploadImage(post_idx: Int) {
        val idx = post_idx
        val type = DAClient.IMAGE_TYPE_ACHIVEMENT_POST

        if (mAdapter != null) {
            Utils.multiUploadWithTransferUtility(
                context!!.applicationContext,
                mAdapter!!.mArray as ArrayList<File>,
                "$idx/$type",
                object :
                    IOS3ImageUploaderListener {
                    override fun onMutiStateCompleted(adressList: ArrayList<String>) {
                        super.onMutiStateCompleted(adressList)
                        // todo :
                        updateProfileImage(idx, type, adressList)
                    }

                    override fun onStateCompleted(
                        id: Int,
                        state: TransferState,
                        imageBucketAddress: String
                    ) {

                    }

                    override fun onError(id: Int, ex: java.lang.Exception?) {

                    }
                })
        }
    }

    /**
     * Http
     * Profile Image Update
     */
    private fun updateProfileImage(idx: Int, type: Int, url: ArrayList<String>) {
        val list = ArrayList<String>()
        for (s in url) {
            list.add(s)
        }
        DAClient.uploadsImage(idx, type, list, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (context != null) {
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()

                    if (code == DAClient.SUCCESS) {
                        (activity as ActivityMain).onBackPressed(true)
                    }
                }
            }
        })
    }

    /**
     * Recyclerview Listener
     */
    private val recyclerViewListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_achivement_list, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            if (mAdapter != null) {
                val file = mAdapter!!.get(i) as File
                val ivImage = h.getItemView<ImageView>(R.id.iv_achivement)
                val ivDelete = h.getItemView<ImageView>(R.id.iv_delete)
                Glide.with(context!!).load(file).placeholder(R.drawable.ic_image_black)
                    .into(ivImage)

                if (isEdit) {
                    ivDelete.visibility = View.VISIBLE
                    ivDelete.setOnClickListener(View.OnClickListener {
                        mAdapter!!.remove(i)
                        mAdapter!!.notifyDataSetChanged()
//                        initRightBtn()
                    })
                } else {
                    ivDelete.visibility = View.GONE
                }
            }
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD_IMAGES) {
                val fileArray =
                    data!!.getSerializableExtra(ActivityCameraGallery.REQUEST_IMAGE_FILES) as ArrayList<File>

                if (fileArray != null) {
                    if (fileArray.size > 0) {
                        if (mAdapter != null) {
                            for (file in fileArray) {
                                mAdapter!!.add(file)
                            }
                        }
                    }
                }
            }
        }
    }
}