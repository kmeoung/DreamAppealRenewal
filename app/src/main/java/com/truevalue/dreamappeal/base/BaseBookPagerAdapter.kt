package com.truevalue.dreamappeal.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.truevalue.dreamappeal.R

class BaseBookPagerAdapter(
    context: Context,
    list: ArrayList<Any>,
    item_id: Int,
    listener: IOBookPagerListener
) :
    PagerAdapter() {

    interface IOBookPagerListener {
        fun onBindViewPager(bean: Any, view: View)
    }

    private var cellWidth = 0f
    private val mInflater: LayoutInflater
    private val mContext: Context
    private val mListener: IOBookPagerListener
    private val mItemId: Int
    var mList: ArrayList<Any>
        private set

    init {
        mContext = context
        mInflater = LayoutInflater.from(context)
        mList = list
        mListener = listener
        mItemId = item_id
    }

    override fun getCount(): Int {
        return mList.size
    }

    override fun getPageWidth(position: Int): Float {
        if (cellWidth <= 0) {
            cellWidth = Math.round(mContext.resources.getDimension(R.dimen.thumb)).toFloat()
        }
        val count = mContext.resources.displayMetrics.widthPixels / cellWidth
        return 1 / count + 0.03f
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val bean = mList[position] // 데이터 받아오기

        val view: View = mInflater.inflate(mItemId, container, false)
        mListener.onBindViewPager(bean, view)
        container.addView(view, 0)
        return view
    }

}