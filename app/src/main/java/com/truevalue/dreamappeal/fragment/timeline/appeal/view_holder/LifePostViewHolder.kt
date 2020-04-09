package com.truevalue.dreamappeal.fragment.timeline.appeal.view_holder

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.truevalue.dreamappeal.base_new.adapter.BaseHolder
import com.truevalue.dreamappeal.fragment.timeline.adapter.ImageAdapter
import com.truevalue.dreamappeal.fragment.timeline.adapter.TagAdapter
import com.truevalue.dreamappeal.fragment.timeline.adapter.TimeLineData
import com.truevalue.dreamappeal.utils.load
import com.truevalue.dreamappeal.utils.value
import kotlinx.android.synthetic.main.item_life_post.*
import kotlin.math.max

class LifePostViewHolder(view: View) : BaseHolder<TimeLineData>(view) {
    private var imageAdapter: ImageAdapter? = null

    init {
        imageAdapter =
            ImageAdapter(
                LayoutInflater.from(itemView.context)
            )
        rvImages.adapter = imageAdapter
        rvImages.setHasFixedSize(true)
        rvImages.apply {
            var snapHelper: PagerSnapHelper? = null
            if (onFlingListener == null) {
                snapHelper = PagerSnapHelper()
                snapHelper.attachToRecyclerView(this)
            }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val centerView = snapHelper?.findSnapView(layoutManager)
                        if (centerView != null) {
                            val pos = layoutManager?.getPosition(centerView)
                            tvIndicator.text = "${pos.value() + 1}/${imageAdapter?.itemCount}"
                        }
                    }
                }
            })
        }

    }

    override fun bind(data: TimeLineData, position: Int) {
        civAvatar.load("https://kprofiles.com/wp-content/uploads/2019/11/D6aGkQlUcAAgf_n-533x800.jpg")
        tvName.text = "모든 포지션을 잘 수비하는 올라운드"
        tvDes.text = "모든 포지션을 잘 수비하는 올라운드"
        tvContents.text = "윗몸일으키기 훈련 꾸준하게 해서 코어의 근력을 키우자"
        tvCommentCount.text = "2"
        tvCheerCount.text = "2"
        imageAdapter?.setDataSource(
            listOf(
                "https://upload.wikimedia.org/wikipedia/commons/5/5a/Books_HD_%288314929977%29.jpg",
                "https://upload.wikimedia.org/wikipedia/commons/5/5a/Books_HD_%288314929977%29.jpg"
            )
        )
        val pos =
            max((rvImages.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition(), 0)
        tvIndicator.text = "${pos + 1}/${imageAdapter?.itemCount}"
    }
}