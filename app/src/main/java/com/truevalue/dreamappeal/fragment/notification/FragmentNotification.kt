package com.truevalue.dreamappeal.fragment.notification

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityComment
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanNotification
import com.truevalue.dreamappeal.bean.Item
import com.truevalue.dreamappeal.fragment.dream_board.concern.FragmentConcernDetail
import com.truevalue.dreamappeal.fragment.profile.FragmentProfile
import com.truevalue.dreamappeal.fragment.profile.blueprint.FragmentActionPost
import com.truevalue.dreamappeal.fragment.profile.performance.FragmentBestPost
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import com.truevalue.dreamappeal.utils.Noti_Param
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.bottom_main_view.*
import kotlinx.android.synthetic.main.fragment_notification.*
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException

class FragmentNotification : BaseFragment() {


    private var mBean: BeanNotification?

    private var mViewType = VIEW_TYPE_MY_NOTI

    private var mAdapter: BaseRecyclerViewAdapter? = null

    companion object {
        private const val VIEW_TYPE_FOLLOWING = 0
        private const val VIEW_TYPE_MY_NOTI = 1
    }

    init {
        mBean = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_notification, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        initView()
        // init RecyclerView Adapter
        initAdapter()
        // 상단 View Type 설정
        setTabView(mViewType)
        // View Click Listener
        onClickView()
        // Notification 가져오기
        getNotification()
    }

    /**
     * View 초기화
     */
    private fun initView() {
    }

