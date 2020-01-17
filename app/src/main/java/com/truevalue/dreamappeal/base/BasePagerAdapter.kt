package com.truevalue.dreamappeal.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.truevalue.dreamappeal.bean.BeanPromotion

/**
 * Promotion Pager Adapter
 */
class BasePagerAdapter(context: Context?, listener: IOBasePagerListener) :
    PagerAdapter() {

    private val mContext: Context?
    private val mArray: ArrayList<Any>
    private val mListener: IOBasePagerListener?

    interface IOBasePagerListener {
        fun onBindViewPager(any: Any, view: ImageView, position: Int)
    }

    init {
        mContext = context
        mListener = listener
    }

    override fun getCount(): Int {
        return mArray.size
    }

    fun add(item: Any) {
        mArray.add(item)
    }

    fun clear() {
        mArray.clear()
    }

    operator fun get(i: Int): Any {
        return mArray[i]
    }

    fun getAll(): ArrayList<Any> {
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
        val view = ImageView(mContext)
        val bean = mArray[position]

        mListener?.let { listener ->
            listener.onBindViewPager(bean, view, position)
        }
//

        container.addView(view, 0)
        return view
    }

    init {
        mArray = ArrayList()
    }
}