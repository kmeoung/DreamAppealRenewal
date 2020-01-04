package com.truevalue.dreamappeal.fragment.dream_board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener

class FragmentAddBoard : BaseFragment() {

    private var mAdapter : BaseRecyclerViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_board, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // View 초기화
        initView()
        // Adapter 초기화
        initAdapter()
        // View Click 초기화
        onClickView()
    }

    /**
     * View 초기화
     */
    private fun initView(){

    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter(){

    }

    /**
     * View Click 초기화
     */
    private fun onClickView(){

    }

    /**
     * RecyclerView 이미지 Listener
     */
    private val rvImageListener = object : IORecyclerViewListener{
        override val itemCount: Int
            get() = if(mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {

        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}