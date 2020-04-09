package com.truevalue.dreamappeal.fragment.timeline.appeal

import android.view.LayoutInflater
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base_new.adapter.BaseAdapter
import com.truevalue.dreamappeal.base_new.adapter.BaseHolder
import com.truevalue.dreamappeal.fragment.timeline.appeal.view_holder.ActionPostViewHolder
import com.truevalue.dreamappeal.utils.load
import kotlinx.android.synthetic.main.item_image.*


class ImageAdapter(
    inflater: LayoutInflater
) :
    BaseAdapter<String, BaseHolder<String>>(inflater) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<String> {
        return object : BaseHolder<String>(
            inflater.inflate(R.layout.item_image, parent, false)
        ) {
            override fun bind(data: String, position: Int) {
                ivLogo.load(data)
            }
        }
    }

}