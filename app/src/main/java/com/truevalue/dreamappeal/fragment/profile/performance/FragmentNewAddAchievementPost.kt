package com.truevalue.dreamappeal.fragment.profile.performance

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
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
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_add_action_post.et_comment
import kotlinx.android.synthetic.main.fragment_add_action_post.rv_image
import kotlinx.android.synthetic.main.fragment_add_new_achievement_post.*
import okhttp3.Call
import org.json.JSONObject
import java.io.File
import java.io.IOException

class FragmentNewAddAchievementPost : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter? = null
    private var mImages: ArrayList<File>? = null
    private var mUrls: ArrayList<String>? = null
    private var mBestIdx: Int = -1
    private var postIdx : Int = -1
    private var mTitle : String? = null
    private var mContents : String? = null
    private var mDialog : ProgressDialog? = null

    companion object {
        fun newInstance(images: ArrayList<File>, best_idx: Int): FragmentNewAddAchievementPost {
            val fragment = FragmentNewAddAchievementPost()
            fragment.mImages = images
            fragment.mBestIdx = best_idx
            return fragment
        }

        fun newInstance(url: ArrayList<String>,best_idx : Int, post_idx : Int,title : String,
                        contents : String): FragmentNewAddAchievementPost {
            val fragment = FragmentNewAddAchievementPost()
            fragment.mUrls = url
            fragment.mBestIdx = best_idx
            fragment.postIdx = post_idx
            fragment.mTitle = title
            fragment.mContents = contents
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_new_achievement_post, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // init View
        initView()
        // init rv Adapter
        initAdapter()
        // init Data
        initData()
        // Click Listener
        onClickView()
    }

    /**
     * View 초기화
     */
    private fun initView() {
        mDialog = ProgressDialog(context!!)
        mDialog!!.setCancelable(false)

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                iv_check.isSelected =
                    (!et_comment.text.toString().isNullOrEmpty() && !et_title.text.toString().isNullOrEmpty())
            }
        }

        et_title.addTextChangedListener(textWatcher)
        et_comment.addTextChangedListener(textWatcher)

        if(postIdx > -1){
            tv_text_btn.visibility = GONE
            iv_check.visibility = VISIBLE
            tv_title.text = getString(R.string.str_edit_achivement_post)

            et_title.setText(mTitle)
            et_comment.setText(mContents)
            et_title.setSelection(et_title.length())
            et_comment.setSelection(et_comment.length())
        }else {
            tv_text_btn.visibility = GONE
            iv_check.visibility = VISIBLE
            tv_title.text = getString(R.string.str_add_achivement_post)
        }
    }

    /**
     * RecyclerView Adpater 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvListener)
        rv_image.adapter = mAdapter
        rv_image.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    /**
     * RecyclerView Init Data
     */
    private fun initData() {
        if (mImages != null && mImages!!.size > 0) {
            for (i in 0 until mImages!!.size) {
                mAdapter!!.add(mImages!![i])
            }
        }else if(mUrls != null && mUrls!!.size > 0){
            for (i in 0 until mUrls!!.size) {
                mAdapter!!.add(mUrls!![i])
            }
        }
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_back_black -> activity!!.onBackPressed()
                iv_check -> {

                    if (iv_check.isSelected) {
                        if(postIdx > -1){
                            updateAchievementPost()
                        }else {
                            addAchievementPost()
                        }
                    }
                }
            }
        }
        iv_check.setOnClickListener(listener)
        iv_back_black.setOnClickListener(listener)
    }

    /**
     * Http
     * Achievement Post 등록
     */
    private fun addAchievementPost(){
        if (mDialog != null && !mDialog!!.isShowing) mDialog!!.show()
        val title = et_title.text.toString()
        val contents = et_comment.text.toString()
        DAClient.addAchievementPost(title,contents,mBestIdx,object : DAHttpCallback{
            override fun onFailure(call: Call, e: IOException) {
                super.onFailure(call, e)
                if (mDialog != null && mDialog!!.isShowing) mDialog!!.dismiss()
            }

            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if(context != null){
                    Toast.makeText(context!!.applicationContext,message,Toast.LENGTH_SHORT).show()

                    if(code == DAClient.SUCCESS){
                        val json = JSONObject(body)
                        val result = json.getJSONObject("result")
                        val insertId = result.getInt("insertId")
                        uploadImage(insertId)
                    }else if (mDialog != null && mDialog!!.isShowing) mDialog!!.dismiss()
                }else if (mDialog != null && mDialog!!.isShowing) mDialog!!.dismiss()
            }
        })
    }

    /**
     * Http
     * Achievement Post 수정
     */
    private fun updateAchievementPost(){
        val title = et_title.text.toString()
        val contents = et_comment.text.toString()
        DAClient.updateAchievementPost(title,contents,postIdx,object : DAHttpCallback{
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if(context != null){
                    Toast.makeText(context!!.applicationContext,message,Toast.LENGTH_SHORT).show()

                    if(code == DAClient.SUCCESS){
                        activity!!.setResult(RESULT_OK)
                        activity!!.onBackPressed()
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
        var isCalled = false
        if (mAdapter != null) {
            Utils.multiUploadWithTransferUtility(
                context!!.applicationContext,
                mAdapter!!.mArray as ArrayList<File>,
                "$type/$idx",
                object :
                    IOS3ImageUploaderListener {
                    override fun onMutiStateCompleted(adressList: ArrayList<String>) {
                        super.onMutiStateCompleted(adressList)
                        if(!isCalled) {
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
                        if (mDialog != null && mDialog!!.isShowing) mDialog!!.dismiss()
                    }
                })
        }else if (mDialog != null && mDialog!!.isShowing) mDialog!!.dismiss()
    }

    /**
     * Http
     * Profile Image Update
     */
    private fun updateProfileImage(idx: Int, type: String, url: ArrayList<String>) {
        val list = ArrayList<String>()
        for (s in url) {
            list.add(s)
        }
        DAClient.uploadsImage(idx, type, list, object : DAHttpCallback {
            override fun onFailure(call: Call, e: IOException) {
                super.onFailure(call, e)
                if (mDialog != null && mDialog!!.isShowing) mDialog!!.dismiss()
            }

            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (mDialog != null && mDialog!!.isShowing) mDialog!!.dismiss()
                if (context != null) {

                    if (code == DAClient.SUCCESS) {
                        activity!!.setResult(RESULT_OK)
                        activity!!.finish()
                    }else{
                        Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    /**
     * RecyclerView Listener
     */
    private val rvListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_add_action_post_img, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            if (mAdapter != null) {
                val ivImage = h.getItemView<ImageView>(R.id.iv_image)
                val image = mAdapter!!.get(i)

                Glide.with(context!!)
                    .load(image)
                    .placeholder(R.drawable.ic_image_black)
                    .centerCrop()
                    .into(ivImage)

            }
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }


    override fun onDestroy() {
        if (mDialog != null && mDialog!!.isShowing) mDialog!!.dismiss()
        super.onDestroy()
    }
}