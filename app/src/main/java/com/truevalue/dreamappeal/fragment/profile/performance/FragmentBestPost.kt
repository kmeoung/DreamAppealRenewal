package com.truevalue.dreamappeal.fragment.profile.performance

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityComment
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BasePagerAdapter
import com.truevalue.dreamappeal.bean.BeanAchivementPostDetail
import com.truevalue.dreamappeal.bean.BeanImages
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
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
    private var mBestIdx = -1;
    private var mBean: BeanAchivementPostDetail? = null
    private var mAdapter: BasePagerAdapter<String>? = null

    companion object {
        fun newInstance(post_idx: Int, best_idx: Int): FragmentBestPost {
            val fragment = FragmentBestPost()
            fragment.mPostIdx = post_idx
            fragment.mBestIdx = best_idx
            return fragment
        }
    }

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
        tv_achivement_title.text = "${getString(R.string.str_best_performance)} $mBestIdx"
        // 상단 이미지 정사각형 설정
        Utils.setImageViewSquare(context, rl_images)
    }

    /**
     * Pager Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BasePagerAdapter(context!!)
        pager_image.adapter = mAdapter
        pager_image.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tv_indicator.text = ((position + 1).toString() + " / " + mAdapter!!.getCount())
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

                }
                iv_comment->{
                    val intent = Intent(context!!, ActivityComment::class.java)
                    intent.putExtra(ActivityComment.EXTRA_VIEW_TYPE, ActivityComment.EXTRA_TYPE_ACHIEVEMENT_POST)
                    intent.putExtra(ActivityComment.EXTRA_INDEX,mPostIdx)
                    intent.putExtra(ActivityComment.EXTRA_OFF_KEYBOARD," ")
                    startActivity(intent)
                }
                ll_comment, ll_comment_detail -> {
                    val intent = Intent(context!!, ActivityComment::class.java)
                    intent.putExtra(ActivityComment.EXTRA_VIEW_TYPE, ActivityComment.EXTRA_TYPE_ACHIEVEMENT_POST)
                    intent.putExtra(ActivityComment.EXTRA_INDEX,mPostIdx)
                    startActivity(intent)
                }
                ll_share -> {

                }
                iv_more -> {
                    showMoreDialog()
                }
            }
        }
        iv_back.setOnClickListener(listener)
        ll_cheering.setOnClickListener(listener)
        iv_comment.setOnClickListener(listener)
        ll_comment.setOnClickListener(listener)
        ll_comment_detail.setOnClickListener(listener)
        ll_share.setOnClickListener(listener)
        iv_more.setOnClickListener(listener)
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
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()

                    if (code == DAClient.SUCCESS) {
                        val json = JSONObject(body)
                        val achivementPost = json.getJSONObject("achievement_post")

                        val bean = Gson().fromJson<BeanAchivementPostDetail>(
                            achivementPost.toString(),
                            BeanAchivementPostDetail::class.java
                        )

                        val thumbnail = achivementPost.getJSONArray("images")

                        val images = ArrayList<BeanImages>()
                        for (i in 0 until thumbnail.length()) {
                            val image = Gson().fromJson<BeanImages>(
                                thumbnail.getJSONObject(i).toString(),
                                BeanImages::class.java
                            )
                            images.add(image)
                        }
                        bean.Images = images

                        mBean = bean
                        setData(bean)
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
                getString(R.string.str_down_best_post),
                getString(R.string.str_edit),
                getString(R.string.str_delete)
            )
        val builder =
            AlertDialog.Builder(context)
        builder.setItems(list) { _, i ->
            when (list[i]) {
                getString(R.string.str_down_best_post) -> downBestAchivement(mBestIdx)
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
     * 베스트 포스트 내리기
     */
    private fun downBestAchivement(best_idx: Int) {
        DAClient.downBestPost(best_idx, object : DAHttpCallback {
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
        // todo : 아직 검증이 필요함
        setReadMore(tv_contents, bean.content, 3)
        tv_cheering.text = String.format("%d${getString(R.string.str_count)}", bean.like_count)
        tv_comment.text = String.format("%d${getString(R.string.str_count)}", bean.comment_count)
        ll_cheering.isSelected = bean.status
        tv_time.text = convertFromDate(bean.register_date)
        tv_indicator.setText("0 / 0")
        tv_indicator.setText("1 / " + bean.Images.size)
        for (i in 0 until bean.Images.size) {
            val image = bean.Images[i]
            mAdapter!!.add(image.image_url)
        }
        mAdapter!!.notifyDataSetChanged()
    }
}