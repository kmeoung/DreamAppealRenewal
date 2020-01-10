package com.truevalue.dreamappeal.fragment.dream_board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.bean.BeanTimeline
import com.truevalue.dreamappeal.fragment.timeline.FragmentTimeline
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.fragment_event.*

class FragmentEvent : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter? = null

    companion object{
        private const val RV_TYPE_ITEM = 0
        private const val RV_TYPE_LOADING = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_event, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        initView()
        // RecyclerView Adapter 초기화
        initAdapter()
        // View Click Listener
        onClickView()
        // 임시 데이터 bind
        bindTempData()
    }

    /**
     * View 초기화
     */
    private fun initView(){
        rl_images.run {
            Utils.setImageViewSquare(context,this,7,3)
        }
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = mAdapter ?: BaseRecyclerViewAdapter(rvEventListener)
        rv_event.run {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    /**
     * View Click Listener
     */
    private fun onClickView(){
        val listener = View.OnClickListener{
            when(it){
                iv_refresh->{

                }
            }
        }
        iv_refresh.setOnClickListener(listener)
    }

    private fun bindTempData(){
        mAdapter?.let {
            for(i in 0 .. 100){
                it.add("")
            }
        }

    }


    /**
     * RecyclerView Event Listener
     */
    private val rvEventListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = mAdapter?.size() ?: 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            when(viewType){
                RV_TYPE_ITEM->
                    return BaseViewHolder.newInstance(R.layout.listitem_event, parent, false)
                RV_TYPE_LOADING->
                    return BaseViewHolder.newInstance(R.layout.listitem_white_more, parent, false)
            }
            return BaseViewHolder.newInstance(R.layout.listitem_event, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            if (RV_TYPE_ITEM == getItemViewType(i)) {
                val ivEvent = h.getItemView<ImageView>(R.id.iv_event)
                Utils.setImageViewSquare(context,ivEvent,100,23)
            } else if (RV_TYPE_LOADING == getItemViewType(i)) {
//                getTimeLineData(
//                    true,
//                    (mAdatper!!.get(mAdatper!!.size() - 1) as BeanTimeline).idx,
//                    false
//                )
            }
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}