package com.truevalue.dreamappeal.fragment.profile.performance

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.annotation.NonNull
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.bean.BeanAchievementPost
import com.truevalue.dreamappeal.bean.BeanBestPost
import com.truevalue.dreamappeal.bean.BeanPerformance
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.fragment_performance.*
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException

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
        // Bind Temp Data
//        bindTempData()
        // 주요 성과 가져오기
        getAchievementPostMain()
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
     * 있는 데이터 집어넣기
     */
    private fun bindData() {
        if (mBeanPerformance != null) {
            mBestPostList!!.clear()
            for(i in 0 until mBeanPerformance!!.best_posts.size){
                mBestPostList!!.add(mBeanPerformance!!.best_posts[i])
            }

            mAdapter!!.clear()
            for (i in 0 until mBeanPerformance!!.achievement_posts.size) {
                mAdapter!!.add(mBeanPerformance!!.achievement_posts[i])
            }
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
            mCurrentPage,
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
                        Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
                            .show()

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

                            val achievementPosts = json.getJSONArray("achievement_posts")
                            mAdapter!!.clear()
                            mBeanPerformance!!.achievement_posts.clear()
                            for (i in 0 until achievementPosts.length()) {
                                val bean = Gson().fromJson<BeanAchievementPost>(
                                    achievementPosts.getJSONObject(i).toString(),
                                    BeanAchievementPost::class.java
                                )
                                mAdapter!!.add(bean)
                                mBeanPerformance!!.achievement_posts.add(bean)
                            }

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
//            var any = mAdapter!!.mArray[i]
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
     * View Page 자동 스크롤 Handler
     * todo : 현재 getCurrentItem null 오류가 있습니다.
     */
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            val position = vp_pager.getCurrentItem()
            if (mPagerAdapter != null) {
                if (position >= mPagerAdapter!!.getCount() - 1) {
                    vp_pager.setCurrentItem(0)
                } else {
                    vp_pager.setCurrentItem(position + 1)
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
//                        val intent = Intent(context, ActivityBestachievementDetail::class.java)
//                        intent.putExtra(
//                            ActivityBestachievementDetail.EXTRA_BEST_achievement_INDEX,
//                            bean.getIdx()
//                        )
//                        intent.putExtra(
//                            ActivityBestachievementDetail.EXTRA_BEST_achievement_BEST_INDEX,
//                            position + 1
//                        )
//                        startActivityForResult(
//                            intent,
//                            FragmentMain.REQUEST_PERFORMANCE_BEST_achievement
//                        )
                    })
                }
            }
            container.addView(view)
            return view
        }
    }
}