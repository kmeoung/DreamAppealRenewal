package com.truevalue.dreamappeal.base_new.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

open class BaseHolder<V>(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {
    open fun bind(data: V, position: Int){}
    override val containerView: View?
        get() = itemView
}
