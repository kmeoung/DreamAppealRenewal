package com.truevalue.dreamappeal.fragment.profile.blueprint

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityAddPost
import com.truevalue.dreamappeal.activity.ActivityComment
import com.truevalue.dreamappeal.activity.ActivityFollowCheering
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BasePagerAdapter
import com.truevalue.dreamappeal.bean.BeanActionPostDetail
import com.truevalue.dreamappeal.bean.BeanActionPostImage
import com.truevalue.dreamappeal.fragment.profile.FragmentProfile
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_best_post.tv_title
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.bottom_post_view.*
import kotlinx.android.synthetic.main.fragment_action_post.*
import okhttp3.Call
import org.json.JSONObject

class FagmentActionPost : BaseFragment() {

    private var mAdapter: BasePagerAdapter<String>? = null
    private var mPostIdx = -1
    private var mViewUserIdx = -1
    private var mBean: BeanActionPostDetail? = null
    private var isDreamNoteType: String? = null
    private var mTags : String? = null
    private val EXTRA_CHANGE_CATEGORY = 3030

    companion object {
        val TYPE_DREAM_NOTE_IDEA = "TYPE_DREAM_NOTE_IDEA"
        val TYPE_DREAM_NOTE_LIFE = "TYPE_DREAM_NOTE_LIFE"

        fun newInstance(post_idx: Int, view_user_idx: Int): FagmentActionPost {
            val fragment = FagmentActionPost()
            fragment.mPostIdx = post_idx
            fragment.mViewUserIdx = view_user_idx
            return fragment
        }

        fun newInstance(
            post_idx: Int,
            view_user_idx: Int,
            dream_note_type: String
        ): FagmentActionPost {
            val fragment = FagmentActionPost()
            fragment.mPostIdx = post_idx
            fragment.mViewUserIdx = view_user_idx
            fragment.isDreamNoteType = dream_note_type
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_action_post, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        initView()
        // Pager Adapter 초기화
        initAdapter()
        // View Click Listener
        onViewClicked()
        // 데이터 바인딩
        getActionPost()
    }

    /**
     * View 초기화
     */
    private fun initView() {
        // 상단 이미지 정사각형 설정
        Utils.setImageViewSquare(context, rl_images)
        // text 설정
        tv_title.text = getString(R.string.str_level_choice_action_post)

        if (mViewUserIdx == Comm_Prefs.getUserProfileIndex()) {
            iv_action_more.visibility = VISIBLE
        } else iv_action_more.visibility = GONE

        if (isDreamNoteType != null) {

            when (isDreamNoteType) {
                TYPE_DREAM_NOTE_LIFE -> {
                    iv_circle.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_circle_green
                        )
                    )
                    tv_arrow.setTextColor(ContextCompat.getColor(context!!, R.color.asparagus))
                    iv_side_img.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_side_green
                        )
                    )
                }
                TYPE_DREAM_NOTE_IDEA -> {
                    iv_circle.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_circle_yellow
                        )
                    )
                    tv_arrow.setTextColor(ContextCompat.getColor(context!!, R.color.yellow_orange))
                    iv_side_img.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_side_yellow
                        )
                    )
                }
                else -> {
                    iv_circle.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_circle_blue
                        )
                    )
                    tv_arrow.setTextColor(ContextCompat.getColor(context!!, R.color.main_blue))
                    iv_side_img.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_side_blue
                        )
                    )
                }
            }
        } else {
            iv_circle.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_circle_blue
                )
            )
            tv_arrow.setTextColor(ContextCompat.getColor(context!!, R.color.main_blue))
            iv_side_img.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_side_blue
                )
            )
        }
    }

    /**
     * View Clicked
     */
    private fun onViewClicked() {
        val listener = View.OnClickListener {
            when (it) {
                iv_back_black -> activity!!.onBackPressed()
                ll_cheering -> {
                    actionLike()
                }
                ll_comment_detail -> {
                    val intent = Intent(context!!, ActivityComment::class.java)
                    intent.putExtra(
                        ActivityComment.EXTRA_VIEW_TYPE,
                        ActivityComment.EXTRA_TYPE_ACTION_POST
                    )
                    intent.putExtra(ActivityComment.EXTRA_INDEX, mPostIdx)
                    intent.putExtra(ActivityComment.EXTRA_OFF_KEYBOARD, " ")
                    startActivityForResult(intent, ActivityComment.REQUEST_REPLACE_USER_IDX)
                }
                ll_comment -> {
                    val intent = Intent(context!!, ActivityComment::class.java)
                    intent.putExtra(
                        ActivityComment.EXTRA_VIEW_TYPE,
                        ActivityComment.EXTRA_TYPE_ACTION_POST
                    )
                    intent.putExtra(ActivityComment.EXTRA_INDEX, mPostIdx)
                    startActivityForResult(intent, ActivityComment.REQUEST_REPLACE_USER_IDX)
                }
                ll_share -> {

                }
                iv_action_more -> {
                    showMoreDialog()
                }
                ll_cheering_detail->{
                    val intent = Intent(context, ActivityFollowCheering::class.java)
                    intent.putExtra(
                        ActivityFollowCheering.EXTRA_VIEW_TYPE,
                        ActivityFollowCheering.VIEW_TYPE_CHEERING_ACTION
                    )
                    intent.putExtra(ActivityFollowCheering.REQUEST_VIEW_LIST_IDX, mPostIdx)
                    startActivityForResult(intent, ActivityFollowCheering.REQUEST_REPLACE_USER_IDX)
                }
            }
        }
        iv_back_black.setOnClickListener(listener)
        ll_cheering.setOnClickListener(listener)
        ll_comment_detail.setOnClickListener(listener)
        ll_comment.setOnClickListener(listener)
        ll_share.setOnClickListener(listener)
        iv_action_more.setOnClickListener(listener)
        ll_cheering_detail.setOnClickListener(listener)
    }

    /**
     * 더보기 Dialog 띄우기
     */
    private fun showMoreDialog() {
        val list = if (isDreamNoteType != null) arrayOf(
            getString(R.string.str_edit),
            getString(R.string.str_delete)
        ) else arrayOf(
            getString(R.string.str_edit_level),
            getString(R.string.str_edit),
            getString(R.string.str_delete)
        )
        val builder =
            AlertDialog.Builder(context)
        builder.setItems(list) { _, i ->
            when (list[i]) {
                getString(R.string.str_edit_level) -> {
                    val intent = Intent(context!!, ActivityAddPost::class.java)
                    intent.putExtra(
                        ActivityAddPost.EDIT_VIEW_TYPE,
                        ActivityAddPost.EDIT_CHANGE_CATEGORY
                    )
                    intent.putExtra(ActivityAddPost.EDIT_POST_IDX, mPostIdx)
                    intent.putExtra(ActivityAddPost.REQUEST_CATEOGORY_IDX, mBean!!.object_idx)
                    intent.putExtra(ActivityAddPost.REQUEST_CATEOGORY_DETAIL_IDX, mBean!!.step_idx)
                    startActivityForResult(intent, EXTRA_CHANGE_CATEGORY)
                }
                getString(R.string.str_edit) -> {

                    val intent = Intent(context!!, ActivityAddPost::class.java)
                    intent.putExtra(
                        ActivityAddPost.EDIT_VIEW_TYPE,
                        ActivityAddPost.EDIT_ACTION_POST
                    )
                    intent.putExtra(ActivityAddPost.REQUEST_TAGS,mTags)
                    intent.putExtra(ActivityAddPost.EDIT_POST_IDX, mPostIdx)
                    intent.putExtra(ActivityAddPost.REQUEST_IAMGE_FILES, mAdapter!!.getAll())
                    intent.putExtra(ActivityAddPost.REQUEST_CONTENTS, tv_contents.text.toString())
                    startActivity(intent)
                }

                getString(R.string.str_delete) -> {
                    val builder =
                        AlertDialog.Builder(context)
                            .setTitle(getString(R.string.str_delete_post_title))
                            .setMessage(getString(R.string.str_delete_post_contents))
                            .setPositiveButton(
                                getString(R.string.str_yes)
                            ) { dialog, _ ->
                                deletePost()
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
     * Post 삭제
     */
    fun deletePost() {
        DAClient.deleteActionPostsDetail(mPostIdx, object : DAHttpCallback {
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
                        (activity as ActivityMain).onBackPressed(false)
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == ActivityComment.REQUEST_REPLACE_USER_IDX) {
                getActionPost()
            } else if (requestCode == EXTRA_CHANGE_CATEGORY) {
                (activity as ActivityMain).onBackPressed(false)
            }
        } else if (resultCode == ActivityComment.RESULT_CODE ||
            resultCode == ActivityFollowCheering.RESULT_CODE
        ) {
            if (requestCode == ActivityComment.REQUEST_REPLACE_USER_IDX ||
                requestCode == ActivityFollowCheering.REQUEST_REPLACE_USER_IDX
            ) {
                val view_user_idx = data!!.getIntExtra(ActivityComment.RESULT_REPLACE_USER_IDX, -1)
                (activity as ActivityMain).replaceFragment(
                    FragmentProfile.newInstance(view_user_idx),
                    true
                )
            }
        }
    }

    /**
     * Http
     * 인증 좋아요
     */
    private fun actionLike() {
        DAClient.likeActionPost(mPostIdx, object : DAHttpCallback {
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
                        val status = json.getBoolean("status")
                        iv_cheering.isSelected = status
                        val count = json.getInt("count")
                        tv_cheering.text = "${count}개"
                    }
                }
            }
        })
    }

    /**
     * Pager Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BasePagerAdapter(context!!)
        pager_image.adapter = mAdapter
        pager_image.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tv_indicator.text = if(mAdapter!!.getCount() > 0) ((position + 1).toString() + " / " + mAdapter!!.getCount()) else "0 / 0"
            }
        })
    }

    /**
     * Http
     * Action Post 상세 조회
     */
    private fun getActionPost() {
        DAClient.getActionPostsDetail(mPostIdx, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (context != null) {

                    if (code == DAClient.SUCCESS) {
                        val json = JSONObject(body)
                        val profile = json.getJSONObject("profile")
                        val value_style = profile.getString("value_style")
                        val job = profile.getString("job")
                        val profile_image = profile.getString("profile_image")
                        val nickname = profile.getString("nickname")

                        tv_value_style.text = value_style
                        tv_job.text = "$job $nickname"
                        Glide.with(context!!)
                            .load(profile_image)
                            .placeholder(R.drawable.drawer_user)
                            .circleCrop()
                            .into(iv_dream_profile)

                        val actionPost = json.getJSONObject("action_post")

                        val tags = actionPost.getString("tags")

                        mTags = tags
                        var strTags = ""
                        if(!mTags.isNullOrEmpty()){
                            tv_tag.visibility = VISIBLE
                            if(mTags!!.contains(",".toRegex())) {

                                val tags = mTags!!.split(",".toRegex())
                                for (i in 0 until tags.size) {
                                    strTags = "$strTags #${tags[i]} "
                                }
                            }else{
                                strTags = " #${mTags!!}"
                            }
                        }else{
                            strTags = getString(R.string.str_tag)
                            tv_tag.visibility = GONE
                        }
                        tv_tag.text = strTags

                        val bean = Gson().fromJson<BeanActionPostDetail>(
                            actionPost.toString(),
                            BeanActionPostDetail::class.java
                        )

                        try {
                            mAdapter!!.clear()
                            val images = actionPost.getJSONArray("images")
                            for (i in 0 until images.length()) {
                                val image = images.getJSONObject(i)
                                val beanImage = Gson().fromJson<BeanActionPostImage>(
                                    image.toString(),
                                    BeanActionPostImage::class.java
                                )
                                mAdapter!!.add(beanImage.image_url)
                                tv_indicator.text = "0 / 0"
                                tv_indicator.text = "1 / " + images.length()
                            }
                            mAdapter!!.notifyDataSetChanged()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            setData(bean)
                        }
                    }else{
                        Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    /**
     * Data binding
     */
    private fun setData(bean: BeanActionPostDetail) {
        mBean = bean
        iv_cheering.isSelected = bean.status

        if (bean.object_name.isNullOrEmpty() && bean.step_name.isNullOrEmpty()) {
            ll_object_step.visibility = View.GONE
        } else {
            ll_object_step.visibility = View.VISIBLE
            tv_object.text = bean.object_name

            if (bean.step_name.isNullOrEmpty()) {
                ll_step_line.visibility = View.GONE
            } else {
                tv_step.text = bean.step_name
                ll_step_line.visibility = View.VISIBLE
            }
        }

        tv_time.text = Utils.convertFromDate(bean.register_date)

        tv_contents.text = bean.content

        tv_cheering.text = "${bean.like_count}개"
        tv_comment.text = "${bean.comment_count}개"

    }

}