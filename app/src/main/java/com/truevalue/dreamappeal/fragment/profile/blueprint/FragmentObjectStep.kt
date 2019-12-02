package com.truevalue.dreamappeal.fragment.profile.blueprint

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.*
import com.truevalue.dreamappeal.fragment.profile.FragmentAddPage
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.action_bar_other.iv_more
import kotlinx.android.synthetic.main.bottom_comment_view.*
import kotlinx.android.synthetic.main.bottom_comment_view.iv_profile
import kotlinx.android.synthetic.main.fragment_blueprint.*
import kotlinx.android.synthetic.main.fragment_object_step.*
import kotlinx.android.synthetic.main.layout_object_step_header.*
import kotlinx.android.synthetic.main.listitem_dream_list.*
import kotlinx.android.synthetic.main.listitem_object_step_sub_header.*
import okhttp3.Call
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class FragmentObjectStep : BaseFragment() {
    private var mBean: BeanBlueprintObject? = null
    private var mAdapter: BaseRecyclerViewAdapter? = null
    private var mObjectBean : BeanObjectStep? = null

    companion object {
        fun newInstance(bean: BeanBlueprintObject): FragmentObjectStep {
            val fragment = FragmentObjectStep()
            fragment.mBean = bean
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_object_step, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View Click Listener
        onClickView()
        // 데이터 초기화
        initData()
        // recyclerview adapter 초기화
        initAdapter()
    }

    /**
     * VIew OnClick Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_back_black -> (activity as ActivityMain).onBackPressed()
                iv_more -> {
                    showMoreDialog()
                }
                tv_detail_step -> {
                    if(mObjectBean != null) {
                        (activity as ActivityMain).replaceFragment(
                            FragmentAddPage.newInstance(
                                FragmentAddPage.VIEW_TYPE_ADD_STEP_DETAIL,mObjectBean!!.idx
                            ), addToBack = true, isMainRefresh = false
                        )
                    }
                }
            }
        }

        iv_back_black.setOnClickListener(listener)
        iv_more.setOnClickListener(listener)
        tv_detail_step.setOnClickListener(listener)

    }

    /**
     * 더보기 Dialog 띄우기
     */
    private fun showMoreDialog() {
        val list =
            arrayOf(
                getString(R.string.str_down_best_post),
                getString(R.string.str_edit),
                getString(R.string.str_delete)
            )
        val builder =
            AlertDialog.Builder(context)
        builder.setItems(list) { _, i ->
            when (list[i]) {
                getString(R.string.str_down_best_post) -> {

                }
                getString(R.string.str_edit) -> if (mBean != null) {
                    // todo : 수정
                }
                getString(R.string.str_delete) -> {
                    val builder =
                        AlertDialog.Builder(context)
                            .setTitle(getString(R.string.str_delete_post_title))
                            .setMessage(getString(R.string.str_delete_post_contents))
                            .setPositiveButton(
                                getString(R.string.str_yes)
                            ) { dialog, which ->
                                if (mBean != null) {

                                }
                                dialog.dismiss()
                            }
                            .setNegativeButton(
                                getString(R.string.str_no)
                            ) { dialog, _ -> dialog.dismiss() }
                    val dialog = builder.create()
                    dialog.show()
                }
            }
        }
        builder.create().show()
    }

    /**
     * 받아 온 데이터 초기화
     */
    private fun initData() {
        if (mBean != null) {
            getObjects(mBean!!.idx)
        }
    }

    /**
     * Http
     * 실천 목표 조회
     */
    private fun getObjects(object_idx: Int) {
        DAClient.getObjects(object_idx, object : DAHttpCallback {
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
                        if (mAdapter == null) return

                        mAdapter!!.clear()

                        val json = JSONObject(body)
                        val `object` = json.getJSONObject("object")
                        val bean = Gson().fromJson<BeanObjectStep>(
                            `object`.toString(),
                            BeanObjectStep::class.java
                        )

                        mObjectBean = bean

                        tv_object_step_title.text = bean.object_name
//                        "total_action_post_count":0, todo : 추후 사용
                        try {
                            val commentCount = json.getInt("comment_count")

                            if (commentCount < 1000) {
                                tv_comment.text = commentCount.toString()
                            } else {
                                val k = commentCount / 1000
                                if (k < 1000) {
                                    tv_comment.text = "${k}K"
                                } else {
                                    val m = k / 1000
                                    tv_comment.text = "${m}M"
                                }
                            }

                            val image = json.getString("user_image")
                            if (TextUtils.isEmpty(image))
                                Glide.with(context!!).load(R.drawable.drawer_user).apply(
                                    RequestOptions().circleCrop()
                                ).into(iv_profile)
                            else
                                Glide.with(context!!).load(image).placeholder(R.drawable.drawer_user).apply(
                                    RequestOptions().circleCrop()
                                ).into(iv_profile)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                        try {
                            val noneStepActionPost = json.getJSONArray("none_step_action_post")
                            for (i in 0 until noneStepActionPost.length()) {
                                val `object` = noneStepActionPost.getJSONObject(i)
                                val actionPost = Gson().fromJson<BeanActionPost>(
                                    `object`.toString(),
                                    BeanActionPost::class.java
                                )
                                mAdapter!!.add(actionPost)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                        try {
                            val objectSteps = json.getJSONArray("object_steps")
                            for (i in 0 until objectSteps.length()) {
                                val objectStep = objectSteps.getJSONObject(i)
                                val beanObjectHeader = Gson().fromJson<BeanActionPostHeader>(
                                    objectStep.toString(),
                                    BeanActionPostHeader::class.java
                                )
                                mAdapter!!.add(beanObjectHeader)
                                val actionPosts = objectStep.getJSONArray("action_posts")
                                for (j in 0 until actionPosts.length()) {
                                    val actionPost = actionPosts.getJSONObject(j)
                                    val beanActionPost = Gson().fromJson<BeanActionPost>(
                                        actionPost.toString(),
                                        BeanActionPost::class.java
                                    )
                                    mAdapter!!.add(beanActionPost)
                                }
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }
                }
            }
        })
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvListener)
        rv_achivement_ing.adapter = mAdapter

        val glm = GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
        glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(i: Int): Int {
                return if (mAdapter!!.get(i) is BeanObjectStepHeader
                    || mAdapter!!.get(i) is String
                )
                    3
                else
                    1
            }
        }
        rv_achivement_ing.layoutManager = glm
    }

    private val rvListener = object : IORecyclerViewListener {
        private val TYPE_HEADER = 1
        private val TYPE_ITEM = 0
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            if (viewType == TYPE_HEADER) return BaseViewHolder.newInstance(
                R.layout.listitem_object_step_sub_header,
                parent,
                false
            )
            return BaseViewHolder.newInstance(R.layout.listitem_object_step_image, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {

        }

        override fun getItemViewType(i: Int): Int {
            if (mAdapter!!.get(i) is BeanObjectStepHeader
                || mAdapter!!.get(i) is String
            ) return TYPE_HEADER
            return TYPE_ITEM
        }
    }
}