package com.truevalue.dreamappeal.fragment.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_dream_point_usage.*
import kotlinx.android.synthetic.main.fragment_notification.*

class FragmentNotification : BaseFragment() {

    private val VIEW_TYPE_FOLLOWING = 0
    private val VIEW_TYPE_MY_NOTI = 1

    private var mViewType = VIEW_TYPE_FOLLOWING

    private var mAdapter: BaseRecyclerViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_notification, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        initView()
        // init RecyclerView Adapter
        initAdapter()
        // 상단 View Type 설정
        setTabView(mViewType)
        // View Click Listener
        onClickView()
    }

    /**
     * View 초기화
     */
    private fun initView() {
    }

    /**
     * RecycerView Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvNotiListener)
        rv_noti.adapter = mAdapter
        rv_noti.layoutManager = LinearLayoutManager(context!!)
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                tv_following->{
                    setTabView(VIEW_TYPE_FOLLOWING)
                }
                tv_my_noti->{
                    setTabView(VIEW_TYPE_MY_NOTI)
                }
            }
        }
        tv_following.setOnClickListener(listener)
        tv_my_noti.setOnClickListener(listener)
    }

    /**
     * 상단 탭 설정
     */
    private fun setTabView(view_type: Int) {
        mViewType = view_type
        when (view_type) {
            VIEW_TYPE_FOLLOWING -> {
                tv_following.isSelected = true
                tv_my_noti.isSelected = false
                iv_following.visibility = View.VISIBLE
                iv_my_noti.visibility = View.INVISIBLE

            }
            VIEW_TYPE_MY_NOTI -> {
                tv_following.isSelected = false
                tv_my_noti.isSelected = true
                iv_following.visibility = View.INVISIBLE
                iv_my_noti.visibility = View.VISIBLE
            }
        }
    }


    /**
     * Notification RecyclerView Listener
     */
    val rvNotiListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_select_step_object, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            val tvTitle = h.getItemView<TextView>(R.id.tv_title)
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}