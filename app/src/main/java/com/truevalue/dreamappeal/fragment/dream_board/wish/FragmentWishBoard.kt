package com.truevalue.dreamappeal.fragment.dream_board.wish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.bean.BeanConcern
import com.truevalue.dreamappeal.bean.BeanPromotion
import com.truevalue.dreamappeal.bean.BeanWish
import com.truevalue.dreamappeal.fragment.dream_board.FragmentAddBoard
import com.truevalue.dreamappeal.fragment.dream_board.concern.FragmentConcern
import com.truevalue.dreamappeal.fragment.dream_board.event.FragmentEventDetail
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_event.*
import kotlinx.android.synthetic.main.fragment_wish_board.*
import kotlinx.android.synthetic.main.fragment_wish_board.pager_image
import kotlinx.android.synthetic.main.fragment_wish_board.rl_images
import kotlinx.android.synthetic.main.fragment_wish_board.tv_indicator
import okhttp3.Call
import org.json.JSONObject

class FragmentWishBoard : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter?
    private var mPagerAdapter: BasePagerAdapter?
    private var isLast: Boolean

    companion object {
        private const val RV_TYPE_ITEM = 0
        private const val RV_TYPE_LOADING = 1

        private const val MAX_GET_ONCE_ITEM = 20
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

        // View 초기화
        initView()
        // Adapter 초기화
        initAdapter()
        // View Click Listener
        onClickView()
        // 소원 게시판 가져오기
        getWish()
    }

    /**
     * View 초기화
     */
    private fun initView() {
        tv_title.text = getString(R.string.str_wish_main_title)
        iv_back_black.visibility = GONE
        iv_back_blue.visibility = VISIBLE

        rl_images.run {
            Utils.setImageViewSquare(context, this, 7, 3)
        }
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
            override fun onBindViewPager(
                any: Any,
                view: ImageView,
                position: Int,
                arrayList: ArrayList<Any>
            ) {
                val bean = any as BeanPromotion
                context?.let {
                    Glide.with(it)
                        .load(bean.thumbnail_url)
                        .placeholder(R.drawable.ic_image_white)
                        .centerCrop()
                        .thumbnail(0.1f)
                        .into(view)

                    view.setOnClickListener {
                        if (!bean.url.isNullOrEmpty()) {
                            (activity as ActivityMain)
                                .replaceFragment(
                                    FragmentEventDetail.newInstance(bean.url!!),
                                    addToBack = true,
                                    isMainRefresh = false
                                )
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
                    if (mPagerAdapter!!.count > 1) {
                        tv_indicator.text =
                            ((position + 1).toString() + " / " + mPagerAdapter!!.getCount())
                    }
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
                iv_back_blue -> {
                    (activity as ActivityMain).onBackPressed(false)
                }
                ll_add_wish -> {
                    (activity as ActivityMain).replaceFragment(
                        FragmentAddBoard.newInstance(FragmentAddBoard.TYPE_ADD_WISH),
                        addToBack = true,
                        isMainRefresh = false
                    )
                }
            }
        }
        iv_back_blue.setOnClickListener(listener)
        ll_add_wish.setOnClickListener(listener)
    }

    /**
     * Http
     * 소원 게시판 가져오기
     */
    private fun getWish() {
        DAClient.getWish(object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (code == DAClient.SUCCESS) {
                    isLast = false
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
                        if (promotions.length() > 1) {
                            tv_indicator.visibility = VISIBLE
                            tv_indicator.text = (1.toString() + " / " + promotions.length())
                        } else {
                            tv_indicator.visibility = GONE
                        }
                    }

                    val wishes = json.getJSONArray("wishes")
                    mAdapter?.let {

                        if (MAX_GET_ONCE_ITEM > wishes.length()) {
                            isLast = true
                            it.notifyDataSetChanged()
                        }

                        it.clear()
                        for (i in 0 until wishes.length()) {
                            val wish = wishes.getJSONObject(i)
                            val bean = Gson().fromJson<BeanWish>(
                                wish.toString(),
                                BeanWish::class.java
                            )

                            it.add(bean)
                        }
                    }

                } else if (code == DAClient.NO_MORE_POST) {
                    isLast = true
                    mAdapter!!.notifyDataSetChanged()
                } else {
                    context?.let {
                        Toast.makeText(it.applicationContext, message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        })
    }

    /**
     * Http
     * 추가 조회
     */
    private fun getMoreWish(row_num: Int) {
        DAClient.getMoreWish(row_num, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (code == DAClient.SUCCESS) {
                    val json = JSONObject(body)
                    try {
                        val wishes = json.getJSONArray("wishes")
                        mAdapter?.let {
                            if (MAX_GET_ONCE_ITEM > wishes.length()) {
                                isLast = true
                                it.notifyDataSetChanged()
                            }

                            for (i in 0 until wishes.length()) {
                                val obj = wishes.get(i)
                                val bean = Gson().fromJson<BeanWish>(
                                    obj.toString(),
                                    BeanWish::class.java
                                )
                                it.add(bean)
                            }
                        }
                    } catch (e: Exception) {
                    }
                } else if (code == DAClient.NO_MORE_POST) {
                    isLast = true
                    mAdapter!!.notifyDataSetChanged()
                } else {
                    context?.let {
                        isLast = true
                        mAdapter!!.notifyDataSetChanged()
                        Toast.makeText(it.applicationContext, message, Toast.LENGTH_SHORT).show()
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
            get() = if (mAdapter != null) if (mAdapter!!.size() > (MAX_GET_ONCE_ITEM - 1) && !isLast) mAdapter!!.size() + 1 else mAdapter!!.size() else 0

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

                val bean = mAdapter?.get(i) as BeanWish

                val tvLikeCount = h.getItemView<TextView>(R.id.tv_like_count)
                val tvTitle = h.getItemView<TextView>(R.id.tv_title)

                tvTitle.text = bean.title
                tvLikeCount.text = Utils.getCommentView(bean.count)

                h.itemView.setOnClickListener {
                    (activity as ActivityMain).replaceFragment(
                        FragmentWishDetail.newInstance(bean.idx),
                        addToBack = true,
                        isMainRefresh = false
                    )
                }

            } else if (RV_TYPE_LOADING == getItemViewType(i)) {
                val bean = (mAdapter?.size()!!.minus(1)) as BeanWish
                getMoreWish(bean.row_num)
            }
        }

        override fun getItemViewType(i: Int): Int {
            if (mAdapter!!.size() > (MAX_GET_ONCE_ITEM - 1) && mAdapter!!.size() == i && !isLast) {
                return FragmentConcern.RV_TYPE_ITEM_MORE
            }
            return FragmentConcern.RV_TYPE_ITEM
        }
    }
}