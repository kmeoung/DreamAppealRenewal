package com.truevalue.dreamappeal.fragment.dream_board.concern

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.fragment_concern_detail.*
import kotlinx.android.synthetic.main.fragment_concern_detail.rl_images

class FragmentConcernDetail : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter?
    private var mAdapterImage: BasePagerAdapter?

    init {
        mAdapter = null
        mAdapterImage = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_concern_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // view 초기화
        initView()
        // adapter 초기화
        initAdapter()
    }

    /**
     * View 초기화
     */
    private fun initView() {
        // 상단 이미지 정사각형 설정
        Utils.setImageViewSquare(context, rl_images)
    }

    /**
     * Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvListener)
        rv_re_concern.run {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }

        mAdapterImage = BasePagerAdapter(context, object : BasePagerAdapter.IOBasePagerListener {
            override fun onBindViewPager(any: Any, view: ImageView, position: Int) {

            }
        })

        pager_image.run {
            adapter = mAdapterImage
            addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    mAdapterImage?.let {
                        tv_indicator.text =
                            if (it.count > 0) ((position + 1).toString() + " / " + it.count) else "0 / 0"
                    }
                }
            })
        }
    }


    /**
     * 답글 RecyclerView Listener
     */
    private val rvListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = mAdapter?.size() ?: 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_re_concern, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {

        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}