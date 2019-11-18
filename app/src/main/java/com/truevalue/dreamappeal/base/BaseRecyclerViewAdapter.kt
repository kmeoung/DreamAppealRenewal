package com.truevalue.dreamappeal.base

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import java.util.*
import kotlin.collections.ArrayList

class BaseRecyclerViewAdapter(listener: IORecyclerViewListener) :
    RecyclerView.Adapter<BaseViewHolder>() {
    private val mListener = listener
    val mArray : ArrayList<Any>

    init {
        mArray = java.util.ArrayList<Any>()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder {
        return mListener.onCreateViewHolder(p0, p1)
    }

    override fun getItemCount(): Int = mListener.itemCount

    override fun onBindViewHolder(p0: BaseViewHolder, p1: Int) {
        mListener.onBindViewHolder(p0, p1)
    }

    override fun getItemViewType(position: Int): Int = mListener.getItemViewType(position)

    fun add(t: Any) {
        mArray.add(t)
        notifyDataSetChanged()
    }

    fun get(i: Int): Any = mArray[i]

    fun clear(){
        mArray.clear()
        notifyDataSetChanged()
    }

    fun remove(i : Int){
        mArray.removeAt(i)
        notifyDataSetChanged()
    }

    fun size() : Int = mArray.size

    fun getList() : ArrayList<Any>{
        return mArray
    }
}


