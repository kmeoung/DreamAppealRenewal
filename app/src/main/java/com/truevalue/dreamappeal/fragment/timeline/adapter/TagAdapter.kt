package com.truevalue.dreamappeal.fragment.timeline.adapter

import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base_new.adapter.BaseAdapter
import com.truevalue.dreamappeal.base_new.adapter.BaseHolder
import kotlinx.android.synthetic.main.item_mart_detail_tag.*


class TagAdapter(inflater: LayoutInflater) : BaseAdapter<String, BaseHolder<String>>(inflater) {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): BaseHolder<String> {
        return ViewHolder(inflater.inflate(R.layout.item_mart_detail_tag, parent, false))
    }

    inner class ViewHolder(itemView: View) : BaseHolder<String>(itemView) {
        override fun bind(data: String, position: Int) {
            tvHashtag.text = data
        }
    }
}