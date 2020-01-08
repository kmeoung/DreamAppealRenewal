package com.truevalue.dreamappeal.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivitySearch
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.bean.BeanAppealer
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import kotlinx.android.synthetic.main.fragment_search_board.*
import okhttp3.Call
import org.json.JSONObject

class FragmentSearchBoard : BaseFragment(), ActivitySearch.IOSearchListener {

    private var mAdapter: BaseRecyclerViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_search_board, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Rv Adapter 초기화
        initAdapter()
        // 데이터 초기화
        initData()
        // 임시 데이터 추가
        bindTempData()
        // 초반 데이터 가져오기
        getBoardSearch()
    }

    /**
     * Data 초기화
     */
    private fun initData() {
        (activity as ActivitySearch).mSearchListener = this
    }

    /**
     * 임시 데이터 추가
     */
    private fun bindTempData() {
        for (i in 0 until 100) {
            mAdapter!!.add("")
        }
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvListener)
        rv_board.adapter = mAdapter
        rv_board.layoutManager = GridLayoutManager(context!!, 2)
        rv_board.addItemDecoration(BaseGridItemDecorate(context!!, 2.0f, 2))
    }

    /**
     * Http
     * 게시글 추천
     */
    private fun getBoardSearch() {

        DAClient.searchBoard(object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {

                if (code == DAClient.SUCCESS) {

                } else {
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    /**
     * Http
     * 게시글 검색
     */
    private fun getBoardSearch(keyword: String) {

        DAClient.searchBoard(keyword, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()

                if (code == DAClient.SUCCESS) {

                }
            }
        })
    }

    /**
     * RecyclerView Listener
     */
    private val rvListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_search_board, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            val ivImage = h.getItemView<ImageView>(R.id.iv_image)
            val llTitle = h.getItemView<LinearLayout>(R.id.ll_title)
            val tvTitle = h.getItemView<TextView>(R.id.tv_title)
            val tvSubTitle = h.getItemView<TextView>(R.id.tv_sub_title)

            Glide.with(context!!)
                .load(R.drawable.ic_image_black)
                .into(ivImage)

            tvTitle.text = "TEMP"
            tvSubTitle.text = "TEMP"
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }

    /**
     * 검색 Listener
     */
    override fun onSearch(keyword: String) {
        if (keyword.isNullOrEmpty()) {
            getBoardSearch()
        } else {
            getBoardSearch(keyword)
        }
    }
}