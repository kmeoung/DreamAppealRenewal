package com.truevalue.dreamappeal.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivitySearch
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanAddress
import com.truevalue.dreamappeal.bean.BeanHistoryTag
import com.truevalue.dreamappeal.bean.BeanPopularTag
import com.truevalue.dreamappeal.bean.BeanSearchBoard
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.fragment_search_board.*
import okhttp3.Call
import org.json.JSONObject

class FragmentSearchTag : BaseFragment(), ActivitySearch.IOSearchListener {

    private var mAdapter: BaseRecyclerViewAdapter? = null

    companion object {
        private const val RV_TYPE_HEADER = 0
        private const val RV_TYPE_POPULAR_ITEM = 1
        private const val RV_TYPE_HISTORY_ITEM = 2
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
            mAdapter!!.add(
                BeanAddress(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                )
            )
        }
        mAdapter!!.add("Temp")
        for (i in 0 until 5) {
            mAdapter!!.add(
                BeanAddress(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                )
            )
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
                    val json = JSONObject(body)

                    mAdapter?.let {
                        it.clear()
                        it.add(getString(R.string.str_popular_tag))
                        val tag_popular = json.getJSONArray("tag_popular")

                        for (i in 0 until tag_popular.length()) {
                            val tag = tag_popular.getJSONObject(i)
                            val bean = Gson().fromJson<BeanPopularTag>(
                                tag.toString(),
                                BeanPopularTag::class.java
                            )
                            it.add(bean)
                        }
                        it.add(getString(R.string.str_recent_search))
                        val tag_history = json.getJSONArray("tag_history")
                        for (i in 0 until tag_history.length()) {
                            val tag = tag_history.getJSONObject(i)
                            val bean = Gson().fromJson<BeanHistoryTag>(
                                tag.toString(),
                                BeanHistoryTag::class.java
                            )
                            it.add(bean)
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
                if (code == DAClient.SUCCESS) {
                    val json = JSONObject(body)

                    mAdapter?.let {
                        it.clear()
//                        val tag_popular = json.getJSONArray("tag_popular")
//
//                        for (i in 0 until tag_popular.length()) {
//                            val tag = tag_popular.getJSONObject(i)
//                            val bean = Gson().fromJson<BeanPopularTag>(
//                                tag.toString(),
//                                BeanPopularTag::class.java
//                            )
//                            it.add(bean)
//                        }
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
     * RecyclerView Listener
     */
    private val rvListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = mAdapter?.size() ?: 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            when (viewType) {
                RV_TYPE_HEADER -> return BaseViewHolder.newInstance(
                    R.layout.listitem_search_tag_header,
                    parent,
                    false
                )
                RV_TYPE_POPULAR_ITEM,
                RV_TYPE_HISTORY_ITEM -> return BaseViewHolder.newInstance(
                    R.layout.listitem_search_tag,
                    parent,
                    false
                )
            }
            return BaseViewHolder(View(context!!))
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            mAdapter?.let {
                if (getItemViewType(i) == RV_TYPE_HEADER) {
                    val title = it.get(i) as String
                    val tvTitle = h.getItemView<TextView>(R.id.tv_title)
                    tvTitle.text = title
                } else if (getItemViewType(i) == RV_TYPE_POPULAR_ITEM) {

                    val tvTag = h.getItemView<TextView>(R.id.tv_tag)
                    val tvSize = h.getItemView<TextView>(R.id.tv_size)

                    val bean = it.get(i) as BeanPopularTag

                    tvTag.text = "#${bean.tag_name}"
                    tvSize.text = "${Utils.getCommentView(bean.cnt)}개 게시물"

                } else if(getItemViewType(i) == RV_TYPE_HISTORY_ITEM){
                    val tvTag = h.getItemView<TextView>(R.id.tv_tag)
                    val tvSize = h.getItemView<TextView>(R.id.tv_size)

                    val bean = it.get(i) as BeanHistoryTag

                    tvTag.text = "#${bean.keyword}"
                    tvSize.text = "${Utils.getCommentView(bean.cnt)}개 게시물"
                }
            }
        }

        override fun getItemViewType(i: Int): Int {
            mAdapter?.let {
                when (it.get(i)) {
                    is String -> return RV_TYPE_HEADER
                    is BeanPopularTag -> return RV_TYPE_POPULAR_ITEM
                    is BeanHistoryTag -> return RV_TYPE_HISTORY_ITEM
                    else -> {
                    }
                }
            }
            return RV_TYPE_HEADER
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