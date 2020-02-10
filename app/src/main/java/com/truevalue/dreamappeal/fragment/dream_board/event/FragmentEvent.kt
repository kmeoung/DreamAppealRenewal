package com.truevalue.dreamappeal.fragment.dream_board.event

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.activity.ActivityRank
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.bean.BeanEventCard
import com.truevalue.dreamappeal.bean.BeanPromotion
import com.truevalue.dreamappeal.fragment.dream_board.wish.FragmentWishBoard
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.fragment_event.*
import okhttp3.Call
import org.json.JSONObject

class FragmentEvent : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter? = null
    private var mPagerAdapter: BasePagerAdapter? = null

    companion object {
        private const val RV_TYPE_ITEM = 0
        private const val RV_TYPE_LOADING = 1

        const val EVENT_TYPE_DEFAULT = 0
        const val EVENT_TYPE_WISH = 1
        const val EVENT_TYPE_RANK = 2

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

        mPagerAdapter = BasePagerAdapter(context, object : BasePagerAdapter.IOBasePagerListener {
            override fun onBindViewPager(any: Any, view: ImageView, position: Int, arrayList: ArrayList<Any>) {
                val bean = any as BeanPromotion
                context?.let {
                    Glide.with(it)
                        .load(bean.thumbnail_url)
                        .placeholder(R.drawable.ic_image_white)
                        .centerCrop()
                        .thumbnail(0.1f)
                        .into(view)


                    view.setOnClickListener {

                        bean.event_type?.let { type ->
                            when (type) {
                                EVENT_TYPE_DEFAULT -> {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentEventDetail.newInstance(bean.idx),
                                        addToBack = true,
                                        isMainRefresh = false
                                    )
                                }
                                EVENT_TYPE_WISH -> {
                                    (activity as ActivityMain)
                                        .replaceFragment(
                                            FragmentWishBoard(),
                                            addToBack = true,
                                            isMainRefresh = false
                                        )
                                }
                                EVENT_TYPE_RANK -> {
                                    val intent = Intent(context!!, ActivityRank::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }


            }
        })
        pager_image.run {
            adapter = mPagerAdapter
            addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if(mPagerAdapter!!.count > 1) {
                        tv_indicator.text =
                            ((position + 1).toString() + " / " + mPagerAdapter!!.getCount())
                    }
                }
            })
        }


        swipy_bottom.setOnRefreshListener{
            getEvent()
            swipy_bottom.isRefreshing = false
        }
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_refresh -> {
                    getEvent()
                }
                tv_show_all -> {
                    (activity as ActivityMain).replaceFragment(
                        FragmentPromotionAll(),
                        addToBack = true,
                        isMainRefresh = false
                    )
                }
            }
        }
        iv_refresh.setOnClickListener(listener)
        tv_show_all.setOnClickListener(listener)
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
                    var value = json.getString("value")
                    value = if(value.length > 15) "${value.subSequence(0, 15)}..." else value
                    tv_event_title.text = "${value}에 도움될만한 소식"
                    tv_event_title.text = Utils.replaceTextColor(context,tv_event_title.text.toString(),R.color.nice_blue,value)
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
                        if(promotions.length() > 1){
                            tv_indicator.visibility = VISIBLE
                            tv_indicator.text = (1.toString() + " / " + promotions.length())
                        }else{
                            tv_indicator.visibility = GONE
                        }

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

                h.itemView.setOnClickListener {
                    // todo : 추후 추가
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
}