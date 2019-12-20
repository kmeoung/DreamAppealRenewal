package com.truevalue.dreamappeal.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivitySearch
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanAppealer
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.fragment_search_appealer.*
import okhttp3.Call
import org.json.JSONObject

class FragmentSearchAppealer : BaseFragment(), ActivitySearch.IOSearchListener {

    private val TYPE_MODIFIER = 0
    private val TYPE_LOCATION = 1

    private var mSearchType = TYPE_MODIFIER

    private var mAdapter: BaseRecyclerViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_search_appealer, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init Data
        initData()
        // View Click
        onClickView()
        // init adapter
        initAdapter()

        setSearchType(mSearchType)

        getAppealerSearch()
    }

    /**
     * Data 초기화
     */
    private fun initData() {
        (activity as ActivitySearch).mSearchListener = this
    }

    /**
     * RecyclerView Adapter Init
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvListener)
        rv_appealer.adapter = mAdapter
        rv_appealer.layoutManager = LinearLayoutManager(context!!)
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                tv_modifier -> {
                    if (mSearchType != TYPE_MODIFIER)
                        setSearchType(TYPE_MODIFIER)
                }
                tv_location -> {
                    if (mSearchType != TYPE_LOCATION)
                        setSearchType(TYPE_LOCATION)
                }
            }
        }
        tv_modifier.setOnClickListener(listener)
        tv_location.setOnClickListener(listener)
    }

    /**
     * Set Search View Type
     */
    private fun setSearchType(search_type: Int) {
        mSearchType = search_type

        when (mSearchType) {
            TYPE_MODIFIER -> {
                tv_modifier.isSelected = true
                tv_location.isSelected = false

            }
            TYPE_LOCATION -> {
                tv_modifier.isSelected = false
                tv_location.isSelected = true
            }
        }
    }

    /**
     * RecyclerView Listener
     */
    private val rvListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_search_appealer, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            val bean = mAdapter!!.get(i) as BeanAppealer
            val ivProfile = h.getItemView<ImageView>(R.id.iv_profile)
            val tvValueStyle = h.getItemView<TextView>(R.id.tv_value_style)
            val tvJob = h.getItemView<TextView>(R.id.tv_job)
            val tvName = h.getItemView<TextView>(R.id.tv_name)
            val ivDelete = h.getItemView<ImageView>(R.id.iv_delete)

            if (!bean.image.isNullOrEmpty()) {
                Glide.with(context!!)
                    .load(bean.image)
                    .placeholder(R.drawable.drawer_user)
                    .into(ivProfile)
            }

            tvValueStyle.text = if (bean.value_style.isNullOrEmpty()) "" else bean.value_style
            tvJob.text = if (bean.job.isNullOrEmpty()) "" else bean.job
            tvName.text = if (bean.nickname.isNullOrEmpty()) "" else bean.nickname

            ivDelete.setOnClickListener(View.OnClickListener {
                // todo : 여기는 잘 모르겠습니다
            })
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }

    /**
     * Http
     * 어필러 추천
     */
    private fun getAppealerSearch() {

        DAClient.searchAppealer(object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()

                if (code == DAClient.SUCCESS) {
                    val json = JSONObject(body)
                    val appealers = json.getJSONArray("appealers")
                    mAdapter!!.clear()
                    for (i in 0 until appealers.length()) {
                        val appealer = appealers.getJSONObject(i)
                        val bean = Gson().fromJson<BeanAppealer>(
                            appealer.toString(),
                            BeanAppealer::class.java
                        )
                        if (bean.idx != null)
                            mAdapter!!.add(bean)
                    }
                }
            }
        })
    }

    /**
     * Http
     * 어필러 검색
     */
    private fun getAppealerSearch(keyword: String) {

        DAClient.searchAppealer(keyword, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()

                if (code == DAClient.SUCCESS) {
                    val json = JSONObject(body)
                    val appealers = json.getJSONArray("appealers")
                    mAdapter!!.clear()
                    for (i in 0 until appealers.length()) {
                        val appealer = appealers.getJSONObject(i)
                        val bean = Gson().fromJson<BeanAppealer>(
                            appealer.toString(),
                            BeanAppealer::class.java
                        )
                        mAdapter!!.add(bean)
                    }
                }
            }
        })
    }

    /**
     * 검색 Listener
     */
    override fun onSearch(keyword: String) {
        if (keyword.isNullOrEmpty()) {
            getAppealerSearch()
        } else {
            getAppealerSearch(keyword)
        }
    }
}