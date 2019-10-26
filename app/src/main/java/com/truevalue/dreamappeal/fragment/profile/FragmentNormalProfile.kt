package com.truevalue.dreamappeal.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import kotlinx.android.synthetic.main.fragment_normal_profile.*

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

        // RecyclerView Adatpr init
        initAdapter()
        // bind Temp Data
        bindTempData()
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