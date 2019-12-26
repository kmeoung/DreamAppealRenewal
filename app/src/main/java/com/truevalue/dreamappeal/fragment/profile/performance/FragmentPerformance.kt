package com.truevalue.dreamappeal.fragment.profile.performance

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityComment
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanAchievementPost
import com.truevalue.dreamappeal.bean.BeanBestPost
import com.truevalue.dreamappeal.bean.BeanPerformance
import com.truevalue.dreamappeal.fragment.profile.FragmentProfile
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.fragment_performance.*
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException

@Deprecated("Not Used")
class FragmentPerformance : BaseFragment(), IORecyclerViewListener,
    SwipeRefreshLayout.OnRefreshListener {

    private val TOP_BANNER_DELAY = 1000 * 7

    private var mAdapter: BaseRecyclerViewAdapter? = null
    private var mPagerAdapter: ViewPagerAdapter? = null

    private var mCurrentPage = 0
    private var mBestPostList: ArrayList<BeanBestPost?>? = null

    private var mBeanPerformance: BeanPerformance? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_performance, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Data 초기화
        initData()
        // View 초기화
        initView()
        // RecyclerView Adapter 초기화
        initAdapter()
        // bind Data
        bindData()
        // View Click Listener
        onClickView()
        // Bind Temp Data
//        bindTempData()
        if (mBeanPerformance == null) {
            // 주요 성과 가져오기
            getAchievementPostMain()
        }
    }

    override fun onResume() {
        super.onResume()
        // 자동 스크롤 시작
        startPageRolling()
    }

    override fun onPause() {
        super.onPause()
        // 자동 스크롤 종료
        stopPageRolling()
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_add_achievement -> {
                    (activity as ActivityMain).replaceFragment(FragmentAddAchievementPost(), true)
                }
            }
        }
        iv_add_achievement.setOnClickListener(listener)
    }

    /**
     * Main에서 넘어온 Refresh 요청
     */
    override fun OnServerRefresh() {
        super.OnServerRefresh()
        getAchievementPostMain()
    }

    /**
     * 있는 데이터 집어넣기
     */
    private fun bindData() {
        if (mBeanPerformance != null) {
            mBestPostList!!.clear()
            for (i in 0 until mBeanPerformance!!.best_posts.size) {
                mBestPostList!!.add(mBeanPerformance!!.best_posts[i])
            }

            mAdapter!!.clear()
            for (i in 0 until mBeanPerformance!!.achievement_posts.size) {
                mAdapter!!.add(mBeanPerformance!!.achievement_posts[i])
            }

            if(mPagerAdapter != null) mPagerAdapter?.notifyDataSetChanged()
        }
    }

    /**
     * Data 초기화
     */
    private fun initData() {
        if (mBestPostList == null) {
            mBestPostList = ArrayList<BeanBestPost?>()
        }
    }

    /**
     * Bind Temp Data
     */
    private fun bindTempData() {
        for (i in 0..10) {
            mAdapter!!.add("")
        }
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        if (context != null) {
            mAdapter = BaseRecyclerViewAdapter(this)
            rv_dream_description.adapter = mAdapter
            rv_dream_description.layoutManager =
                LinearLayoutManager(context)

            mPagerAdapter = ViewPagerAdapter(context!!)
            vp_pager.adapter = mPagerAdapter

            // ViewPager 사용자가 스크롤 시 잠시 Hander를 끄고
            // 일정 시간이 지나면 다시 자동 스크롤 진행
            vp_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    if (vp_pager.adapter != null) {
                        if (vp_pager.adapter!!.getCount() > 1) {
                            when (state) {
                                ViewPager.SCROLL_STATE_DRAGGING -> stopPageRolling()
                                ViewPager.SCROLL_STATE_SETTLING -> startPageRolling()
                            }
                        }
                    }
                }
            })
        }
    }

    /**
     * Http
     * 주요 성과 페이지 조회
     */
    private fun getAchievementPostMain() {
        // todo : 현재 조회하고 있는 Profile User Index 를 사용해야 합니다. +
        val profile_idx = Comm_Prefs.getUserProfileIndex()
        DAClient.achievementPostMain(profile_idx,
            object : DAHttpCallback {
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
                    if (context != null) {

                        if (code == DAClient.SUCCESS) {
                            mBeanPerformance = BeanPerformance(
                                null,
                                ArrayList(), ArrayList()
                            )

                            val json = JSONObject(body)
                            val profileImage = json.getString("profile_image")

                            mBeanPerformance!!.profile_image = profileImage
                            mBeanPerformance!!.best_posts.clear()


                            val bestPosts = json.getJSONObject("best_posts")
                            mBestPostList!!.clear()

                            for (i in 1..3) {
                                try {
                                    val bestPost = bestPosts.getJSONObject("best_post_$i")
                                    val bean = Gson().fromJson<BeanBestPost>(
                                        bestPost.toString(),
                                        BeanBestPost::class.java
                                    )
                                    mBeanPerformance!!.best_posts.add(bean)
                                    mBestPostList!!.add(bean)
                                } catch (e: Exception) {
                                    mBeanPerformance!!.best_posts.add(null)
                                    mBestPostList!!.add(null)
                                }
                            }

                            if(mPagerAdapter != null) mPagerAdapter?.notifyDataSetChanged()

                            val achievementPosts = json.getJSONArray("achievement_posts")
                            mAdapter!!.clear()
                            mBeanPerformance!!.achievement_posts.clear()
                            for (i in 0 until achievementPosts.length()) {
                                val bean = Gson().fromJson<BeanAchievementPost>(
                                    achievementPosts.getJSONObject(i).toString(),
                                    BeanAchievementPost::class.java
                                )

                                val thumbnail = achievementPosts.getJSONObject(i)
                                    .getJSONArray("thumbnail_image")
                                if (thumbnail.length() > 0) {
                                    val image = thumbnail.getJSONObject(0)
                                    val imageUrl = image.getString("image_url")
                                    bean.thumbnailImage = imageUrl
                                }

                                mAdapter!!.add(bean)
                                mBeanPerformance!!.achievement_posts.add(bean)
                            }

                        }else{
                            Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            })
    }

    /**
     * Init View
     */
    private fun initView() {
        Utils.setSwipeRefreshLayout(srl_refresh, this)
    }

    /**
     * RecyclerView Item Count
     */
    override val itemCount: Int
        get() = if (mAdapter != null) mAdapter!!.mArray.size else 0

    /**
     * RecyclerView Create View Holder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        BaseViewHolder.newInstance(R.layout.listitem_achievement_post, parent, false)

    /**
     * RecyclerView Bind View Holder
     */
    override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
        if (mAdapter != null) {
            // todo : Data Class 파일
            var bean = mAdapter!!.get(i) as BeanAchievementPost
            val tvTitle = h.getItemView<TextView>(R.id.tv_title)
            val tvContents = h.getItemView<TextView>(R.id.tv_contents)
            val ivThumbnail = h.getItemView<ImageView>(R.id.iv_thumbnail)
            val ivProfile = h.getItemView<ImageView>(R.id.iv_profile)
            val llItem = h.getItemView<LinearLayout>(R.id.ll_item)
            val iBtnMore = h.getItemView<ImageButton>(R.id.ibtn_more)
            val ivComment = h.getItemView<ImageView>(R.id.iv_comment)
            val llCommentDetailll = h.getItemView<LinearLayout>(R.id.ll_comment_detail)
            val llComment = h.getItemView<LinearLayout>(R.id.ll_comment)

            llCommentDetailll.setOnClickListener(View.OnClickListener {
                val intent = Intent(context!!,ActivityComment::class.java)
                intent.putExtra(ActivityComment.EXTRA_VIEW_TYPE,ActivityComment.EXTRA_TYPE_ACHIEVEMENT_POST)
                intent.putExtra(ActivityComment.EXTRA_INDEX,bean.idx)
                intent.putExtra(ActivityComment.EXTRA_OFF_KEYBOARD," ")
                startActivityForResult(intent,ActivityComment.REQUEST_REPLACE_USER_IDX)
            })

            llComment.setOnClickListener(View.OnClickListener {
                val intent = Intent(context!!,ActivityComment::class.java)
                intent.putExtra(ActivityComment.EXTRA_VIEW_TYPE,ActivityComment.EXTRA_TYPE_ACHIEVEMENT_POST)
                intent.putExtra(ActivityComment.EXTRA_INDEX,bean.idx)
                startActivityForResult(intent,ActivityComment.REQUEST_REPLACE_USER_IDX)
            })

            iBtnMore.setOnClickListener(View.OnClickListener {
                showPopupMenu(it,bean)
            })

            tvTitle.text = bean.title
            tvContents.text = bean.content

            Utils.setImageViewSquare(context, ivThumbnail)

            Glide.with(this).load(mBeanPerformance!!.profile_image)
                .placeholder(R.drawable.drawer_user)
                .into(ivProfile)

            Glide.with(this).load(bean.thumbnailImage)
                .placeholder(R.drawable.ic_image_black)
                .into(ivThumbnail)

            llItem.setOnClickListener(View.OnClickListener {
                (activity as ActivityMain).replaceFragment(
                    FragmentAchivementPostDetail.newInstance(bean.idx),
                    addToBack = true,
                    isMainRefresh = true
                )
            })
        }
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
     * RecyclerView Item View Type
     */
    override fun getItemViewType(i: Int): Int = 0

    /**
     * 위에서 아래로 스와이프 시 Refresh
     */
    override fun onRefresh() {
        // 여기다가 서버요청
        getAchievementPostMain()
    }

    /**
     * 더보기 PopupMenu 띄우기
     */
    private fun showPopupMenu(ivMore : View, bean : BeanAchievementPost) {
        val popupMenu = PopupMenu(context!!, ivMore)
        popupMenu.menu.add(getString(R.string.str_sleect_best_post_1))
        popupMenu.menu.add(getString(R.string.str_sleect_best_post_2))
        popupMenu.menu.add(getString(R.string.str_sleect_best_post_3))
        popupMenu.menu.add(getString(R.string.str_edit))
        popupMenu.menu.add(getString(R.string.str_delete))

        popupMenu.setOnMenuItemClickListener {
            when (it.title) {
                getString(R.string.str_sleect_best_post_1) -> upBestAchivement(bean,1)
                getString(R.string.str_sleect_best_post_2) -> upBestAchivement(bean,2)
                getString(R.string.str_sleect_best_post_3) -> upBestAchivement(bean,3)
                getString(R.string.str_edit) -> {
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
                                deleteAchivement(bean)
                                dialog.dismiss()
                            }
                            .setNegativeButton(
                                getString(R.string.str_no)
                            ) { dialog, which -> dialog.dismiss() }
                    val dialog = builder.create()
                    dialog.show()
                }
            }
            false
        }
        popupMenu.show()
    }

    /**
     * Http
     * 베스트 포스트 설정
     */
    private fun upBestAchivement(bean : BeanAchievementPost, best_idx : Int){
        DAClient.upBestPost(bean.idx,best_idx,object : DAHttpCallback{
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
                        onRefresh()
                    }
                }
            }
        })
    }

    /**
     * Http
     * 게시물 삭제
     */
    private fun deleteAchivement(bean : BeanAchievementPost){
        DAClient.deleteachievementPost(bean.idx,object : DAHttpCallback{
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
                        onRefresh()
                    }
                }
            }
        })
    }

    /**
     * View Page 자동 스크롤 Handler
     * todo : 현재 getCurrentItem null 오류가 있습니다.
     */
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            val position = vp_pager.currentItem
            if (mPagerAdapter != null) {
                if (position >= mPagerAdapter!!.count - 1) {
                    vp_pager.currentItem = 0
                } else {
                    vp_pager.currentItem = position + 1
                }
            }
        }
    }

    /**
     * 자동 스크롤 시작
     */
    private fun startPageRolling() {
        if (!handler.hasMessages(0)) {
            handler.sendEmptyMessageDelayed(0, TOP_BANNER_DELAY.toLong())
        }
    }

    /**
     * 자동 스크롤 정지
     */
    private fun stopPageRolling() {
        handler.removeMessages(0)
    }


    /**
     * ViewPager Adapter
     */
    private inner class ViewPagerAdapter(context: Context) : PagerAdapter() {
        private val mInflater: LayoutInflater

        override fun getCount(): Int = 3

        init {
            mInflater = LayoutInflater.from(context)
        }

        override fun destroyItem(@NonNull container: ViewGroup, position: Int, @NonNull `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(@NonNull view: View, @NonNull `object`: Any): Boolean {
            return view === `object`
        }

        override fun getItemPosition(@NonNull `object`: Any): Int {
            // todo : 추후 제거 바람
            return POSITION_NONE
        }

        @NonNull
        override fun instantiateItem(@NonNull container: ViewGroup, position: Int): Any {
            val view = mInflater.inflate(R.layout.layout_achievement, container, false)
            val tvTitle = view.findViewById<TextView>(R.id.tv_title)
            tvTitle.text = getString(R.string.str_best_performance) + " " + (position + 1)
            if (mBestPostList != null) {
                if (mBestPostList!!.size > position && mBestPostList!![position] != null) {
                    val bean = mBestPostList!![position]!!
                    val tvBestPostachievement =
                        view.findViewById<TextView>(R.id.tv_best_achievement)
                    tvBestPostachievement.text = bean.title

                    view.setOnClickListener(View.OnClickListener {
                        // todo : 상세 이동
                        /*(activity as ActivityMain).replaceFragment(
                            FragmentBestPost.newInstance(bean.idx,position + 1,mV),
                            addToBack = true,
                            isMainRefresh = true
                        )*/
                    })
                }
            }
            container.addView(view)
            return view
        }
    }
}