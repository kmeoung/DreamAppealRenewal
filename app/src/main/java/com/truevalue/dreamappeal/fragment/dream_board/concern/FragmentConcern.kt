package com.truevalue.dreamappeal.fragment.dream_board.concern

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.fragment.dream_board.FragmentAddBoard
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import kotlinx.android.synthetic.main.fragment_concern.*
import okhttp3.Call

class FragmentConcern : BaseFragment() {

    private var mPopularAdapter: BaseRecyclerViewAdapter?
    private var mRecentAdapter: BaseRecyclerViewAdapter?
    private var mPostType: Int

    init {
        mPopularAdapter = null
        mRecentAdapter = null
        mPostType = POST_TYPE_WEEKLY
    }

    companion object {
        const val POST_TYPE_WEEKLY = 0
        const val POST_TYPE_TODAY = 1
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
        // View Click Listener
        onClickView()
        // 기본 데이터 조회
        getConcern()
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

                } else {
                    context?.let {
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
    }

    /**
     * 주목받는 글 설정
     */
    private fun setPopularType(post_type : Int){
        mPostType = post_type
        when (mPostType) {
            POST_TYPE_WEEKLY -> {
                tv_weekly.setTextColor(Color.YELLOW)
                tv_today.setTextColor(Color.WHITE)

            }
            POST_TYPE_TODAY -> {
                tv_weekly.setTextColor(Color.WHITE)
                tv_today.setTextColor(Color.YELLOW)

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
            get() = mRecentAdapter?.size() ?: 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_concern_item, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {

        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}