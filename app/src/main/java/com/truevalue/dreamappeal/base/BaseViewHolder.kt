package com.truevalue.dreamappeal.base

import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class BaseViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun <T : View> getItemView(resid: Int): T {
        return itemView.findViewById(resid)
    }

    companion object {
        fun newInstance(
            layoutid: Int,
            viewGroup: ViewGroup,
            attachToRoot: Boolean
        ): BaseViewHolder {
            val view =
                LayoutInflater.from(viewGroup.context).inflate(layoutid, viewGroup, attachToRoot)
            return BaseViewHolder(view)
        }
    }

}