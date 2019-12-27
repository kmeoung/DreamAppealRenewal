package com.truevalue.dreamappeal.fragment.timeline

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.*
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.bean.BeanTimeline
import com.truevalue.dreamappeal.fragment.profile.FragmentProfile
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_timeline.*
import kotlinx.android.synthetic.main.fragment_timeline.*
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException


class FragmentTimeline : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private var mAdatper: BaseRecyclerViewAdapter? = null

    private val RV_TYPE_TIMELINE = 0
    private val RV_TYPE_TIMELINE_MORE = 1

    private var isLast = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_timeline, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init View
        initView()
        // Init Adapter
        initAdapter()
        // View OnClick Listener
        onClickView()
        // init Data
        isLast = false
        getTimeLineData(false, -1, true)
    }

    /**
     * View 초기화
     */
    private fun initView() {
        Utils.setSwipeRefreshLayout(srl_refresh, this)
    }

    /**
     * Init RecyclerView Adapter
     */
    private fun initAdapter() {
        mAdatper = BaseRecyclerViewAdapter(rvListener)
        rv_timeline.adapter = mAdatper
        rv_timeline.layoutManager = LinearLayoutManager(context!!)

        /*rv_timeline.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!rv_timeline.canScrollVertically(-1)) {
                } else if (!rv_timeline.canScrollVertically(1)) {
                    if(!isLast){
                        getTimeLineData(
                            true,
                            (mAdatper!!.get(mAdatper!!.size() - 1) as BeanTimeline).idx,
                            false
                        )
                    }
                } else {

                }
            }
        })*/
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_search -> {
                    val intent = Intent(context!!, ActivitySearch::class.java)
                    startActivityForResult(intent, ActivitySearch.REQUEST_REPLACE_USER_IDX)
                }
                ll_timeline -> {
                    rv_timeline.smoothScrollToPosition(0)
                }
            }
        }
        iv_search.setOnClickListener(listener)
        ll_timeline.setOnClickListener(listener)
    }

    /**
     * Http
     * TimeLine
     */
    private fun getTimeLineData(refresh: Boolean, last_idx: Int, isClear: Boolean) {
        DAClient.getTimeLine(refresh, last_idx, object : DAHttpCallback {
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
                if (context != null) {
                    srl_refresh.isRefreshing = false

                    if (code == DAClient.SUCCESS) {
                        val json = JSONObject(body)
                        if (isClear) mAdatper!!.clear()
                        try {
                            val posts = json.getJSONArray("posts")

                            if (posts.length() < 1) {
                                isLast = true
                                mAdatper!!.notifyDataSetChanged()
                            }
                            for (i in 0 until posts.length()) {

                                val post = posts.getJSONObject(i)
                                val images = post.getJSONArray("images")

                                val bean = Gson().fromJson<BeanTimeline>(
                                    post.toString(),
                                    BeanTimeline::class.java
                                )

                                var imageList = ArrayList<String>()

                                for (j in 0 until images.length()) {

                                    val image = images.getJSONObject(j)

                                    val url = image.getString("url")
                                    imageList.add(url)
                                }
                                bean.imageList = imageList
                                mAdatper!!.add(bean)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }else{
                        if(code == "NO_MORE_POST"){
                            isLast = true
                            mAdatper!!.notifyDataSetChanged()
                        }else if(code == DAClient.FAIL){
                            ActivityCompat.finishAffinity(activity!!)
                            val intent = Intent(context!!,ActivityIntro::class.java)
                            Comm_Prefs.setUserProfileIndex(-1)
                            Comm_Prefs.setToken(null)
                            startActivity(intent)
                        }
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
            get() = if (mAdatper != null) if(mAdatper!!.size() > 4 && !isLast) mAdatper!!.size() + 1 else mAdatper!!.size()  else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            if (RV_TYPE_TIMELINE == viewType) {
                return BaseViewHolder.newInstance(R.layout.listitem_timeline, parent, false)
            } else if (RV_TYPE_TIMELINE_MORE == viewType) {
                return BaseViewHolder.newInstance(R.layout.listitem_timeline_more, parent, false)
            }
            return BaseViewHolder.newInstance(R.layout.listitem_timeline, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            if (RV_TYPE_TIMELINE == getItemViewType(i)) {
                onTimelineBindViewHolder(h, i)
            } else if (RV_TYPE_TIMELINE_MORE == getItemViewType(i)) {
                getTimeLineData(
                    true,
                    (mAdatper!!.get(mAdatper!!.size() - 1) as BeanTimeline).idx,
                    false
                )
            }
        }

        override fun getItemViewType(i: Int): Int {
            if (mAdatper!!.size() > 4 && mAdatper!!.size() == i && !isLast) {
                return RV_TYPE_TIMELINE_MORE
            }
            return RV_TYPE_TIMELINE
        }

        /**
         * Timeline Bind View Holder
         */
        private fun onTimelineBindViewHolder(h: BaseViewHolder, i: Int) {
            val bean = mAdatper!!.get(i) as BeanTimeline
            val ivProfile = h.getItemView<ImageView>(R.id.iv_dream_profile)
            val ivMore = h.getItemView<ImageView>(R.id.iv_action_more)
            val tvValueStyle = h.getItemView<TextView>(R.id.tv_value_style)
            val tvJob = h.getItemView<TextView>(R.id.tv_job)
            val llObjectStep = h.getItemView<LinearLayout>(R.id.ll_object_step)
            val ivCircle = h.getItemView<ImageView>(R.id.iv_circle)
            val tvObject = h.getItemView<TextView>(R.id.tv_object)
            val llStepLine = h.getItemView<LinearLayout>(R.id.ll_step_line)
            val tvArrow = h.getItemView<TextView>(R.id.tv_arrow)
            val tvStep = h.getItemView<TextView>(R.id.tv_step)
            val rlImages = h.getItemView<RelativeLayout>(R.id.rl_images)
            val pagerImages = h.getItemView<ViewPager>(R.id.pager_image)
            val llIndicator = h.getItemView<LinearLayout>(R.id.ll_indicator)
            val tvIndicator = h.getItemView<TextView>(R.id.tv_indicator)
            val tvContents = h.getItemView<TextView>(R.id.tv_contents)
            val ivSideImg = h.getItemView<ImageView>(R.id.iv_side_img)
            val tvCheering = h.getItemView<TextView>(R.id.tv_cheering)
            val ivComment = h.getItemView<ImageView>(R.id.iv_comment)
            val tvComment = h.getItemView<TextView>(R.id.tv_comment)
            val llCheering = h.getItemView<LinearLayout>(R.id.ll_cheering)
            val ivCheering = h.getItemView<ImageView>(R.id.iv_cheering)
            val llComment = h.getItemView<LinearLayout>(R.id.ll_comment)
            val llShare = h.getItemView<LinearLayout>(R.id.ll_share)
            val tvTime = h.getItemView<TextView>(R.id.tv_time)
            val llCheeringDetail = h.getItemView<LinearLayout>(R.id.ll_cheering_detail)

            val pagerAdapter = BasePagerAdapter<String>(context!!)
            pagerImages.adapter = pagerAdapter
            pagerImages.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tvIndicator.text =
                        ((position + 1).toString() + " / " + pagerAdapter!!.getCount())
                }
            })

            if(bean.profile_idx == Comm_Prefs.getUserProfileIndex()){
                ivMore.visibility = VISIBLE
            }else ivMore.visibility = GONE

            tvIndicator.text = ((1).toString() + " / " + bean.imageList.size)
            for (j in 0 until bean.imageList.size) {
                pagerAdapter.add(bean.imageList[j])
            }
            pagerAdapter.notifyDataSetChanged()

            when (bean.post_type) {
                0 -> {
                    ivCircle.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_circle_blue
                        )
                    )
                    tvArrow.setTextColor(ContextCompat.getColor(context!!, R.color.main_blue))
                    ivSideImg.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_side_blue
                        )
                    )
                }
                1 -> {
                    ivCircle.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_circle_green
                        )
                    )
                    tvArrow.setTextColor(ContextCompat.getColor(context!!, R.color.asparagus))
                    ivSideImg.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_side_green
                        )
                    )
                }
                2 -> {
                    ivCircle.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_circle_yellow
                        )
                    )
                    tvArrow.setTextColor(ContextCompat.getColor(context!!, R.color.yellow_orange))
                    ivSideImg.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_side_yellow
                        )
                    )
                }
            }

            if (!bean.profile_image.isNullOrEmpty()) {
                Glide.with(context!!)
                    .load(bean.profile_image)
                    .placeholder(R.drawable.drawer_user)
                    .circleCrop()
                    .thumbnail(0.1f)
                    .into(ivProfile)
            }

            if (bean.profile_idx != Comm_Prefs.getUserProfileIndex()) {
                ivProfile.setOnClickListener(View.OnClickListener {
                    (activity as ActivityMain).replaceFragment(
                        FragmentProfile.newInstance(bean.profile_idx),
                        true
                    )
                })
            }

            ivMore.setOnClickListener(View.OnClickListener {
                showPopupMenu(ivMore, bean)
            })

            tvValueStyle.text = if (bean.value_style.isNullOrEmpty()) "" else bean.value_style
            tvJob.text = if (bean.job.isNullOrEmpty()) "" else bean.job
            if (bean.object_title.isNullOrEmpty() && bean.step_title.isNullOrEmpty()) {
                llObjectStep.visibility = GONE
            } else {
                llObjectStep.visibility = VISIBLE
                tvObject.text = bean.object_title

                if (bean.step_title.isNullOrEmpty()) {
                    llStepLine.visibility = GONE
                } else {
                    tvStep.text = bean.step_title
                    llStepLine.visibility = VISIBLE
                }
            }

            Utils.setImageViewSquare(context!!, rlImages)

            tvTime.text = Utils.convertFromDate(bean.register_date)

            tvContents.text = bean.content

            tvCheering.text = "${bean.like_count}개"
            tvComment.text = "${bean.comment_count}개"

            // todo : 여기 bean.post_type 에 맞게 해야함 서버관련 문제 때문에 상의 후 진행
            ivComment.setOnClickListener(View.OnClickListener {
                val intent = Intent(context!!, ActivityComment::class.java)
                intent.putExtra(
                    ActivityComment.EXTRA_VIEW_TYPE,
                    ActivityComment.EXTRA_TYPE_ACTION_POST
                )
                intent.putExtra(ActivityComment.EXTRA_INDEX, bean.idx)
                intent.putExtra(ActivityComment.EXTRA_OFF_KEYBOARD, " ")
                startActivityForResult(intent, ActivityComment.REQUEST_REPLACE_USER_IDX)
            })
            llComment.setOnClickListener(View.OnClickListener {
                val intent = Intent(context!!, ActivityComment::class.java)
                intent.putExtra(
                    ActivityComment.EXTRA_VIEW_TYPE,
                    ActivityComment.EXTRA_TYPE_ACTION_POST
                )
                intent.putExtra(ActivityComment.EXTRA_INDEX, bean.idx)
                startActivityForResult(intent, ActivityComment.REQUEST_REPLACE_USER_IDX)
            })

            llCheering.setOnClickListener(View.OnClickListener {
                actionLike(bean)
            })

            llCheeringDetail.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, ActivityFollowCheering::class.java)
                intent.putExtra(
                    ActivityFollowCheering.EXTRA_VIEW_TYPE,
                    ActivityFollowCheering.VIEW_TYPE_CHEERING_ACTION
                )
                intent.putExtra(ActivityFollowCheering.REQUEST_VIEW_LIST_IDX, bean.idx)
                startActivityForResult(intent, ActivityFollowCheering.REQUEST_REPLACE_USER_IDX)
            })

            ivCheering.isSelected = bean.status

        }
    }

    /**
     * Show PopupMenu
     */
    private fun showPopupMenu(ivMore: View, bean: BeanTimeline) {
        val popupMenu = PopupMenu(context!!, ivMore)
        popupMenu.menu.add(getString(R.string.str_edit))
        popupMenu.menu.add(getString(R.string.str_delete))

        popupMenu.setOnMenuItemClickListener {
            when (it.title) {
                getString(R.string.str_edit) -> {
                    val intent = Intent(context!!, ActivityAddPost::class.java)
                    intent.putExtra(
                        ActivityAddPost.EDIT_VIEW_TYPE,
                        ActivityAddPost.EDIT_ACTION_POST
                    )
                    intent.putExtra(ActivityAddPost.EDIT_POST_IDX, bean.idx)
                    intent.putExtra(ActivityAddPost.REQUEST_IAMGE_FILES, bean.imageList)
                    intent.putExtra(ActivityAddPost.REQUEST_CONTENTS, bean!!.content)
                    startActivity(intent)
                }
                getString(R.string.str_delete) -> {
                    val builder =
                        AlertDialog.Builder(context!!)
                            .setTitle(getString(R.string.str_delete_post_title))
                            .setMessage(getString(R.string.str_delete_post_contents))
                            .setPositiveButton(
                                getString(R.string.str_yes)
                            ) { dialog, _ ->
                                deletePost(bean)
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
     * Post 삭제
     */
    fun deletePost(bean: BeanTimeline) {
        DAClient.deleteActionPostsDetail(bean.idx, object : DAHttpCallback {
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
                        mAdatper!!.remove(bean)
                        mAdatper!!.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ActivityComment.REQUEST_REPLACE_USER_IDX ||
                requestCode == ActivitySearch.REQUEST_REPLACE_USER_IDX) {

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
    private fun actionLike(bean: BeanTimeline) {
        DAClient.likeActionPost(bean.idx, object : DAHttpCallback {
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
                        bean.status = status
                        val count = json.getInt("count")
                        bean.like_count = count
                        mAdatper!!.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    /**
     * On Refresh
     */
    override fun onRefresh() {
        isLast = false
        getTimeLineData(false, -1, true)
    }
}