package com.truevalue.dreamappeal.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.CheckBox
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
import com.truevalue.dreamappeal.bean.BeanScrapMember
import com.truevalue.dreamappeal.bean.ScrapTarget
import com.truevalue.dreamappeal.fragment.dream_board.concern.FragmentConcern
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.action_bar_other.tv_title
import kotlinx.android.synthetic.main.activity_follow.*
import kotlinx.android.synthetic.main.activity_follow.et_search
import kotlinx.android.synthetic.main.fragment_concern.*
import kotlinx.android.synthetic.main.listitem_follow.*
import okhttp3.Call
import org.json.JSONObject

/**
 * Created by Taewoong
 * Follower, Following, Cheering, Scrap
 */
class ActivitySFA : BaseActivity() {
    private var mAdapter: BaseRecyclerViewAdapter?

    private var scrapList : ArrayList<Int>

    // Scrap only
    private var mNotiCode : String?
    private var mItemIndex : Int?
    private val keyboardHandler: Handler
    // Other Use
    private var mViewType: String?
    private var mListIdx: Int

    init {
        mViewType = null
        mListIdx = -1
        mAdapter = null
        mNotiCode = null
        mItemIndex = null
        scrapList = ArrayList()
        keyboardHandler = Handler(Handler.Callback {
            getScrapMember(et_search.text.toString())
            true
        })
    }

