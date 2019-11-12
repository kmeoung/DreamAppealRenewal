package com.truevalue.dreamappeal.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityDreamPoint
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanDreamPointGetUsage
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_dream_point_usage.*
import okhttp3.Call
import org.json.JSONObject

class FragmentDreamPointUsage : BaseFragment() {

    private val VIEW_TYPE_GET = 0
    private val VIEW_TYPE_USE = 1

    private var mViewType = VIEW_TYPE_GET

    private var mAdapter: BaseRecyclerViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dream_point_usage, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View init
        initView()
        // RecyclerView Adapter
        initAdatper()
        // OnClickListener
        onClickView()
        // 임시 데이터 바인딩
        bindTempData()
        // Tab View 설정
        setTabView(mViewType)

    }

    private fun bindTempData() {
        for (i in 0..10) {
            mAdapter!!.add("")
        }
    }

    /**
     * View Init
     */
    private fun initView() {
        // Action Bar 설정
        (activity as ActivityDreamPoint).iv_back_black.visibility = View.GONE
        (activity as ActivityDreamPoint).iv_back_blue.visibility = View.VISIBLE
        (activity as ActivityDreamPoint).iv_check.visibility = View.GONE
        (activity as ActivityDreamPoint).tv_title.text = getString(R.string.str_dream_point_usage)

    }

    /**
     * 상단 Tab 설정
     */
    private fun setTabView(view_type: Int) {
        mViewType = view_type
        when (view_type) {
            VIEW_TYPE_GET -> {
                tv_get_list.isSelected = true
                tv_use_list.isSelected = false
                iv_get_list.visibility = VISIBLE
                iv_use_list.visibility = INVISIBLE
                // todo : 여기가 정말 적당한 위치인지 추후에 확인 필요
                getHistory()
            }
            VIEW_TYPE_USE -> {
                tv_use_list.isSelected = true
                tv_get_list.isSelected = false
                iv_get_list.visibility = INVISIBLE
                iv_use_list.visibility = VISIBLE
            }
        }
    }

    /**
     * Adapter Init
     */
    private fun initAdatper() {
        mAdapter = BaseRecyclerViewAdapter(recyclerViewListener)
        rv_usage.adapter = mAdapter
        rv_usage.layoutManager = LinearLayoutManager(context)
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                (activity as ActivityDreamPoint).iv_back_blue -> activity!!.onBackPressed()
                tv_get_list -> setTabView(VIEW_TYPE_GET)
                tv_use_list -> setTabView(VIEW_TYPE_USE)
            }
        }

        (activity as ActivityDreamPoint).iv_back_blue.setOnClickListener(listener)
        tv_get_list.setOnClickListener(listener)
        tv_use_list.setOnClickListener(listener)
    }

    /**
     * RecyclerView Listener
     */
    private val recyclerViewListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_dream_point_usage, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {

        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }

    /**
     * Http
     * 획득내역 리스트 가져오기
     */
    private fun getHistory() {
        DAClient.historyGet(object : DAHttpCallback {
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
                        val json = JSONObject(body)
                        mAdapter!!.clear()
                        try {
                            val missionPointList = json.getJSONArray("mission_point_list")
                            for (i in 0 until missionPointList.length()) {
                                val bean = Gson().fromJson<BeanDreamPointGetUsage>(
                                    missionPointList.get(i).toString(),
                                    BeanDreamPointGetUsage::class.java
                                )
                                mAdapter!!.add(bean)
                            }
                        } catch (e: Exception) { }

                        try {
                            val couponPointList = json.getJSONArray("coupon_point_list")
                            for (i in 0 until couponPointList.length()) {
                                val bean = Gson().fromJson<BeanDreamPointGetUsage>(
                                    couponPointList.get(i).toString(),
                                    BeanDreamPointGetUsage::class.java
                                )
                                mAdapter!!.add(bean)
                            }
                        } catch (e: Exception) {
                        }
                    }
                }
            }
        })
    }
}