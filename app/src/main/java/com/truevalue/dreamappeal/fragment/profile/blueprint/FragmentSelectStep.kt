package com.truevalue.dreamappeal.fragment.profile.blueprint

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
import kotlinx.android.synthetic.main.fragment_object_step.*

class FragmentSelectStep : BaseFragment(){

    private var mAdapter : BaseRecyclerViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_recyclerview,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 초기화Dr
        initAdapter()
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvListener)
        rv_achivement_ing.adapter = mAdapter
        rv_achivement_ing.layoutManager = LinearLayoutManager(context)
    }

    private val rvListener = object : IORecyclerViewListener{
        private val VIEW_TYPE_TOPIC = 0
        private val VIEW_TYPE_HEADER = 1
        private val VIEW_TYPE_OBJECT = 2
        private val VIEW_TYPE_DETAIL = 3


        override val itemCount: Int
            get() = if(mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            when(viewType){
                VIEW_TYPE_TOPIC->return BaseViewHolder.newInstance(R.layout.listitem_select_step_topic,parent,false)
                VIEW_TYPE_HEADER->return BaseViewHolder.newInstance(R.layout.listitem_select_step_header,parent,false)
                VIEW_TYPE_OBJECT->return BaseViewHolder.newInstance(R.layout.listitem_select_step_object,parent,false)
                else->return BaseViewHolder.newInstance(R.layout.listitem_select_detail_step,parent,false)
            }
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {

        }

        override fun getItemViewType(i: Int): Int {
            // todo : 여기서 분기처리
            return 0
        }
    }
}