package com.truevalue.dreamappeal.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import kotlinx.android.synthetic.main.activity_img_scaling.*
import kotlinx.android.synthetic.main.activity_img_scaling.pager_image
import kotlinx.android.synthetic.main.activity_img_scaling.tv_indicator
import kotlinx.android.synthetic.main.fragment_post_detail.*

class ActivityImgScaling : BaseActivity() {

    private lateinit var mAdapter: ScalingAdapter
    private var mArrayImg: ArrayList<String>
    private var mPosition = 0

    companion object {
        const val EXTRA_IMAGES = "EXTRA_IMAGES"
        const val EXTRA_IMAGE_POSITION = "EXTRA_IMAGE_POSITION"
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

        onClickView()
    }

    /**
     * Data 초기화
     */
    private fun initData() {
        intent.getSerializableExtra(EXTRA_IMAGES)?.let {
            mArrayImg = it as ArrayList<String>
        }

        intent.getIntExtra(EXTRA_IMAGE_POSITION,-1)?.let {
            mPosition = it
        }

        tv_indicator.text = "${mPosition + 1} / ${mArrayImg.size}"
    }

    /**
     * ViewPager 초기화
     */
    private fun initAdapter() {
        mAdapter = ScalingAdapter()
        pager_image.adapter = mAdapter
        pager_image.setCurrentItem(mPosition,false)
        pager_image.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tv_indicator.text = if(mArrayImg.size > 0) ((position + 1).toString() + " / " + mArrayImg.size) else "0 / 0"
            }
        })
    }

    private fun onClickView(){
        iv_back.setOnClickListener {
            onBackPressed()
        }
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