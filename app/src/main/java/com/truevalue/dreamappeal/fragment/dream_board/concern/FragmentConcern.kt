package com.truevalue.dreamappeal.fragment.dream_board.concern

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanConcern
import com.truevalue.dreamappeal.bean.BeanFragmentConcern
import com.truevalue.dreamappeal.fragment.dream_board.FragmentAddBoard
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import kotlinx.android.synthetic.main.fragment_concern.*
import okhttp3.Call
import org.json.JSONException
import org.json.JSONObject

class FragmentConcern : BaseFragment() {

    private var mPopularAdapter: BaseRecyclerViewAdapter?
    private var mRecentAdapter: BaseRecyclerViewAdapter?
    private var mPostType: Int
    private var mBeanFragment: BeanFragmentConcern?
    private var isLast: Boolean

    private val keyboardHandler: Handler

    init {
        mPopularAdapter = null
        mRecentAdapter = null
        mPostType = POST_TYPE_WEEKLY
        mBeanFragment = null
        isLast = false
        keyboardHandler = Handler(Handler.Callback {
            getConcern(et_search.text.toString())
            true
        })

    }

    companion object {
        private const val DELAY = 1000L
        const val POST_TYPE_WEEKLY = 0
        const val POST_TYPE_TODAY = 1

        const val RV_TYPE_ITEM = 0
        const val RV_TYPE_ITEM_MORE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_concern, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView Adapter 초기화
        initAdapter()
        // View 초기화
        initView()
        // View Click Listener
        onClickView()
        // 기본 데이터 조회
        getConcern()
    }


