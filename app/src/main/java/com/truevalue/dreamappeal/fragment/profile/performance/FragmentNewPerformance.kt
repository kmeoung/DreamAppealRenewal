package com.truevalue.dreamappeal.fragment.profile.performance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.bean.BeanAchievementPost
import com.truevalue.dreamappeal.bean.BeanBestPost
import com.truevalue.dreamappeal.bean.BeanPerformance
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.fragment_performance.*
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException

class FragmentNewPerformance : BaseFragment(), IORecyclerViewListener,
    SwipeRefreshLayout.OnRefreshListener {

    private var mAdapter: BaseRecyclerViewAdapter2<Any?>? = null
    private var mCurrentPage = 0
    private var mBeanPerformance: BeanPerformance? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_new_performance, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Data 초기화
        initData()
        // View 초기화
        initView()
        // RecyclerView Adapter 초기화
        initAdapter()
        // bind Data
        bindData()
        // Bind Temp Data
//        bindTempData()
        if (mBeanPerformance == null) {
            // 주요 성과 가져오기
            getAchievementPostMain()
        }
    }

    /**
     * Main에서 넘어온 Refresh 요청
     */
    override fun OnServerRefresh() {
        super.OnServerRefresh()
        getAchievementPostMain()
    }

    /**
     * 있는 데이터 집어넣기
     */
    private fun bindData() {
        if (mBeanPerformance != null) {
            mAdapter!!.clear()
            for (i in 0 until mBeanPerformance!!.best_posts.size) {
                mAdapter!!.add(mBeanPerformance!!.best_posts[i])
            }


        }
    }

    /**
     * Data 초기화
     */
    private fun initData() {
    }

    /**
     * Bind Temp Data
     */
    private fun bindTempData() {
        for (i in 0..10) {
            mAdapter!!.add("")
        }
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        if (context != null) {
            mAdapter = BaseRecyclerViewAdapter2(this)
            rv_recycle.adapter = mAdapter
            rv_recycle.layoutManager =
                LinearLayoutManager(context)
        }
    }

    /**
     * Http
     * 주요 성과 페이지 조회
     */
    private fun getAchievementPostMain() {
        // todo : 현재 조회하고 있는 Profile User Index 를 사용해야 합니다. +
        val profile_idx = Comm_Prefs.getUserProfileIndex()
        DAClient.achievementPostMain(profile_idx,
            mCurrentPage,
            object : DAHttpCallback {
                override fun onFailure(call: Call, e: IOException) {
                    super.onFailure(call, e)
                    srl_refresh.isRefreshing = false
                }

                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    srl_refresh.isRefreshing = false
                    if (context != null) {
                        Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
                            .show()

                        if (code == DAClient.SUCCESS) {
                            mBeanPerformance = BeanPerformance(
                                null,
                                ArrayList(), ArrayList()
                            )

                            val json = JSONObject(body)
                            val profileImage = json.getString("profile_image")

                            mBeanPerformance!!.profile_image = profileImage
                            mBeanPerformance!!.best_posts.clear()


                            val bestPosts = json.getJSONObject("best_posts")
                            mAdapter!!.clear()
                            for (i in 1..3) {
                                try {
                                    val bestPost = bestPosts.getJSONObject("best_post_$i")
                                    val bean = Gson().fromJson<BeanBestPost>(
                                        bestPost.toString(),
                                        BeanBestPost::class.java
                                    )
                                    mBeanPerformance!!.best_posts.add(bean)
                                    mAdapter!!.add(bean)
                                } catch (e: Exception) {
                                    mBeanPerformance!!.best_posts.add(null)
                                    mAdapter!!.add(null)
                                }
                            }
                        }
                    }
                }
            })
    }

    /**
     * Init View
     */
    private fun initView() {
        Utils.setSwipeRefreshLayout(srl_refresh, this)
    }

    private val TYPE_FULL_ITEM = 0
    private val TYPE_EMPTY_ITEM = 1

    /**
     * RecyclerView Item Count
     */
    override val itemCount: Int
        get() = if (mAdapter != null) mAdapter!!.mArray.size else 0

    /**
     * RecyclerView Create View Holder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder{
        if(TYPE_FULL_ITEM == viewType){
            return BaseViewHolder.newInstance(R.layout.listitem_best_post, parent, false)
        }
        return BaseViewHolder.newInstance(R.layout.listitem_best_post_default, parent, false)
    }


    /**
     * RecyclerView Bind View Holder
     */
    override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
        if (mAdapter != null) {
            val tvPost = h.getItemView<TextView>(R.id.tv_post)
            if(getItemViewType(i) == TYPE_FULL_ITEM) {
                val bean = mAdapter!!.get(i) as BeanBestPost
                val ivPost = h.getItemView<ImageView>(R.id.iv_post)
//                Glide.with(context!!)
//                    .load(bean.)
//                    .placeholder(R.drawable.ic_image_gray)
//                    .into(ivPost)

                tvPost.text = bean.title

                h.itemView.setOnClickListener(View.OnClickListener {
                    (activity as ActivityMain).replaceFragment(
                        FragmentBestPost.newInstance(bean.idx, i + 1),
                        addToBack = true,
                        isMainRefresh = true
                    )
                })
            }else{
                tvPost.text = Utils.replaceTextColor(context,tvPost,getString(R.string.str_best_post))
                h.itemView.setOnClickListener(View.OnClickListener {
                    (activity as ActivityMain).replaceFragment(FragmentAddAchivementPost(), true)
                })
            }
        }
    }

    /**
     * RecyclerView Item View Type
     */
    override fun getItemViewType(i: Int): Int{
        if(mAdapter!!.get(i) == null) return TYPE_EMPTY_ITEM
        return TYPE_FULL_ITEM
    }

    /**
     * 위에서 아래로 스와이프 시 Refresh
     */
    override fun onRefresh() {
        // 여기다가 서버요청
        getAchievementPostMain()
    }

}