package com.truevalue.dreamappeal.fragment.dream_board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
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
import kotlinx.android.synthetic.main.fragment_promotion.*
import okhttp3.Call
import org.json.JSONObject

class FragmentPromotionAll : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_promotion, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        initView()
        // RecyclerView / ViewPager Adapter 초기화
        initAdapter()
        // View Click Listener
        onClickView()

        // bind data
        getPromotions()
    }

    /**
     * View 초기화
     */
    private fun initView() {
    }

    /**
     * RecyclerView / ViewPager Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = mAdapter ?: BaseRecyclerViewAdapter(rvEventListener)
        rv_promotion.run {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {

            }
        }
    }

    /**
     * Http
     * 기본 Event 전체 조회
     */
    private fun getPromotions() {
        DAClient.getBoardEventAll(object : DAHttpCallback {
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
                    mAdapter?.let {
                        it.clear()
                        for (i in 0 until promotions.length()) {
                            val promotion = promotions.getJSONObject(i)
                            val bean = Gson().fromJson<BeanPromotion>(
                                promotion.toString(),
                                BeanPromotion::class.java
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
            return BaseViewHolder.newInstance(R.layout.listitem_promotion, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            val bean = mAdapter?.get(i) as BeanPromotion

            val ivPromotion = h.getItemView<ImageView>(R.id.iv_promotion)
            Utils.setImageViewSquare(context, ivPromotion, 7, 3)

            context?.let {
                Glide.with(it)
                    .load(bean.thumbnail_url)
                    .placeholder(R.drawable.ic_image_white)
                    .centerCrop()
                    .thumbnail(0.1f)
                    .into(ivPromotion)
            }

            h.itemView.setOnClickListener {
                (activity as ActivityMain).replaceFragment(
                    FragmentEventDetail.newInstance(bean.idx),
                    addToBack = true,
                    isMainRefresh = false
                )
            }
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}