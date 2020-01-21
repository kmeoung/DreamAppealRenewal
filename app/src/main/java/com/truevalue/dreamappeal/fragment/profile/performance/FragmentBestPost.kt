package com.truevalue.dreamappeal.fragment.profile.performance

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager.widget.ViewPager.*
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityAddPost
import com.truevalue.dreamappeal.activity.ActivityComment
import com.truevalue.dreamappeal.activity.ActivityFollowCheering
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseImagePagerAdapter
import com.truevalue.dreamappeal.bean.BeanAchivementPostDetail
import com.truevalue.dreamappeal.fragment.profile.FragmentProfile
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import com.truevalue.dreamappeal.utils.Utils
import com.truevalue.dreamappeal.utils.Utils.convertFromDate
import com.truevalue.dreamappeal.utils.Utils.setReadMore
import kotlinx.android.synthetic.main.action_bar_best_post.*
import kotlinx.android.synthetic.main.bottom_post_view.*
import kotlinx.android.synthetic.main.fragment_post_detail.*
import okhttp3.Call
import org.json.JSONObject

class FragmentBestPost : BaseFragment() {

    private var mPostIdx = -1
    private var mBean: BeanAchivementPostDetail? = null
    private var mAdapterImage: BaseImagePagerAdapter<String>? = null
    private var mViewUserIdx = -1

    companion object {
        fun newInstance(post_idx: Int, view_user_idx: Int): FragmentBestPost {
            val fragment = FragmentBestPost()
            fragment.mPostIdx = post_idx
            fragment.mViewUserIdx = view_user_idx
            return fragment
        }
    }

