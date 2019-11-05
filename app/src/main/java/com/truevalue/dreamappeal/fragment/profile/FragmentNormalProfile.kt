package com.truevalue.dreamappeal.fragment.profile

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
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_normal_profile.*
import okhttp3.Call

class FragmentNormalProfile : BaseFragment() {

    // Recyclerview Adapter
    private var mAdapter: BaseRecyclerViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_normal_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initView
        initView()
        // View Click
        onClickView()
        // 내 유저 데이터 가져오기
        getUserProfile()
        // RecyclerView Adatpr init
        initAdapter()
        // bind Temp Data
        bindTempData()
    }

    /**
     * View 초기화
     */
    private fun initView() {
        // 상단 바 설정
        iv_back_black.visibility = View.GONE
        iv_back_blue.visibility = View.VISIBLE
        tv_title.text = getString(R.string.str_normal_profile)
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_back_blue -> activity!!.onBackPressed()
                iv_edit_normal_profile -> {
                    (activity as ActivityMain).replaceFragment(FragmentNormalProfileEdit(), true)
                }
                iv_add_group -> {

                }
            }
        }

        iv_back_blue.setOnClickListener(listener)
        iv_edit_normal_profile.setOnClickListener(listener)
        iv_add_group.setOnClickListener(listener)
    }

    /**
     * HTTP
     * 개인정보 조회
     */
    private fun getUserProfile(){
        DAClient.getMyUserData(object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if(context != null){
                    Toast.makeText(context!!.applicationContext,message, Toast.LENGTH_SHORT).show()

                    if(code == DAClient.SUCCESS){
                        // todo : 데이터 바인딩 필요
                    }
                }
            }
        })
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(listener)
        rv_normal_profile_group.adapter = mAdapter
        rv_normal_profile_group.layoutManager = LinearLayoutManager(context)
    }

    /**
     * Bind Temp Data
     */
    private fun bindTempData(){
        for(i in 1 .. 10){
            mAdapter!!.add("")
        }
    }

    private val listener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_normal_profile_group, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {

        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}