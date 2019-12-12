package com.truevalue.dreamappeal.fragment.profile.blueprint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanCategory
import com.truevalue.dreamappeal.bean.BeanCategoryDetail
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_level_choice.*
import okhttp3.Call
import org.json.JSONObject

class FragmentLevelChoice : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter? = null
    private var mAdapterDetail: BaseRecyclerViewAdapter? = null

    private val TYPE_IDEA = 0
    private val TYPE_LIKE = 1
    private val TYPE_ACTION_POST = 2

    private var mCategoryType = TYPE_IDEA


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_level_choice, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        initView()
        // Recyclerview 초기화
        initAdapter()
        // View Click Listener
        onClickListener()
        // 초기화
        setList(mCategoryType)
    }

    /**
     * View 초기화
     */
    private fun initView() {
        tv_title.text = getString(R.string.str_title_level_choice)
        iv_back_blue.visibility = VISIBLE
        iv_back_black.visibility = GONE
        iv_check.visibility = VISIBLE
    }

    /**
     * View Click Listener
     */
    private fun onClickListener() {
        val listener = View.OnClickListener {
            when (it) {
                iv_back_blue -> (activity!!.onBackPressed())
                iv_check -> {

                }
                ll_idea -> {
                    setList(TYPE_IDEA)
                }
                ll_like -> {
                    setList(TYPE_LIKE)
                }
                ll_action_post -> {
                    setList(TYPE_ACTION_POST)
                }
            }
        }
        iv_back_blue.setOnClickListener(listener)
        iv_check.setOnClickListener(listener)
        ll_idea.setOnClickListener(listener)
        ll_like.setOnClickListener(listener)
        ll_action_post.setOnClickListener(listener)
    }

    /**
     * type 설정
     */
    private fun setList(type: Int) {
        mCategoryType = type
        when (type) {
            TYPE_IDEA -> {
                rv_category.visibility = GONE
                rv_category_detail.visibility = GONE
                iv_check_idea.isSelected = true
                iv_check_like.isSelected = false
                iv_check_action_post.isSelected = false
                tv_category.visibility = GONE
                tv_category_detail.visibility = GONE
            }
            TYPE_LIKE -> {
                rv_category.visibility = GONE
                rv_category_detail.visibility = GONE
                iv_check_idea.isSelected = false
                iv_check_like.isSelected = true
                iv_check_action_post.isSelected = false
                tv_category.visibility = GONE
                tv_category_detail.visibility = GONE
            }
            TYPE_ACTION_POST -> {
                rv_category.visibility = VISIBLE
                rv_category_detail.visibility = VISIBLE
                iv_check_idea.isSelected = false
                iv_check_like.isSelected = false
                iv_check_action_post.isSelected = true
                tv_category.visibility = VISIBLE
                tv_category_detail.visibility = VISIBLE
                // 실천인증 가져오기
                getCategory()
            }
        }

    }

    /**
     * Http
     * 실천인증 가져오기
     */
    private fun getCategory() {
        DAClient.getActionPostCategoty(object : DAHttpCallback {
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
                        mAdapter!!.clear()
                        val `object` = JSONObject(body)
                        val objects = `object`.getJSONArray("objects")
                        for (i in 0 until objects.length()) {
                            val bean = Gson().fromJson<BeanCategory>(
                                objects.getJSONObject(i).toString(),
                                BeanCategory::class.java
                            )
                            mAdapter!!.add(bean)
                        }
                    }
                }
            }
        })
    }

    /**
     * Http
     * 실천인증 세부단계 가져오기
     */
    private fun getCategoryDetail(idx: Int) {
        DAClient.getActionPostCategotyDetail(idx, object : DAHttpCallback {
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
                        mAdapterDetail!!.clear()
                        val `object` = JSONObject(body)
                        val objects = `object`.getJSONArray("object_steps")
                        for (i in 0 until objects.length()) {
                            val bean = Gson().fromJson<BeanCategoryDetail>(
                                objects.getJSONObject(i).toString(),
                                BeanCategoryDetail::class.java
                            )
                            mAdapterDetail!!.add(bean)
                        }
                    }
                }
            }
        })
    }


    /**
     * Recycler View Adapter
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvListener)
        rv_category.adapter = mAdapter
        rv_category.layoutManager = LinearLayoutManager(context)

        mAdapterDetail = BaseRecyclerViewAdapter(rvListenerDetail)
        rv_category_detail.adapter = mAdapterDetail
        rv_category_detail.layoutManager = LinearLayoutManager(context)
    }

    private var selectedCategoryIdx = -1
    private var selectedCategoryDetailIdx = -1

    private val rvListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(
                R.layout.listitem_level_choice,
                parent,
                false
            )
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            if(mAdapter != null){
                val bean = mAdapter!!.get(i) as BeanCategory
                val llBg = h.getItemView<LinearLayout>(R.id.ll_bg)
                val tvCircle = h.getItemView<TextView>(R.id.tv_circle)
                val tvTitle = h.getItemView<TextView>(R.id.tv_title)

                tvTitle.text = bean.object_name

                if(selectedCategoryIdx == bean.idx){
                    llBg.setBackgroundColor(ContextCompat.getColor(context!!,R.color.nice_blue))
                    tvCircle.isSelected = true
                    tvTitle.isSelected = true
                }else{
                    llBg.setBackgroundColor(ContextCompat.getColor(context!!,R.color.white))
                    tvCircle.isSelected = false
                    tvTitle.isSelected = false
                }

                llBg.setOnClickListener(View.OnClickListener {
                    selectedCategoryIdx = bean.idx
                    getCategoryDetail(selectedCategoryIdx)
                    mAdapter!!.notifyDataSetChanged()
                })
            }
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }

    private val rvListenerDetail = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapterDetail != null) mAdapterDetail!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(
                R.layout.listitem_level_choice,
                parent,
                false
            )
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            if(mAdapterDetail != null){
                val bean = mAdapterDetail!!.get(i) as BeanCategoryDetail
                val llBg = h.getItemView<LinearLayout>(R.id.ll_bg)
                val tvCircle = h.getItemView<TextView>(R.id.tv_circle)
                val tvTitle = h.getItemView<TextView>(R.id.tv_title)

                tvTitle.text = bean.title

                if(selectedCategoryDetailIdx == bean.idx){
                    llBg.setBackgroundColor(ContextCompat.getColor(context!!,R.color.nice_blue))
                    tvCircle.isSelected = true
                    tvTitle.isSelected = true
                }else{
                    llBg.setBackgroundColor(ContextCompat.getColor(context!!,R.color.white))
                    tvCircle.isSelected = false
                    tvTitle.isSelected = false
                }

                llBg.setOnClickListener(View.OnClickListener {
                    selectedCategoryDetailIdx = bean.idx
                    mAdapterDetail!!.notifyDataSetChanged()
                })
            }
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}