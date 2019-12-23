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
import com.truevalue.dreamappeal.fragment.profile.FragmentProfile
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.activity_follow.*
import kotlinx.android.synthetic.main.fragment_dream_present.*
import okhttp3.Call
import org.json.JSONArray
import org.json.JSONObject

class ActivityFollow : BaseActivity() {

    private var mViewType: String? = null
    private var mViewUserIdx = -1

    companion object {
        val EXTRA_VIEW_TYPE = "EXTRA_VIEW_TYPE"
        val VIEW_TYPE_FOLLOWING = "VIEW_TYPE_FOLLOWING"
        val VIEW_TYPE_FOLLOWER = "VIEW_TYPE_FOLLOWER"

        val RESULT_REPLACE_USER_IDX = "RESULT_REPLACE_USER_IDX"
        val REQUEST_REPLACE_USER_IDX = 4000
        val REQUEST_VIEW_USER_IDX = "REQUEST_VIEW_USER_IDX"
    }

    private var mAdapter: BaseRecyclerViewAdapter? = null

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
        mViewType = intent.getStringExtra(EXTRA_VIEW_TYPE)
        if (!mViewType.isNullOrEmpty()) {
            when (mViewType) {
                VIEW_TYPE_FOLLOWER -> {
                    tv_title.text = getString(R.string.str_follower)
                    mViewUserIdx = intent.getIntExtra(RESULT_REPLACE_USER_IDX,-1)
                    getFollower()
                }
                VIEW_TYPE_FOLLOWING -> {
                    tv_title.text = getString(R.string.str_menu_following)
                    getFollowing()
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
        rv_follow.layoutManager = LinearLayoutManager(this@ActivityFollow)
        rv_follow.adapter = mAdapter
    }

    /**
     * Http
     * 나를 팔로우한 사람들 리스트 가져오기
     */
    private fun getFollower() {
        // todo : 현재 보고있는 프로필의 인덱스를 넣어야 합니다
        val profile_idx = mViewUserIdx
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
                    mAdapter!!.clear()
                    for (i in 0 until followers.length()) {
                        val follower = followers.getJSONObject(i)
                        val bean =
                            Gson().fromJson<BeanFollow>(follower.toString(), BeanFollow::class.java)
                        mAdapter!!.add(bean)
                    }
                }else{
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
                    mAdapter!!.clear()
                    for (i in 0 until following.length()) {
                        val follow = following.getJSONObject(i)
                        val bean =
                            Gson().fromJson<BeanFollow>(follow.toString(), BeanFollow::class.java)
                        mAdapter!!.add(bean)
                    }
                }else{
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    /**
     * RecyclerView Listener
     */
    private val listener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_follower, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            val ivProfile = h.getItemView<ImageView>(R.id.iv_profile)
            val tvValueStyle = h.getItemView<TextView>(R.id.tv_value_style)
            val tvJob = h.getItemView<TextView>(R.id.tv_job)
            val tvName = h.getItemView<TextView>(R.id.tv_name)
            val tvAddFollow = h.getItemView<TextView>(R.id.tv_add_follow)

            val bean = mAdapter!!.get(i) as BeanFollow

            if (bean.status == 1) {
                tvAddFollow.setTextColor(
                    ContextCompat.getColor(
                        this@ActivityFollow,
                        R.color.black
                    )
                )
                tvAddFollow.background = ContextCompat.getDrawable(
                    this@ActivityFollow,
                    R.drawable.bg_round_rectangle_gray2
                )
                tvAddFollow.text = getString(R.string.str_following)
            } else {
                tvAddFollow.setTextColor(
                    ContextCompat.getColor(
                        this@ActivityFollow,
                        R.color.white
                    )
                )
                tvAddFollow.background = ContextCompat.getDrawable(
                    this@ActivityFollow,
                    R.drawable.bg_round_rectangle_blue_2
                )
                tvAddFollow.text = getString(R.string.str_add_follow)
            }

            ivProfile.setOnClickListener(View.OnClickListener {
                val intent = Intent()
                intent.putExtra(ActivityFollow.RESULT_REPLACE_USER_IDX, bean.idx)
                setResult(RESULT_OK, intent)
                finish()
            })


            Glide.with(this@ActivityFollow)
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
                    if (this@ActivityFollow != null) {
                        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()

                        if (code == DAClient.SUCCESS) {
                            // todo : 여기서 팔로우 설정
                            val json = JSONObject(body)
                            val status = json.getInt("status")
                            bean!!.status = status
                            mAdapter!!.notifyDataSetChanged()
                        }
                    }
                }
            })
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}