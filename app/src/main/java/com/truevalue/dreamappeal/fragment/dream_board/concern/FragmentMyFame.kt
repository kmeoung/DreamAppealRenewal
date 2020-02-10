package com.truevalue.dreamappeal.fragment.dream_board.concern

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.activity.ActivityRank
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanMyFame
import com.truevalue.dreamappeal.bean.ConcernHistory
import com.truevalue.dreamappeal.bean.ReConcernHistory
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_my_fame.*
import okhttp3.Call
import org.json.JSONObject

class FragmentMyFame : BaseFragment() {
    private var mAdapter: BaseRecyclerViewAdapter?
    private var mBean: BeanMyFame?

    private var mViewType = VIEW_TYPE_TEXT

    companion object {
        private const val VIEW_TYPE_TEXT = 0
        private const val VIEW_TYPE_COMMENT = 1
    }

    init {
        mAdapter = null
        mBean = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_my_fame, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // view 초기화
        initView()
        // Adapter 초기화
        initAdapter()
        // View Click Listener
        onClickView()
        // 상단바 설정
        setTabView(mViewType)
        // 활동내역 조회
        getStatus()
    }


    /**
     * Http
     * 내 활동내역 조회
     */
    private fun getStatus() {
        DAClient.getConcernStatus(object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (code == DAClient.SUCCESS) {
                    val json = JSONObject(body)
                    val bean = Gson().fromJson<BeanMyFame>(json.toString(), BeanMyFame::class.java)
                    mBean = bean

                    bean.user.image?.let { image ->
                        Glide.with(context!!)
                            .load(image)
                            .circleCrop()
                            .placeholder(R.drawable.drawer_user)
                            .into(iv_profile)
                    }

                    tv_user.text = "${bean.user.nickname} 님의 명성도"
                    tv_fame.text = bean.user.reputation.toString()

                    setTabView(mViewType)
                } else {
                    context?.let {
                        Toast.makeText(it.applicationContext, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    /**
     * 상단 Tab 설정
     */
    private fun setTabView(view_type: Int) {
        mViewType = view_type
        when (view_type) {
            VIEW_TYPE_TEXT -> {
                tv_fame_text.isSelected = true
                tv_fame_comment.isSelected = false
                iv_fame_text.visibility = VISIBLE
                iv_fame_comment.visibility = View.INVISIBLE
                tv_sub_title.text = getString(R.string.str_fame_text)

                mBean?.let { bean ->
                    mAdapter?.let { adapter ->
                        adapter.clear()
                        for (i in bean.concern_history.indices) {
                            val bean = bean.concern_history[i]
                            adapter.add(bean)
                        }
                    }
                }
            }
            VIEW_TYPE_COMMENT -> {
                tv_fame_text.isSelected = false
                tv_fame_comment.isSelected = true
                iv_fame_text.visibility = View.INVISIBLE
                iv_fame_comment.visibility = VISIBLE
                tv_sub_title.text = getString(R.string.str_fame_comment)

                mBean?.let { bean ->
                    mAdapter?.let { adapter ->
                        adapter.clear()
                        for (i in bean.re_concern_history.indices) {
                            val bean = bean.re_concern_history[i]
                            adapter.add(bean)
                        }
                    }
                }
            }
        }
    }

    /**
     * View 초기화
     */
    private fun initView() {
        iv_back_blue.visibility = VISIBLE
        iv_back_black.visibility = GONE
        tv_title.text = getString(R.string.str_my_fame)
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvListener)
        rv_fame.run {
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
                iv_back_blue -> {
                    (activity as ActivityMain).onBackPressed(false)
                }
                tv_fame_text -> {
                    if (VIEW_TYPE_TEXT != mViewType) {
                        setTabView(VIEW_TYPE_TEXT)
                    }
                }
                tv_fame_comment -> {
                    if (VIEW_TYPE_COMMENT != mViewType) {
                        setTabView(VIEW_TYPE_COMMENT)
                    }
                }
                btn_check_rank->{
                    val intent = Intent(context!!, ActivityRank::class.java)
                    intent.putExtra(ActivityRank.EXTRA_VIEW_TYPE_REPUTATION,"REPUTATION")
                    startActivity(intent)
                }
            }
        }
        iv_back_blue.setOnClickListener(listener)
        tv_fame_text.setOnClickListener(listener)
        tv_fame_comment.setOnClickListener(listener)
        btn_check_rank.setOnClickListener(listener)
    }

    private val rvListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = mAdapter?.size() ?: 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_concern_item, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            val tvRecommand = h.getItemView<TextView>(R.id.tv_recommand)
            val tvTitle = h.getItemView<TextView>(R.id.tv_title)
            val tvReConcern = h.getItemView<TextView>(R.id.tv_re_concern)
            val tvStrReConcern = h.getItemView<TextView>(R.id.tv_str_re_concern)
            val llConcernItemBg = h.getItemView<LinearLayout>(R.id.ll_concern_item_bg)

            if (mAdapter?.get(i) is ConcernHistory) {
                val bean = mAdapter?.get(i) as ConcernHistory

                tvRecommand.text = bean.adopted.toString()
                tvTitle.text = bean.title
                tvReConcern.text = bean.count.toString()

                llConcernItemBg.isSelected = (bean.adopted == 1)
                tvStrReConcern.isSelected = (bean.adopted == 1)
                tvReConcern.isSelected = (bean.adopted == 1)

                h.itemView.setOnClickListener {
                    (activity as ActivityMain).replaceFragment(
                        FragmentConcernDetail.newInstance(
                            bean.idx
                        ), addToBack = true, isMainRefresh = false
                    )
                }
            } else if (mAdapter?.get(i) is ReConcernHistory) {
                val bean = mAdapter?.get(i) as ReConcernHistory

                tvRecommand.text = bean.adopted.toString()
                tvTitle.text = bean.title
                tvReConcern.text = bean.count.toString()

                llConcernItemBg.isSelected = (bean.adopted == 1)
                tvStrReConcern.isSelected = (bean.adopted == 1)
                tvReConcern.isSelected = (bean.adopted == 1)

                h.itemView.setOnClickListener {
                    (activity as ActivityMain).replaceFragment(
                        FragmentConcernDetail.newInstance(
                            bean.idx
                        ), addToBack = true, isMainRefresh = false
                    )
                }
            }
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}