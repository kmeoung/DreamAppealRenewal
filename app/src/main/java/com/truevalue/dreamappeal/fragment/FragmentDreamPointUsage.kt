package com.truevalue.dreamappeal.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityDreamPoint
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.bean.BeanDreamPointGetUsage
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_dream_point_usage.*
import okhttp3.Call
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class FragmentDreamPointUsage : BaseFragment() {

    private val VIEW_TYPE_GET = 0
    private val VIEW_TYPE_USE = 1

    private var mViewType = VIEW_TYPE_GET

    private var mAdapter: BaseRecyclerViewAdapter2<BeanDreamPointGetUsage>? = null

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
//        bindTempData()
        // Tab View 설정
        setTabView(mViewType)

    }

    private fun bindTempData() {
        for (i in 0..10) {
            mAdapter!!.add(BeanDreamPointGetUsage("","",0,"2019-11-13 12:11:11"))
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
                tv_accumulate.text = getString(R.string.str_accumulate_get)
                // todo : 여기가 정말 적당한 위치인지 추후에 확인 필요
                getHistory()
            }
            VIEW_TYPE_USE -> {
                tv_use_list.isSelected = true
                tv_get_list.isSelected = false
                iv_get_list.visibility = INVISIBLE
                iv_use_list.visibility = VISIBLE
                tv_accumulate.text = getString(R.string.str_accumulate_use)
                // todo : 현재 정책이 나온것이 없습니다.
                if(mAdapter != null){
                    mAdapter!!.clear()
                    tv_point.text = 0.toString()
                }
            }
        }
    }

    /**
     * Adapter Init
     */
    private fun initAdatper() {
        mAdapter = BaseRecyclerViewAdapter2(recyclerViewListener)
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
            if(mAdapter != null) {
                val bean = mAdapter!!.get(i)

                val tvTitle = h.getItemView<TextView>(R.id.tv_title)
                val tvDate = h.getItemView<TextView>(R.id.tv_date)
                val tvPoint = h.getItemView<TextView>(R.id.tv_point)
                val totalName = "${bean.type} ${bean.name}"
                tvTitle.text = Utils.replaceTextColor(context,totalName,bean.name)
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val sdf2 = SimpleDateFormat("yyyy. MM. dd HH:mm:ss")
                val date = sdf.parse(bean.date)
                tvDate.text = sdf2.format(date)
                tvPoint.text = String.format("%,d",bean.point)
            }
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
                            val missionPointList = json.getJSONArray("get_point_list")
                            for (i in 0 until missionPointList.length()) {
                                val bean = Gson().fromJson<BeanDreamPointGetUsage>(
                                    missionPointList.get(i).toString(),
                                    BeanDreamPointGetUsage::class.java
                                )
                                mAdapter!!.add(bean)
                            }
                        } catch (e: Exception) { }

                    }
                }
            }
        })
    }
}