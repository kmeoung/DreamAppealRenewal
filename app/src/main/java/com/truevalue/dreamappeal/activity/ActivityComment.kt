package com.truevalue.dreamappeal.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanCommentDetail
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.activity_comment_detail.*
import kotlinx.android.synthetic.main.bottom_comment_view.*
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ActivityComment : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {

    private var mAdapter: BaseRecyclerViewAdapter? = null

    private var mIndex: Int
    private var mViewType: String?
    private var mParentIdx: Int
    private var mTagIdx: Int
    private var mIsEdit: Boolean

    init {
        mIndex = -1
        mViewType = null
        mParentIdx = -1
        mTagIdx = -1
        mIsEdit = false
    }

    companion object {
        private const val RV_TYPE_COMMENT = 0
        private const val RV_TYPE_REPLY = 1

        const val EXTRA_VIEW_TYPE = "EXTRA_VIEW_TYPE"

        const val EXTRA_TYPE_PROFILE = "EXTRA_TYPE_PROFILE"
        const val EXTRA_TYPE_BLUEPRINT = "EXTRA_TYPE_BLUEPRINT"
        const val EXTRA_TYPE_ACHIEVEMENT_POST = "EXTRA_TYPE_ACHIEVEMENT_POST"
        const val EXTRA_TYPE_ACTION_POST = "EXTRA_TYPE_ACTION_POST"

        const val EXTRA_INDEX = "EXTRA_INDEX"
        const val EXTRA_OFF_KEYBOARD = "EXTRA_OFF_KEYBOARD"

        const val REQUEST_REPLACE_USER_IDX = 2000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_detail)

        // init View
        initView()
        // init RecyclerView Adapter
        initAdapter()
        // init Data
        initData(true)
        // View Click Listener
        onClickView()
    }

    /**
     * View 초기화
     */
    private fun initView() {
        tv_title.text = getString(R.string.str_title_comment_detail)

        intent.getStringExtra(EXTRA_VIEW_TYPE)?.let {
            mViewType = it
            mIndex = intent.getIntExtra(EXTRA_INDEX, -1)
        }

        Utils.setSwipeRefreshLayout(srl_refresh, this)
        rl_comment.visibility = GONE
        btn_commit_comment.visibility = VISIBLE

        intent.getStringExtra(EXTRA_OFF_KEYBOARD)?.let {
            bottom_comment.visibility = VISIBLE
        } ?: kotlin.run {
            et_comment.isFocusableInTouchMode = true
            et_comment.requestFocus()
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(et_comment, 0)
            bottom_comment.visibility = VISIBLE
        }

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
                btn_commit_comment.isSelected = !et_comment.text.toString().isNullOrEmpty()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    /**
     * RecyclerView Adapter Init
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvListener)
        rv_comments.run {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@ActivityComment)
        }
    }

    /**
     * 데이터 초기화
     */
    private fun initData(isScroll: Boolean) {
        when (mViewType) {
            EXTRA_TYPE_PROFILE -> getPresentComments(isScroll)
            EXTRA_TYPE_BLUEPRINT -> getBlueprintComments(isScroll)
            EXTRA_TYPE_ACHIEVEMENT_POST -> getAchievementPostComments(isScroll)
            EXTRA_TYPE_ACTION_POST -> getActionPostComments(isScroll)
        }
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_back_black -> {
                    setResult(RESULT_OK)
                    finish()
                }
                btn_commit_comment -> if (btn_commit_comment.isSelected) if (mIsEdit) updateComment(
                    mParentIdx
                ) else addComment(mParentIdx, mTagIdx)
                iv_writer_reply_close -> {
                    initComment()
                }
                tv_title -> {
                    rv_comments.smoothScrollToPosition(0)
                }
            }
        }
        iv_back_black.setOnClickListener(listener)
        btn_commit_comment.setOnClickListener(listener)
        iv_writer_reply_close.setOnClickListener(listener)
        tv_title.setOnClickListener(listener)
    }

    /**
     * Http
     * 내 꿈 소개 댓글 가져오기
     */
    private fun getPresentComments(isScroll: Boolean) {
        val dst_profile_idx = mIndex
        DAClient.getProfileComment(dst_profile_idx, object : DAHttpCallback {
            override fun onFailure(call: Call, e: IOException) {
                super.onFailure(call, e)
                srl_refresh.isRefreshing = false
            }

            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                srl_refresh.isRefreshing = false

                if (code == DAClient.SUCCESS) {
                    getCommentAction(body, isScroll)
                } else {
                    Toast.makeText(
                        this@ActivityComment.applicationContext,
                        message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        })
    }

    /**
     * Http
     * 발전계획 댓글 가져오기
     */
    @Deprecated("Blueprint 댓글 현재 비 활성화")
    private fun getBlueprintComments(isScroll: Boolean) {
        val dst_profile_idx = mIndex
        DAClient.getBlueprintComment(dst_profile_idx, object : DAHttpCallback {
            override fun onFailure(call: Call, e: IOException) {
                super.onFailure(call, e)
                srl_refresh.isRefreshing = false
            }

            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                srl_refresh.isRefreshing = false

                if (code == DAClient.SUCCESS) {
                    getCommentAction(body, isScroll)
                } else {
                    Toast.makeText(
                        this@ActivityComment.applicationContext,
                        message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        })
    }

    /**
     * Http
     * 실현성과 댓글 가져오기
     */
    private fun getAchievementPostComments(isScroll: Boolean) {
        val post_idx = mIndex
        DAClient.getAchievementPostComment(post_idx, object : DAHttpCallback {
            override fun onFailure(call: Call, e: IOException) {
                super.onFailure(call, e)
                srl_refresh.isRefreshing = false
            }

            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                srl_refresh.isRefreshing = false

                if (code == DAClient.SUCCESS) {
                    getCommentAction(body, isScroll)
                } else {
                    Toast.makeText(
                        this@ActivityComment.applicationContext,
                        message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        })
    }

    /**
     * Http
     * 실천인증 댓글 가져오기
     */
    private fun getActionPostComments(isScroll: Boolean) {
        val post_idx = mIndex
        DAClient.getActionPostComment(post_idx, object : DAHttpCallback {
            override fun onFailure(call: Call, e: IOException) {
                super.onFailure(call, e)
                srl_refresh.isRefreshing = false
            }

            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                srl_refresh.isRefreshing = false

                if (code == DAClient.SUCCESS) {
                    getCommentAction(body, isScroll)
                } else {
                    Toast.makeText(
                        this@ActivityComment.applicationContext,
                        message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        })
    }

    /**
     * 댓글 추가
     */
    private fun addComment(parent_idx: Int, tag_idx: Int) {
        when (mViewType) {
            EXTRA_TYPE_PROFILE -> addPresentComment(parent_idx, tag_idx)
            EXTRA_TYPE_BLUEPRINT -> addBlueprintComment(parent_idx)
            EXTRA_TYPE_ACHIEVEMENT_POST -> addAchievementPostComment(parent_idx, tag_idx)
            EXTRA_TYPE_ACTION_POST -> addActionPostComment(parent_idx, tag_idx)
        }
    }

    /**
     * Http
     * 내 꿈 소개 댓글 추가
     */
    private fun addPresentComment(parent_idx: Int, tag_idx: Int) {
        val dst_profile_idx = mIndex
        val writer_idx = Comm_Prefs.getUserProfileIndex()
        val contents = et_comment.text.toString()
        DAClient.addProfileComment(
            dst_profile_idx,
            writer_idx,
            parent_idx,
            tag_idx,
            contents,
            sendCommentListener
        )
    }

    /**
     * Http
     * 발전계획 댓글 추가
     */
    @Deprecated("Blueprint 댓글 비 활성화")
    private fun addBlueprintComment(parent_idx: Int) {
        val dst_profile_idx = mIndex
        val writer_idx = Comm_Prefs.getUserProfileIndex()
        val contents = et_comment.text.toString()
        DAClient.addBlueprintComment(
            dst_profile_idx,
            writer_idx,
            parent_idx,
            contents,
            sendCommentListener
        )
    }

    /**
     * Http
     * 실현성과 댓글 추가
     */
    private fun addAchievementPostComment(parent_idx: Int, tag_idx: Int) {
        val post_idx = mIndex
        val writer_idx = Comm_Prefs.getUserProfileIndex()
        val contents = et_comment.text.toString()
        DAClient.addAchievementPostComment(
            post_idx,
            writer_idx,
            parent_idx,
            contents,
            tag_idx,
            sendCommentListener
        )
    }

    /**
     * Http
     * 실천인증 댓글 추가
     */
    private fun addActionPostComment(parent_idx: Int, tag_idx: Int) {
        val post_idx = mIndex
        val writer_idx = Comm_Prefs.getUserProfileIndex()
        val contents = et_comment.text.toString()
        DAClient.addActionPostComment(
            post_idx,
            writer_idx,
            parent_idx,
            contents,
            tag_idx,
            sendCommentListener
        )
    }

    /**
     * 댓글 수정
     */
    private fun updateComment(comment_idx: Int) {
        when (mViewType) {
            EXTRA_TYPE_PROFILE -> updatePresentComment(comment_idx)
            EXTRA_TYPE_BLUEPRINT -> updateBlueprintComment(comment_idx)
            EXTRA_TYPE_ACHIEVEMENT_POST -> updateAchievementPostComment(comment_idx)
            EXTRA_TYPE_ACTION_POST -> updateActionPostComment(comment_idx)
        }
    }

    /**
     * Http
     * 내 꿈 소개 댓글 수정
     */
    private fun updatePresentComment(comment_idx: Int) {
        val contents = et_comment.text.toString()
        DAClient.updateProfileComment(comment_idx, contents, updateCommentListener)
    }

    /**
     * Http
     * 발전계획 댓글 수정
     */
    private fun updateBlueprintComment(comment_idx: Int) {
        val contents = et_comment.text.toString()
        DAClient.updateBlueprintComment(
            comment_idx,
            contents,
            updateCommentListener
        )
    }

    /**
     * Http
     * 실현성과 댓글 수정
     */
    private fun updateAchievementPostComment(comment_idx: Int) {
        val contents = et_comment.text.toString()
        DAClient.updateAchievementPostComment(
            comment_idx,
            contents,
            updateCommentListener
        )
    }

    /**
     * Http
     * 실천인증 댓글 수정
     */
    private fun updateActionPostComment(comment_idx: Int) {
        val contents = et_comment.text.toString()
        DAClient.updateActionPostComment(
            comment_idx,
            contents,
            updateCommentListener
        )
    }

    /**
     * 댓글 삭제
     */
    private fun deleteComment(comment_idx: Int) {
        when (mViewType) {
            EXTRA_TYPE_PROFILE -> deletePresentComment(comment_idx)
            EXTRA_TYPE_BLUEPRINT -> deleteBlueprintComment(comment_idx)
            EXTRA_TYPE_ACHIEVEMENT_POST -> deleteAchievementPostComment(comment_idx)
            EXTRA_TYPE_ACTION_POST -> deleteActionPostComment(comment_idx)
        }
    }

    /**
     * Http
     * 내 꿈 소개 댓글 삭제
     */
    private fun deletePresentComment(comment_idx: Int) {
        DAClient.deleteProfileComment(comment_idx, updateCommentListener)
    }

    /**
     * Http
     * 발전계획 댓글 삭제
     */
    private fun deleteBlueprintComment(comment_idx: Int) {
        DAClient.deleteBlueprintComment(comment_idx, updateCommentListener)
    }

    /**
     * Http
     * 실현성과 댓글 삭제
     */
    private fun deleteAchievementPostComment(comment_idx: Int) {
        DAClient.deleteAchievementPostComment(comment_idx, updateCommentListener)
    }

    /**
     * Http
     * 실천인증 댓글 삭제
     */
    private fun deleteActionPostComment(comment_idx: Int) {
        DAClient.deleteActionPostComment(comment_idx, updateCommentListener)
    }


    /**
     * 댓글 액션
     */
    private fun getCommentAction(body: String, isScroll: Boolean) {
        val json = JSONObject(body)
        val comments = json.getJSONArray("comments")
        val image = json.getString("image")

        Glide.with(this@ActivityComment)
            .load(image)
            .placeholder(R.drawable.drawer_user)
            .circleCrop()
            .into(iv_profile)

        mAdapter?.let { mAdapter ->
            mAdapter.clear()
            val parent: ArrayList<BeanCommentDetail> =
                ArrayList()
            val reply: LinkedHashMap<Int, ArrayList<BeanCommentDetail>> =
                LinkedHashMap()
            for (i in 0 until comments.length()) {
                val bean: BeanCommentDetail = Gson().fromJson<BeanCommentDetail>(
                    comments.getJSONObject(i).toString(),
                    BeanCommentDetail::class.java
                )
                if (bean.parent_idx == 0) {
                    parent.add(bean)
                } else {
                    reply[bean.parent_idx] = reply[bean.parent_idx] ?: ArrayList()

                    reply[bean.parent_idx]?.let {
                        it.add(bean)
                    }
                }
            }

            for (i in parent.indices) {
                val bean: BeanCommentDetail = parent[i]
                mAdapter.add(bean)

                reply[bean.idx]?.let {
                    for (j in it.indices) {
                        val replyBean: BeanCommentDetail = it[j]
                        replyBean.parent_name = bean.name
                        mAdapter.add(replyBean)
                    }
                }
            }
            if (isScroll && mAdapter.size() > 0) {
                if (mAdapter.size() > 15) {
                    rv_comments.scrollToPosition(mAdapter.size() - 1)
                } else {
                    rv_comments.smoothScrollToPosition(mAdapter.size() - 1)
                }
            }
        }
    }

    /**
     * 댓글 변화되었을때 리스너
     */
    private val updateCommentListener = object : DAHttpCallback {
        override fun onResponse(
            call: Call,
            serverCode: Int,
            body: String,
            code: String,
            message: String
        ) {
            Toast.makeText(this@ActivityComment.applicationContext, message, Toast.LENGTH_SHORT)
                .show()
            if (code == DAClient.SUCCESS) {
                et_comment.setText("")
                initComment()
                initData(true)
            }
        }
    }

    private val sendCommentListener = object : DAHttpCallback {
        override fun onResponse(
            call: Call,
            serverCode: Int,
            body: String,
            code: String,
            message: String
        ) {

            if (code == DAClient.SUCCESS) {
                et_comment.setText("")
                initComment()
                initData(true)
            } else {
                Toast.makeText(this@ActivityComment.applicationContext, message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    /**
     * Show PopupMenu
     */
    private fun showPopupMenu(ivMore: View, bean: BeanCommentDetail) {
        val popupMenu = PopupMenu(this, ivMore)
        popupMenu.menu.add(getString(R.string.str_edit))
        popupMenu.menu.add(getString(R.string.str_delete))

        popupMenu.setOnMenuItemClickListener {
            when (it.title) {
                getString(R.string.str_edit) -> {
                    setUpdateComment(bean)
                }
                getString(R.string.str_delete) -> {
                    val builder =
                        AlertDialog.Builder(this)
                            .setTitle(getString(R.string.str_delete_comment_title))
                            .setMessage(getString(R.string.str_delete_comment_contents))
                            .setPositiveButton(
                                getString(R.string.str_yes)
                            ) { dialog, _ ->
                                deleteComment(bean.idx)
                                dialog.dismiss()
                            }
                            .setNegativeButton(
                                getString(R.string.str_no)
                            ) { dialog, _ -> dialog.dismiss() }
                    val dialog = builder.create()
                    dialog.show()
                }
            }
            false
        }
        popupMenu.show()
    }


    /**
     * RecyclerView Listener
     */
    private val rvListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = mAdapter?.size() ?: 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            if (RV_TYPE_COMMENT == viewType) {
                return BaseViewHolder.newInstance(R.layout.listitem_comment, parent, false)
            }
            return BaseViewHolder.newInstance(R.layout.listitem_reply, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            mAdapter?.let {
                val bean = it.get(i) as BeanCommentDetail
                val ivProfile = h.getItemView<ImageView>(R.id.iv_profile)
                val llProfile = h.getItemView<LinearLayout>(R.id.ll_profile)
                val tvComment = h.getItemView<TextView>(R.id.tv_comment)
                val tvTime = h.getItemView<TextView>(R.id.tv_time)
                val ivLike = h.getItemView<ImageView>(R.id.iv_like)
                val tvLike = h.getItemView<TextView>(R.id.tv_like)
                val tvAddReply = h.getItemView<TextView>(R.id.tv_add_reply)

                if (getItemViewType(i) == RV_TYPE_COMMENT) {
                    val tvValueStyle = h.getItemView<TextView>(R.id.tv_value_style)
                    val tvJob = h.getItemView<TextView>(R.id.tv_job)

                    tvValueStyle.text =
                        if (bean.value_style.isNullOrEmpty()) "" else bean.value_style
                    tvJob.text =
                        "${if (bean.job.isNullOrEmpty()) "" else bean.job} ${if (bean.name.isNullOrEmpty()) "" else bean.name}"
                } else {
                    val tvName = h.getItemView<TextView>(R.id.tv_name)
                    val tvTag = h.getItemView<TextView>(R.id.tv_tag)
                    tvName.text = if (bean.name.isNullOrEmpty()) "" else bean.name
                    tvTag.text =
                        if (bean.tag_profile_idx != null) "@${bean.tag_name}" else if (bean.parent_name.isNullOrEmpty()) "" else "@${bean.parent_name}"
                }
                tvComment.text = if (bean.content.isNullOrEmpty()) "" else bean.content

                if (bean.image.isNullOrEmpty()) {
                    Glide.with(this@ActivityComment)
                        .load(R.drawable.drawer_user)
                        .circleCrop()
                        .into(ivProfile)
                } else {
                    Glide.with(this@ActivityComment)
                        .load(bean.image)
                        .placeholder(R.drawable.drawer_user)
                        .circleCrop()
                        .into(ivProfile)
                }

                tvTime.text = Utils.convertFromDate(bean.register_date)
                tvLike.text = String.format("%d", bean.like_count)

                ivLike.isSelected = bean.status

                val likeClickListener = View.OnClickListener {
                    setLikeComment(bean)
                }

                ivLike.setOnClickListener(likeClickListener)
                tvLike.setOnClickListener(likeClickListener)

                tvAddReply.setOnClickListener {
                    setReplyComment(bean)
                }
                if (bean.writer_idx == Comm_Prefs.getUserProfileIndex()) {
                    h.itemView.setOnLongClickListener {
                        showPopupMenu(tvComment, bean)
                        true
                    }
                } else {
                    llProfile.setOnClickListener {
                        val intent = Intent()
                        intent.putExtra(RESULT_REPLACE_USER_IDX, bean.writer_idx)
                        setResult(RESULT_CODE, intent)
                        finish()
                    }
                }
            }
        }

        override fun getItemViewType(i: Int): Int {
            mAdapter?.let {
                val bean = it.get(i) as BeanCommentDetail
                return if (bean.parent_idx === 0) RV_TYPE_COMMENT else RV_TYPE_REPLY
            }

            return 0
        }
    }

    /**
     * Http
     * 댓글 좋아요
     */
    private fun setLikeComment(bean: BeanCommentDetail) {

        val likeCommentListener = object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {

                if (code == DAClient.SUCCESS) {
                    val json = JSONObject(body)
                    val status = json.getBoolean("status")
                    bean.status = status
                    bean.like_count = if (bean.status) {
                        bean.like_count + 1
                    } else {
                        bean.like_count - 1
                    }

                    mAdapter?.let {
                        it.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        when (mViewType) {
            EXTRA_TYPE_PROFILE -> DAClient.likeDreamPresentComment(bean.idx, likeCommentListener)
            EXTRA_TYPE_BLUEPRINT -> DAClient.likeBlueprintComment(bean.idx, likeCommentListener)
            EXTRA_TYPE_ACHIEVEMENT_POST -> DAClient.likeAchievementPostComment(
                bean.idx,
                likeCommentListener
            )
            EXTRA_TYPE_ACTION_POST -> DAClient.likeActionPostComment(bean.idx, likeCommentListener)
        }
    }

    /**
     * Comment Reply 설정
     */
    private fun setReplyComment(bean: BeanCommentDetail) {
        ll_writer.visibility = VISIBLE
        tv_writer.text = bean.name
        val idx = if (bean.parent_idx > 0) bean.parent_idx else bean.idx
        mParentIdx = idx
        mTagIdx = bean.writer_idx
        et_comment.isFocusableInTouchMode = true
        et_comment.requestFocus()
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(et_comment, 0)
        bottom_comment.visibility = VISIBLE
    }

    /**
     * Comment Update 설정
     */
    private fun setUpdateComment(bean: BeanCommentDetail) {
        ll_writer.visibility = VISIBLE
        tv_writer.text = bean.name
        et_comment.setText(bean.content)
        et_comment.setSelection(et_comment.length())
        mIsEdit = true
        mParentIdx = bean.idx

        et_comment.isFocusableInTouchMode = true
        et_comment.requestFocus()
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(et_comment, 0)
        bottom_comment.visibility = VISIBLE
    }

    /**
     * Comment 초기화
     */
    private fun initComment() {
        mParentIdx = -1
        mTagIdx = -1
        tv_writer.text = ""
        mIsEdit = false
        ll_writer.visibility = GONE
    }

    override fun onRefresh() {
        initData(true)
    }
}