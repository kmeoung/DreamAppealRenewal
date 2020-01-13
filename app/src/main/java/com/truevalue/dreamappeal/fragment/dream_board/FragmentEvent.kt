package com.truevalue.dreamappeal.fragment.dream_board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanEventCard
import com.truevalue.dreamappeal.bean.BeanPromotion
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.fragment_event.*
import okhttp3.Call
import org.json.JSONObject

class FragmentEvent : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter? = null
    private var mPagerAdapter: PromotionAdapter? = null

    companion object {
        private const val RV_TYPE_ITEM = 0
        private const val RV_TYPE_LOADING = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_event, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        initView()
        // RecyclerView / ViewPager Adapter 초기화
        initAdapter()
        // View Click Listener
        onClickView()

        // bind data
        getEvent()
    }

    /**
     * View 초기화
     */
    private fun initView() {
        rl_images.run {
            Utils.setImageViewSquare(context, this, 7, 3)
        }
    }

    /**
     * RecyclerView / ViewPager Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = mAdapter ?: BaseRecyclerViewAdapter(rvEventListener)
        rv_event.run {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }

        mPagerAdapter = PromotionAdapter()
        pager_image.run {
            adapter = mPagerAdapter
            addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tv_indicator.text =
                        ((position + 1).toString() + " / " + mPagerAdapter!!.getCount())
                }
            })
        }
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_refresh -> {

                }
            }
        }
        iv_refresh.setOnClickListener(listener)
    }

    private fun bindTempData() {
        mAdapter?.let {
            for (i in 0..100) {
                it.add("")
            }
        }
    }

    /**
     * Http
     * 기본 Event 조회
     */
    private fun getEvent() {
        DAClient.getBoardEvent(object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (code == DAClient.SUCCESS) {
                    val json = JSONObject(body)
                    val promotions = json.getJSONArray("promotions")
                    mPagerAdapter?.let {
                        it.clear()
                        for (i in 0 until promotions.length()) {
                            val promotion = promotions.getJSONObject(i)
                            val bean = Gson().fromJson<BeanPromotion>(
                                promotion.toString(),
                                BeanPromotion::class.java
                            )

                            it.add(bean)
                        }
                        it.notifyDataSetChanged()
                        tv_indicator.text = (1.toString() + " / " + promotions.length())
                    }

                    val event_cards = json.getJSONArray("event_cards")
                    mAdapter?.let {
                        it.clear()
                        for (i in 0 until event_cards.length()) {
                            val event = event_cards.getJSONObject(i)
                            val bean = Gson().fromJson<BeanEventCard>(
                                event.toString(),
                                BeanEventCard::class.java
                            )

                            it.add(bean)
                        }
                    }
                } else {
                    context?.let {
                        Toast.makeText(it.applicationContext, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }


    /**
     * RecyclerView Event Listener
     */
    private val rvEventListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = mAdapter?.size() ?: 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            when (viewType) {
                RV_TYPE_ITEM ->
                    return BaseViewHolder.newInstance(R.layout.listitem_event, parent, false)
                RV_TYPE_LOADING ->
                    return BaseViewHolder.newInstance(R.layout.listitem_white_more, parent, false)
            }
            return BaseViewHolder.newInstance(R.layout.listitem_event, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            if (RV_TYPE_ITEM == getItemViewType(i)) {

                val bean = mAdapter?.get(i) as BeanEventCard

                val ivEvent = h.getItemView<ImageView>(R.id.iv_event)
                Utils.setImageViewSquare(context, ivEvent, 100, 23)

                context?.let {
                    Glide.with(it)
                        .load(bean.url)
                        .placeholder(R.drawable.ic_image_white)
                        .centerCrop()
                        .thumbnail(0.1f)
                        .into(ivEvent)
                }

            } else if (RV_TYPE_LOADING == getItemViewType(i)) {
//                getTimeLineData(
//                    true,
//                    (mAdatper!!.get(mAdatper!!.size() - 1) as BeanTimeline).idx,
//                    false
//                )
            }
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }

    /**
     * Promotion Pager Adapter
     */
    inner class PromotionAdapter :
        PagerAdapter() {
        private val mArray: ArrayList<BeanPromotion>

        override fun getCount(): Int {
            return mArray.size
        }

        fun add(item: BeanPromotion) {
            mArray.add(item)
        }

        fun clear(){
            mArray.clear()
        }

        operator fun get(i: Int): BeanPromotion {
            return mArray[i]
        }

        fun getAll() : ArrayList<BeanPromotion>{
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
            val imageView = ImageView(context)
            context?.let {
                val bean = mArray[position]
                Glide.with(it)
                    .load(bean.thumbnail_url)
                    .placeholder(R.drawable.ic_image_white)
                    .centerCrop()
                    .thumbnail(0.1f)
                    .into(imageView)


                imageView.setOnClickListener{
                    // todo : 상세 보기 페이지 이동
                }
            }

            container.addView(imageView, 0)
            return imageView
        }

        init {
            mArray = ArrayList()
        }
    }
}