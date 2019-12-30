package com.truevalue.dreamappeal.base

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.truevalue.dreamappeal.utils.Utils

class BaseGridItemDecorate(context: Context, divDp: Float, columnCount: Int) :ItemDecoration(){

    val div : Int
    val itemCount : Int
    init {
        div = Utils.DpToPixel(context,divDp)
        itemCount = columnCount
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        outRect.left = div

        if (parent.getChildAdapterPosition(view) % itemCount == (itemCount - 1)) {
            outRect.right = div
        }

        outRect.top = div
    }
}