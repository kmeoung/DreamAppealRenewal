package com.truevalue.dreamappeal.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.truevalue.dreamappeal.R
import java.util.*
import kotlin.collections.ArrayList

class BasePagerAdapter<String>(private val mContext: Context,private var isCrop : Boolean? = false) :
    PagerAdapter() {
    private val mArray: ArrayList<String>

    override fun getCount(): Int {
        return mArray.size
    }

    fun add(item: String) {
        mArray.add(item)
    }

    fun clear(){
        mArray.clear()
    }

    operator fun get(i: Int): String {
        return mArray[i]
    }

    fun getAll() : ArrayList<String>{
        return mArray
    }

    override fun isViewFromObject(
        view: View,
        `object`: Any
    ): Boolean {
        return view === `object`
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {
        container.removeView(`object` as ImageView)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(mContext)
        val url = mArray[position]
        if(isCrop == false) {
            Glide.with(mContext)
                .load(url)
                .placeholder(R.drawable.ic_image_white)
                .centerCrop()
                .thumbnail(0.1f)
                .into(imageView)
        }else{
            Glide.with(mContext)
                .load(url)
                .placeholder(R.drawable.ic_image_white)
                .into(imageView)
        }
        container.addView(imageView, 0)
        return imageView
    }

    init {
        mArray = ArrayList()
    }
}