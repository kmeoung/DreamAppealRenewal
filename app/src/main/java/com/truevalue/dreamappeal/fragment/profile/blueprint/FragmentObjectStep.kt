package com.truevalue.dreamappeal.fragment.profile.blueprint

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityComment
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanActionPost
import com.truevalue.dreamappeal.bean.BeanActionPostHeader
import com.truevalue.dreamappeal.bean.BeanBlueprintObject
import com.truevalue.dreamappeal.bean.BeanObjectStep
import com.truevalue.dreamappeal.fragment.profile.FragmentAddPage
import com.truevalue.dreamappeal.fragment.profile.FragmentProfile
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.bottom_comment_view.*
import kotlinx.android.synthetic.main.fragment_object_step.*
import kotlinx.android.synthetic.main.layout_object_step_header.*
import okhttp3.Call
import org.json.JSONException
import org.json.JSONObject

class FragmentObjectStep : BaseFragment() {
    private var mBean: BeanBlueprintObject? = null
    private var mAdapter: BaseRecyclerViewAdapter? = null
    private var mObjectBean: BeanObjectStep? = null

    private var mViewUserIdx: Int = -1

    companion object {
        fun newInstance(bean: BeanBlueprintObject,view_user_idx: Int): FragmentObjectStep {
            val fragment = FragmentObjectStep()
            fragment.mViewUserIdx = view_user_idx
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
        // init View
        initView()
        // View Click Listener
        onClickView()
        // 데이터 초기화
        initData()
        // recyclerview adapter 초기화
        initAdapter()
    }

    /**
     * View 초기화
     */
    private fun initView() {
        tv_title.text = getString(R.string.str_object_step)

        if(mViewUserIdx == Comm_Prefs.getUserProfileIndex()){
            iv_object_step_more.visibility = VISIBLE
            tv_detail_step.visibility = VISIBLE
        }else{
            iv_object_step_more.visibility = GONE
            tv_detail_step.visibility = GONE
        }

        // 댓글 설정
        et_comment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                if (!et_comment.text.toString().isNullOrEmpty()) {
                    btn_commit_comment.visibility = View.VISIBLE
                    rl_comment.visibility = View.GONE
                } else {
                    btn_commit_comment.visibility = View.GONE
                    rl_comment.visibility = View.VISIBLE
                }
                btn_commit_comment.isSelected = !et_comment.text.toString().isNullOrEmpty()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    /**
     * VIew OnClick Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_back_black -> (activity as ActivityMain).onBackPressed()
                iv_object_step_more -> {
                    showMoreDialog()
                }
                tv_detail_step -> {
                    if (mObjectBean != null) {
                        (activity as ActivityMain).replaceFragment(
                            FragmentAddPage.newInstance(
                                FragmentAddPage.VIEW_TYPE_ADD_STEP_DETAIL, mObjectBean!!.idx
                            ), addToBack = true, isMainRefresh = false
                        )
                    }
                }
                rl_comment -> {
                    val intent = Intent(context!!, ActivityComment::class.java)
                    intent.putExtra(
                        ActivityComment.EXTRA_VIEW_TYPE,
                        ActivityComment.EXTRA_TYPE_BLUEPRINT
                    )
                    intent.putExtra(
                        ActivityComment.EXTRA_INDEX,
                        mViewUserIdx
                    ) // todo : 현재 보고있는 유저의 Index를 넣어야 합니다
                    startActivityForResult(intent,ActivityComment.REQUEST_REPLACE_USER_IDX)
                }
                btn_commit_comment -> {
                    if (btn_commit_comment.isSelected) addBlueprintComment()
                }
            }
        }

        iv_back_black.setOnClickListener(listener)
        iv_object_step_more.setOnClickListener(listener)
        tv_detail_step.setOnClickListener(listener)
        rl_comment.setOnClickListener(listener)
        btn_commit_comment.setOnClickListener(listener)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == ActivityComment.REQUEST_REPLACE_USER_IDX) {
                val view_user_idx = data!!.getIntExtra(ActivityComment.RESULT_REPLACE_USER_IDX,-1)
                (activity as ActivityMain).replaceFragment(FragmentProfile.newInstance(view_user_idx),true)
            }
        }
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
                                beanObjectHeader.position = (i + 1)
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
     * Http
     * 발전계획 댓글 추가
     */
    private fun addBlueprintComment() {
        val dst_profile_idx = mViewUserIdx // todo : 현재 보고있는 profile을 넣어야 함
        val writer_idx = Comm_Prefs.getUserProfileIndex()
        val contents = et_comment.text.toString()
        DAClient.addBlueprintComment(
            dst_profile_idx,
            writer_idx,
            0,
            contents,
            object : DAHttpCallback {
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
                        .show()
                    if (code == DAClient.SUCCESS) {
                        et_comment.setText("")
                        // todo : 여기서 혹시 더 필요한게 있으면 추가바람
                    }
                }
            }

        )
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
                return if (mAdapter!!.get(i) is BeanActionPostHeader
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

            if (getItemViewType(i) == TYPE_HEADER) {
                val bean = mAdapter!!.get(i) as BeanActionPostHeader

                val tvPosition = h.getItemView<TextView>(R.id.tv_position)
                val tvTitle = h.getItemView<TextView>(R.id.tv_title)
                val ivMore = h.getItemView<ImageView>(R.id.iv_more)

                tvPosition.text = bean.position.toString()
                tvTitle.text = bean.title
                if(mViewUserIdx == Comm_Prefs.getUserProfileIndex()){
                    ivMore.visibility = VISIBLE
                }else ivMore.visibility = GONE

                ivMore.setOnClickListener(View.OnClickListener {
                    // todo : 여기서는 기능 구현이 필요합니다
                })

            } else if (getItemViewType(i) == TYPE_ITEM) {
                val bean = mAdapter!!.get(i) as BeanActionPost

                val ivImage = h.getItemView<ImageView>(R.id.iv_image)

                Glide.with(context!!)
                    .load(bean.thumbnail_image)
                    .placeholder(R.drawable.ic_image_white)
                    .into(ivImage)

                h.itemView.setOnClickListener(View.OnClickListener {
                    (activity as ActivityMain).replaceFragment(
                        FagmentActionPost.newInstance(bean.idx,mViewUserIdx),
                        addToBack = true,
                        isMainRefresh = false
                    )
                })
            }
        }

        override fun getItemViewType(i: Int): Int {
            if (mAdapter!!.get(i) is BeanActionPostHeader
                || mAdapter!!.get(i) is String
            ) return TYPE_HEADER
            return TYPE_ITEM
        }
    }
}