package com.truevalue.dreamappeal.fragment.profile.blueprint

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.bean.BeanCategory
import com.truevalue.dreamappeal.bean.BeanCategoryDetail
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_level_choice.*
import okhttp3.Call
import org.json.JSONObject
import java.io.File
import java.io.IOException

class FragmentLevelChoice : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter? = null
    private var mAdapterDetail: BaseRecyclerViewAdapter? = null

    private var mDialog: ProgressDialog? = null

    private val TYPE_IDEA = 0
    private val TYPE_LIKE = 1
    private val TYPE_ACTION_POST = 2

    private var mCategoryType = -1

    private var mImages: ArrayList<File>? = null
    private var mTags: ArrayList<String>? = null
    private var postContents: String? = null

    private var selectedCategoryIdx = -1
    private var selectedCategoryDetailIdx = 0

    private var mPostIdx: Int = -1

    companion object {
        fun newInstance(
            images: ArrayList<File>?,
            tags: ArrayList<String>?,
            post_contents: String
        ): FragmentLevelChoice {
            val fragment = FragmentLevelChoice()
            fragment.mImages = images
            fragment.mTags = tags
            fragment.postContents = post_contents

            return fragment
        }

        fun newInstance(
            post_idx: Int,
            category_idx: Int,
            category_detail_idx: Int
        ): FragmentLevelChoice {
            val fragment = FragmentLevelChoice()
            fragment.mPostIdx = post_idx
            fragment.selectedCategoryIdx = category_idx
            fragment.selectedCategoryDetailIdx = category_detail_idx
            fragment.mCategoryType = fragment.TYPE_ACTION_POST
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_level_choice, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        initView()
        // Recyclerview 초기화
        initAdapter()
        // View Click Listener
        onClickListener()
        // 초기화
        setList(mCategoryType)
    }

    /**
     * View 초기화
     */
    private fun initView() {
        tv_title.text = getString(R.string.str_title_level_choice)
        iv_back_blue.visibility = VISIBLE
        iv_back_black.visibility = GONE
        iv_check.visibility = VISIBLE

        if (mPostIdx > -1) {
            ll_idea.visibility = GONE
            ll_like.visibility = GONE
        } else {
            ll_idea.visibility = VISIBLE
            ll_like.visibility = VISIBLE
        }

        mDialog = ProgressDialog(context!!)
        mDialog!!.setCancelable(false)
    }

    /**
     * View Click Listener
     */
    private fun onClickListener() {
        val listener = View.OnClickListener {
            when (it) {
                iv_back_blue -> (activity!!.onBackPressed())
                iv_check -> {
                    if (iv_check.isSelected) {
                        if (mPostIdx > -1) {
                            updateActionPost()
                        } else {
                            addActionPost()
                        }
                    }
                }
                ll_idea -> {
                    if (mCategoryType != TYPE_IDEA)
                        setList(TYPE_IDEA)
                }
                ll_like -> {
                    if (mCategoryType != TYPE_LIKE)
                        setList(TYPE_LIKE)
                }
                ll_action_post -> {
                    if (mCategoryType != TYPE_ACTION_POST)
                        setList(TYPE_ACTION_POST)
                }
            }
        }
        iv_back_blue.setOnClickListener(listener)
        iv_check.setOnClickListener(listener)
        ll_idea.setOnClickListener(listener)
        ll_like.setOnClickListener(listener)
        ll_action_post.setOnClickListener(listener)
    }

    /**
     * type 설정
     */
    private fun setList(type: Int) {
        mCategoryType = type
        iv_check.isSelected = isCheckEnable()
        when (type) {
            TYPE_IDEA -> {
                rv_category.visibility = GONE
                rv_category_detail.visibility = GONE
                iv_check_idea.isSelected = true
                iv_check_like.isSelected = false
                iv_check_action_post.isSelected = false
                tv_category.visibility = GONE
                tv_category_detail.visibility = GONE
                if (mAdapter != null) mAdapter!!.notifyDataSetChanged()
                if (mAdapterDetail != null) mAdapterDetail!!.notifyDataSetChanged()
            }
            TYPE_LIKE -> {
                rv_category.visibility = GONE
                rv_category_detail.visibility = GONE
                iv_check_idea.isSelected = false
                iv_check_like.isSelected = true
                iv_check_action_post.isSelected = false
                tv_category.visibility = GONE
                tv_category_detail.visibility = GONE
                if (mAdapter != null) mAdapter!!.notifyDataSetChanged()
                if (mAdapterDetail != null) mAdapterDetail!!.notifyDataSetChanged()
            }
            TYPE_ACTION_POST -> {
                rv_category.visibility = VISIBLE
                rv_category_detail.visibility = VISIBLE
                iv_check_idea.isSelected = false
                iv_check_like.isSelected = false
                iv_check_action_post.isSelected = true
                tv_category.visibility = VISIBLE
                tv_category_detail.visibility = VISIBLE

                mAdapterDetail!!.clear()
                // 실천인증 가져오기
                getCategory()

                if (mPostIdx > -1) {
                    getCategoryDetail(selectedCategoryIdx)
                }
            }
            else -> {
                rv_category.visibility = GONE
                rv_category_detail.visibility = GONE
                iv_check_idea.isSelected = false
                iv_check_like.isSelected = false
                iv_check_action_post.isSelected = false
                tv_category.visibility = GONE
                tv_category_detail.visibility = GONE
                if (mAdapter != null) mAdapter!!.notifyDataSetChanged()
                if (mAdapterDetail != null) mAdapterDetail!!.notifyDataSetChanged()
            }
        }
    }

    /**
     * Check Btn 활설화 / 비활성화
     */
    private fun isCheckEnable(): Boolean {
        when (mCategoryType) {
            TYPE_IDEA -> {
                selectedCategoryIdx = -1
                selectedCategoryDetailIdx = 0
                return true
            }
            TYPE_LIKE -> {
                selectedCategoryIdx = -1
                selectedCategoryDetailIdx = 0
                return true
            }
            TYPE_ACTION_POST -> {
                return selectedCategoryIdx > 0
            }
            else->false
        }
        return false
    }

    /**
     * Http
     * 실천인증 가져오기
     */
    private fun getCategory() {
        DAClient.getActionPostCategoty(object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (context != null) {

                    if (code == DAClient.SUCCESS) {
                        mAdapter!!.clear()
                        val `object` = JSONObject(body)
                        val objects = `object`.getJSONArray("objects")
                        for (i in 0 until objects.length()) {
                            val bean = Gson().fromJson<BeanCategory>(
                                objects.getJSONObject(i).toString(),
                                BeanCategory::class.java
                            )
                            mAdapter!!.add(bean)
                        }
                    } else {
                        Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        })
    }

    /**
     * Http
     * 실천인증 세부단계 가져오기
     */
    private fun getCategoryDetail(idx: Int) {
        DAClient.getActionPostCategotyDetail(idx, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (context != null) {

                    if (code == DAClient.SUCCESS) {
                        mAdapterDetail!!.clear()
                        mAdapterDetail!!.add(
                            BeanCategoryDetail(
                                0,
                                getString(R.string.str_no_select)
                            )
                        )
                        val `object` = JSONObject(body)
                        val objects = `object`.getJSONArray("object_steps")
                        for (i in 0 until objects.length()) {
                            val bean = Gson().fromJson<BeanCategoryDetail>(
                                objects.getJSONObject(i).toString(),
                                BeanCategoryDetail::class.java
                            )
                            mAdapterDetail!!.add(bean)
                        }
                    } else {
                        Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        })
    }

    /**
     * Http
     * 수정
     */
    private fun updateActionPost() {
        DAClient.updateActionPosts(
            mPostIdx,
            selectedCategoryIdx,
            selectedCategoryDetailIdx,
            null,
            null,
            null,
            object : DAHttpCallback {
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    if (context != null) {
                        Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
                            .show()
                        if (code == DAClient.SUCCESS) {
                            activity!!.onBackPressed()
                        }
                    }
                }
            })
    }

    /**
     * Action Post 추가
     */
    private fun addActionPost() {
        if(mCategoryType == -1) return

        if (mDialog != null && !mDialog!!.isShowing) mDialog!!.show()

        val contents = postContents
        var tags = ""

        if (mTags != null || mTags!!.size > 0) {
            for (i in 0 until mTags!!.size) {
                if (i == 0) tags = mTags!![i]
                else tags = "$tags,${mTags!![i]}"
            }
        }


        val post_type = when (mCategoryType) {
            TYPE_ACTION_POST -> DAClient.POST_TYPE_ACTION
            TYPE_IDEA -> DAClient.POST_TYPE_IDEA
            TYPE_LIKE -> DAClient.POST_TYPE_LIFE
            else -> DAClient.POST_TYPE_ACTION
        }
        val object_idx = if (selectedCategoryIdx == -1) null else selectedCategoryIdx
        val step_idx =
            if (selectedCategoryIdx == -1) null else selectedCategoryDetailIdx
        DAClient.addActionPost(
            contents,
            post_type,
            tags,
            object_idx,
            step_idx,
            object : DAHttpCallback {
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
                    if (context != null) {
                        Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
                            .show()

                        if (code == DAClient.SUCCESS) {
                            val json = JSONObject(body)
                            val result = json.getJSONObject("result")
                            val insertId = result.getInt("insertId")
                            uploadImage(insertId)
                        } else if (mDialog != null && mDialog!!.isShowing) mDialog!!.dismiss()
                    } else if (mDialog != null && mDialog!!.isShowing) mDialog!!.dismiss()
                }
            })
    }

    /**
     * Http
     * 이미지 업로드
     */
    private fun uploadImage(post_idx: Int) {
        val idx = post_idx
        val type = DAClient.IMAGE_TYPE_ACTION_POST
        var isCalled = false
        if (mAdapter != null && mImages != null) {
            Utils.multiUploadWithTransferUtility(
                context!!.applicationContext,
                mImages!!,
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
                        if (mDialog != null && mDialog!!.isShowing) mDialog!!.dismiss()
                    }
                })
        }
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
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()

                    if (code == DAClient.SUCCESS) {
                        activity!!.setResult(RESULT_OK)
                        activity!!.finish()
                    }
                }
            }
        })
    }

    /**
     * Recycler View Adapter
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvListener)
        rv_category.adapter = mAdapter
        rv_category.layoutManager = LinearLayoutManager(context)

        mAdapterDetail = BaseRecyclerViewAdapter(rvListenerDetail)
        rv_category_detail.adapter = mAdapterDetail
        rv_category_detail.layoutManager = LinearLayoutManager(context)
    }

    private val rvListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(
                R.layout.listitem_level_choice,
                parent,
                false
            )
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            if (mAdapter != null) {
                val bean = mAdapter!!.get(i) as BeanCategory
                val llBg = h.getItemView<LinearLayout>(R.id.ll_bg)
                val tvCircle = h.getItemView<TextView>(R.id.tv_circle)
                val tvTitle = h.getItemView<TextView>(R.id.tv_title)

                tvTitle.text = bean.object_name

                if (selectedCategoryIdx == bean.idx) {
                    llBg.setBackgroundColor(ContextCompat.getColor(context!!, R.color.nice_blue))
                    tvCircle.isSelected = true
                    tvTitle.isSelected = true
                } else {
                    llBg.setBackgroundColor(ContextCompat.getColor(context!!, R.color.white))
                    tvCircle.isSelected = false
                    tvTitle.isSelected = false
                }

                llBg.setOnClickListener(View.OnClickListener {
                    if (selectedCategoryIdx != bean.idx) {
                        selectedCategoryIdx = bean.idx
                        selectedCategoryDetailIdx = 0
                        iv_check.isSelected = isCheckEnable()
                        getCategoryDetail(selectedCategoryIdx)
                        mAdapter!!.notifyDataSetChanged()
                    }
                })
            }
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }

    private val rvListenerDetail = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapterDetail != null) mAdapterDetail!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(
                R.layout.listitem_level_choice,
                parent,
                false
            )
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            if (mAdapterDetail != null) {
                val bean = mAdapterDetail!!.get(i) as BeanCategoryDetail
                val llBg = h.getItemView<LinearLayout>(R.id.ll_bg)
                val tvCircle = h.getItemView<TextView>(R.id.tv_circle)
                val tvTitle = h.getItemView<TextView>(R.id.tv_title)

                tvTitle.text = bean.title

                if (selectedCategoryDetailIdx == bean.idx) {
                    llBg.setBackgroundColor(ContextCompat.getColor(context!!, R.color.nice_blue))
                    tvCircle.isSelected = true
                    tvTitle.isSelected = true
                } else {
                    llBg.setBackgroundColor(ContextCompat.getColor(context!!, R.color.white))
                    tvCircle.isSelected = false
                    tvTitle.isSelected = false
                }

                llBg.setOnClickListener {
                    selectedCategoryDetailIdx = bean.idx
                    iv_check.isSelected = isCheckEnable()
                    mAdapterDetail!!.notifyDataSetChanged()
                }
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