package com.truevalue.dreamappeal.fragment.profile.dream_present

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityCameraGallery
import com.truevalue.dreamappeal.activity.ActivityComment
import com.truevalue.dreamappeal.activity.ActivityFollow
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.bean.BeanDreamPresent
import com.truevalue.dreamappeal.fragment.profile.FragmentProfile
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.bottom_post_view.*
import kotlinx.android.synthetic.main.fragment_dream_present.*
import kotlinx.android.synthetic.main.fragment_dream_present.iv_cheering
import kotlinx.android.synthetic.main.fragment_dream_present.iv_comment
import kotlinx.android.synthetic.main.fragment_dream_present.ll_cheering
import kotlinx.android.synthetic.main.fragment_dream_present.ll_comment
import kotlinx.android.synthetic.main.fragment_dream_present.ll_comment_detail
import kotlinx.android.synthetic.main.fragment_dream_present.ll_share
import kotlinx.android.synthetic.main.fragment_dream_present.tv_cheering
import kotlinx.android.synthetic.main.fragment_dream_present.tv_comment
import okhttp3.Call
import org.json.JSONObject
import java.io.File
import java.io.IOException

class FragmentDreamPresent : BaseFragment(), IORecyclerViewListener,
    SwipeRefreshLayout.OnRefreshListener {

    private var mAdapter: BaseRecyclerViewAdapter? = null
    private var mBean: BeanDreamPresent? = null

    private val REQUEST_CODE_PICK_PROFILE_IMAGE = 1000

    private var mViewUserIdx: Int = -1

    companion object {
        fun newInstance(view_user_idx: Int): FragmentDreamPresent {
            val fragment = FragmentDreamPresent()
            fragment.mViewUserIdx = view_user_idx
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_dream_present, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init Adapter
        initAdapter()
        // 초기 데이터 설정
        bindData()
        // View 초기화
        initView()
        // 클릭 Listener
        onClickView()
        if (mBean == null) {
            getProfile()
        }
    }

    /**
     * Main에서 넘어온 Refresh 요청
     */
    override fun OnServerRefresh() {
        super.OnServerRefresh()
        getProfile()
    }

    /**
     * 임시 데이터 Bind
     */
    private fun bindTempData() {
        for (i in 0 until 3) mAdapter!!.add("")
    }

    /**
     * View 초기화
     */
    private fun initView() {
        // Default View 키워드 색상 변경
        val default_dream_title = getString(R.string.str_default_dream_title)
        val default_dream_description = getString(R.string.str_default_dream_description)
        val default_merit = getString(R.string.str_default_merit)
        val default_morive = getString(R.string.str_default_motive)

        var spDreamTitle = Utils.replaceTextColor(
            context,
            default_dream_title,
            getString(R.string.str_designation)
        )
        var spDreamDescription = Utils.replaceTextColor(
            context,
            default_dream_description,
            getString(R.string.str_explanation)
        )
        var spMerit = Utils.replaceTextColor(context, default_merit, getString(R.string.str_merit))
        var spMotive =
            Utils.replaceTextColor(context, default_morive, getString(R.string.str_motive))

        tv_init_dream_title.text = spDreamTitle
        tv_init_dream_description.text = spDreamDescription
        tv_init_merit_and_motive.text = TextUtils.concat(spMerit, " ", spMotive)
        // Swipe Refresh Layout 설정
        Utils.setSwipeRefreshLayout(srl_refresh, this)
    }


    /**
     * Http
     * 내 꿈 소개 조회
     */
    private fun getProfile() {
        DAClient.getProfiles(mViewUserIdx, object : DAHttpCallback {
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
                        val json = JSONObject(body)
                        val profile = json.getJSONObject("profile")
                        val gson = Gson()
                        mBean = gson.fromJson<BeanDreamPresent>(
                            profile.toString(),
                            BeanDreamPresent::class.java
                        )
                        if (mBean != null) {
                            mBean!!.descriptions = ArrayList()
                            try {
                                val description_spec = profile.getJSONArray("description_spec")
                                for (i in 0 until description_spec.length()) {
                                    val jsonObject = description_spec.getJSONObject(i)
                                    val content = jsonObject.getString("content")
                                    mBean!!.descriptions.add(content)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                bindData()
                            }
                        }
                    }
                }
            }
        })
    }

    /**
     * 변수에 저장된 데이터를 다시 Binding 처리
     *
     */
    private fun bindData() {
        if (mBean != null) {
            val bean = mBean!!
            // todo : 팔로워 필요
            if(bean.follow_status != null){
                tv_add_follow.visibility = VISIBLE
                if(bean.follow_status == 1){
                    tv_add_follow.setTextColor(ContextCompat.getColor(context!!,R.color.black))
                    tv_add_follow.background = ContextCompat.getDrawable(context!!,R.drawable.bg_round_rectangle_gray2)
                    tv_add_follow.text = getString(R.string.str_following)
                }else{
                    tv_add_follow.setTextColor(ContextCompat.getColor(context!!,R.color.white))
                    tv_add_follow.background = ContextCompat.getDrawable(context!!,R.drawable.bg_round_rectangle_blue_2)
                    tv_add_follow.text = getString(R.string.str_add_follow)
                }
            }else{
                tv_add_follow.visibility = GONE
            }
            tv_follwer.text = bean.follow_count.toString()
            tv_cheering.text = "${bean.like_count}개"
            tv_comment.text = "${bean.comment_count}개"
            tv_achievement_post_count.text = bean.achievement_post_count.toString()
            tv_action_post_count.text = bean.action_post_count.toString()
            iv_cheering.isSelected = bean.status
            tv_dream_level.text = String.format("Lv.%02d", bean.level)
            tv_dream_name.text = when (bean.profile_order) {
                1 -> getString(R.string.str_first_dream)
                2 -> getString(R.string.str_second_dream)
                3 -> getString(R.string.str_third_dream)
                4 -> getString(R.string.str_forth_dream)
                5 -> getString(R.string.str_fifth_dream)
                6 -> getString(R.string.str_sixth_dream)
                7 -> getString(R.string.str_seventh_dream)
                8 -> getString(R.string.str_eighth_dream)
                9 -> getString(R.string.str_ninth_dream)
                10 -> getString(R.string.str_tenth_dream)
                else -> getString(R.string.str_first_dream)
            }

            Glide.with(this)
                .load(bean.image)
                .placeholder(R.drawable.drawer_user)
                .apply(RequestOptions().circleCrop())
                .into(iv_dream_profile)

            if (bean.value_style.isNullOrEmpty() && bean.job.isNullOrEmpty()) {
                tv_init_dream_title.visibility = VISIBLE
                tv_value_style.text = ""
                tv_job.text = ""
            } else {
                tv_init_dream_title.visibility = GONE

                tv_value_style.text = bean.value_style
                tv_job.text = bean.job
            }

            if (bean.description.isNullOrEmpty()) {
                tv_init_dream_description.visibility = VISIBLE
                tv_dream_description.text = ""
            } else {
                tv_dream_description.text = bean.description
            }

            if (bean.meritNmotive.isNullOrEmpty()) tv_init_merit_and_motive.visibility =
                VISIBLE
            else tv_init_merit_and_motive.visibility = GONE

            tv_merit_and_motive.text = bean.meritNmotive

            if (mAdapter != null) mAdapter!!.clear()

            if (bean.descriptions.size > 0) {
                tv_init_dream_description.visibility = GONE

                for (i in 0 until bean.descriptions.size) {
                    val content = bean.descriptions[i]
                    mAdapter!!.add(content)
                }
            } else {
                tv_init_dream_description.visibility = VISIBLE
            }
        }
    }

    /**
     * VIew OnClick Listener
     */
    private fun onClickView() {
        var listener = View.OnClickListener {
            when (it) {
                ll_dreams -> {
                    // replace to Dream List
                    (activity as ActivityMain).replaceFragment(
                        FragmentDreamList.newInstance(
                            mViewUserIdx
                        ), true
                    )
                }
                ll_follower -> {
                    // replace to Follower
                    val intent = Intent(context, ActivityFollow::class.java)
                    intent.putExtra(
                        ActivityFollow.EXTRA_VIEW_TYPE,
                        ActivityFollow.VIEW_TYPE_FOLLOWER
                    )
                    startActivityForResult(intent,ActivityFollow.REQUEST_REPLACE_USER_IDX)
                }
                iv_dream_profile -> {
                    // replace to Gallery and Camera
                    if (mViewUserIdx == Comm_Prefs.getUserProfileIndex()) {
                        val intent = Intent(context, ActivityCameraGallery::class.java)
                        intent.putExtra(
                            ActivityCameraGallery.SELECT_TYPE,
                            ActivityCameraGallery.EXTRA_IMAGE_SINGLE_SELECT
                        )
                        intent.putExtra(
                            ActivityCameraGallery.VIEW_TYPE,
                            ActivityCameraGallery.EXTRA_DREAM_PROFILE
                        )
                        startActivityForResult(intent, REQUEST_CODE_PICK_PROFILE_IMAGE)
                    }
                }
                ll_dream_title,
                tv_init_dream_title -> {
                    // replace to Dream Title
                    if (mViewUserIdx == Comm_Prefs.getUserProfileIndex()) {
                        (activity as ActivityMain).replaceFragment(
                            FragmentDreamTitle.newInstance(mBean),
                            true
                        )
                    }
                }
                tv_dream_description,
                tv_init_dream_description -> {
                    // replace to Dream Description
                    if (mViewUserIdx == Comm_Prefs.getUserProfileIndex()) {
                        (activity as ActivityMain).replaceFragment(
                            FragmentDreamDescription.newInstance(mBean),
                            true
                        )
                    }
                }
                tv_merit_and_motive,
                tv_init_merit_and_motive -> {
                    // replace to Merit and Motive
                    if (mViewUserIdx == Comm_Prefs.getUserProfileIndex()) {
                        (activity as ActivityMain).replaceFragment(
                            FragmentMeritAndMotive.newInstance(mBean),
                            true
                        )
                    }
                }
                btn_dream_description_more -> {
                    // Expend Description View
                }
                btn_merit_and_motive_more -> {
                    // Expend Merit Motive View
                }
                ll_cheering -> {
                    profileLike()
                }
                iv_comment -> {
                    if (context != null) {
                        val intent = Intent(context!!, ActivityComment::class.java)
                        // todo : 현재 사용자가 보고있는 페이지의 idx를 보내야 합니다.
                        intent.putExtra(
                            ActivityComment.EXTRA_INDEX,
                            mViewUserIdx
                        )
                        intent.putExtra(
                            ActivityComment.EXTRA_VIEW_TYPE,
                            ActivityComment.EXTRA_TYPE_PROFILE
                        )
                        intent.putExtra(
                            ActivityComment.EXTRA_OFF_KEYBOARD,
                            "OFF"
                        )
                        startActivityForResult(intent,ActivityComment.REQUEST_REPLACE_USER_IDX)
                    }
                }
                ll_comment -> {
                    if (context != null) {
                        val intent = Intent(context!!, ActivityComment::class.java)
                        // todo : 현재 사용자가 보고있는 페이지의 idx를 보내야 합니다.
                        intent.putExtra(
                            ActivityComment.EXTRA_INDEX,
                            mViewUserIdx
                        )
                        intent.putExtra(
                            ActivityComment.EXTRA_VIEW_TYPE,
                            ActivityComment.EXTRA_TYPE_PROFILE
                        )
                        startActivityForResult(intent,ActivityComment.REQUEST_REPLACE_USER_IDX)
                    }
                }
                ll_share -> {

                }
                tv_add_follow->{
                    follow()
                }
            }
        }

        ll_dreams.setOnClickListener(listener)
        ll_follower.setOnClickListener(listener)
        iv_dream_profile.setOnClickListener(listener)
        ll_dream_title.setOnClickListener(listener)
        tv_init_dream_title.setOnClickListener(listener)
        tv_dream_description.setOnClickListener(listener)
        tv_init_dream_description.setOnClickListener(listener)
        tv_merit_and_motive.setOnClickListener(listener)
        tv_init_merit_and_motive.setOnClickListener(listener)
        btn_dream_description_more.setOnClickListener(listener)
        btn_merit_and_motive_more.setOnClickListener(listener)
        ll_cheering.setOnClickListener(listener)
        iv_comment.setOnClickListener(listener)
        ll_comment.setOnClickListener(listener)
        ll_share.setOnClickListener(listener)
        tv_add_follow.setOnClickListener(listener)
    }

    /**
     * Dream Description List Init Adapter
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(this)
        rv_dream_description.adapter = mAdapter
        rv_dream_description.layoutManager =
            LinearLayoutManager(context)
    }

    /**
     * RecyclerView Item Count
     */
    override val itemCount: Int
        get() = if (mAdapter != null) mAdapter!!.mArray.size else 0

    /**
     * RecyclerView Create View Holder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        BaseViewHolder.newInstance(R.layout.listitem_dot_text, parent, false)

    /**
     * RecyclerView Bind View Holder
     */
    override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
        if (mAdapter != null) {
            val content: String = mAdapter!!.mArray[i] as String
            h.getItemView<TextView>(R.id.tv_contents).text = content
            h.itemView.setOnClickListener(View.OnClickListener {
                (activity as ActivityMain).replaceFragment(
                    FragmentDreamDescription.newInstance(mBean),
                    true
                )
            })
        }
    }

    /**
     * Http
     * 프로필 좋아요
     */
    private fun profileLike() {
        // todo : 현재 조회중인 프로필 idx를 넣어야 합니다
        val profile_idx = mViewUserIdx
        DAClient.likeDreamPresent(profile_idx, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (context != null) {
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()

                    if (code == DAClient.SUCCESS) {
                        val json = JSONObject(body)
                        val status = json.getBoolean("status")
                        iv_cheering.isSelected = status
                        val count = json.getInt("count")
                        tv_cheering.text = "${count}개"
                    }
                }
            }
        })
    }

    /**
     * Http
     * 팔로우 / 언팔로우
     */
    private fun follow() {
        // todo : 보고있는 profile index 를 여기다가 넣어야 합니다
        val profile_idx = mViewUserIdx
        DAClient.follow(profile_idx, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (context != null) {
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()

                    if (code == DAClient.SUCCESS) {
                        // todo : 여기서 팔로우 설정
                        val json = JSONObject(body)
                        val status = json.getInt("status")
                        mBean!!.follow_status = status
                        if(status == 1){
                            tv_add_follow.setTextColor(ContextCompat.getColor(context!!,R.color.black))
                            tv_add_follow.background = ContextCompat.getDrawable(context!!,R.drawable.bg_round_rectangle_gray2)
                            tv_add_follow.text = getString(R.string.str_following)
                        }else{
                            tv_add_follow.setTextColor(ContextCompat.getColor(context!!,R.color.white))
                            tv_add_follow.background = ContextCompat.getDrawable(context!!,R.drawable.bg_round_rectangle_blue_2)
                            tv_add_follow.text = getString(R.string.str_add_follow)
                        }
                    }
                }
            }
        })
    }


    /**
     * RecyclerView Item View Type
     */
    override fun getItemViewType(i: Int): Int = 0

    /**
     * 위에서 아래로 스와이프 시 Refresh
     */
    override fun onRefresh() {
        getProfile()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK_PROFILE_IMAGE) {
                val fileArray : ArrayList<File> =
                    data!!.getSerializableExtra(ActivityCameraGallery.REQUEST_IMAGE_FILES) as ArrayList<File>

                if (fileArray.size > 0) {

                    val idx = mViewUserIdx
                    val type = DAClient.IMAGE_TYPE_PROFILE

                    Utils.uploadWithTransferUtility(
                        context!!.applicationContext,
                        fileArray[0],
                        "$type/$idx",
                        object :
                            IOS3ImageUploaderListener {
                            override fun onStateCompleted(
                                id: Int,
                                state: TransferState,
                                imageBucketAddress: String
                            ) {
                                updateProfileImage(idx, type, imageBucketAddress)
                            }

                            override fun onError(id: Int, ex: java.lang.Exception?) {

                            }
                        })

                    Glide.with(this)
                        .load(fileArray[0])
                        .placeholder(R.drawable.drawer_user)
                        .apply(RequestOptions().circleCrop())
                        .into(iv_dream_profile)
                }
            }else if (requestCode == ActivityComment.REQUEST_REPLACE_USER_IDX || requestCode == ActivityFollow.REQUEST_REPLACE_USER_IDX
                || requestCode == ActivityFollow.REQUEST_REPLACE_USER_IDX) {
                val view_user_idx = data!!.getIntExtra(ActivityComment.RESULT_REPLACE_USER_IDX,-1)
                (activity as ActivityMain).replaceFragment(FragmentProfile.newInstance(view_user_idx),true)
            }
        }
    }

    /**
     * Http
     * Profile Image Update
     */
    private fun updateProfileImage(idx: Int, type: String, url: String) {
        val list = ArrayList<String>()
        list.add(url)
        DAClient.uploadsImage(idx, type, list, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (context != null) {
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()

                    if (code == DAClient.SUCCESS) {

                    }
                }
            }
        })
    }
}

