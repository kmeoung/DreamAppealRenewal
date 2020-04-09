package com.truevalue.dreamappeal.fragment.timeline.appeal

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.*
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.base_new.fragment.BaseTabFragment
import com.truevalue.dreamappeal.base_new.viewmodel.EmptyViewModel
import com.truevalue.dreamappeal.bean.BeanTimeline
import com.truevalue.dreamappeal.fragment.profile.FragmentProfile
import com.truevalue.dreamappeal.fragment.profile.blueprint.FragmentActionPost
import com.truevalue.dreamappeal.fragment.profile.blueprint.FragmentAddActionPost
import com.truevalue.dreamappeal.fragment.timeline.appeal.connect_friend.facebook.FacebookContactActivity
import com.truevalue.dreamappeal.fragment.timeline.appeal.connect_friend.kakao.KakaoContactActivity
import com.truevalue.dreamappeal.fragment.timeline.appeal.connect_friend.kakao.KakaoContactViewModel
import com.truevalue.dreamappeal.fragment.timeline.appeal.connect_friend.phone.PhoneContactActivity
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.*
import kotlinx.android.synthetic.main.bottom_main_view.*
import kotlinx.android.synthetic.main.fragment_timeline.*
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException


class FragmentTimeline : BaseTabFragment<EmptyViewModel>(), SwipeRefreshLayout.OnRefreshListener {
    override val classViewModel: Class<EmptyViewModel> = EmptyViewModel::class.java
    override val layoutId: Int = R.layout.fragment_timeline
//    private var mAdapter: BaseRecyclerViewAdapter? = null

    companion object {
        private const val EXTRA_CHANGE_CATEGORY = 3030

        private const val RV_TYPE_TIMELINE = 0
        private const val RV_TYPE_TIMELINE_MORE = 1

        private const val RV_TYPE_BEST_POST = 2
        private const val RV_TYPE_ABILITY = 3
        private const val RV_TYPE_DREAM_DESCRIPTION = 4
        private const val RV_TYPE_MERIT_AND_MOTIVE = 5
        private const val RV_TYPE_DREAM_TITLE = 6
        private const val RV_TYPE_OBJECT = 7
        private const val RV_TYPE_OBJECT_COMPLETE = 8
        private const val RV_TYPE_OPPORTUNITY = 9

        fun newInstance(): FragmentTimeline {
            return FragmentTimeline()
        }
    }

    private var isLast = false
    private var mAdapter: TimeLineDataAdapter? = null

    override fun onFirstRender() {
        setupUI()


//        // init View
//        initView()
//        // Init Adapter
//        initAdapter()
//        // View OnClick Listener
//        onClickView()
//        // init Data
//        isLast = false
//        getTimeLineData(false, -1, true)
    }

    private fun setupUI() {
        tvOpenPhoneContact.setOnClickListener {
            startActivityForResult(
                Intent(context, PhoneContactActivity::class.java),
                Constants.PHONE_CODE
            )
        }
        tvOpenKakao.setOnClickListener {
            startActivityForResult(
                Intent(context, KakaoContactActivity::class.java),
                Constants.FB_CODE
            )
        }
        tvOpenFacebook.setOnClickListener {
            startActivityForResult(
                Intent(context, FacebookContactActivity::class.java),
                Constants.FB_CODE
            )
        }
        srlRefresh.gone()

        rvPosts.apply {
            mAdapter = TimeLineDataAdapter(layoutInflater)
            adapter = mAdapter
            setHasFixedSize(true)
        }
        mAdapter?.setDataSource(
            listOf(
                TimeLineData(),
                TimeLineData(),
                TimeLineData(),
                TimeLineData()
            )
        )
    }

    override fun onRefresh() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Constants.PHONE_CODE, Constants.FB_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    srlRefresh.visible()
                    llEmpty.gone()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

//    /**
//     * View 초기화
//     */
//    private fun initView() {
//        Utils.setSwipeRefreshLayout(srl_refresh, this)
//    }

    /**
     * Init RecyclerView Adapter
     */
//    private fun initAdapter() {
//        mAdapter = BaseRecyclerViewAdapter(rvListener)
//        rv_timeline.adapter = mAdapter
//        rv_timeline.layoutManager = LinearLayoutManager(context!!)
//    }