    /**
     * RecycerView Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvNotiListener)
        rv_noti.adapter = mAdapter
        rv_noti.layoutManager = LinearLayoutManager(context!!)

        Utils.setSwipeRefreshLayout(srl_refresh, SwipeRefreshLayout.OnRefreshListener {
            getNotification()
        })
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = OnClickListener {
            when (it) {
                tv_following -> {
                    setTabView(VIEW_TYPE_FOLLOWING)
                }
                tv_my_noti -> {
                    setTabView(VIEW_TYPE_MY_NOTI)
                }
            }
        }
        tv_following.setOnClickListener(listener)
        tv_my_noti.setOnClickListener(listener)
    }

    /**
     * Http
     * 알림 가져오기
     */
    private fun getNotification() {
        DAClient.getNotification(object : DAHttpCallback {
            override fun onFailure(call: Call, e: IOException) {
                super.onFailure(call, e)
                srl_refresh?.run {
                    isRefreshing = false
                }
            }

            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                srl_refresh?.run {
                    isRefreshing = false
                    if (code == DAClient.SUCCESS) {
                        val tvNoti = (activity as ActivityMain).tv_notification
                        tvNoti.visibility = GONE

                        val json = JSONObject(body)

                        val bean = Gson().fromJson<BeanNotification>(
                            json.toString(),
                            BeanNotification::class.java
                        )

                        if (bean.private_items.isEmpty() &&
                            bean.following_items.isNotEmpty()
                        ) {
                            mViewType = VIEW_TYPE_FOLLOWING

                        }
                        setTabView(mViewType, bean)
                    } else {
                        context?.let {
                            Toast.makeText(it.applicationContext, message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        })
    }

    /**
     * Noti 설정
     */
    private fun setNotification(bean: BeanNotification?) {
        bean?.let { bean ->
            mBean = bean
            mAdapter?.let { adapter ->
                adapter.clear()
                when (mViewType) {
                    VIEW_TYPE_FOLLOWING -> {
                        if (bean.following_items.isNotEmpty()) {
                            for (i in bean.following_items.indices) {
                                val item = bean.following_items[i]
                                adapter.add(item)
                            }
                        }
                    }
                    VIEW_TYPE_MY_NOTI -> {
                        if (bean.private_items.isNotEmpty()) {
                            for (i in bean.private_items.indices) {
                                val item = bean.private_items[i]
                                adapter.add(item)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 상단 탭 설정
     */
    private fun setTabView(view_type: Int, bean: BeanNotification? = mBean) {
        mViewType = view_type
        when (view_type) {
            VIEW_TYPE_FOLLOWING -> {
                tv_following.isSelected = true
                tv_my_noti.isSelected = false
                iv_following.visibility = VISIBLE
                iv_my_noti.visibility = INVISIBLE
                setNotification(bean)
            }
            VIEW_TYPE_MY_NOTI -> {
                tv_following.isSelected = false
                tv_my_noti.isSelected = true
                iv_following.visibility = INVISIBLE
                iv_my_noti.visibility = VISIBLE
                setNotification(bean)
            }
        }
    }


    /**
     * Notification RecyclerView Listener
     */
    val rvNotiListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_notification, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            val bean = mAdapter?.get(i) as Item
            val tvContents = h.getItemView<TextView>(R.id.tv_contents)
            val tvTime = h.getItemView<TextView>(R.id.tv_time)
            val llbg = h.getItemView<LinearLayout>(R.id.ll_bg)
            tvTime.text = Utils.convertFromDate(bean.reg_date)


            llbg.setBackgroundColor(
                if (bean.checked == 0) ContextCompat.getColor(
                    context!!,
                    R.color.noti_no_select
                ) else ContextCompat.getColor(context!!, R.color.white)
            )

            tvContents.text = "${bean.contents_bold ?: ""}${bean.contents_regular ?: ""}"
            bean.contents_bold?.let { bold ->
                bean.contents_regular?.let {
                    var spDreamDescription = Utils.replaceTextType(
                        context,
                        tvContents,
                        bold
                    )
                    tvContents.text = spDreamDescription
                }
            }
            setContentsView(h, bean)
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }

    private fun setContentsView(h: BaseViewHolder, bean: Item) {
        context?.let { context ->
            val ivProfile = h.getItemView<ImageView>(R.id.iv_profile)
            val ivFlame = h.getItemView<ImageView>(R.id.iv_flame)
            val ivPost = h.getItemView<ImageView>(R.id.iv_post)
            val rlSourceProfile = h.getItemView<RelativeLayout>(R.id.rl_source_profile)
            val tvContents = h.getItemView<TextView>(R.id.tv_contents)
            if (!bean.thumbnail_image.isNullOrEmpty()) {
                ivPost.visibility = VISIBLE
                Glide.with(context)
                    .load(bean.thumbnail_image)
                    .centerCrop()
                    .into(ivPost)
            } else {
                ivPost.visibility = GONE
            }

            if (!bean.image.isNullOrEmpty()) {
                Glide.with(context)
                    .load(bean.image)
                    .circleCrop()
                    .placeholder(R.drawable.drawer_user)
                    .into(ivProfile)
            } else {
                Glide.with(context)
                    .load(R.drawable.drawer_user)
                    .circleCrop()
                    .into(ivProfile)
            }

            rlSourceProfile.setOnClickListener {
                if (bean.code != Noti_Param.BEST_PROFILE &&
                    bean.code != Noti_Param.BEST_ACTION &&
                    bean.code != Noti_Param.BEST_IDEA &&
                    bean.code != Noti_Param.NOTICES &&
                    bean.code != Noti_Param.TARGET_NOTI
                ) {
                    if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                        profileChange(bean, object : ReplaceListener {
                            override fun replace() {
                                (activity as ActivityMain).replaceFragment(
                                    FragmentProfile.newInstance(bean.source_idx),
                                    true
                                )
                            }
                        })
                    } else {
                        (activity as ActivityMain).replaceFragment(
                            FragmentProfile.newInstance(bean.source_idx),
                            true
                        )
                    }
                }
            }

            ivProfile.setBackgroundColor(ContextCompat.getColor(context!!, R.color.transparent))
            when (bean.code) {
                Noti_Param.SHARE_PROFILE -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentProfile.newInstance(bean.item_idx),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentProfile.newInstance(bean.item_idx),
                                true
                            )
                        }
                    }
                }
                Noti_Param.SHARE_ACTION -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    when (bean.post_type) {
                                        DAClient.POST_TYPE_ACTION -> {
                                            (activity as ActivityMain).replaceFragment(
                                                FragmentActionPost.newInstance(
                                                    bean.item_idx,
                                                    bean.source_idx
                                                ),
                                                true
                                            )
                                        }
                                        DAClient.POST_TYPE_IDEA -> {
                                            (activity as ActivityMain).replaceFragment(
                                                FragmentActionPost.newInstance(
                                                    bean.item_idx,
                                                    bean.source_idx,
                                                    FragmentActionPost.TYPE_DREAM_NOTE_IDEA
                                                ),
                                                true
                                            )
                                        }
                                        DAClient.POST_TYPE_LIFE -> {
                                            (activity as ActivityMain).replaceFragment(
                                                FragmentActionPost.newInstance(
                                                    bean.item_idx,
                                                    bean.source_idx,
                                                    FragmentActionPost.TYPE_DREAM_NOTE_LIFE
                                                ),
                                                true
                                            )
                                        }
                                    }
                                }
                            })
                        } else {
                            when (bean.post_type) {
                                DAClient.POST_TYPE_ACTION -> {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentActionPost.newInstance(
                                            bean.item_idx,
                                            bean.source_idx
                                        ),
                                        true
                                    )
                                }
                                DAClient.POST_TYPE_IDEA -> {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentActionPost.newInstance(
                                            bean.item_idx,
                                            bean.source_idx,
                                            FragmentActionPost.TYPE_DREAM_NOTE_IDEA
                                        ),
                                        true
                                    )
                                }
                                DAClient.POST_TYPE_LIFE -> {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentActionPost.newInstance(
                                            bean.item_idx,
                                            bean.source_idx,
                                            FragmentActionPost.TYPE_DREAM_NOTE_LIFE
                                        ),
                                        true
                                    )
                                }
                            }
                        }
                    }

                }
                Noti_Param.SHARE_ACHIEVEMENT -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentBestPost.newInstance(
                                            bean.item_idx,
                                            bean.source_idx
                                        ),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentBestPost.newInstance(
                                    bean.item_idx,
                                    bean.source_idx
                                ),
                                true
                            )
                        }
                    }

                }
                Noti_Param.TARGET_NOTI,
                Noti_Param.NOTICES,
                Noti_Param.BEST_PROFILE,
                Noti_Param.BEST_ACTION,
                Noti_Param.BEST_IDEA -> {
                    ivFlame.visibility = GONE
                    tvContents.text = "${bean.contents_regular ?: ""}"

                    ivProfile.setBackgroundResource(R.drawable.bg_stroke_circle)
                    Glide.with(context)
                        .load(R.drawable.ic_notification)
                        .circleCrop()
                        .into(ivProfile)

                    h.itemView.setOnClickListener(null)
                }
                Noti_Param.PROFILE_LIKE -> {
                    ivFlame.visibility = VISIBLE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentProfile.newInstance(bean.item_idx),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentProfile.newInstance(bean.item_idx),
                                true
                            )
                        }
                    }
                }
                Noti_Param.PROFILE_COMMENT_LIKE -> {
                    ivFlame.visibility = VISIBLE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentProfile.newInstance(bean.item_idx),
                                        true
                                    )

                                    val intent = Intent(context!!, ActivityComment::class.java)
                                    intent.putExtra(
                                        ActivityComment.EXTRA_INDEX,
                                        bean.item_idx
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_VIEW_TYPE,
                                        ActivityComment.EXTRA_TYPE_PROFILE
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_OFF_KEYBOARD,
                                        "OFF"
                                    )
                                    startActivityForResult(
                                        intent,
                                        ActivityComment.REQUEST_REPLACE_USER_IDX
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentProfile.newInstance(bean.item_idx),
                                true
                            )

                            val intent = Intent(context!!, ActivityComment::class.java)
                            intent.putExtra(
                                ActivityComment.EXTRA_INDEX,
                                bean.item_idx
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_VIEW_TYPE,
                                ActivityComment.EXTRA_TYPE_PROFILE
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_OFF_KEYBOARD,
                                "OFF"
                            )
                            startActivityForResult(
                                intent,
                                ActivityComment.REQUEST_REPLACE_USER_IDX
                            )
                        }
                    }
                }
                Noti_Param.ACTION_LIKE -> {
                    ivFlame.visibility = VISIBLE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentActionPost.newInstance(
                                            bean.item_idx,
                                            bean.source_idx
                                        ),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentActionPost.newInstance(bean.item_idx, bean.source_idx),
                                true
                            )
                        }
                    }
                }
                Noti_Param.LIFE_LIKE -> {
                    ivFlame.visibility = VISIBLE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentActionPost.newInstance(
                                            bean.item_idx, bean.source_idx,
                                            FragmentActionPost.TYPE_DREAM_NOTE_LIFE
                                        ),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentActionPost.newInstance(
                                    bean.item_idx, bean.source_idx,
                                    FragmentActionPost.TYPE_DREAM_NOTE_LIFE
                                ),
                                true
                            )
                        }
                    }
                }
                Noti_Param.IDEA_LIKE -> {
                    ivFlame.visibility = VISIBLE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentActionPost.newInstance(
                                            bean.item_idx, bean.source_idx,
                                            FragmentActionPost.TYPE_DREAM_NOTE_IDEA
                                        ),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentActionPost.newInstance(
                                    bean.item_idx, bean.source_idx,
                                    FragmentActionPost.TYPE_DREAM_NOTE_IDEA
                                ),
                                true
                            )
                        }
                    }
                }
                Noti_Param.ACHIEVEMENT_LIKE -> {
                    ivFlame.visibility = VISIBLE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentBestPost.newInstance(
                                            bean.item_idx,
                                            bean.source_idx
                                        ),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentBestPost.newInstance(
                                    bean.item_idx,
                                    bean.source_idx
                                ),
                                true
                            )
                        }
                    }
                }
                Noti_Param.ACTION_COMMENT_LIKE -> {
                    ivFlame.visibility = VISIBLE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentActionPost.newInstance(
                                            bean.item_idx,
                                            bean.source_idx
                                        ),
                                        true
                                    )

                                    val intent = Intent(context!!, ActivityComment::class.java)
                                    intent.putExtra(
                                        ActivityComment.EXTRA_INDEX,
                                        bean.item_idx
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_VIEW_TYPE,
                                        ActivityComment.EXTRA_TYPE_ACTION_POST
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_OFF_KEYBOARD,
                                        "OFF"
                                    )
                                    startActivityForResult(
                                        intent,
                                        ActivityComment.REQUEST_REPLACE_USER_IDX
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentActionPost.newInstance(bean.item_idx, bean.source_idx),
                                true
                            )

                            val intent = Intent(context!!, ActivityComment::class.java)
                            intent.putExtra(
                                ActivityComment.EXTRA_INDEX,
                                bean.item_idx
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_VIEW_TYPE,
                                ActivityComment.EXTRA_TYPE_ACTION_POST
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_OFF_KEYBOARD,
                                "OFF"
                            )
                            startActivityForResult(
                                intent,
                                ActivityComment.REQUEST_REPLACE_USER_IDX
                            )
                        }
                    }
                }
                Noti_Param.LIFE_COMMENT_LIKE -> {
                    ivFlame.visibility = VISIBLE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentActionPost.newInstance(
                                            bean.item_idx, bean.source_idx,
                                            FragmentActionPost.TYPE_DREAM_NOTE_LIFE
                                        ),
                                        true
                                    )

                                    val intent = Intent(context!!, ActivityComment::class.java)
                                    intent.putExtra(
                                        ActivityComment.EXTRA_INDEX,
                                        bean.item_idx
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_VIEW_TYPE,
                                        ActivityComment.EXTRA_TYPE_ACTION_POST
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_OFF_KEYBOARD,
                                        "OFF"
                                    )
                                    startActivityForResult(
                                        intent,
                                        ActivityComment.REQUEST_REPLACE_USER_IDX
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentActionPost.newInstance(
                                    bean.item_idx, bean.source_idx,
                                    FragmentActionPost.TYPE_DREAM_NOTE_LIFE
                                ),
                                true
                            )

                            val intent = Intent(context!!, ActivityComment::class.java)
                            intent.putExtra(
                                ActivityComment.EXTRA_INDEX,
                                bean.item_idx
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_VIEW_TYPE,
                                ActivityComment.EXTRA_TYPE_ACTION_POST
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_OFF_KEYBOARD,
                                "OFF"
                            )
                            startActivityForResult(
                                intent,
                                ActivityComment.REQUEST_REPLACE_USER_IDX
                            )
                        }
                    }
                }
                Noti_Param.IDEA_COMMENT_LIKE -> {
                    ivFlame.visibility = VISIBLE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentActionPost.newInstance(
                                            bean.item_idx, bean.source_idx,
                                            FragmentActionPost.TYPE_DREAM_NOTE_IDEA
                                        ),
                                        true
                                    )

                                    val intent = Intent(context!!, ActivityComment::class.java)
                                    intent.putExtra(
                                        ActivityComment.EXTRA_INDEX,
                                        bean.item_idx
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_VIEW_TYPE,
                                        ActivityComment.EXTRA_TYPE_ACTION_POST
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_OFF_KEYBOARD,
                                        "OFF"
                                    )
                                    startActivityForResult(
                                        intent,
                                        ActivityComment.REQUEST_REPLACE_USER_IDX
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentActionPost.newInstance(
                                    bean.item_idx, bean.source_idx,
                                    FragmentActionPost.TYPE_DREAM_NOTE_IDEA
                                ),
                                true
                            )

                            val intent = Intent(context!!, ActivityComment::class.java)
                            intent.putExtra(
                                ActivityComment.EXTRA_INDEX,
                                bean.item_idx
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_VIEW_TYPE,
                                ActivityComment.EXTRA_TYPE_ACTION_POST
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_OFF_KEYBOARD,
                                "OFF"
                            )
                            startActivityForResult(
                                intent,
                                ActivityComment.REQUEST_REPLACE_USER_IDX
                            )
                        }
                    }
                }
                Noti_Param.ACHIEVEMET_COMMENT_LIKE -> {
                    ivFlame.visibility = VISIBLE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentBestPost.newInstance(
                                            bean.item_idx,
                                            bean.source_idx
                                        ),
                                        true
                                    )

                                    val intent = Intent(context!!, ActivityComment::class.java)
                                    intent.putExtra(
                                        ActivityComment.EXTRA_INDEX,
                                        bean.item_idx
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_VIEW_TYPE,
                                        ActivityComment.EXTRA_TYPE_ACHIEVEMENT_POST
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_OFF_KEYBOARD,
                                        "OFF"
                                    )
                                    startActivityForResult(
                                        intent,
                                        ActivityComment.REQUEST_REPLACE_USER_IDX
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentBestPost.newInstance(
                                    bean.item_idx,
                                    bean.source_idx
                                ),
                                true
                            )

                            val intent = Intent(context!!, ActivityComment::class.java)
                            intent.putExtra(
                                ActivityComment.EXTRA_INDEX,
                                bean.item_idx
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_VIEW_TYPE,
                                ActivityComment.EXTRA_TYPE_ACHIEVEMENT_POST
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_OFF_KEYBOARD,
                                "OFF"
                            )
                            startActivityForResult(
                                intent,
                                ActivityComment.REQUEST_REPLACE_USER_IDX
                            )
                        }
                    }

                }

                Noti_Param.PROFILE_FOLLOW -> {
                    ivFlame.visibility = GONE

                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentProfile.newInstance(bean.item_idx),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentProfile.newInstance(bean.item_idx),
                                true
                            )
                        }
                    }

                }
                Noti_Param.PROFILE_COMMENT -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentProfile.newInstance(bean.item_idx),
                                        true
                                    )

                                    val intent = Intent(context!!, ActivityComment::class.java)
                                    intent.putExtra(
                                        ActivityComment.EXTRA_INDEX,
                                        bean.item_idx
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_VIEW_TYPE,
                                        ActivityComment.EXTRA_TYPE_PROFILE
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_OFF_KEYBOARD,
                                        "OFF"
                                    )
                                    startActivityForResult(
                                        intent,
                                        ActivityComment.REQUEST_REPLACE_USER_IDX
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentProfile.newInstance(bean.item_idx),
                                true
                            )

                            val intent = Intent(context!!, ActivityComment::class.java)
                            intent.putExtra(
                                ActivityComment.EXTRA_INDEX,
                                bean.item_idx
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_VIEW_TYPE,
                                ActivityComment.EXTRA_TYPE_PROFILE
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_OFF_KEYBOARD,
                                "OFF"
                            )
                            startActivityForResult(
                                intent,
                                ActivityComment.REQUEST_REPLACE_USER_IDX
                            )
                        }
                    }
                }
                Noti_Param.ACTION_COMMENT -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentActionPost.newInstance(
                                            bean.item_idx,
                                            bean.source_idx
                                        ),
                                        true
                                    )

                                    val intent = Intent(context!!, ActivityComment::class.java)
                                    intent.putExtra(
                                        ActivityComment.EXTRA_INDEX,
                                        bean.item_idx
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_VIEW_TYPE,
                                        ActivityComment.EXTRA_TYPE_ACTION_POST
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_OFF_KEYBOARD,
                                        "OFF"
                                    )
                                    startActivityForResult(
                                        intent,
                                        ActivityComment.REQUEST_REPLACE_USER_IDX
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentActionPost.newInstance(bean.item_idx, bean.source_idx),
                                true
                            )

                            val intent = Intent(context!!, ActivityComment::class.java)
                            intent.putExtra(
                                ActivityComment.EXTRA_INDEX,
                                bean.item_idx
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_VIEW_TYPE,
                                ActivityComment.EXTRA_TYPE_ACTION_POST
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_OFF_KEYBOARD,
                                "OFF"
                            )
                            startActivityForResult(
                                intent,
                                ActivityComment.REQUEST_REPLACE_USER_IDX
                            )
                        }
                    }
                }
                Noti_Param.LIFE_COMMENT -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentActionPost.newInstance(
                                            bean.item_idx, bean.source_idx,
                                            FragmentActionPost.TYPE_DREAM_NOTE_LIFE
                                        ),
                                        true
                                    )

                                    val intent = Intent(context!!, ActivityComment::class.java)
                                    intent.putExtra(
                                        ActivityComment.EXTRA_INDEX,
                                        bean.item_idx
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_VIEW_TYPE,
                                        ActivityComment.EXTRA_TYPE_ACTION_POST
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_OFF_KEYBOARD,
                                        "OFF"
                                    )
                                    startActivityForResult(
                                        intent,
                                        ActivityComment.REQUEST_REPLACE_USER_IDX
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentActionPost.newInstance(
                                    bean.item_idx, bean.source_idx,
                                    FragmentActionPost.TYPE_DREAM_NOTE_LIFE
                                ),
                                true
                            )

                            val intent = Intent(context!!, ActivityComment::class.java)
                            intent.putExtra(
                                ActivityComment.EXTRA_INDEX,
                                bean.item_idx
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_VIEW_TYPE,
                                ActivityComment.EXTRA_TYPE_ACTION_POST
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_OFF_KEYBOARD,
                                "OFF"
                            )
                            startActivityForResult(
                                intent,
                                ActivityComment.REQUEST_REPLACE_USER_IDX
                            )
                        }
                    }

                }
                Noti_Param.IDEA_COMMENT -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentActionPost.newInstance(
                                            bean.item_idx, bean.source_idx,
                                            FragmentActionPost.TYPE_DREAM_NOTE_IDEA
                                        ),
                                        true
                                    )

                                    val intent = Intent(context!!, ActivityComment::class.java)
                                    intent.putExtra(
                                        ActivityComment.EXTRA_INDEX,
                                        bean.item_idx
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_VIEW_TYPE,
                                        ActivityComment.EXTRA_TYPE_ACTION_POST
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_OFF_KEYBOARD,
                                        "OFF"
                                    )
                                    startActivityForResult(
                                        intent,
                                        ActivityComment.REQUEST_REPLACE_USER_IDX
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentActionPost.newInstance(
                                    bean.item_idx, bean.source_idx,
                                    FragmentActionPost.TYPE_DREAM_NOTE_IDEA
                                ),
                                true
                            )

                            val intent = Intent(context!!, ActivityComment::class.java)
                            intent.putExtra(
                                ActivityComment.EXTRA_INDEX,
                                bean.item_idx
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_VIEW_TYPE,
                                ActivityComment.EXTRA_TYPE_ACTION_POST
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_OFF_KEYBOARD,
                                "OFF"
                            )
                            startActivityForResult(
                                intent,
                                ActivityComment.REQUEST_REPLACE_USER_IDX
                            )
                        }
                    }

                }
                Noti_Param.ACHIEVEMENT_COMMENT -> {
                    ivFlame.visibility = GONE

                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentBestPost.newInstance(
                                            bean.item_idx,
                                            bean.source_idx
                                        ),
                                        true
                                    )

                                    val intent = Intent(context!!, ActivityComment::class.java)
                                    intent.putExtra(
                                        ActivityComment.EXTRA_INDEX,
                                        bean.item_idx
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_VIEW_TYPE,
                                        ActivityComment.EXTRA_TYPE_ACHIEVEMENT_POST
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_OFF_KEYBOARD,
                                        "OFF"
                                    )
                                    startActivityForResult(
                                        intent,
                                        ActivityComment.REQUEST_REPLACE_USER_IDX
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentBestPost.newInstance(
                                    bean.item_idx,
                                    bean.source_idx
                                ),
                                true
                            )

                            val intent = Intent(context!!, ActivityComment::class.java)
                            intent.putExtra(
                                ActivityComment.EXTRA_INDEX,
                                bean.item_idx
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_VIEW_TYPE,
                                ActivityComment.EXTRA_TYPE_ACHIEVEMENT_POST
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_OFF_KEYBOARD,
                                "OFF"
                            )
                            startActivityForResult(
                                intent,
                                ActivityComment.REQUEST_REPLACE_USER_IDX
                            )
                        }
                    }

                }
                Noti_Param.ACTION_RE_COMMENT -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentActionPost.newInstance(
                                            bean.item_idx,
                                            bean.source_idx
                                        ),
                                        true
                                    )

                                    val intent = Intent(context!!, ActivityComment::class.java)
                                    intent.putExtra(
                                        ActivityComment.EXTRA_INDEX,
                                        bean.item_idx
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_VIEW_TYPE,
                                        ActivityComment.EXTRA_TYPE_ACTION_POST
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_OFF_KEYBOARD,
                                        "OFF"
                                    )
                                    startActivityForResult(
                                        intent,
                                        ActivityComment.REQUEST_REPLACE_USER_IDX
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentActionPost.newInstance(bean.item_idx, bean.source_idx),
                                true
                            )

                            val intent = Intent(context!!, ActivityComment::class.java)
                            intent.putExtra(
                                ActivityComment.EXTRA_INDEX,
                                bean.item_idx
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_VIEW_TYPE,
                                ActivityComment.EXTRA_TYPE_ACTION_POST
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_OFF_KEYBOARD,
                                "OFF"
                            )
                            startActivityForResult(
                                intent,
                                ActivityComment.REQUEST_REPLACE_USER_IDX
                            )
                        }
                    }

                }
                Noti_Param.LIFE_RE_COMMENT -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentActionPost.newInstance(
                                            bean.item_idx, bean.source_idx,
                                            FragmentActionPost.TYPE_DREAM_NOTE_LIFE
                                        ),
                                        true
                                    )

                                    val intent = Intent(context!!, ActivityComment::class.java)
                                    intent.putExtra(
                                        ActivityComment.EXTRA_INDEX,
                                        bean.item_idx
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_VIEW_TYPE,
                                        ActivityComment.EXTRA_TYPE_ACTION_POST
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_OFF_KEYBOARD,
                                        "OFF"
                                    )
                                    startActivityForResult(
                                        intent,
                                        ActivityComment.REQUEST_REPLACE_USER_IDX
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentActionPost.newInstance(
                                    bean.item_idx, bean.source_idx,
                                    FragmentActionPost.TYPE_DREAM_NOTE_LIFE
                                ),
                                true
                            )

                            val intent = Intent(context!!, ActivityComment::class.java)
                            intent.putExtra(
                                ActivityComment.EXTRA_INDEX,
                                bean.item_idx
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_VIEW_TYPE,
                                ActivityComment.EXTRA_TYPE_ACTION_POST
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_OFF_KEYBOARD,
                                "OFF"
                            )
                            startActivityForResult(
                                intent,
                                ActivityComment.REQUEST_REPLACE_USER_IDX
                            )
                        }
                    }

                }
                Noti_Param.IDEA_RE_COMMENT -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentActionPost.newInstance(
                                            bean.item_idx, bean.source_idx,
                                            FragmentActionPost.TYPE_DREAM_NOTE_IDEA
                                        ),
                                        true
                                    )

                                    val intent = Intent(context!!, ActivityComment::class.java)
                                    intent.putExtra(
                                        ActivityComment.EXTRA_INDEX,
                                        bean.item_idx
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_VIEW_TYPE,
                                        ActivityComment.EXTRA_TYPE_ACTION_POST
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_OFF_KEYBOARD,
                                        "OFF"
                                    )
                                    startActivityForResult(
                                        intent,
                                        ActivityComment.REQUEST_REPLACE_USER_IDX
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentActionPost.newInstance(
                                    bean.item_idx, bean.source_idx,
                                    FragmentActionPost.TYPE_DREAM_NOTE_IDEA
                                ),
                                true
                            )

                            val intent = Intent(context!!, ActivityComment::class.java)
                            intent.putExtra(
                                ActivityComment.EXTRA_INDEX,
                                bean.item_idx
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_VIEW_TYPE,
                                ActivityComment.EXTRA_TYPE_ACTION_POST
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_OFF_KEYBOARD,
                                "OFF"
                            )
                            startActivityForResult(
                                intent,
                                ActivityComment.REQUEST_REPLACE_USER_IDX
                            )
                        }
                    }
                }
                Noti_Param.PROFILE_RE_COMMENT -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentProfile.newInstance(bean.item_idx),
                                        true
                                    )

                                    val intent = Intent(context!!, ActivityComment::class.java)
                                    intent.putExtra(
                                        ActivityComment.EXTRA_INDEX,
                                        bean.item_idx
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_VIEW_TYPE,
                                        ActivityComment.EXTRA_TYPE_PROFILE
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_OFF_KEYBOARD,
                                        "OFF"
                                    )
                                    startActivityForResult(
                                        intent,
                                        ActivityComment.REQUEST_REPLACE_USER_IDX
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentProfile.newInstance(bean.item_idx),
                                true
                            )

                            val intent = Intent(context!!, ActivityComment::class.java)
                            intent.putExtra(
                                ActivityComment.EXTRA_INDEX,
                                bean.item_idx
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_VIEW_TYPE,
                                ActivityComment.EXTRA_TYPE_PROFILE
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_OFF_KEYBOARD,
                                "OFF"
                            )
                            startActivityForResult(
                                intent,
                                ActivityComment.REQUEST_REPLACE_USER_IDX
                            )
                        }
                    }

                }
                Noti_Param.ACHIEVEMENT_RE_COMMENT -> {
                    ivFlame.visibility = GONE

                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentBestPost.newInstance(
                                            bean.item_idx,
                                            bean.source_idx
                                        ),
                                        true
                                    )

                                    val intent = Intent(context!!, ActivityComment::class.java)
                                    intent.putExtra(
                                        ActivityComment.EXTRA_INDEX,
                                        bean.item_idx
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_VIEW_TYPE,
                                        ActivityComment.EXTRA_TYPE_ACHIEVEMENT_POST
                                    )
                                    intent.putExtra(
                                        ActivityComment.EXTRA_OFF_KEYBOARD,
                                        "OFF"
                                    )
                                    startActivityForResult(
                                        intent,
                                        ActivityComment.REQUEST_REPLACE_USER_IDX
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentBestPost.newInstance(
                                    bean.item_idx,
                                    bean.source_idx
                                ),
                                true
                            )

                            val intent = Intent(context!!, ActivityComment::class.java)
                            intent.putExtra(
                                ActivityComment.EXTRA_INDEX,
                                bean.item_idx
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_VIEW_TYPE,
                                ActivityComment.EXTRA_TYPE_ACHIEVEMENT_POST
                            )
                            intent.putExtra(
                                ActivityComment.EXTRA_OFF_KEYBOARD,
                                "OFF"
                            )
                            startActivityForResult(
                                intent,
                                ActivityComment.REQUEST_REPLACE_USER_IDX
                            )
                        }
                    }

                }
                Noti_Param.CONCERN_COMMENT_ADDED -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentConcernDetail.newInstance(bean.item_idx),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentConcernDetail.newInstance(bean.item_idx),
                                true
                            )
                        }
                    }

                }
                Noti_Param.CONCERN_VOTED -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentConcernDetail.newInstance(bean.item_idx),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentConcernDetail.newInstance(bean.item_idx),
                                true
                            )
                        }
                    }

                }
                Noti_Param.RE_CONCERN_ADOPTED -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentConcernDetail.newInstance(bean.item_idx),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentConcernDetail.newInstance(bean.item_idx),
                                true
                            )
                        }
                    }

                }
                Noti_Param.RE_CONCERN_VOTED -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentConcernDetail.newInstance(bean.item_idx),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentConcernDetail.newInstance(bean.item_idx),
                                true
                            )
                        }
                    }
                }
                Noti_Param.COMPLETE_PROFILE_OBJECT -> {
                    ivFlame.visibility = GONE

                    ivProfile.setBackgroundResource(R.drawable.bg_stroke_circle)
                    Glide.with(context)
                        .load(R.drawable.ic_noti_horn)
                        .circleCrop()
                        .into(ivProfile)

                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentProfile.newInstance(bean.item_idx),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentProfile.newInstance(bean.item_idx),
                                true
                            )
                        }
                    }
                }
                Noti_Param.PROFILE_VALUE_STYLE_JOB -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentProfile.newInstance(bean.item_idx),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentProfile.newInstance(bean.item_idx),
                                true
                            )
                        }
                    }

                }
                Noti_Param.PROFILE_DESCRIPTION -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentProfile.newInstance(bean.item_idx),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentProfile.newInstance(bean.item_idx),
                                true
                            )
                        }
                    }

                }
                Noti_Param.PROFILE_MERIT_MOTIVE -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentProfile.newInstance(bean.item_idx),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentProfile.newInstance(bean.item_idx),
                                true
                            )
                        }
                    }

                }
                Noti_Param.PROFILE_ACHIEVEMENT_POST -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentProfile.newInstance(bean.item_idx),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentProfile.newInstance(bean.item_idx),
                                true
                            )
                        }
                    }
                }
                Noti_Param.ABILITY -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentProfile.newInstance(bean.item_idx),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentProfile.newInstance(bean.item_idx),
                                true
                            )
                        }
                    }

                }
                Noti_Param.OPPORTUNITY -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentProfile.newInstance(bean.item_idx),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentProfile.newInstance(bean.item_idx),
                                true
                            )
                        }
                    }

                }
                Noti_Param.PROFILE_OBJECT -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentProfile.newInstance(bean.item_idx),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentProfile.newInstance(bean.item_idx),
                                true
                            )
                        }
                    }

                }
                Noti_Param.ADD_ACTION -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentActionPost.newInstance(
                                            bean.item_idx,
                                            bean.source_idx
                                        ),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentActionPost.newInstance(bean.item_idx, bean.source_idx),
                                true
                            )
                        }
                    }

                }
                Noti_Param.ADD_LIFE -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentActionPost.newInstance(
                                            bean.item_idx,
                                            bean.source_idx,
                                            FragmentActionPost.TYPE_DREAM_NOTE_LIFE
                                        ),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentActionPost.newInstance(
                                    bean.item_idx,
                                    bean.source_idx,
                                    FragmentActionPost.TYPE_DREAM_NOTE_LIFE
                                ),
                                true
                            )
                        }
                    }

                }
                Noti_Param.ADD_IDEA -> {
                    ivFlame.visibility = GONE
                    h.itemView.setOnClickListener {
                        checkNoti(bean)
                        if (Comm_Prefs.getUserProfileIndex() != bean.profile_idx) {
                            profileChange(bean, object : ReplaceListener {
                                override fun replace() {
                                    (activity as ActivityMain).replaceFragment(
                                        FragmentActionPost.newInstance(
                                            bean.item_idx,
                                            bean.source_idx,
                                            FragmentActionPost.TYPE_DREAM_NOTE_IDEA
                                        ),
                                        true
                                    )
                                }
                            })
                        } else {
                            (activity as ActivityMain).replaceFragment(
                                FragmentActionPost.newInstance(
                                    bean.item_idx,
                                    bean.source_idx,
                                    FragmentActionPost.TYPE_DREAM_NOTE_IDEA
                                ),
                                true
                            )
                        }
                    }

                }
                else -> {

                }
            }
        }
    }

    private fun profileChange(bean: Item, listener: ReplaceListener) {
        DAClient.profileChange(bean.profile_idx, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (code == DAClient.SUCCESS) {
                    val json = JSONObject(body)
                    val token = json.getString("token")
                    val profile_idx = json.getInt("profile_idx")
                    Comm_Prefs.setUserProfileIndex(profile_idx)
                    Comm_Prefs.setToken(token)
                    Toast.makeText(
                        context!!.applicationContext,
                        getString(R.string.str_notification_change_profile),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    listener.replace()

                } else {
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }

    /**
     * Http
     * Noti 봤는지 안봤는지 여부 확인
     */
    private fun checkNoti(bean: Item) {
        val strJson = Gson().toJson(bean)
        val json = JSONObject(strJson)
        DAClient.checkNotification(json, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (code != DAClient.SUCCESS) {
                    context?.let {
                        Toast.makeText(it.applicationContext, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private interface ReplaceListener {
        fun replace()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_CODE) {
            if (requestCode == ActivityComment.REQUEST_REPLACE_USER_IDX) {
                val view_user_idx = data!!.getIntExtra(RESULT_REPLACE_USER_IDX, -1)
                (activity as ActivityMain).replaceFragment(
                    FragmentProfile.newInstance(view_user_idx),
                    true
                )
            }
        }
    }
}