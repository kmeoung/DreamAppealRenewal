package com.truevalue.dreamappeal.fragment.timeline.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base_new.adapter.BaseAdapter
import com.truevalue.dreamappeal.base_new.adapter.BaseHolder
import com.truevalue.dreamappeal.fragment.timeline.appeal.view_holder.ActionPostViewHolder
import com.truevalue.dreamappeal.fragment.timeline.appeal.view_holder.IdeaPostViewHolder
import com.truevalue.dreamappeal.fragment.timeline.appeal.view_holder.LifePostViewHolder
import com.truevalue.dreamappeal.fragment.timeline.appeal.view_holder.SharedPostViewHolder


class TimeLineDataAdapter(
    inflater: LayoutInflater
//    , var onClickFollow: (TimeLineData) -> Unit
//    , var onClickInvite: (TimeLineData) -> Unit
) :
    BaseAdapter<TimeLineData, BaseHolder<TimeLineData>>(inflater) {
    override fun getItemViewType(position: Int): Int {
        return dataSource[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<TimeLineData> {
        return when (viewType) {
            0 -> ActionPostViewHolder(inflater, parent)
            1 -> LifePostViewHolder(inflater, parent)
            2 -> IdeaPostViewHolder(inflater, parent)
            else -> SharedPostViewHolder(inflater, parent)
        }
    }

}

data class TimeLineData(
    var type: Int = 0
) {

}