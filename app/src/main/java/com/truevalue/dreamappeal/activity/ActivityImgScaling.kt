package com.truevalue.dreamappeal.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import kotlinx.android.synthetic.main.activity_img_scaling.*

class ActivityImgScaling : BaseActivity() {

    private lateinit var mAdapter: ScalingAdapter
    private var mArrayImg: ArrayList<String>

    companion object {
        private const val EXTRA_IMAGES = "EXTRA_IMAGES"
    }

    init {
        mArrayImg = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_img_scaling)
        // 이미지 데이터 가져오기
        initData()
        // View Pager 초기화
        initAdapter()
    }

    /**
     * Data 초기화
     */
    private fun initData() {
        if (intent.getSerializableExtra(EXTRA_IMAGES) != null) {
            mArrayImg = intent.getSerializableExtra(EXTRA_IMAGES) as ArrayList<String>
        }
    }

    /**
     * ViewPager 초기화
     */
    private fun initAdapter() {
        mAdapter = ScalingAdapter()
        pager_img.adapter = mAdapter
    }

    private inner class ScalingAdapter : PagerAdapter() {
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
            container.removeView(`object` as PhotoView)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val imageView = PhotoView(this@ActivityImgScaling)
            val url = mArrayImg[position]
            Glide.with(this@ActivityImgScaling)
                .load(url)
                .placeholder(R.drawable.ic_image_white)
                .centerCrop()
                .thumbnail(0.1f)
                .into(imageView)

            container.addView(imageView, 0)
            return imageView
        }

        override fun getCount(): Int {
            return mArrayImg.size
        }
    }
}