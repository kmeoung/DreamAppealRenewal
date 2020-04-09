package com.truevalue.dreamappeal.fragment.timeline.appeal

import android.view.LayoutInflater
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base_new.adapter.BaseAdapter
import com.truevalue.dreamappeal.base_new.adapter.BaseHolder
import com.truevalue.dreamappeal.fragment.timeline.appeal.view_holder.ActionPostViewHolder


class TimeLineDataAdapter(
    inflater: LayoutInflater
//    , var onClickFollow: (TimeLineData) -> Unit
//    , var onClickInvite: (TimeLineData) -> Unit
) :
    BaseAdapter<TimeLineData, BaseHolder<TimeLineData>>(inflater) {
    override fun getItemViewType(position: Int): Int {
        return when (dataSource[position].type) {
            0 -> R.layout.item_action_post
            else -> R.layout.item_action_post
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<TimeLineData> {
        return when (viewType) {
            R.layout.item_action_post -> ActionPostViewHolder(
                inflater.inflate(viewType, parent, false)
            )
            else -> ActionPostViewHolder(inflater.inflate(viewType, parent, false))
        }
    }

}

data class TimeLineData(
    var type: Int = 0
) {

}