    private val REQUEST_EDIT_PAGE = 2020

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_best_post, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        initView()
        // Pager Adapter 초기화
        initAdapter()
        // View Click Listener
        onViewClicked()
        // 데이터 바인딩
        achivementPostDetail()
    }

    /**
     * View 초기화
     */
    private fun initView() {
        // 상단 이미지 정사각형 설정
        Utils.setImageViewSquare(context, rl_images)

        if (mViewUserIdx == Comm_Prefs.getUserProfileIndex()) {
            iv_more.visibility = View.VISIBLE
        } else iv_more.visibility = View.GONE
    }

    /**
     * Pager Adapter 초기화
     */
    private fun initAdapter() {
        mAdapterImage = BaseImagePagerAdapter(context!!)
        pager_image.adapter = mAdapterImage
        pager_image.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tv_indicator.text = if(mAdapterImage!!.getCount() > 0) ((position + 1).toString() + " / " + mAdapterImage!!.getCount()) else "0 / 0"
            }
        })
    }

    /**
     * View Clicked
     */
    private fun onViewClicked() {
        val listener = View.OnClickListener {
            when (it) {
                iv_back -> activity!!.onBackPressed()
                ll_cheering -> {
                    achievementLike()
                }
                ll_comment_detail -> {
                    val intent = Intent(context!!, ActivityComment::class.java)
                    intent.putExtra(
                        ActivityComment.EXTRA_VIEW_TYPE,
                        ActivityComment.EXTRA_TYPE_ACHIEVEMENT_POST
                    )
                    intent.putExtra(ActivityComment.EXTRA_INDEX, mPostIdx)
                    intent.putExtra(ActivityComment.EXTRA_OFF_KEYBOARD, " ")
                    startActivityForResult(intent, ActivityComment.REQUEST_REPLACE_USER_IDX)
                }
                ll_comment -> {
                    val intent = Intent(context!!, ActivityComment::class.java)
                    intent.putExtra(
                        ActivityComment.EXTRA_VIEW_TYPE,
                        ActivityComment.EXTRA_TYPE_ACHIEVEMENT_POST
                    )
                    intent.putExtra(ActivityComment.EXTRA_INDEX, mPostIdx)
                    startActivityForResult(intent, ActivityComment.REQUEST_REPLACE_USER_IDX)
                }
                ll_share -> {

                }
                iv_more -> {
                    showMoreDialog()
                }
                ll_cheering_detail -> {
                    val intent = Intent(context, ActivityFollowCheering::class.java)
                    intent.putExtra(
                        ActivityFollowCheering.EXTRA_VIEW_TYPE,
                        ActivityFollowCheering.VIEW_TYPE_CHEERING_ACHIEVEMENT
                    )
                    intent.putExtra(ActivityFollowCheering.REQUEST_VIEW_LIST_IDX, mPostIdx)
                    startActivityForResult(intent, ActivityFollowCheering.REQUEST_REPLACE_USER_IDX)
                }
            }
        }
        iv_back.setOnClickListener(listener)
        ll_cheering.setOnClickListener(listener)
        ll_comment_detail.setOnClickListener(listener)
        ll_comment.setOnClickListener(listener)
        ll_share.setOnClickListener(listener)
        iv_more.setOnClickListener(listener)
        ll_cheering_detail.setOnClickListener(listener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ActivityComment.REQUEST_REPLACE_USER_IDX ||
                requestCode == ActivityFollowCheering.REQUEST_REPLACE_USER_IDX
            ) {
                achivementPostDetail()
            } else if (requestCode == REQUEST_EDIT_PAGE) {
                achivementPostDetail()
            }
        } else if (resultCode == RESULT_CODE
        ) {
            if (requestCode == ActivityComment.REQUEST_REPLACE_USER_IDX ||
                requestCode == ActivityFollowCheering.REQUEST_REPLACE_USER_IDX
            ) {
                val view_user_idx = data!!.getIntExtra(RESULT_REPLACE_USER_IDX, -1)
                (activity as ActivityMain).replaceFragment(
                    FragmentProfile.newInstance(view_user_idx),
                    true
                )
            }
        }
    }

    /**
     * Http
     * 성과 좋아요
     */
    private fun achievementLike() {
        DAClient.likeAchievementPost(mPostIdx, object : DAHttpCallback {
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
     * 상세 페이지 데이터 조회
     */
    private fun achivementPostDetail() {
        DAClient.achievementPostDetail(mPostIdx, object : DAHttpCallback {
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
                        val achivementPost = json.getJSONObject("achievement_post")

                        val bean = Gson().fromJson<BeanAchivementPostDetail>(
                            achivementPost.toString(),
                            BeanAchivementPostDetail::class.java
                        )

                        val thumbnail = achivementPost.getJSONArray("images")

                        val images = ArrayList<String>()
                        for (i in 0 until thumbnail.length()) {
                            val image = thumbnail.getJSONObject(i)
                            val url = image.getString("url")
                            images.add(url)
                        }
                        bean.Images = images

                        mBean = bean
                        setData(bean)
                    } else {
                        Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        })
    }

    /**
     * 더보기 Dialog 띄우기
     */
    private fun showMoreDialog() {
        val list =
            arrayOf(
                getString(R.string.str_edit),
                getString(R.string.str_delete)
            )
        val builder =
            AlertDialog.Builder(context)
        builder.setItems(list) { _, i ->
            when (list[i]) {
                getString(R.string.str_edit) -> if (mBean != null) {
                    val intent = Intent(context!!, ActivityAddPost::class.java)
                    intent.putExtra(
                        ActivityAddPost.EDIT_VIEW_TYPE,
                        ActivityAddPost.EDIT_ACHIEVEMENT_POST
                    )
                    intent.putExtra(ActivityAddPost.EDIT_POST_IDX, mPostIdx)
                    intent.putExtra(ActivityAddPost.REQUEST_IAMGE_FILES, mAdapterImage!!.getAll())
                    intent.putExtra(ActivityAddPost.REQUEST_TITLE, mBean!!.title)
                    intent.putExtra(ActivityAddPost.REQUEST_CONTENTS, mBean!!.content)
                    startActivityForResult(intent, REQUEST_EDIT_PAGE)
                }
                getString(R.string.str_delete) -> {
                    val builder =
                        AlertDialog.Builder(context)
                            .setTitle(getString(R.string.str_delete_post_title))
                            .setMessage(getString(R.string.str_delete_post_contents))
                            .setPositiveButton(
                                getString(R.string.str_yes)
                            ) { dialog, which ->
                                if (mBean != null) deleteAchivement(mBean!!)
                                dialog.dismiss()
                            }
                            .setNegativeButton(
                                getString(R.string.str_no)
                            ) { dialog, which -> dialog.dismiss() }
                    val dialog = builder.create()
                    dialog.show()
                }
            }
        }
        builder.create().show()
    }

    /**
     * Http
     * 게시물 삭제
     */
    private fun deleteAchivement(bean: BeanAchivementPostDetail) {
        DAClient.deleteachievementPost(bean.idx, object : DAHttpCallback {
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
     * Data 셋팅
     */
    private fun setData(bean: BeanAchivementPostDetail) {
        tv_title.text = bean.title
        iv_cheering.isSelected = bean.status
        // todo : 아직 검증이 필요함
        setReadMore(tv_contents, bean.content, 3)
        tv_cheering.text = String.format("%d${getString(R.string.str_count)}", bean.like_count)
        tv_comment.text = String.format("%d${getString(R.string.str_count)}", bean.comment_count)
        ll_cheering.isSelected = bean.status
        tv_time.text = convertFromDate(bean.register_date)
        tv_indicator.text = "0 / 0"
        tv_indicator.text = "1 / " + bean.Images.size
        for (i in 0 until bean.Images.size) {
            val image = bean.Images[i]
            mAdapterImage!!.add(image)
        }
        mAdapterImage!!.notifyDataSetChanged()
    }
}