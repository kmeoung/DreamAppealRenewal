package com.truevalue.dreamappeal.activity

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanRank
import com.truevalue.dreamappeal.bean.Rank
import com.truevalue.dreamappeal.fragment.dream_board.concern.FragmentConcern
import com.truevalue.dreamappeal.fragment.dream_board.wish.FragmentWishBoard
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.activity_rank.*
import okhttp3.Call
import org.json.JSONObject

class ActivityRank : BaseActivity() {

    private var mAdapter: BaseRecyclerViewAdapter?
    private var mViewType: Int
    private var mRangeType: String
    private var mBean: BeanRank?
    private var isMyRank: Boolean
    private var isLast: Boolean

    init {
        mAdapter = null
        mViewType = VIEW_TYPE_ALL
        mRangeType = RANGE_WEEKLY
        mBean = null
        isMyRank = false
        isLast = false
    }

    companion object {
        // 종합
        private const val VIEW_TYPE_ALL = 0
        // 실천
        private const val VIEW_TYPE_ACTION = 1
        // 영감
        private const val VIEW_TYPE_IDEA = 2
        // 일상
        private const val VIEW_TYPE_LIFE = 3
        // 명성도
        private const val VIEW_TYPE_REPUTATION = 4

        private const val RANGE_TOTAL = "total"
        private const val RANGE_WEEKLY = "weekly"

        private const val RV_TYPE_ITEM = 0
        private const val RV_TYPE_LOADING = 1

        const val EXTRA_VIEW_TYPE_REPUTATION = "EXTRA_VIEW_TYPE_REPUTATION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank)

        // Page Action
        onAction()
    }

    /**
     * Page Action
     */
    private fun onAction() {
        initData()
        // rv adapter 초기화
        initAdapter()
        // View Click Listener
        onClickView()

        setRangeType(mRangeType)
    }

    /**
     * 데이터 초기화
     */
    private fun initData(){
        intent.getStringExtra(EXTRA_VIEW_TYPE_REPUTATION)?.let {
            mViewType = VIEW_TYPE_REPUTATION
            tv_rank_title.text = getString(R.string.str_fame_rank_title)
        }?:kotlin.run {
            tv_rank_title.text = getString(R.string.str_appeal_rank)
        }
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener { view ->
            when (view) {
                tv_all -> {
                    if (isMyRank || mViewType != VIEW_TYPE_ALL) {
                        isLast = false
                        isMyRank = false
                        setRvType(VIEW_TYPE_ALL)
                    }
                }
                tv_action -> {
                    if (isMyRank || mViewType != VIEW_TYPE_ACTION) {
                        isLast = false
                        isMyRank = false
                        setRvType(VIEW_TYPE_ACTION)
                    }
                }
                tv_idea -> {
                    if (isMyRank || mViewType != VIEW_TYPE_IDEA) {
                        isLast = false
                        isMyRank = false
                        setRvType(VIEW_TYPE_IDEA)
                    }
                }
                tv_life -> {
                    if (isMyRank || mViewType != VIEW_TYPE_LIFE) {
                        isLast = false
                        isMyRank = false
                        setRvType(VIEW_TYPE_LIFE)
                    }
                }

                tv_weekly -> {
                    if (isMyRank || mRangeType != RANGE_WEEKLY) {
                        isLast = false
                        isMyRank = false
                        setRangeType(RANGE_WEEKLY)
                    }
                }
                tv_accumulate -> {
                    if (isMyRank || mRangeType != RANGE_TOTAL) {
                        isLast = false
                        isMyRank = false
                        setRangeType(RANGE_TOTAL)
                    }
                }
                ll_my -> {
                    mBean?.let { bean ->
                        isLast = false
                        isMyRank = true
                        setMyData(bean)
                    }
                }
            }
        }
        tv_all.setOnClickListener(listener)
        tv_action.setOnClickListener(listener)
        tv_idea.setOnClickListener(listener)
        tv_life.setOnClickListener(listener)
        tv_weekly.setOnClickListener(listener)
        tv_accumulate.setOnClickListener(listener)
        ll_my.setOnClickListener(listener)
    }

