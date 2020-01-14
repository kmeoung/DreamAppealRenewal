package com.truevalue.dreamappeal.fragment.dream_board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.bean.BeanEventCard
import com.truevalue.dreamappeal.bean.BeanPromotion
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.fragment_wish_board.*
import kotlinx.android.synthetic.main.fragment_wish_board.tv_indicator
import okhttp3.Call
import org.json.JSONObject

class FragmentWishBoard : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter?
    private var mPagerAdapter: BasePagerAdapter?
    private var isLast : Boolean

    companion object {
        private const val RV_TYPE_ITEM = 0
        private const val RV_TYPE_LOADING = 1
    }


    init {
        mAdapter = null
        mPagerAdapter = null
        isLast = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_wish_board, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Adapter 초기화
        initAdapter()
        // 소원 게시판 가져오기
        getWish()
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvListener)
        rv_wish.run {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }

        mPagerAdapter = BasePagerAdapter(context, object : BasePagerAdapter.IOBasePagerListener {
            override fun onBindViewPager(bean: Any, view: ImageView, position: Int) {
                val bean = bean as BeanPromotion
                context?.let {
                    Glide.with(it)
                        .load(bean.thumbnail_url)
                        .placeholder(R.drawable.ic_image_white)
                        .centerCrop()
                        .thumbnail(0.1f)
                        .into(view)


                    view.setOnClickListener {
                        
                    }
                }


            }
        })
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
     * Http
     * 소원 게시판 가져오기
     */
    private fun getWish(){
        DAClient.getWish(object : DAHttpCallback{
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if(code == DAClient.SUCCESS){
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

                }else{
                    context?.let {
                        Toast.makeText(it,message,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    /**
     * RecyclerView Listener
     */
    private val rvListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) if (mAdapter!!.size() > 4 && !isLast) mAdapter!!.size() + 1 else mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            when (viewType) {
                RV_TYPE_ITEM ->
                    return BaseViewHolder.newInstance(R.layout.listitem_wish, parent, false)
                RV_TYPE_LOADING ->
                    return BaseViewHolder.newInstance(R.layout.listitem_white_more, parent, false)
            }
            return BaseViewHolder.newInstance(R.layout.listitem_wish, parent, false)
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
                    (activity as ActivityMain).replaceFragment(
                        FragmentEventDetail.newInstance(bean.idx),
                        addToBack = true,
                        isMainRefresh = false
                    )
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