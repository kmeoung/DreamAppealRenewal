package com.truevalue.dreamappeal.fragment.dream_board.concern

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener

class FragmentConcern : BaseFragment() {

    private var mAdapter : BaseRecyclerViewAdapter?

    init {
        mAdapter = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_concern, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView Adapter 초기화
        initAdapter()
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter(){
        mAdapter = BaseRecyclerViewAdapter(rvListener)
    }

    /**
     * Concern RecyclerView Listener
     */
    private val rvListener = object : IORecyclerViewListener{
        override val itemCount: Int
            get() = mAdapter?.size() ?: 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getItemViewType(i: Int): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}