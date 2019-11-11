package com.truevalue.dreamappeal.fragment

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
import kotlinx.android.synthetic.main.fragment_dream_point.*

class FragmentDreamPoint : BaseFragment() {

    private var mAdapter : BaseRecyclerViewAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dream_point, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // recyclerview adapter init
        initAdatper()
        // View Click Listener
        onClickView()
        // 임시 데이터 Bind
        bindTempData()
    }

    /**
     * Bind Temp Data
     */
    private fun bindTempData(){
        for(i in 1 .. 10){
            mAdapter!!.add("")
        }
    }

    /**
     * Adapter Init
     */
    private fun initAdatper(){
        mAdapter = BaseRecyclerViewAdapter(listener)
        rv_mission.adapter = mAdapter
        rv_mission.layoutManager = LinearLayoutManager(context)
    }

    /**
     * View OnClick Listener
     */
    private fun onClickView(){

    }

    val listener = object : IORecyclerViewListener{
        override val itemCount: Int
            get() = if(mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_mission,parent,false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {

        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}