    /**
    //     * View Click Listener
    //     */
//    private fun onClickView() {
////        val listener = View.OnClickListener {
////            when (it) {
////                ll_timeline -> {
////                    rv_timeline.smoothScrollToPosition(0)
////                }
////            }
////        }
////        ll_timeline.setOnClickListener(listener)
//    }
//
//    /**
//     * Http
//     * TimeLine
//     */
////    private fun getTimeLineData(refresh: Boolean, last_idx: Int, isClear: Boolean) {
////        DAClient.getTimeLine(refresh, last_idx, object : DAHttpCallback {
////            override fun onFailure(call: Call, e: IOException) {
////                super.onFailure(call, e)
////                srl_refresh?.run {
////                    isRefreshing = false
////                }
////            }
////
////            override fun onResponse(
////                call: Call,
////                serverCode: Int,
////                body: String,
////                code: String,
////                message: String
////            ) {
////                if (context != null) {
////                    srl_refresh?.run {
////                        isRefreshing = false
////
////                        if (code == DAClient.SUCCESS) {
////                            val json = JSONObject(body)
////                            if (isClear) mAdapter!!.clear()
////                            val notiCount = json.getInt("unconfirmed_alert_count")
////                            val tvNoti = (activity as ActivityMain).tv_notification
////
////                            if (notiCount > 0) {
////                                tvNoti.text = notiCount.toString()
////                                tvNoti.visibility = VISIBLE
////                            } else {
////                                tvNoti.visibility = GONE
////                            }
////                            try {
////                                val posts = json.getJSONArray("posts")
////
////                                if (posts.length() < 1) {
////                                    isLast = true
////                                    mAdapter!!.notifyDataSetChanged()
////                                }
////                                for (i in 0 until posts.length()) {
////
////                                    val post = posts.getJSONObject(i)
////
////                                    val bean = Gson().fromJson<BeanTimeline>(
////                                        post.toString(),
////                                        BeanTimeline::class.java
////                                    )
////
////                                    mAdapter!!.add(bean)
////                                }
////
////                                if (mAdapter!!.size() < 1) {
////                                    ll_no_data.visibility = VISIBLE
////                                } else {
////                                    ll_no_data.visibility = GONE
////                                }
////                            } catch (e: Exception) {
////                                e.printStackTrace()
////                            }
////
////                        } else {
////                            if (code == DAClient.NO_MORE_POST) {
////                                isLast = true
////                                mAdapter!!.notifyDataSetChanged()
////                            } else if (code == DAClient.FAIL) {
////                                ActivityCompat.finishAffinity(activity!!)
////                                val intent =
////                                    Intent(context!!, ActivityIntro::class.java)
////                                Comm_Prefs.allReset()
////                                startActivity(intent)
////                            }
////                            Toast.makeText(
////                                context!!.applicationContext,
////                                message,
////                                Toast.LENGTH_SHORT
////                            )
////                                .show()
////                        }
////                    }
////                }
////            }
////        })
////    }
//
//    /**
//     * RecyclerView Listener
//     */
//    private val rvListener = object : IORecyclerViewListener {
//        override val itemCount: Int
//            get() = if (mAdapter != null) if (mAdapter!!.size() > 4 && !isLast) mAdapter!!.size() + 1 else mAdapter!!.size() else 0
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
//
//            when (viewType) {
//                RV_TYPE_TIMELINE ->
//                    return BaseViewHolder.newInstance(R.layout.listitem_timeline, parent, false)
//                RV_TYPE_TIMELINE_MORE ->
//                    return BaseViewHolder.newInstance(R.layout.listitem_white_more, parent, false)
//                RV_TYPE_ABILITY ->
//                    return BaseViewHolder.newInstance(R.layout.listitem_noti_ability, parent, false)
//                RV_TYPE_BEST_POST ->
//                    return BaseViewHolder.newInstance(
//                        R.layout.listitem_noti_best_post,
//                        parent,
//                        false
//                    )
//                RV_TYPE_DREAM_DESCRIPTION ->
//                    return BaseViewHolder.newInstance(
//                        R.layout.listitem_noti_dream_description,
//                        parent,
//                        false
//                    )
//                RV_TYPE_DREAM_TITLE ->
//                    return BaseViewHolder.newInstance(
//                        R.layout.listitem_noti_dream_title,
//                        parent,
//                        false
//                    )
//                RV_TYPE_MERIT_AND_MOTIVE ->
//                    return BaseViewHolder.newInstance(
//                        R.layout.listitem_noti_dream_merit_and_motive,
//                        parent,
//                        false
//                    )
//                RV_TYPE_OBJECT ->
//                    return BaseViewHolder.newInstance(R.layout.listitem_noti_object, parent, false)
//                RV_TYPE_OBJECT_COMPLETE ->
//                    return BaseViewHolder.newInstance(
//                        R.layout.listitem_noti_object_complete,
//                        parent,
//                        false
//                    )
//                RV_TYPE_OPPORTUNITY ->
//                    return BaseViewHolder.newInstance(
//                        R.layout.listitem_noti_opportunity,
//                        parent,
//                        false
//                    )
//                else ->
//                    return BaseViewHolder.newInstance(R.layout.listitem_timeline, parent, false)
//            }
//        }
//
//        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
//
//            when (getItemViewType(i)) {
//                RV_TYPE_TIMELINE ->
//                    onTimelineBindViewHolder(h, i)
//                RV_TYPE_TIMELINE_MORE -> {
//                    (mAdapter!!.get(mAdapter!!.size() - 1) as BeanTimeline).idx?.let {
//                        getTimeLineData(
//                            true,
//                            it,
//                            false
//                        )
//                    }
//                }
//                RV_TYPE_BEST_POST -> {
//                    val bean = mAdapter?.get(i) as BeanTimeline
//                    val tvName = h.getItemView<TextView>(R.id.tv_name)
//                    val tvTitle = h.getItemView<TextView>(R.id.tv_title)
//                    val name =
//                        if (!bean.contents_bold.isNullOrEmpty()) {
//                            if (bean.contents_bold.length > 24) {
//                                "${bean.contents_bold.subSequence(
//                                    0,
//                                    24
//                                )}..."
//                            } else bean.contents_bold
//                        } else ""
//                    tvName.text = name
//                    bean.title?.let { title ->
//                        tvTitle.text = title
//                    }
//                    val tvGoView = h.getItemView<TextView>(R.id.tv_go_view)
//                    tvGoView.setOnClickListener {
//                        bean.item_idx?.let { item_idx ->
//                            (activity as ActivityMain).replaceFragment(
//                                FragmentProfile.newInstance(item_idx),
//                                true
//                            )
//                        }
//                    }
//                    val tvTime = h.getItemView<TextView>(R.id.tv_time)
//                    tvTime.text = Utils.convertFromDate(bean.register_date)
//                }
//                RV_TYPE_DREAM_TITLE -> {
//                    val bean = mAdapter?.get(i) as BeanTimeline
//                    val tvName = h.getItemView<TextView>(R.id.tv_name)
//                    val ivProfile = h.getItemView<ImageView>(R.id.iv_profile)
//                    val tvValueStyle = h.getItemView<TextView>(R.id.tv_value_style)
//                    val tvJob = h.getItemView<TextView>(R.id.tv_job)
//                    val tvTime = h.getItemView<TextView>(R.id.tv_time)
//                    tvTime.text = Utils.convertFromDate(bean.register_date)
//
//                    if (!bean.profile_image.isNullOrEmpty()) {
//                        Glide.with(context!!)
//                            .load(bean.profile_image)
//                            .circleCrop()
//                            .placeholder(R.drawable.drawer_user)
//                            .into(ivProfile)
//                    } else {
//                        Glide.with(context!!)
//                            .load(R.drawable.drawer_user)
//                            .circleCrop()
//                            .into(ivProfile)
//                    }
//
//                    if (!bean.value_style.isNullOrEmpty()) tvValueStyle.text = bean.value_style
//                    if (!bean.job.isNullOrEmpty()) tvJob.text = bean.job
//                    val name =
//                        if (!bean.contents_bold.isNullOrEmpty()) {
//                            if (bean.contents_bold.length > 24) {
//                                "${bean.contents_bold.subSequence(
//                                    0,
//                                    24
//                                )}..."
//                            } else bean.contents_bold
//                        } else ""
//                    tvName.text = name
//                    val tvGoView = h.getItemView<TextView>(R.id.tv_go_view)
//                    tvGoView.setOnClickListener {
//                        bean.item_idx?.let { item_idx ->
//                            (activity as ActivityMain).replaceFragment(
//                                FragmentProfile.newInstance(item_idx),
//                                true
//                            )
//                        }
//                    }
//                }
//                RV_TYPE_ABILITY,
//                RV_TYPE_DREAM_DESCRIPTION,
//                RV_TYPE_MERIT_AND_MOTIVE,
//                RV_TYPE_OBJECT,
//                RV_TYPE_OBJECT_COMPLETE,
//                RV_TYPE_OPPORTUNITY -> {
//                    val bean = mAdapter?.get(i) as BeanTimeline
//                    val tvName = h.getItemView<TextView>(R.id.tv_name)
//                    val name =
//                        if (!bean.contents_bold.isNullOrEmpty()) {
//                            if (bean.contents_bold.length > 24) {
//                                "${bean.contents_bold.subSequence(
//                                    0,
//                                    24
//                                )}..."
//                            } else bean.contents_bold
//                        } else ""
//                    tvName.text = name
//                    val tvGoView = h.getItemView<TextView>(R.id.tv_go_view)
//                    val tvTime = h.getItemView<TextView>(R.id.tv_time)
//                    tvTime.text = Utils.convertFromDate(bean.register_date)
//                    tvGoView.setOnClickListener {
//                        bean.item_idx?.let { item_idx ->
//                            (activity as ActivityMain).replaceFragment(
//                                FragmentProfile.newInstance(item_idx),
//                                true
//                            )
//                        }
//                    }
//                }
//            }
//        }
//
//        override fun getItemViewType(i: Int): Int {
//            if (mAdapter!!.size() > 4 && mAdapter!!.size() == i && !isLast) {
//                return RV_TYPE_TIMELINE_MORE
//            } else {
//                return when ((mAdapter?.get(i) as BeanTimeline).code) {
//                    Noti_Param.ABILITY -> RV_TYPE_ABILITY
//                    Noti_Param.PROFILE_ACHIEVEMENT_POST -> RV_TYPE_BEST_POST
//                    Noti_Param.PROFILE_DESCRIPTION -> RV_TYPE_DREAM_DESCRIPTION
//                    Noti_Param.PROFILE_VALUE_STYLE_JOB -> RV_TYPE_DREAM_TITLE
//                    Noti_Param.PROFILE_MERIT_MOTIVE -> RV_TYPE_MERIT_AND_MOTIVE
//                    Noti_Param.PROFILE_OBJECT -> RV_TYPE_OBJECT
//                    Noti_Param.COMPLETE_PROFILE_OBJECT -> RV_TYPE_OBJECT_COMPLETE
//                    Noti_Param.OPPORTUNITY -> RV_TYPE_OPPORTUNITY
//                    else -> RV_TYPE_TIMELINE
//                }
//            }
//            return RV_TYPE_TIMELINE
//        }
//
//        /**
//         * Timeline Bind View Holder
//         */
//        private fun onTimelineBindViewHolder(h: BaseViewHolder, i: Int) {
//            val bean = mAdapter!!.get(i) as BeanTimeline
//            val ivProfile = h.getItemView<ImageView>(R.id.iv_dream_profile)
//            val llDreamTitle = h.getItemView<LinearLayout>(R.id.ll_dream_title)
//            val ivMore = h.getItemView<ImageView>(R.id.iv_action_more)
//            val tvValueStyle = h.getItemView<TextView>(R.id.tv_value_style)
//            val tvJob = h.getItemView<TextView>(R.id.tv_job)
//            val llObjectStep = h.getItemView<LinearLayout>(R.id.ll_object_step)
//            val ivCircle = h.getItemView<ImageView>(R.id.iv_circle)
//            val tvObject = h.getItemView<TextView>(R.id.tv_object)
//            val llStepLine = h.getItemView<LinearLayout>(R.id.ll_step_line)
//            val tvStep = h.getItemView<TextView>(R.id.tv_step)
//            val rlImages = h.getItemView<RelativeLayout>(R.id.rl_images)
//            val pagerImages = h.getItemView<ViewPager>(R.id.pager_image)
//            val llIndicator = h.getItemView<LinearLayout>(R.id.ll_indicator)
//            val tvIndicator = h.getItemView<TextView>(R.id.tv_indicator)
//            val tvContents = h.getItemView<TextView>(R.id.tv_contents)
//            val ivSideImg = h.getItemView<ImageView>(R.id.iv_side_img)
//            val tvCheering = h.getItemView<TextView>(R.id.tv_cheering)
//            val ivComment = h.getItemView<ImageView>(R.id.iv_comment)
//            val tvComment = h.getItemView<TextView>(R.id.tv_comment)
//            val llCheering = h.getItemView<LinearLayout>(R.id.ll_cheering)
//            val ivCheering = h.getItemView<ImageView>(R.id.iv_cheering)
//            val llComment = h.getItemView<LinearLayout>(R.id.ll_comment)
//            val llShare = h.getItemView<LinearLayout>(R.id.ll_share)
//            val tvTime = h.getItemView<TextView>(R.id.tv_time)
//            val llCheeringDetail = h.getItemView<LinearLayout>(R.id.ll_cheering_detail)
//            val llCommentDetail = h.getItemView<LinearLayout>(R.id.ll_comment_detail)
//            val tvTag = h.getItemView<TextView>(R.id.tv_tag)
//            val llOriginUser = h.getItemView<LinearLayout>(R.id.ll_origin_user)
//            val tvOriginUser = h.getItemView<TextView>(R.id.tv_origin_user)
//
//            llOriginUser.visibility = if (bean.copied == 1) {
//                tvOriginUser.text = if (bean.origin_post_writer != null) {
//                    bean.origin_post_writer.let {
//                        val user = "${it.value_style ?: ""} ${it.job ?: ""} ${it.nickname ?: ""}"
//                        "${if (user.length < 31) user else "${user.subSequence(
//                            0,
//                            30
//                        )}..."}님의 게시물입니다"
//                    }
//                } else {
//                    "퍼온 게시물입니다"
//                }
//                VISIBLE
//            } else {
//                GONE
//            }
//
//
//            val pagerAdapter = BaseImagePagerAdapter<String>(context!!)
//            pagerImages.adapter = pagerAdapter
//            pagerImages.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
//                override fun onPageSelected(position: Int) {
//                    super.onPageSelected(position)
//                    if (pagerAdapter.count > 1) {
//                        tvIndicator.text =
//                            ((position + 1).toString() + " / " + pagerAdapter.count)
//                    }
//                }
//            })
//
//
//            var strTags = ""
//            if (!bean.tags.isNullOrEmpty()) {
//                tvTag.visibility = VISIBLE
//                if (bean.tags!!.contains(",".toRegex())) {
//
//                    val tags = bean.tags!!.split(",".toRegex())
//                    for (i in tags.indices) {
//                        strTags = "$strTags #${tags[i]} "
//                    }
//                } else {
//                    strTags = " #${bean.tags!!}"
//                }
//            } else {
//                strTags = getString(R.string.str_tag)
//                tvTag.visibility = GONE
//            }
//            tvTag.text = strTags
//
//            bean.images?.let { imageList ->
//                if (imageList.size > 1) {
//                    llIndicator.visibility = VISIBLE
//                    tvIndicator.text =
//                        if (imageList.isNotEmpty()) ((1).toString() + " / " + imageList.size) else ((0).toString() + " / " + imageList.size)
//                } else {
//                    llIndicator.visibility = GONE
//                }
//                for (j in imageList.indices) {
//                    imageList[j].url?.let { url ->
//                        pagerAdapter.add(url)
//                    }
//                }
//            } ?: kotlin.run {
//                llIndicator.visibility = GONE
//            }
//            pagerAdapter.notifyDataSetChanged()
//
//
//            if (Comm_Prefs.getUserProfileIndex() == bean.profile_idx) ivMore.visibility =
//                VISIBLE
//            else ivMore.visibility = GONE
//
//            when (bean.post_type) {
//                FragmentActionPost.ACTION_POST -> {
//                    ivSideImg.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            context!!,
//                            R.drawable.ic_side_blue
//                        )
//                    )
//                }
//                FragmentActionPost.ACTION_LIFE -> {
//                    ivSideImg.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            context!!,
//                            R.drawable.ic_side_green
//                        )
//                    )
//                }
//                FragmentActionPost.ACTION_IDEA -> {
//                    ivSideImg.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            context!!,
//                            R.drawable.ic_side_yellow
//                        )
//                    )
//                }
//            }
//
//            if (!bean.profile_image.isNullOrEmpty()) {
//                Glide.with(context!!)
//                    .load(bean.profile_image)
//                    .placeholder(R.drawable.drawer_user)
//                    .circleCrop()
//                    .thumbnail(0.1f)
//                    .into(ivProfile)
//            }
//
//            if (bean.profile_idx != Comm_Prefs.getUserProfileIndex()) {
//
//                val changeProfileListener = View.OnClickListener {
//                    bean.profile_idx?.let { profile_idx ->
//                        (activity as ActivityMain).replaceFragment(
//                            FragmentProfile.newInstance(profile_idx),
//                            true
//                        )
//                    }
//                }
//
//                ivProfile.setOnClickListener(changeProfileListener)
//                llDreamTitle.setOnClickListener(changeProfileListener)
//            }
//
//            llShare.setOnClickListener {
//                showShareMenu(llShare, bean)
//            }
//
//            ivMore.setOnClickListener {
//                showMoreMenu(ivMore, bean)
//            }
//
//            tvValueStyle.text = if (bean.value_style.isNullOrEmpty()) "" else bean.value_style
//            tvJob.text =
//                if (bean.job.isNullOrEmpty()) "" else "${bean.job} ${if (bean.nickname.isNullOrEmpty()) "" else bean.nickname}"
//            if (bean.object_title.isNullOrEmpty() && bean.step_title.isNullOrEmpty()) {
//                llObjectStep.visibility = GONE
//            } else {
//                llObjectStep.visibility = VISIBLE
//                tvObject.text = bean.object_title
//
//                if (bean.step_title.isNullOrEmpty()) {
//                    llStepLine.visibility = GONE
//                } else {
//                    tvStep.text = bean.step_title
//                    llStepLine.visibility = VISIBLE
//                }
//            }
//
//            Utils.setImageViewSquare(context!!, rlImages)
//
//            tvTime.text = Utils.convertFromDate(bean.register_date ?: "")
//
//            tvContents.text = bean.content
//
//            tvCheering.text = bean.like_count.toString()
//            tvComment.text = "${bean.comment_count}개"
//
//            llCommentDetail.setOnClickListener {
//                val intent = Intent(context!!, ActivityComment::class.java)
//                intent.putExtra(
//                    ActivityComment.EXTRA_VIEW_TYPE,
//                    ActivityComment.EXTRA_TYPE_ACTION_POST
//                )
//                intent.putExtra(ActivityComment.EXTRA_INDEX, bean.idx)
//                intent.putExtra(ActivityComment.EXTRA_OFF_KEYBOARD, "OFF")
//                startActivityForResult(intent, ActivityComment.REQUEST_REPLACE_USER_IDX)
//            }
//            llComment.setOnClickListener {
//                val intent = Intent(context!!, ActivityComment::class.java)
//                intent.putExtra(
//                    ActivityComment.EXTRA_VIEW_TYPE,
//                    ActivityComment.EXTRA_TYPE_ACTION_POST
//                )
//                intent.putExtra(ActivityComment.EXTRA_INDEX, bean.idx)
//                startActivityForResult(intent, ActivityComment.REQUEST_REPLACE_USER_IDX)
//            }
//
//            llCheering.setOnClickListener {
//                actionLike(bean)
//            }
//
//            llCheeringDetail.setOnClickListener {
//                val intent = Intent(context, ActivitySFA::class.java)
//                intent.putExtra(
//                    ActivitySFA.EXTRA_VIEW_TYPE,
//                    ActivitySFA.VIEW_TYPE_CHEERING_ACTION
//                )
//                intent.putExtra(ActivitySFA.REQUEST_VIEW_LIST_IDX, bean.idx)
//                startActivityForResult(intent, ActivitySFA.REQUEST_REPLACE_USER_IDX)
//            }
//
//            ivCheering.isSelected = bean.status ?: false
//        }
//    }
//
//    /**
//     * Show More Menu
//     */
//    private fun showMoreMenu(ivMore: View, bean: BeanTimeline) {
//        val popupMenu = PopupMenu(context!!, ivMore)
//        if (bean.post_type == FragmentActionPost.ACTION_POST) popupMenu.menu.add(getString(R.string.str_edit_level))
//        // 퍼온 게시물은 수정 불가
//        if (bean.copied == 0) popupMenu.menu.add(getString(R.string.str_edit))
//        popupMenu.menu.add(getString(R.string.str_delete))
//
//        popupMenu.setOnMenuItemClickListener {
//            when (it.title) {
//                getString(R.string.str_edit_level) -> {
//                    val intent = Intent(context!!, ActivityAddPost::class.java)
//                    intent.putExtra(
//                        ActivityAddPost.EDIT_VIEW_TYPE,
//                        ActivityAddPost.EDIT_CHANGE_CATEGORY
//                    )
//                    intent.putExtra(ActivityAddPost.EDIT_POST_IDX, bean.idx)
//                    intent.putExtra(ActivityAddPost.REQUEST_CATEOGORY_IDX, bean.object_idx)
//                    intent.putExtra(ActivityAddPost.REQUEST_CATEOGORY_DETAIL_IDX, bean.step_idx)
//                    startActivityForResult(
//                        intent,
//                        EXTRA_CHANGE_CATEGORY
//                    )
//                }
//                getString(R.string.str_edit) -> {
//
//                    bean.images?.let { images ->
//                        val intent = Intent(context!!, ActivityAddPost::class.java)
//                        intent.putExtra(
//                            ActivityAddPost.EDIT_VIEW_TYPE,
//                            ActivityAddPost.EDIT_ACTION_POST
//                        )
//
//                        val array = ArrayList<String>()
//
//                        for (i in images.indices) {
//                            images[i].url?.let { url ->
//                                array.add(url)
//                            }
//                        }
//
//                        intent.putExtra(ActivityAddPost.EDIT_POST_IDX, bean.idx)
//                        intent.putExtra(ActivityAddPost.REQUEST_IAMGE_FILES, array)
//                        intent.putExtra(ActivityAddPost.REQUEST_CONTENTS, bean!!.content)
//                        intent.putExtra(ActivityAddPost.REQUEST_TAGS, bean!!.tags)
//                        startActivityForResult(
//                            intent,
//                            FragmentAddActionPost.REQUEST_TIMELINE_EDIT_SUCCESS
//                        )
//                    }
//
//                }
//                getString(R.string.str_delete) -> {
//                    val builder =
//                        AlertDialog.Builder(context!!)
//                            .setTitle(getString(R.string.str_delete_post_title))
//                            .setMessage(getString(R.string.str_delete_post_contents))
//                            .setPositiveButton(
//                                getString(R.string.str_yes)
//                            ) { dialog, _ ->
//                                deletePost(bean)
//                                dialog.dismiss()
//                            }
//                            .setNegativeButton(
//                                getString(R.string.str_no)
//                            ) { dialog, _ -> dialog.dismiss() }
//                    val dialog = builder.create()
//                    dialog.show()
//                }
//            }
//            false
//        }
//        popupMenu.show()
//    }
//
//    /**
//     * Show Share Menu
//     */
//    private fun showShareMenu(ivShare: View, bean: BeanTimeline) {
//        val popupMenu = PopupMenu(context!!, ivShare)
//
//        // Action Like 는 저장하기 X
//        if (bean.post_type != FragmentActionPost.ACTION_LIFE) {
//            popupMenu.menu.add(getString(R.string.str_save))
//        }
//        popupMenu.menu.add(getString(R.string.str_scrap))
//
//        popupMenu.setOnMenuItemClickListener {
//            when (it.title) {
//                getString(R.string.str_save) -> {
//                    bean.idx?.let { idx ->
//                        saveIdeaPost(idx)
//                    }
//                }
//                getString(R.string.str_scrap) -> {
//                    val intent = Intent(context!!, ActivitySFA::class.java)
//                    intent.putExtra(ActivitySFA.EXTRA_VIEW_TYPE, ActivitySFA.VIEW_TYPE_SCRAP)
//                    intent.putExtra(ActivitySFA.EXTRA_ITEM_INDEX, bean.idx)
//                    intent.putExtra(ActivitySFA.EXTRA_NOTI_CODE, Noti_Param.SHARE_ACTION)
//                    startActivity(intent)
//                }
//            }
//            false
//        }
//        popupMenu.show()
//    }
//
//
//    private fun saveIdeaPost(post_idx: Int) {
//        DAClient.saveIdeaPost(post_idx, object : DAHttpCallback {
//            override fun onResponse(
//                call: Call,
//                serverCode: Int,
//                body: String,
//                code: String,
//                message: String
//            ) {
//                context?.let {
//                    Toast.makeText(it.applicationContext, message, Toast.LENGTH_SHORT).show()
//                }
//            }
//        })
//    }
//
//
//    /**
//     * Post 삭제
//     */
//    private fun deletePost(bean: BeanTimeline) {
//        bean.idx?.let { idx ->
//            DAClient.deleteActionPostsDetail(idx, object : DAHttpCallback {
//                override fun onResponse(
//                    call: Call,
//                    serverCode: Int,
//                    body: String,
//                    code: String,
//                    message: String
//                ) {
//                    if (context != null) {
//                        Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
//                            .show()
//
//                        if (code == DAClient.SUCCESS) {
//                            mAdapter!!.remove(bean)
//                            mAdapter!!.notifyDataSetChanged()
//                        }
//                    }
//                }
//            })
//        }
//
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        when (requestCode) {
//            Constants.PHONE_CODE -> {
//            }
//            else -> {
//                super.onActivityResult(requestCode, resultCode, data)
//            }
//        }
//
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == ActivityComment.REQUEST_REPLACE_USER_IDX
//            ) {
//
//            } else if (requestCode == EXTRA_CHANGE_CATEGORY) {
//                isLast = false
//                getTimeLineData(false, -1, true)
//            }
//        } else if (resultCode == RESULT_CODE) {
//            if (requestCode == ActivityComment.REQUEST_REPLACE_USER_IDX ||
//                requestCode == ActivitySFA.REQUEST_REPLACE_USER_IDX
//            ) {
//                val view_user_idx = data!!.getIntExtra(RESULT_REPLACE_USER_IDX, -1)
//                (activity as ActivityMain).replaceFragment(
//                    FragmentProfile.newInstance(view_user_idx),
//                    true
//                )
//            }
//        } else if (resultCode == FragmentAddActionPost.REQUEST_TIMELINE_EDIT_SUCCESS) {
//            if (requestCode == FragmentAddActionPost.REQUEST_TIMELINE_EDIT_SUCCESS) {
//                isLast = false
//                getTimeLineData(false, -1, true)
//            }
//        }
//    }
//
//    /**
//     * Http
//     * 인증 좋아요
//     */
//    private fun actionLike(bean: BeanTimeline) {
//
//        bean.idx?.let { idx ->
//            DAClient.likeActionPost(idx, object : DAHttpCallback {
//                override fun onResponse(
//                    call: Call,
//                    serverCode: Int,
//                    body: String,
//                    code: String,
//                    message: String
//                ) {
//                    if (context != null) {
//
//                        if (code == DAClient.SUCCESS) {
//                            val json = JSONObject(body)
//                            val status = json.getBoolean("status")
//                            bean.status = status
//                            val count = json.getInt("count")
//                            bean.like_count = count
//                            mAdapter!!.notifyDataSetChanged()
//                        } else {
//                            Toast.makeText(
//                                context!!.applicationContext,
//                                message,
//                                Toast.LENGTH_SHORT
//                            )
//                                .show()
//                        }
//                    }
//                }
//            })
//        }
//
//    }
//
//    /**
//     * On Refresh
//     */
//    override fun onRefresh() {
//        isLast = false
//        getTimeLineData(false, -1, true)
//    }
}