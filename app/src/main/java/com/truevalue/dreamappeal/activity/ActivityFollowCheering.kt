package com.truevalue.dreamappeal.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanFollow
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Param
import com.truevalue.dreamappeal.utils.Comm_Prefs
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.activity_follow.*
import okhttp3.Call
import org.json.JSONObject

class ActivityFollowCheering : BaseActivity() {

    private var mViewType: String?
    private var mListIdx: Int
    private var mAdapter: BaseRecyclerViewAdapter?

    init {
        mViewType = null
        mListIdx = -1
        mAdapter = null
    }

    companion object {
        const val EXTRA_VIEW_TYPE = "EXTRA_VIEW_TYPE"
        const val VIEW_TYPE_FOLLOWING = "VIEW_TYPE_FOLLOWING"
        const val VIEW_TYPE_FOLLOWER = "VIEW_TYPE_FOLLOWER"
        const val VIEW_TYPE_CHEERING_PROFILE = "VIEW_TYPE_CHEERING_PROFILE"
        const val VIEW_TYPE_CHEERING_ACTION = "VIEW_TYPE_CHEERING_ACTION"
        const val VIEW_TYPE_CHEERING_ACHIEVEMENT = "VIEW_TYPE_CHEERING_ACHIEVEMENT"

        const val RESULT_REPLACE_USER_IDX = "RESULT_REPLACE_USER_IDX"
        const val REQUEST_REPLACE_USER_IDX = 4000
        const val REQUEST_VIEW_LIST_IDX = "REQUEST_VIEW_LIST_IDX"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follow)
    }

    override fun onResume() {
        super.onResume()
        // action
        onAction()
    }

    /**
     * Action
     */
    private fun onAction() {
        // Init View
        initView()
        // RecyclerView 초기화
        initAdapter()
        // View 클릭 리스너
        onClickView()
        // Init Data
        initData()
    }

    /**
     * 데이터 초기화
     */
    private fun initData() {

        intent.getStringExtra(EXTRA_VIEW_TYPE)?.let {
            mViewType = it
            when (mViewType) {
                VIEW_TYPE_FOLLOWER -> {
                    tv_title.text = getString(R.string.str_follower)
                    mListIdx = intent.getIntExtra(REQUEST_VIEW_LIST_IDX, -1)
                    getFollower()
                }
                VIEW_TYPE_FOLLOWING -> {
                    tv_title.text = getString(R.string.str_menu_following)
                    getFollowing()
                }
                VIEW_TYPE_CHEERING_PROFILE -> {
                    tv_title.text = getString(R.string.str_cheering_member)
                    mListIdx = intent.getIntExtra(REQUEST_VIEW_LIST_IDX, -1)
                    getCheeringMember()
                }
                VIEW_TYPE_CHEERING_ACTION -> {
                    tv_title.text = getString(R.string.str_cheering_member)
                    mListIdx = intent.getIntExtra(REQUEST_VIEW_LIST_IDX, -1)
                    getCheeringMember()
                }
                VIEW_TYPE_CHEERING_ACHIEVEMENT -> {
                    tv_title.text = getString(R.string.str_cheering_member)
                    mListIdx = intent.getIntExtra(REQUEST_VIEW_LIST_IDX, -1)
                    getCheeringMember()
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

    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val clickListener = View.OnClickListener {
            when (it) {
                iv_back_blue -> finish()
            }
        }
        iv_back_blue.setOnClickListener(clickListener)
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        if (mAdapter == null) mAdapter = BaseRecyclerViewAdapter(listener)
        rv_follow.run {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@ActivityFollowCheering)
        }
    }

    /**
     * Http
     * 나를 팔로우한 사람들 리스트 가져오기
     */
    private fun getFollower() {
        val profile_idx = mListIdx
        DAClient.getFollowerList(profile_idx, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {

                if (code == DAClient.SUCCESS) {
                    val json = JSONObject(body)
                    val followers = json.getJSONArray("followers")

                    mAdapter?.let {
                        it.clear()
                        for (i in 0 until followers.length()) {
                            val follower = followers.getJSONObject(i)
                            val bean =
                                Gson().fromJson<BeanFollow>(
                                    follower.toString(),
                                    BeanFollow::class.java
                                )
                            it.add(bean)
                        }
                    }


                } else {
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    /**
     * Http
     * 내가 팔로우한 사람들 리스트 가져오기
     */
    private fun getFollowing() {
        DAClient.getFollowingList(object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {


                if (code == DAClient.SUCCESS) {
                    val json = JSONObject(body)
                    val following = json.getJSONArray("following")
                    mAdapter?.let {
                        it.clear()
                        for (i in 0 until following.length()) {
                            val follow = following.getJSONObject(i)
                            val bean =
                                Gson().fromJson<BeanFollow>(
                                    follow.toString(),
                                    BeanFollow::class.java
                                )
                            it.add(bean)
                        }
                    }

                } else {
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    /**
     * Http
     * 나를 응원해준 어필러 보기
     */
    private fun getCheeringMember() {
        when (mViewType) {
            VIEW_TYPE_CHEERING_PROFILE -> DAClient.getProfileCheering(mListIdx, cheeringListener)
            VIEW_TYPE_CHEERING_ACTION -> DAClient.getActionCheering(mListIdx, cheeringListener)
            VIEW_TYPE_CHEERING_ACHIEVEMENT -> DAClient.getAchievementCheeing(
                mListIdx,
                cheeringListener
            )
        }
    }

    private val cheeringListener = object : DAHttpCallback {
        override fun onResponse(
            call: Call,
            serverCode: Int,
            body: String,
            code: String,
            message: String
        ) {
            if (code == DAClient.SUCCESS) {
                val json = JSONObject(body)
                val following = json.getJSONArray("like_list")

                mAdapter?.let {
                    it.clear()
                    for (i in 0 until following.length()) {
                        val follow = following.getJSONObject(i)
                        val bean =
                            Gson().fromJson<BeanFollow>(follow.toString(), BeanFollow::class.java)
                        it.add(bean)
                    }
                }

            } else {
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    /**
     * RecyclerView Listener
     */
    private val listener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = mAdapter?.size() ?: 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_follow, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            val ivProfile = h.getItemView<ImageView>(R.id.iv_profile)
            val tvValueStyle = h.getItemView<TextView>(R.id.tv_value_style)
            val tvJob = h.getItemView<TextView>(R.id.tv_job)
            val tvName = h.getItemView<TextView>(R.id.tv_name)
            val tvAddFollow = h.getItemView<TextView>(R.id.tv_add_follow)

            val bean = mAdapter!!.get(i) as BeanFollow

            if (mViewType == VIEW_TYPE_CHEERING_ACHIEVEMENT ||
                mViewType == VIEW_TYPE_CHEERING_ACTION ||
                mViewType == VIEW_TYPE_CHEERING_PROFILE
            ) {
                tvAddFollow.visibility = GONE
            } else {
                if (bean.status == 1) {
                    tvAddFollow.setTextColor(
                        ContextCompat.getColor(
                            this@ActivityFollowCheering,
                            R.color.black
                        )
                    )
                    tvAddFollow.background = ContextCompat.getDrawable(
                        this@ActivityFollowCheering,
                        R.drawable.bg_round_rectangle_gray2
                    )
                    tvAddFollow.text = getString(R.string.str_following)
                } else {
                    tvAddFollow.setTextColor(
                        ContextCompat.getColor(
                            this@ActivityFollowCheering,
                            R.color.white
                        )
                    )
                    tvAddFollow.background = ContextCompat.getDrawable(
                        this@ActivityFollowCheering,
                        R.drawable.bg_round_rectangle_blue_2
                    )
                    tvAddFollow.text = getString(R.string.str_add_follow)
                }

                if (bean.idx == Comm_Prefs.getUserProfileIndex()) {
                    tvAddFollow.visibility = GONE
                } else {
                    tvAddFollow.visibility = VISIBLE
                }
            }

            ivProfile.setOnClickListener(View.OnClickListener {
                val intent = Intent()
                intent.putExtra(ActivityFollowCheering.RESULT_REPLACE_USER_IDX, bean.idx)
                setResult(RESULT_CODE, intent)
                finish()
            })


            Glide.with(this@ActivityFollowCheering)
                .load(bean.image)
                .placeholder(R.drawable.drawer_user)
                .circleCrop()
                .into(ivProfile)

            tvValueStyle.text = bean.value_style
            tvJob.text = bean.job
            tvName.text = bean.nickname

            tvAddFollow.setOnClickListener(View.OnClickListener {
                follow(bean.idx, bean)
            })
        }

        /**
         * Http
         * 팔로우 / 언팔로우
         */
        private fun follow(profile_idx: Int, bean: BeanFollow) {
            // todo : 보고있는 profile index 를 여기다가 넣어야 합니다
            DAClient.follow(profile_idx, object : DAHttpCallback {
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()

                    if (code == DAClient.SUCCESS) {
                        val json = JSONObject(body)
                        val status = json.getInt("status")
                        bean.status = status
                        mAdapter!!.notifyDataSetChanged()
                    }
                }
            })
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}