package com.truevalue.dreamappeal.fragment.timeline.nearby.search_address

import android.view.LayoutInflater
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base_new.adapter.BaseAdapter
import com.truevalue.dreamappeal.base_new.adapter.BaseHolder
import kotlinx.android.synthetic.main.item_address.*


class AddressAdapter(
    inflater: LayoutInflater, var onClick: (String) -> Unit
) :
    BaseAdapter<String, BaseHolder<String>>(inflater) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<String> {
        return object : BaseHolder<String>(
            inflater.inflate(R.layout.item_address, parent, false)
        ) {
            init {
                itemView.setOnClickListener {
                    onClick.invoke(dataSource[adapterPosition])
                }
            }

            override fun bind(data: String, position: Int) {
                tvAddress.text = data
            }
        }
    }

}