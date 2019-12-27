package com.truevalue.dreamappeal.fragment.profile.dream_note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
import com.truevalue.dreamappeal.fragment.profile.blueprint.FagmentActionPost
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import kotlinx.android.synthetic.main.fragment_recyclerview.rv_recycle
import kotlinx.android.synthetic.main.fragment_swipe_recyclerview.*
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException

class FragmentDreamNoteIdea : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter? = null
    private var mViewUserIdx : Int = -1

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
    ): View? = inflater.inflate(R.layout.fragment_swipe_recyclerview, container, false)

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
    private fun initView(){
        Utils.setSwipeRefreshLayout(srl_refresh, SwipeRefreshLayout.OnRefreshListener {
            getDreamNoteIdea()
        })
    }

    /**
     * Init RecyclerView Adapter
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(recyclerViewListener)
        rv_recycle.adapter = mAdapter
        rv_recycle.layoutManager = GridLayoutManager(context!!,3)
    }

    /**
     * Http
     * 일상 영감갤러리 가져오기
     */
    private fun getDreamNoteIdea() {
        // todo : 현재 보고있는 프로필의 idx 를 넣어야 합니다
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
                        }else{
                            Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
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
            if(mAdapter != null){
                val bean = mAdapter!!.get(i) as BeanDreamNoteIdea
                val ivIdea = h.getItemView<ImageView>(R.id.iv_idea)

                Glide.with(context!!).load(bean.thumbnail_image).placeholder(R.drawable.ic_image_white).centerCrop().into(ivIdea)

                h.itemView.setOnClickListener(View.OnClickListener {
                    (activity as ActivityMain).replaceFragment(
                        FagmentActionPost.newInstance(bean.idx,mViewUserIdx,FagmentActionPost.TYPE_DREAM_NOTE_IDEA),
                        addToBack = true,isMainRefresh = false
                    )
                })
            }
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}