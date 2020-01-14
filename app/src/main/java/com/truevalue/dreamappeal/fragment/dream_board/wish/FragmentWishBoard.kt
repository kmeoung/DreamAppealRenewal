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
import com.truevalue.dreamappeal.bean.BeanPromotion
import com.truevalue.dreamappeal.bean.BeanWish
import com.truevalue.dreamappeal.fragment.dream_board.event.FragmentEventDetail
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_wish_board.*
import okhttp3.Call
import org.json.JSONObject

class FragmentWishBoard : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter?
    private var mPagerAdapter: BasePagerAdapter?
    private var isLast: Boolean

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

        // View 초기화
        initView()
        // Adapter 초기화
        initAdapter()
        // View Click Listener
        onClickView()
        // 소원 게시판 가져오기
        getWish(false, -1, true)
    }

    /**
     * View 초기화
     */
    private fun initView(){
        tv_title.text = getString(R.string.str_wish_main_title)
        iv_back_black.visibility = GONE
        iv_back_blue.visibility = VISIBLE
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
                        (activity as ActivityMain)
                            .replaceFragment(
                                FragmentEventDetail.newInstance(
                                    bean.idx
                                ),
                                addToBack = true,
                                isMainRefresh = false
                            )
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
     * View Click Listener
     */
    private fun onClickView(){
        val listener = View.OnClickListener{
            when(it){
                iv_back_blue->{
                    (activity as ActivityMain).onBackPressed(false)
                }
            }
        }
        iv_back_blue.setOnClickListener(listener)
    }

    /**
     * Http
     * 소원 게시판 가져오기
     */
    private fun getWish(refresh: Boolean, last_idx: Int, isClear: Boolean) {
        DAClient.getWish(refresh, last_idx, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (code == DAClient.SUCCESS) {
                    if (code == DAClient.SUCCESS) {
                        val json = JSONObject(body)
                        val promotions = json.getJSONArray("promotions")
                        mPagerAdapter?.let {
                            if (isClear) it.clear()
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

                        val wishes = json.getJSONArray("wishes")
                        mAdapter?.let {
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

                    } else if (code == "NO_MORE_POST") {
                        isLast = true
                        mAdapter!!.notifyDataSetChanged()
                    } else {
                        context?.let {
                            Toast.makeText(it.applicationContext, message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                } else {
                    context?.let {
                        Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
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

                val bean = mAdapter?.get(i) as BeanWish

                val tvLikeCount = h.getItemView<TextView>(R.id.tv_like_count)
                val tvTitle = h.getItemView<TextView>(R.id.tv_title)

                tvTitle.text = bean.title
                tvLikeCount.text = Utils.getCommentView(bean.count)

                h.itemView.setOnClickListener {
                    // todo : 페이지 이동
                }

            } else if (RV_TYPE_LOADING == getItemViewType(i)) {
                getWish(
                    true,
                    (mAdapter!!.get(mAdapter!!.size() - 1) as BeanWish).idx,
                    false
                )
            }
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}