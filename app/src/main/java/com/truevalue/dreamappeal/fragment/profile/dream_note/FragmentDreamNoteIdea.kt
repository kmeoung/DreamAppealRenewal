package com.truevalue.dreamappeal.fragment.profile.dream_note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.bean.BeanDreamNoteIdea
import com.truevalue.dreamappeal.fragment.profile.blueprint.FragmentActionPost
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.fragment_dream_note.*
import kotlinx.android.synthetic.main.fragment_recyclerview.rv_recycle
import kotlinx.android.synthetic.main.fragment_swipe_recyclerview.*
import kotlinx.android.synthetic.main.fragment_swipe_recyclerview.srl_refresh
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException

class FragmentDreamNoteIdea : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter? = null
    private var mViewUserIdx: Int = -1

    companion object {
        fun newInstance(view_user_idx: Int): FragmentDreamNoteIdea {
            val fragment =
                FragmentDreamNoteIdea()
            fragment.mViewUserIdx = view_user_idx
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dream_note, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // SwipeRefreshLayout 설정
        initView()
        // RecyclerView Adapter 초기화
        initAdapter()
        // data bind
        getDreamNoteIdea()
    }

    /**
     * SwipeRefreshLayout 설정
     */
    private fun initView() {
        Utils.setSwipeRefreshLayout(srl_refresh, SwipeRefreshLayout.OnRefreshListener {
            getDreamNoteIdea()
        })

        if (mViewUserIdx == Comm_Prefs.getUserProfileIndex()) {
            ll_default_my.visibility = VISIBLE
            tv_default_my_top.visibility = VISIBLE
            sp_margin.visibility = VISIBLE
            tv_default_bold.text = "내게 영감을 주는 자료를 모으는 공간이에요"
            tv_default_my_top.text = "다른 친구의 영감자료를 담아오거나,"
            tv_default_my.text = "를 터치하여 작성할 수 있어요"
        } else {
            tv_default_my_top.visibility = GONE
            ll_default_my.visibility = GONE
            sp_margin.visibility = GONE
            tv_default_bold.text = "상대방이 모은 영감이 여기에 표시됩니다"
        }
    }

    /**
     * Init RecyclerView Adapter
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(recyclerViewListener)
        val itemDecorate = BaseGridItemDecorate(context!!, 1.0f, 3)
        rv_recycle.addItemDecoration(itemDecorate)
        rv_recycle.adapter = mAdapter
        rv_recycle.layoutManager = GridLayoutManager(context!!, 3)
    }

    /**
     * Http
     * 일상 영감갤러리 가져오기
     */
    private fun getDreamNoteIdea() {
        val cur_profile_idx = mViewUserIdx

        DAClient.getDreamNoteIdea(cur_profile_idx,
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

                        if (code == DAClient.SUCCESS) {
                            if (mAdapter == null) return
                            val json = JSONObject(body)
                            mAdapter!!.clear()
                            try {
                                val lifes = json.getJSONArray("idea_posts")
                                for (i in 0 until lifes.length()) {
                                    val bean = Gson().fromJson<BeanDreamNoteIdea>(
                                        lifes.getJSONObject(i).toString(),
                                        BeanDreamNoteIdea::class.java
                                    )
                                    mAdapter!!.add(bean)
                                }
                            } catch (e: Exception) {
                            }
                            ll_default.visibility = if (mAdapter!!.size() > 0) GONE else VISIBLE
                        } else {
                            Toast.makeText(
                                context!!.applicationContext,
                                message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            })
    }

    /**
     * RecyclerView Listener
     */
    private val recyclerViewListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_idea, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            if (mAdapter != null) {
                val bean = mAdapter!!.get(i) as BeanDreamNoteIdea
                val ivIdea = h.getItemView<ImageView>(R.id.iv_idea)
                Glide.with(context!!).load(bean.thumbnail_image)
                    .centerCrop().placeholder(R.drawable.ic_image_white).into(ivIdea)

                h.itemView.setOnClickListener {
                    (activity as ActivityMain).replaceFragment(
                        FragmentActionPost.newInstance(
                            bean.idx,
                            mViewUserIdx,
                            FragmentActionPost.TYPE_DREAM_NOTE_IDEA
                        ),
                        addToBack = true, isMainRefresh = false
                    )
                }
                Utils.setImageItemViewSquare(context, ivIdea)
            }
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}