package com.truevalue.dreamappeal.fragment.profile.blueprint

import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.fragment_blueprint.*

class FragmentBlueprint : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private var mAnOAdapter: BaseRecyclerViewAdapter? = null // Ability & Opportunity Adapter
    private var mObjectAdapter: BaseRecyclerViewAdapter? = null // Object Adapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_blueprint, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        initView()
        // RecyclerView Adpater 초기화
        initAdapter()
        // bind Temp Data
        bindTempData()
    }

    private fun bindTempData() {
        for (i in 0..10) {
            mAnOAdapter!!.add("")
            mObjectAdapter!!.add("")
        }
    }

    /**
     * Init View
     */
    private fun initView() {
        Utils.setSwipeRefreshLayout(srl_refresh, this)
    }

    /**
     * Init RecyclerView Adapter
     */
    private fun initAdapter() {

        mAnOAdapter = BaseRecyclerViewAdapter(abilityAndOpportunityListener)
        mObjectAdapter = BaseRecyclerViewAdapter(objectListener)

        rv_ability_and_opportunity.adapter = mAnOAdapter
        rv_object.adapter = mObjectAdapter

        rv_ability_and_opportunity.layoutManager =
            LinearLayoutManager(context)
        rv_object.layoutManager = LinearLayoutManager(context)
    }

    /**
     * Ability And Opportunity
     * RecyclerView Listener
     */
    private val abilityAndOpportunityListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() =
                if (mAnOAdapter != null) mAnOAdapter!!.mArray.size else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
            BaseViewHolder.newInstance(R.layout.listitem_dot_text, parent, false)

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
        }

        override fun getItemViewType(i: Int): Int = 0
    }

    /**
     * Object
     * RecyclerView Listener
     */
    private val objectListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() =
                if (mObjectAdapter != null) mObjectAdapter!!.mArray.size else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
            BaseViewHolder.newInstance(R.layout.listitem_object, parent, false)

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
        }

        override fun getItemViewType(i: Int): Int = 0
    }

    override fun onRefresh() {
        // 여기서 서버 Refresh
        srl_refresh.isRefreshing = false
    }
}