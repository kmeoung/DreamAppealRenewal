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
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.fragment_performance.*

class FragmentPerformance : BaseFragment(), IORecyclerViewListener,
    SwipeRefreshLayout.OnRefreshListener {

    private val TOP_BANNER_DELAY = 1000 * 7

    private var mAdapter: BaseRecyclerViewAdapter? = null
    private var mPagerAdapter: ViewPagerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_performance, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        initView()
        // RecyclerView Adapter 초기화
        initAdapter()
        // Bind Temp Data
        bindTempData()
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
        BaseViewHolder.newInstance(R.layout.listitem_achivement_post, parent, false)

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
        srl_refresh.isRefreshing = false
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
            val view = mInflater.inflate(R.layout.layout_achivement, container, false)
            val tvTitle = view.findViewById<TextView>(R.id.tv_title)
            tvTitle.text = getString(R.string.str_best_performance) + " " + (position + 1)
//            if (mBestPostList.size > position && mBestPostList.get(position) != null) {
//                val bean = mBestPostList.get(position)
//                val tvBestPostAchivement = view.findViewById(R.id.tv_best_achivement)
//                tvBestPostAchivement.setTextBtn(bean.getTitle())
//                view.setOnClickListener(View.OnClickListener {
//                    val intent = Intent(context, ActivityBestAchivementDetail::class.java)
//                    intent.putExtra(
//                        ActivityBestAchivementDetail.EXTRA_BEST_ACHIVEMENT_INDEX,
//                        bean.getIdx()
//                    )
//                    intent.putExtra(
//                        ActivityBestAchivementDetail.EXTRA_BEST_ACHIVEMENT_BEST_INDEX,
//                        position + 1
//                    )
//                    startActivityForResult(intent, FragmentMain.REQUEST_PERFORMANCE_BEST_ACHIVEMENT)
//                })
//            }
            container.addView(view)
            return view
        }
    }
}