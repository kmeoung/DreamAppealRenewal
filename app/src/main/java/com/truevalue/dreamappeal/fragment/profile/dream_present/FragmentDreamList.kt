package com.truevalue.dreamappeal.fragment.profile.dream_present

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import kotlinx.android.synthetic.main.action_bar_profile_other.*
import kotlinx.android.synthetic.main.fragment_dream_list.*

class FragmentDreamList : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

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
        // action Bar 설정
        iv_menu.visibility = GONE
        iv_back.visibility = VISIBLE
        iv_search.visibility = INVISIBLE
        tv_title.text = getString(R.string.str_title_dream_list)
        // Swipe Refresh Listener
        srl_refresh.setOnRefreshListener(this)
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
        val listener = View.OnClickListener {
            when (it) {
                btn_edit -> {
                    isEdit = !isEdit
                    // todo : 추가 작업 필요
                }
                iv_back->{
                    activity?.onBackPressed()
                }
            }
        }

        btn_edit.setOnClickListener(listener)
        iv_back.setOnClickListener(listener)
    }

    override fun onRefresh() {
        // 여기서 서버 호출
        // todo : 드림 리스트는 재 호출이 필요한가 고민이 필요함
        srl_refresh.isRefreshing = true
    }

    private val recyclerViewListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.mArray.size else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
            BaseViewHolder.newInstance(R.layout.listitem_dream_list, parent, false)

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
        }

        override fun getItemViewType(i: Int): Int = 0
    }
}