    /**
     * View 초기화
     */
    private fun initView() {
        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                keyboardHandler.removeMessages(0)
                if (et_search.text.toString().isNotEmpty()) {
                    keyboardHandler.sendEmptyMessageDelayed(0, DELAY)
                } else {
                    getConcern()
                }
            }
        })
    }

    /**
     * Concern 기본 조회
     */
    private fun getConcern() {
        DAClient.getConcern(object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (code == DAClient.SUCCESS) {
                    isLast = false
                    ll_popular.visibility = VISIBLE
                    ll_recent.visibility = VISIBLE

                    rv_popular.visibility = VISIBLE
                    rv_recent.visibility = VISIBLE

                    val json = JSONObject(body)
                    var popularWList: ArrayList<BeanConcern>? = null
                    var popularDList: ArrayList<BeanConcern>? = null
                    var recentList: ArrayList<BeanConcern>? = null
                    try {
                        val popular_w = json.getJSONArray("popular_w")
                        popularWList = ArrayList()
                        for (i in 0 until popular_w.length()) {
                            val jItem = popular_w.get(i)
                            val bean = Gson().fromJson<BeanConcern>(
                                jItem.toString(),
                                BeanConcern::class.java
                            )
                            popularWList.add(bean)
                        }
                    } catch (e: JSONException) {
                    }
                    try {
                        val popular_d = json.getJSONArray("popular_d")
                        popularDList = ArrayList()
                        for (i in 0 until popular_d.length()) {
                            val jItem = popular_d.get(i)
                            val bean = Gson().fromJson<BeanConcern>(
                                jItem.toString(),
                                BeanConcern::class.java
                            )
                            popularDList.add(bean)
                        }
                    } catch (e: JSONException) {
                    }
                    try {
                        val recent = json.getJSONArray("recent")
                        recentList = ArrayList()
                        for (i in 0 until recent.length()) {
                            val jItem = recent.get(i)
                            val bean = Gson().fromJson<BeanConcern>(
                                jItem.toString(),
                                BeanConcern::class.java
                            )
                            recentList.add(bean)
                        }
                    } catch (e: JSONException) {
                    }

                    mBeanFragment = BeanFragmentConcern(popularWList, popularDList, recentList)

                    setPopularType(mPostType)

                    mBeanFragment?.let { bean ->
                        bean.recent?.let {
                            mRecentAdapter?.let { adapter ->
                                adapter.clear()
                                for (i in 0 until it.size) {
                                    adapter.add(it[i])
                                }
                            }
                        }
                    }

                } else {
                    context?.let {
                        Toast.makeText(it.applicationContext, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    /**
     * Http
     * Concern 검색
     */
    private fun getConcern(keyword: String) {
        DAClient.searchConcern(keyword, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (code == DAClient.SUCCESS) {
                    ll_popular.visibility = GONE
                    ll_recent.visibility = GONE

                    rv_popular.visibility = GONE
                    rv_recent.visibility = VISIBLE

                    val json = JSONObject(body)
                    val posts = json.getJSONArray("posts")
                    mRecentAdapter?.let { adapter ->
                        adapter.clear()
                        for (i in 0 until posts.length()) {
                            val bean = Gson().fromJson<BeanConcern>(
                                posts.getJSONObject(i).toString(),
                                BeanConcern::class.java
                            )
                            adapter.add(bean)
                        }
                    }
                } else {
                    context?.let {
                        Toast.makeText(it.applicationContext, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    /**
     * Http
     * 추가 조회
     */
    private fun getMoreConcern(last_concern_idx: Int) {
        DAClient.getConcernMore(last_concern_idx, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (code == DAClient.SUCCESS) {
                    val json = JSONObject(body)
                    try {
                        val recent_more = json.getJSONArray("recent_more")
                        mRecentAdapter?.let {
                            if (1 > recent_more.length()) {
                                isLast = true
                                it.notifyDataSetChanged()
                            }

                            for (i in 0 until recent_more.length()) {
                                val obj = recent_more.get(i)
                                val bean = Gson().fromJson<BeanConcern>(
                                    obj.toString(),
                                    BeanConcern::class.java
                                )
                                it.add(bean)
                            }
                        }
                    } catch (e: Exception) {
                    }
                } else if (code == DAClient.NO_MORE_POST) {
                    isLast = true
                    mRecentAdapter!!.notifyDataSetChanged()
                } else {
                    context?.let {
                        isLast = true
                        mRecentAdapter!!.notifyDataSetChanged()
                        Toast.makeText(it.applicationContext, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_my -> {
                    (activity as ActivityMain).replaceFragment(
                        FragmentMyFame(),
                        addToBack = true,
                        isMainRefresh = false
                    )
                }
                ll_write -> {
                    (activity as ActivityMain).replaceFragment(
                        FragmentAddBoard.newInstance(FragmentAddBoard.TYPE_ADD_CONCERN),
                        addToBack = true,
                        isMainRefresh = false
                    )
                }
                tv_weekly -> {
                    if (mPostType != POST_TYPE_WEEKLY) {
                        setPopularType(POST_TYPE_WEEKLY)
                    }
                }
                tv_today -> {
                    if (mPostType != POST_TYPE_TODAY) {
                        setPopularType(POST_TYPE_TODAY)
                    }
                }
            }
        }
        iv_my.setOnClickListener(listener)
        ll_write.setOnClickListener(listener)
        tv_weekly.setOnClickListener(listener)
        tv_today.setOnClickListener(listener)
    }

    /**
     * 주목받는 글 설정
     */
    private fun setPopularType(post_type: Int) {
        mPostType = post_type
        when (mPostType) {
            POST_TYPE_WEEKLY -> {
                tv_weekly.setTextColor(Color.YELLOW)
                tv_today.setTextColor(Color.WHITE)
                mBeanFragment?.let { bean ->
                    bean.popular_w?.let {
                        mPopularAdapter?.let { adapter ->
                            adapter.clear()
                            for (i in 0 until it.size) {
                                adapter.add(it[i])
                            }
                        }
                    } ?: kotlin.run {
                        Toast.makeText(
                            context!!.applicationContext,
                            getString(R.string.str_no_data),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            POST_TYPE_TODAY -> {
                tv_weekly.setTextColor(Color.WHITE)
                tv_today.setTextColor(Color.YELLOW)
                mBeanFragment?.let { bean ->
                    bean.popular_d?.let {
                        mPopularAdapter?.let { adapter ->
                            adapter.clear()
                            for (i in 0 until it.size) {
                                adapter.add(it[i])
                            }
                        }
                    } ?: kotlin.run {
                        Toast.makeText(
                            context!!.applicationContext,
                            getString(R.string.str_no_data),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        mPopularAdapter = BaseRecyclerViewAdapter(rvPopularListener)
        mRecentAdapter = BaseRecyclerViewAdapter(rvRecentListener)
        rv_popular.run {
            adapter = mPopularAdapter
            layoutManager = LinearLayoutManager(context)
        }

        rv_recent.run {
            adapter = mRecentAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    /**
     * Concern Popular RecyclerView Listener
     */
    private val rvPopularListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = mPopularAdapter?.size() ?: 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_concern_item, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            val bean = mPopularAdapter?.get(i) as BeanConcern
            val tvRecommand = h.getItemView<TextView>(R.id.tv_recommand)
            val tvTitle = h.getItemView<TextView>(R.id.tv_title)
            val tvReConcern = h.getItemView<TextView>(R.id.tv_re_concern)
            val tvStrReConcern = h.getItemView<TextView>(R.id.tv_str_re_concern)
            val llConcernItemBg = h.getItemView<LinearLayout>(R.id.ll_concern_item_bg)
            tvRecommand.text = bean.votes.toString()
            tvTitle.text = bean.title
            tvReConcern.text = bean.count.toString()

            llConcernItemBg.isSelected = (bean.adopted == 1)
            tvStrReConcern.isSelected = (bean.adopted == 1)
            tvReConcern.isSelected = (bean.adopted == 1)

            h.itemView.setOnClickListener {
                (activity as ActivityMain).replaceFragment(
                    FragmentConcernDetail.newInstance(bean.idx),
                    addToBack = true,
                    isMainRefresh = false
                )
            }
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }


    /**
     * Concern Recent RecyclerView Listener
     */
    private val rvRecentListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mRecentAdapter != null) if (mRecentAdapter!!.size() > 19 && !isLast) mRecentAdapter!!.size() + 1 else mRecentAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            if (RV_TYPE_ITEM_MORE == viewType)
                return BaseViewHolder.newInstance(R.layout.listitem_white_more, parent, false)
            else
                return BaseViewHolder.newInstance(R.layout.listitem_concern_item, parent, false)

        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {

            if (RV_TYPE_ITEM_MORE == getItemViewType(i)) {
                mRecentAdapter?.let {
                    val bean = mRecentAdapter?.get(mRecentAdapter?.size()!!.minus(1)) as BeanConcern
                    getMoreConcern(bean.idx)
                }
            } else {
                val bean = mRecentAdapter?.get(i) as BeanConcern
                val tvRecommand = h.getItemView<TextView>(R.id.tv_recommand)
                val tvTitle = h.getItemView<TextView>(R.id.tv_title)
                val tvReConcern = h.getItemView<TextView>(R.id.tv_re_concern)
                val tvStrReConcern = h.getItemView<TextView>(R.id.tv_str_re_concern)
                val llConcernItemBg = h.getItemView<LinearLayout>(R.id.ll_concern_item_bg)

                tvRecommand.text = bean.adopted.toString()
                tvTitle.text = bean.title
                tvReConcern.text = bean.count.toString()

                llConcernItemBg.isSelected = (bean.adopted == 1)
                tvStrReConcern.isSelected = (bean.adopted == 1)
                tvReConcern.isSelected = (bean.adopted == 1)

                h.itemView.setOnClickListener {
                    (activity as ActivityMain).replaceFragment(
                        FragmentConcernDetail.newInstance(
                            bean.idx
                        ), addToBack = true, isMainRefresh = false
                    )
                }
            }
        }

        override fun getItemViewType(i: Int): Int {
            if (mRecentAdapter!!.size() > 19 && mRecentAdapter!!.size() == i && !isLast) {
                return RV_TYPE_ITEM_MORE
            }
            return RV_TYPE_ITEM
        }
    }
}