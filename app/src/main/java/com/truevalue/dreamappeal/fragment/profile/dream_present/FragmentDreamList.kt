package com.truevalue.dreamappeal.fragment.profile.dream_present

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import kotlinx.android.synthetic.main.action_bar_main.*
import kotlinx.android.synthetic.main.fragment_dream_list.*

class FragmentDreamList : BaseFragment(){

    private var mAdapter: BaseRecyclerViewAdapter? = null
    private var isEdit = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dream_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        initView()
        // RecyclerView Adapter 초기화
        initAdapter()
        // View 클릭 리스너
        onClickView()
    }

    /**
     * Init View
     */
    private fun initView() {
        // activityMain 가져오기
        val activityMain = (activity as ActivityMain)
        // action Bar 설정
        activityMain.mMainViewType = ActivityMain.ACTION_BAR_TYPE_PROFILE_OTHER
        activityMain.iv_menu.visibility = GONE
        activityMain.iv_back_blue.visibility = VISIBLE
        activityMain.iv_search.visibility = INVISIBLE
        activityMain.tv_title.text = getString(R.string.str_title_dream_list)
    }

    /**
     * Init Adapter
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(recyclerViewListener)
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = OnClickListener {
            when (it) {
                btn_edit -> {
                    isEdit = !isEdit
                    // todo : 추가 작업 필요
                }
                (activity as ActivityMain).iv_back_blue->{
                    activity?.onBackPressed()
                }
            }
        }

        btn_edit.setOnClickListener(listener)
        (activity as ActivityMain).iv_back_blue.setOnClickListener(listener)
    }

    private val recyclerViewListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
            BaseViewHolder.newInstance(R.layout.listitem_dream_list, parent, false)

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
        }

        override fun getItemViewType(i: Int): Int = 0
    }
}