    companion object {
        private const val DELAY = 1000L
        const val EXTRA_VIEW_TYPE = "EXTRA_VIEW_TYPE"
        const val VIEW_TYPE_FOLLOWING = "VIEW_TYPE_FOLLOWING"
        const val VIEW_TYPE_FOLLOWER = "VIEW_TYPE_FOLLOWER"
        const val VIEW_TYPE_CHEERING_PROFILE = "VIEW_TYPE_CHEERING_PROFILE"
        const val VIEW_TYPE_CHEERING_ACTION = "VIEW_TYPE_CHEERING_ACTION"
        const val VIEW_TYPE_CHEERING_ACHIEVEMENT = "VIEW_TYPE_CHEERING_ACHIEVEMENT"
        const val VIEW_TYPE_SCRAP = "VIEW_TYPE_SCRAP"

        const val EXTRA_NOTI_CODE = "EXTRA_NOTI_CODE"
        const val EXTRA_ITEM_INDEX = "EXTRA_ITEM_INDEX"

        const val RESULT_REPLACE_USER_IDX = "RESULT_REPLACE_USER_IDX"
        const val REQUEST_REPLACE_USER_IDX = 4000
        const val REQUEST_VIEW_LIST_IDX = "REQUEST_VIEW_LIST_IDX"

        private const val ITEM_VIEW_TYPE_FOLLOW = 0
        private const val ITEM_VIEW_TYPE_SCRAP = 1

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
                VIEW_TYPE_SCRAP->{
                    tv_title.text = getString(R.string.str_scrap)
                    mNotiCode = intent.getStringExtra(EXTRA_NOTI_CODE)
                    mItemIndex = intent.getIntExtra(EXTRA_ITEM_INDEX,-1)
                    getScrapMember()
                    tv_text_btn.visibility = VISIBLE
                    tv_text_btn.text = getString(R.string.str_send)
                    ll_search.visibility = VISIBLE
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
        tv_text_btn.visibility = GONE
        ll_search.visibility = GONE

        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                keyboardHandler.removeMessages(0)
                if (et_search.text.toString().isNotEmpty()) {
                    keyboardHandler.sendEmptyMessageDelayed(0, DELAY)
                } else {
                    getScrapMember()
                }
            }
        })

    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val clickListener = View.OnClickListener {
            when (it) {
                iv_back_blue -> finish()
                tv_text_btn->{
                    if (tv_text_btn.isSelected) {
                        scrap()
                    }
                }
            }
        }
        iv_back_blue.setOnClickListener(clickListener)
        tv_text_btn.setOnClickListener(clickListener)
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        if (mAdapter == null) mAdapter = BaseRecyclerViewAdapter(listener)
        rv_follow.run {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@ActivitySFA)
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
     * Scrap Member 조회
     */
    private fun getScrapMember(){
        DAClient.getScrapMember(object : DAHttpCallback{
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if(code == DAClient.SUCCESS){
                    val json = JSONObject(body)
                    val bean = Gson().fromJson<BeanScrapMember>(json.toString(),BeanScrapMember::class.java)
                    mAdapter?.let {
                        it.clear()
                        for (i in bean.scrap_targets){
                            it.add(i)
                        }
                    }

                }else{
                    Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    /**
     * Http
     * Scrap Member 조회
     */
    private fun getScrapMember(keyword : String){
        DAClient.getScrapMember(keyword,object : DAHttpCallback{
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if(code == DAClient.SUCCESS){
                    val json = JSONObject(body)
                    val bean = Gson().fromJson<BeanScrapMember>(json.toString(),BeanScrapMember::class.java)
                    mAdapter?.let {
                        it.clear()
                        for (i in bean.scrap_targets){
                            it.add(i)
                        }
                    }

                }else{
                    Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
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
     * Scrap
     */
    private fun scrap() {
        if(mNotiCode != null && mItemIndex != null) {
            DAClient.sendScrapMember(scrapList, mNotiCode!!, mItemIndex!!, object : DAHttpCallback {
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()

                    if (code == DAClient.SUCCESS) {
                        finish()
                    }
                }
            })
        }
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
            val cbBox = h.getItemView<CheckBox>(R.id.cb_check)
            if(ITEM_VIEW_TYPE_FOLLOW == getItemViewType(i)) {
                cbBox.visibility = GONE

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
                                this@ActivitySFA,
                                R.color.black
                            )
                        )
                        tvAddFollow.background = ContextCompat.getDrawable(
                            this@ActivitySFA,
                            R.drawable.bg_round_rectangle_gray2
                        )
                        tvAddFollow.text = getString(R.string.str_following)
                    } else {
                        tvAddFollow.setTextColor(
                            ContextCompat.getColor(
                                this@ActivitySFA,
                                R.color.white
                            )
                        )
                        tvAddFollow.background = ContextCompat.getDrawable(
                            this@ActivitySFA,
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

                h.itemView.setOnClickListener {
                    val intent = Intent()
                    intent.putExtra(RESULT_REPLACE_USER_IDX, bean.idx)
                    setResult(RESULT_CODE, intent)
                    finish()
                }


                if(bean.image.isNullOrEmpty()){
                    Glide.with(this@ActivitySFA)
                        .load(R.drawable.drawer_user)
                        .circleCrop()
                        .into(ivProfile)
                }else {
                    Glide.with(this@ActivitySFA)
                        .load(bean.image)
                        .placeholder(R.drawable.drawer_user)
                        .circleCrop()
                        .into(ivProfile)
                }

                tvValueStyle.text = bean.value_style
                tvJob.text = bean.job
                tvName.text = bean.nickname

                tvAddFollow.setOnClickListener {
                    follow(bean.idx, bean)
                }
            }else if(ITEM_VIEW_TYPE_SCRAP == getItemViewType(i)){
                val bean = mAdapter?.get(i) as ScrapTarget
                tvAddFollow.visibility = GONE

                cbBox.visibility = VISIBLE

                tvValueStyle.text = bean.value_style
                tvJob.text = bean.job
                tvName.text = bean.nickname

                if(bean.image.isNullOrEmpty()){
                    Glide.with(this@ActivitySFA)
                        .load(R.drawable.drawer_user)
                        .circleCrop()
                        .into(ivProfile)
                }else {
                    Glide.with(this@ActivitySFA)
                        .load(bean.image)
                        .placeholder(R.drawable.drawer_user)
                        .circleCrop()
                        .into(ivProfile)
                }

                cbBox.isChecked = if(scrapList.size > 0){
                    var check = false
                    for (i in 0 until scrapList.size) {
                        if(scrapList[i] == bean.idx){
                            check = true
                            break
                        }
                    }
                    check
                }else false

                cbBox.setOnClickListener {
                    if(cbBox.isChecked) scrapList.add(bean.idx)
                    else scrapList.remove(bean.idx)
                    tv_text_btn.isSelected = (scrapList.size > 0)
                }

            }
        }

        /**
         * Http
         * 팔로우 / 언팔로우
         */
        private fun follow(profile_idx: Int, bean: BeanFollow) {
            // 보고있는 profile index 를 여기다가 넣어야 합니다
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
            if(mAdapter!!.get(i) is ScrapTarget) return ITEM_VIEW_TYPE_SCRAP
            return ITEM_VIEW_TYPE_FOLLOW
        }
    }
}