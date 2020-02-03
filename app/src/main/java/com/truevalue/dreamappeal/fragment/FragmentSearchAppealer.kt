package com.truevalue.dreamappeal.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityAddrSearch
import com.truevalue.dreamappeal.activity.ActivitySearch
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanAddress
import com.truevalue.dreamappeal.bean.BeanAppealer
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import kotlinx.android.synthetic.main.fragment_search_appealer.*
import okhttp3.Call
import org.json.JSONObject

class FragmentSearchAppealer : BaseFragment(), ActivitySearch.IOSearchListener {

    private var mAdapter: BaseRecyclerViewAdapter?
    private var mAddr : String?

    companion object {
        private const val TYPE_LOCATION = 0
        private const val TYPE_MODIFIER = 1
    }

    private var mSearchType : Int

    init {
        mSearchType = TYPE_LOCATION
        mAdapter = null
        mAddr = null
    }

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
        // 처음 버튼 설정
        setSearchType(mSearchType)
        // 초반 데이터 가져오기
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
        rv_appealer.run {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                tv_modifier -> {
                    Toast.makeText(
                        context!!.applicationContext,
                        getString(R.string.str_not_ready_yet),
                        Toast.LENGTH_SHORT
                    ).show()
//                    if (mSearchType != TYPE_MODIFIER)
//                        setSearchType(TYPE_MODIFIER)
                }
                tv_location -> {
                    if (mSearchType != TYPE_LOCATION)
                        setSearchType(TYPE_LOCATION)
                }
                tv_addr -> {
                    val intent = Intent(context!!, ActivityAddrSearch::class.java)
                    startActivityForResult(intent, ActivitySearch.REQUEST_ADDR)
                }
            }
        }
        tv_modifier.setOnClickListener(listener)
        tv_location.setOnClickListener(listener)
        tv_addr.setOnClickListener(listener)
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
            get() = mAdapter?.size() ?: 0

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
                    .circleCrop()
                    .into(ivProfile)
            }else{
                Glide.with(context!!)
                    .load(R.drawable.drawer_user)
                    .circleCrop()
                    .into(ivProfile)
            }

            tvValueStyle.text = if (bean.value_style.isNullOrEmpty()) "" else bean.value_style
            tvJob.text = if (bean.job.isNullOrEmpty()) "" else bean.job
            tvName.text = if (bean.nickname.isNullOrEmpty()) "" else bean.nickname

            // todo : 추후 기능 추가
            ivDelete.visibility = GONE
            ivDelete.setOnClickListener {

            }

            if (bean.idx != Comm_Prefs.getUserProfileIndex()) {

                h.itemView.setOnClickListener {
                    val intent = Intent()
                    intent.putExtra(RESULT_REPLACE_USER_IDX, bean.idx)
                    activity!!.setResult(RESULT_CODE, intent)
                    activity!!.finish()
                }
            }
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

                if (code == DAClient.SUCCESS) {

                    mAdapter?.let {mAdapter->
                        val json = JSONObject(body)
                        mAddr = json.getString("address")
                        tv_addr.text = mAddr
                        val appealers = json.getJSONArray("appealers")
                        mAdapter.clear()
                        for (i in 0 until appealers.length()) {
                            val appealer = appealers.getJSONObject(i)
                            val bean = Gson().fromJson<BeanAppealer>(
                                appealer.toString(),
                                BeanAppealer::class.java
                            )
                            if (bean.idx != null)
                                mAdapter.add(bean)
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
                if (code == DAClient.SUCCESS) {
                    mAdapter?.let {
                        val json = JSONObject(body)
                        val appealers = json.getJSONArray("appealers")
                        it.clear()
                        for (i in 0 until appealers.length()) {
                            val appealer = appealers.getJSONObject(i)
                            val bean = Gson().fromJson<BeanAppealer>(
                                appealer.toString(),
                                BeanAppealer::class.java
                            )
                            it.add(bean)
                        }
                    }
                }else{
                    context?.let {
                        Toast.makeText(it.applicationContext,message,Toast.LENGTH_SHORT).show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == ActivitySearch.REQUEST_ADDR) {
                data?.let {
                    val bean =
                        it.getSerializableExtra(ActivityAddrSearch.RESULT_ADDRESS) as BeanAddress
                    setUserAddress(bean)
                }
            }
        }
    }

    /**
     * Http
     * 유저 주소 등록
     */
    private fun setUserAddress(bean: BeanAddress) {
        DAClient.setUserAddress(bean.address_name,
            bean.region_1depth_name,
            bean.region_2depth_name,
            bean.region_3depth_name,
            bean.region_3depth_h_name,
            bean.x,
            bean.y,
            bean.zip_code,
            object : DAHttpCallback {
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
                        .show()
                    if (code == DAClient.SUCCESS) {
                        mAddr = "${bean.region_1depth_name} ${bean.region_2depth_name} ${bean.region_3depth_name}"
                        tv_addr.text = mAddr

                    }
                }
            })
    }
}