    private fun setRvType(type: Int = mViewType) {
        mViewType = type
        tv_all.isSelected = (type == VIEW_TYPE_ALL)
        tv_action.isSelected = (type == VIEW_TYPE_ACTION)
        tv_idea.isSelected = (type == VIEW_TYPE_IDEA)
        tv_life.isSelected = (type == VIEW_TYPE_LIFE)
        ll_selector.visibility = VISIBLE
        when (type) {
            VIEW_TYPE_ALL -> {
                getTotalRank(mRangeType)
            }
            VIEW_TYPE_ACTION -> {
                getActionRank(mRangeType)
            }
            VIEW_TYPE_IDEA -> {
                getIdeaRank(mRangeType)
            }
            VIEW_TYPE_LIFE -> {
                getLifeRank(mRangeType)
            }
            VIEW_TYPE_REPUTATION -> {
                ll_selector.visibility = GONE
                getReputationRank()
            }
        }
    }

    private fun setRangeType(type: String) {
        mRangeType = type
        tv_accumulate.isSelected = (type == RANGE_TOTAL)
        tv_weekly.isSelected = (type == RANGE_WEEKLY)

        setRvType()
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvRank)
        rv_rank.run {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    /**
     * Http
     * 종합 가져오기
     */
    private fun getTotalRank(range: String) {
        DAClient.getRankTotal(
            range,
            getInitDataListener
        )
    }

    /**
     * Http
     * 실천 가져오기
     */
    private fun getActionRank(range: String) {
        DAClient.getRankAction(
            range,
            getInitDataListener
        )
    }

    /**
     * Http
     * 영감 가져오기
     */
    private fun getIdeaRank(range: String) {
        DAClient.getRankIdea(
            range,
            getInitDataListener
        )
    }

    /**
     * Http
     * 일상 가져오기
     */
    private fun getLifeRank(range: String) {
        DAClient.getRankLife(range, getInitDataListener)
    }

    /**
     * Http
     * 명성도 가져오기
     */
    private fun getReputationRank() {
        DAClient.getRankReputation(getInitDataListener)
    }

    /**
     * Http
     * 종합 추가 가져오기
     */
    private fun getMoreTotalRank(range: String, lastRowNum: Int) {
        DAClient.getRankTotal(
            range,
            lastRowNum,
            getMoreDataListener
        )
    }

    /**
     * Http
     * 실천 추가 가져오기
     */
    private fun getMoreActionRank(range: String, lastRowNum: Int) {
        DAClient.getRankAction(
            range,
            lastRowNum,
            getMoreDataListener
        )
    }

    /**
     * Http
     * 영감 추가 가져오기
     */
    private fun getMoreIdeaRank(range: String, lastRowNum: Int) {
        DAClient.getRankIdea(
            range,
            lastRowNum,
            getMoreDataListener
        )
    }

    /**
     * Http
     * 일상 추가 가져오기
     */
    private fun getMoreLifeRank(range: String, lastRowNum: Int) {
        DAClient.getRankLife(
            range,
            lastRowNum,
            getMoreDataListener
        )
    }

    /**
     * Http
     * 명성도 추가 가져오기
     */
    private fun getMoreReputationRank(lastRowNum: Int) {
        DAClient.getRankReputation(
            lastRowNum, getMoreDataListener
        )
    }

    private fun setData(bean: BeanRank, isRefresh: Boolean = true) {
        mAdapter?.let {
            if (isRefresh)
                it.clear()

            if (bean.high_rank.size < 9) isLast = true

            for (rank in bean.high_rank) {
                it.add(rank)
            }
        }

        if(isRefresh) {
            tv_all_rank.text = bean.my_rank.ranking.toString()

            bean.my_rank.image?.let { image ->
                Glide.with(this@ActivityRank)
                    .load(image)
                    .circleCrop()
                    .placeholder(R.drawable.drawer_user)
                    .into(iv_my_rank_profile)
            }

            tv_value_style_job.text = "${bean.my_rank.value_style} ${bean.my_rank.job}"
            tv_name.text = "${bean.my_rank.nickname}"

            when (mViewType) {
                VIEW_TYPE_ALL -> {
                    when (mRangeType) {
                        RANGE_TOTAL -> {
                            tv_contents.setTextColor(
                                ContextCompat.getColor(
                                    this@ActivityRank,
                                    R.color.black
                                )
                            )
                            tv_contents.text = "Lv. ${bean.my_rank.level}"
                        }
                        RANGE_WEEKLY -> {
                            tv_contents.setTextColor(
                                ContextCompat.getColor(
                                    this@ActivityRank,
                                    R.color.nice_blue
                                )
                            )
                            tv_contents.text = "+ ${bean.my_rank.exp_sum} exp"
                        }
                    }
                }
                VIEW_TYPE_ACTION,
                VIEW_TYPE_IDEA,
                VIEW_TYPE_LIFE -> {
                    when (mRangeType) {
                        RANGE_TOTAL -> {
                            tv_contents.setTextColor(
                                ContextCompat.getColor(
                                    this@ActivityRank,
                                    R.color.nice_blue
                                )
                            )
                            tv_contents.text = "${bean.my_rank.count} 개"
                        }
                        RANGE_WEEKLY -> {
                            tv_contents.setTextColor(
                                ContextCompat.getColor(
                                    this@ActivityRank,
                                    R.color.nice_blue
                                )
                            )
                            tv_contents.text = "+ ${bean.my_rank.count} 개"
                        }
                    }
                }
                VIEW_TYPE_REPUTATION -> {
                    tv_value_style_job.text = "${bean.my_rank.nickname}"
                    tv_name.visibility = GONE
                    tv_contents.visibility = GONE
                    ll_concern.visibility = VISIBLE
                    tv_fame.text = "${bean.my_rank.point?:0} 점"
                }
            }
        }
    }

    private fun setMyData(bean: BeanRank) {
        mAdapter?.let {
            it.clear()
            for (rank in bean.my_range) {
                it.add(rank)
            }
        }
    }

    private val getInitDataListener = object : DAHttpCallback {
        override fun onResponse(
            call: Call,
            serverCode: Int,
            body: String,
            code: String,
            message: String
        ) {
            if (code == DAClient.SUCCESS) {
                val json = JSONObject(body)
                val bean = Gson().fromJson(json.toString(), BeanRank::class.java)
                mBean = bean
                if (bean.high_rank.size < 9) isLast = true
                setData(bean)
            } else if (code == DAClient.NO_MORE_POST) {
                isLast = true
                mAdapter!!.notifyDataSetChanged()
            } else {
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val getMoreDataListener = object : DAHttpCallback {
        override fun onResponse(
            call: Call,
            serverCode: Int,
            body: String,
            code: String,
            message: String
        ) {
            if (code == DAClient.SUCCESS) {
                val json = JSONObject(body)
                val bean = Gson().fromJson(json.toString(), BeanRank::class.java)
                if (bean.high_rank.size < 9) isLast = true
                setData(bean, false)
            } else if (code == DAClient.NO_MORE_POST) {
                isLast = true
                mAdapter!!.notifyDataSetChanged()
            } else {
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    /**
     * Rv Rank
     */
    private val rvRank = object : IORecyclerViewListener {

        override val itemCount: Int
            get() = if (mAdapter != null) if (mAdapter!!.size() > 8 && !isLast && !isMyRank) mAdapter!!.size() + 1 else mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            when (viewType) {
                RV_TYPE_ITEM ->
                    return BaseViewHolder.newInstance(R.layout.listitem_rank, parent, false)
                RV_TYPE_LOADING ->
                    return BaseViewHolder.newInstance(R.layout.listitem_white_more, parent, false)
                else -> return BaseViewHolder.newInstance(R.layout.listitem_rank, parent, false)
            }
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {

            if (getItemViewType(i) == RV_TYPE_ITEM) {

                val bean = mAdapter?.get(i) as Rank

                val tvRank = h.getItemView<TextView>(R.id.tv_rank)
                val ivRankProfile = h.getItemView<ImageView>(R.id.iv_rank_profile)
                val tvUser = h.getItemView<TextView>(R.id.tv_user)
                val tvContents = h.getItemView<TextView>(R.id.tv_contents)
                val llConcern = h.getItemView<LinearLayout>(R.id.ll_concern)
                val tvFame = h.getItemView<TextView>(R.id.tv_fame)
                tvRank.text = bean.ranking.toString()

                bean.image?.let { image ->
                    Glide.with(this@ActivityRank)
                        .load(image)
                        .circleCrop()
                        .placeholder(R.drawable.drawer_user)
                        .into(ivRankProfile)
                }?:kotlin.run {
                    Glide.with(this@ActivityRank)
                        .load(R.drawable.drawer_user)
                        .circleCrop()
                        .into(ivRankProfile)
                }

                tvUser.text = "${bean.value_style} ${bean.job} ${bean.nickname}"
                var sp = Utils.replaceTextType(
                    this@ActivityRank,
                    tvUser,
                    bean.nickname
                )
                tvUser.text = sp
                tvContents.visibility = VISIBLE
                when (mViewType) {
                    VIEW_TYPE_ALL -> {
                        when (mRangeType) {
                            RANGE_TOTAL -> {
                                tvContents.setTextColor(
                                    ContextCompat.getColor(
                                        this@ActivityRank,
                                        R.color.black
                                    )
                                )
                                tvContents.text = "Lv. ${bean.level}"
                            }
                            RANGE_WEEKLY -> {
                                tvContents.setTextColor(
                                    ContextCompat.getColor(
                                        this@ActivityRank,
                                        R.color.nice_blue
                                    )
                                )
                                tvContents.text = "+ ${bean.exp_sum} exp"
                            }
                        }
                    }
                    VIEW_TYPE_ACTION,
                    VIEW_TYPE_IDEA,
                    VIEW_TYPE_LIFE -> {
                        when (mRangeType) {
                            RANGE_TOTAL -> {
                                tvContents.setTextColor(
                                    ContextCompat.getColor(
                                        this@ActivityRank,
                                        R.color.nice_blue
                                    )
                                )
                                tvContents.text = "${bean.count} 개"
                            }
                            RANGE_WEEKLY -> {
                                tvContents.setTextColor(
                                    ContextCompat.getColor(
                                        this@ActivityRank,
                                        R.color.nice_blue
                                    )
                                )
                                tvContents.text = "+ ${bean.count} 개"
                            }
                        }
                    }
                    VIEW_TYPE_REPUTATION -> {
                        tvUser.text = bean.nickname
                        var sp = Utils.replaceTextType(
                            this@ActivityRank,
                            tvUser,
                            bean.nickname
                        )
                        tvUser.text = sp
                        llConcern.visibility = VISIBLE
                        tvContents.visibility = GONE
                        tvFame.text = "${bean.point?:0} 점"
                    }
                }
            } else {
                val bean = mAdapter?.get((mAdapter?.size()!!.minus(1))) as Rank
                when (mViewType) {
                    VIEW_TYPE_ALL -> {
                        getMoreTotalRank(mRangeType, bean.row_num)
                    }
                    VIEW_TYPE_ACTION -> {
                        getMoreActionRank(mRangeType, bean.row_num)
                    }
                    VIEW_TYPE_IDEA -> {
                        getMoreIdeaRank(mRangeType, bean.row_num)
                    }
                    VIEW_TYPE_LIFE -> {
                        getMoreLifeRank(mRangeType, bean.row_num)
                    }
                    VIEW_TYPE_REPUTATION -> {
                        getMoreReputationRank(bean.row_num)
                    }
                }

            }

        }

        override fun getItemViewType(i: Int): Int {
            if (mAdapter!!.size() > 8 && mAdapter!!.size() == i && !isLast && !isMyRank) {
                return FragmentConcern.RV_TYPE_ITEM_MORE
            }
            return FragmentConcern.RV_TYPE_ITEM
        }
    }
}