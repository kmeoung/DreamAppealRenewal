package com.truevalue.dreamappeal.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivitySearch
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanAddress
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import kotlinx.android.synthetic.main.fragment_search_board.*
import okhttp3.Call

class FragmentSearchTag : BaseFragment(), ActivitySearch.IOSearchListener {

    private var mAdapter: BaseRecyclerViewAdapter? = null

    companion object {
        private const val RV_TYPE_HEADER = 0
        private const val RV_TYPE_ITEM = 1
    }

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
        getTagSearch()
    }

    /**
     * Data 초기화
     */
    private fun initData() {
        (activity as ActivitySearch).mSearchListener = this
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvListener)
        rv_board.adapter = mAdapter
        rv_board.layoutManager = LinearLayoutManager(context!!)
    }

    /**
     * 임시 데이터 추가
     */
    private fun bindTempData() {
        mAdapter!!.add("Temp")
        for (i in 0 until 5) {
            mAdapter!!.add(BeanAddress(null,
                null,
                null,
                null,
                null,
                null,
                null,
                null))
        }
        mAdapter!!.add("Temp")
        for (i in 0 until 5) {
            mAdapter!!.add(BeanAddress(null,
                null,
                null,
                null,
                null,
                null,
                null,
                null))
        }
    }

    /**
     * Http
     * 태그 추천
     */
    private fun getTagSearch() {

        DAClient.searchTag(object : DAHttpCallback {
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
     * 태그 검색
     */
    private fun getTagSearch(keyword: String) {

        DAClient.searchTag(keyword, object : DAHttpCallback {
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
            when (viewType) {
                RV_TYPE_HEADER -> return BaseViewHolder.newInstance(
                    R.layout.listitem_search_tag_header,
                    parent,
                    false
                )
                RV_TYPE_ITEM -> return BaseViewHolder.newInstance(
                    R.layout.listitem_search_tag,
                    parent,
                    false
                )
            }
            return BaseViewHolder(View(context!!))
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            if(getItemViewType(i) == RV_TYPE_HEADER){
                val title = mAdapter!!.get(i) as String
                val tvTitle = h.getItemView<TextView>(R.id.tv_title)
                tvTitle.text = title
            }else if(getItemViewType(i) == RV_TYPE_ITEM){
                val bean = mAdapter!!.get(i)
                val tvTag = h.getItemView<TextView>(R.id.tv_tag)
                val tvSize = h.getItemView<TextView>(R.id.tv_size)
            }
        }

        override fun getItemViewType(i: Int): Int {
            if (mAdapter!!.get(i) is String) return RV_TYPE_HEADER
            return RV_TYPE_ITEM
        }
    }

    /**
     * 검색 Listener
     */
    override fun onSearch(keyword: String) {
        if (keyword.isNullOrEmpty()) {
            getTagSearch()
        } else {
            getTagSearch(keyword)
        }
    }
}