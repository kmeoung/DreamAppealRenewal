package com.truevalue.dreamappeal.fragment.profile.blueprint

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityComment
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BasePagerAdapter
import com.truevalue.dreamappeal.bean.BeanActionPostDetail
import com.truevalue.dreamappeal.bean.BeanActionPostImage
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_best_post.*
import kotlinx.android.synthetic.main.action_bar_best_post.iv_more
import kotlinx.android.synthetic.main.action_bar_best_post.tv_title
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.bottom_post_view.*
import kotlinx.android.synthetic.main.fragment_action_post.*
import kotlinx.android.synthetic.main.fragment_post_detail.*
import kotlinx.android.synthetic.main.fragment_post_detail.pager_image
import kotlinx.android.synthetic.main.fragment_post_detail.rl_images
import kotlinx.android.synthetic.main.fragment_post_detail.tv_indicator
import okhttp3.Call
import org.json.JSONObject
import java.lang.Exception

class FagmentActionPost : BaseFragment() {

    private var mAdapter: BasePagerAdapter<String>? = null

    private var mPostIdx = -1

    companion object {
        fun newInstance(post_idx: Int): FagmentActionPost {
            val fragment = FagmentActionPost()
            fragment.mPostIdx = post_idx
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
    }

    /**
     * View Clicked
     */
    private fun onViewClicked() {
        val listener = View.OnClickListener {
            when (it) {
                iv_back_black -> activity!!.onBackPressed()
                ll_cheering -> {

                }
                iv_comment -> {
                    val intent = Intent(context!!, ActivityComment::class.java)
                    intent.putExtra(
                        ActivityComment.EXTRA_VIEW_TYPE,
                        ActivityComment.EXTRA_TYPE_ACTION_POST
                    )
                    intent.putExtra(ActivityComment.EXTRA_INDEX, mPostIdx)
                    intent.putExtra(ActivityComment.EXTRA_OFF_KEYBOARD, " ")
                    startActivity(intent)
                }
                ll_comment, ll_comment_detail -> {
                    val intent = Intent(context!!, ActivityComment::class.java)
                    intent.putExtra(
                        ActivityComment.EXTRA_VIEW_TYPE,
                        ActivityComment.EXTRA_TYPE_ACTION_POST
                    )
                    intent.putExtra(ActivityComment.EXTRA_INDEX, mPostIdx)
                    startActivity(intent)
                }
                ll_share -> {

                }
                iv_more -> {

                }
            }
        }
        iv_back_black.setOnClickListener(listener)
        ll_cheering.setOnClickListener(listener)
        iv_comment.setOnClickListener(listener)
        ll_comment.setOnClickListener(listener)
        ll_comment_detail.setOnClickListener(listener)
        ll_share.setOnClickListener(listener)
        iv_more.setOnClickListener(listener)
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
                tv_indicator.text = ((position + 1).toString() + " / " + mAdapter!!.getCount())
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
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()

                    if (code == DAClient.SUCCESS) {
                        val json = JSONObject(body)
                        val profile = json.getJSONObject("profile")
                        val value_style = profile.getString("value_style")
                        val job = profile.getString("job")

                        tv_value_style.text = value_style
                        tv_job.text = job

                        val actionPost = json.getJSONObject("action_post")
                        val bean = Gson().fromJson<BeanActionPostDetail>(
                            actionPost.toString(),
                            BeanActionPostDetail::class.java
                        )

                        try {
                            mAdapter!!.clear()
                            val images = actionPost.getJSONArray("images")
                            for (i in 0 until images.length()) {
                                val image = images.getJSONObject(i)
                                val bean = Gson().fromJson<BeanActionPostImage>(
                                    image.toString(),
                                    BeanActionPostImage::class.java
                                )
                                mAdapter!!.add(bean.image_url)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

//                        mBean = bean
//                        setData(bean)
                    }
                }
            }
        })
    }

    /**
     * Data binding
     */
    private fun setData() {
//        val ivProfile = h.getItemView<ImageView>(R.id.iv_dream_profile)
//        val ivMore = h.getItemView<ImageView>(R.id.iv_action_more)
//        val tvValueStyle = h.getItemView<TextView>(R.id.tv_value_style)
//        val tvJob = h.getItemView<TextView>(R.id.tv_job)
//        val llObjectStep = h.getItemView<LinearLayout>(R.id.ll_object_step)
//        val ivCircle = h.getItemView<ImageView>(R.id.iv_circle)
//        val tvObject = h.getItemView<TextView>(R.id.tv_object)
//        val llStepLine = h.getItemView<LinearLayout>(R.id.ll_step_line)
//        val tvArrow = h.getItemView<TextView>(R.id.tv_arrow)
//        val tvStep = h.getItemView<TextView>(R.id.tv_step)
//        val rlImages = h.getItemView<RelativeLayout>(R.id.rl_images)
//        // todo : 여기는 썸네일이 아닌 모든 데이터가 나와야 하는게 아닌가 ?
//        val pagerImages = h.getItemView<ViewPager>(R.id.pager_image)
//        val llIndicator = h.getItemView<LinearLayout>(R.id.ll_indicator)
//        val tvIndicator = h.getItemView<TextView>(R.id.tv_indicator)
//        val tvContents = h.getItemView<TextView>(R.id.tv_contents)
//        val ivSideImg = h.getItemView<ImageView>(R.id.iv_side_img)
//        val tvCheering = h.getItemView<TextView>(R.id.tv_cheering)
//        val ivComment = h.getItemView<ImageView>(R.id.iv_comment)
//        val tvComment = h.getItemView<TextView>(R.id.tv_comment)
//        // todo : 좋아요 및 나머지 추후 연동
//        val llCheering = h.getItemView<LinearLayout>(R.id.ll_cheering)
//        val ivCheering = h.getItemView<ImageView>(R.id.iv_cheering)
//        val llComment = h.getItemView<LinearLayout>(R.id.ll_comment)
//        val llShare = h.getItemView<LinearLayout>(R.id.ll_share)
//        val tvTime = h.getItemView<TextView>(R.id.tv_time)
    }


}