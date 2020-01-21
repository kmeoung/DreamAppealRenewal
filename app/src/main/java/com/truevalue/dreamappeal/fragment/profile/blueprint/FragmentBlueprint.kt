package com.truevalue.dreamappeal.fragment.profile.blueprint

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityComment
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanBlueprint
import com.truevalue.dreamappeal.bean.BeanBlueprintAnO
import com.truevalue.dreamappeal.bean.BeanBlueprintObject
import com.truevalue.dreamappeal.fragment.profile.FragmentAddPage
import com.truevalue.dreamappeal.fragment.profile.FragmentProfile
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.bottom_comment_view.*
import kotlinx.android.synthetic.main.bottom_comment_view.tv_comment
import kotlinx.android.synthetic.main.fragment_blueprint.*
import kotlinx.android.synthetic.main.fragment_blueprint.srl_refresh
import kotlinx.android.synthetic.main.fragment_dream_present.*
import okhttp3.Call
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class FragmentBlueprint : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private var mAnOAdapter: BaseRecyclerViewAdapter? = null // Ability & Opportunity Adapter
    private var mObjectAdapter: BaseRecyclerViewAdapter? = null // Object Adapter
    private var mBean: BeanBlueprint? = null

    private var mViewUserIdx: Int = -1

    private var isMyDreamMore = false

    init {
        isMyDreamMore = false
    }

    companion object {
        fun newInstance(view_user_idx: Int): FragmentBlueprint {
            val fragment = FragmentBlueprint()
            fragment.mViewUserIdx = view_user_idx
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_blueprint, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        initView()
        // RecyclerView Adpater 초기화
        initAdapter()
        // 데이터 바인드
        bindData()
        // View Onclick
        onClickView()
        if (mBean == null) {
            // 발전계획 페이지 조회
            getBlueprint()
        }
        // bind Temp Data
//        bindTempData()
    }

    /**
     * Main에서 넘어온 Refresh 요청
     */
    override fun OnServerRefresh() {
        super.OnServerRefresh()
        getBlueprint()
    }

    private fun bindTempData() {
        for (i in 0..10) {
            mAnOAdapter!!.add("")
            mObjectAdapter!!.add("")
        }
    }

    /**
     * VIew OnClick Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                tv_default_ability_opportunity -> {
                    // replace to Dream List
                    (activity as ActivityMain).replaceFragment(
                        FragmentAnO.newInstance(FragmentAnO.VIEW_TYPE_ABILITY, mViewUserIdx),
                        addToBack = true,
                        isMainRefresh = true
                    )
                }
                tv_default_object,
                iv_add_object -> {
                    if (mViewUserIdx == Comm_Prefs.getUserProfileIndex()) {
                        (activity as ActivityMain).replaceFragment(
                            FragmentAddPage.newInstance(
                                FragmentAddPage.VIEW_TYPE_ADD_STEP
                            ), addToBack = true, isMainRefresh = true
                        )
                    }
                }
                ll_ability -> (activity as ActivityMain).replaceFragment(
                    FragmentAnO.newInstance(FragmentAnO.VIEW_TYPE_ABILITY, mViewUserIdx),
                    addToBack = true,
                    isMainRefresh = true
                )
                ll_opportunity -> (activity as ActivityMain).replaceFragment(
                    FragmentAnO.newInstance(FragmentAnO.VIEW_TYPE_OPPORTUNITY, mViewUserIdx),
                    addToBack = true,
                    isMainRefresh = true
                )
                rl_comment -> {
                    val intent = Intent(context!!, ActivityComment::class.java)
                    intent.putExtra(
                        ActivityComment.EXTRA_VIEW_TYPE,
                        ActivityComment.EXTRA_TYPE_BLUEPRINT
                    )
                    intent.putExtra(
                        ActivityComment.EXTRA_INDEX,
                        mViewUserIdx
                    )
                    startActivityForResult(intent, ActivityComment.REQUEST_REPLACE_USER_IDX)
                }
                btn_commit_comment -> {
                    if (btn_commit_comment.isSelected) addBlueprintComment()
                }
                btn_ability_and_opportunity -> {
                    if (isMyDreamMore) {
                        isMyDreamMore = false
                        btn_ability_and_opportunity.text = getString(R.string.str_more_view)
                    } else {
                        isMyDreamMore = true
                        btn_ability_and_opportunity.text = getString(R.string.str_close_view)
                    }
                    if (mAnOAdapter != null) mAnOAdapter!!.notifyDataSetChanged()
                }
            }
        }

        tv_default_ability_opportunity.setOnClickListener(listener)
        tv_default_object.setOnClickListener(listener)
        iv_add_object.setOnClickListener(listener)
        ll_ability.setOnClickListener(listener)
        ll_opportunity.setOnClickListener(listener)
        rl_comment.setOnClickListener(listener)
        btn_commit_comment.setOnClickListener(listener)
        btn_ability_and_opportunity.setOnClickListener(listener)

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

    /**
     * Http
     * 발전계획 댓글 추가
     */
    private fun addBlueprintComment() {
        val dst_profile_idx = mViewUserIdx //  현재 보고있는 profile을 넣어야 함
        val writer_idx = Comm_Prefs.getUserProfileIndex()
        val contents = et_comment.text.toString()
        DAClient.addBlueprintComment(
            dst_profile_idx,
            writer_idx,
            0,
            contents,
            object : DAHttpCallback {
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
                        .show()
                    if (code == DAClient.SUCCESS) {
                        et_comment.setText("")
                        // TW : 여기서 혹시 더 필요한게 있으면 추가바람
                        val intent = Intent(context!!, ActivityComment::class.java)
                        intent.putExtra(
                            ActivityComment.EXTRA_VIEW_TYPE,
                            ActivityComment.EXTRA_TYPE_BLUEPRINT
                        )
                        intent.putExtra(ActivityComment.EXTRA_OFF_KEYBOARD, "OFF")
                        intent.putExtra(
                            ActivityComment.EXTRA_INDEX,
                            mViewUserIdx
                        )
                        startActivityForResult(intent, ActivityComment.REQUEST_REPLACE_USER_IDX)
                    }
                }
            }

        )
    }

    /**
     * Init View
     */
    private fun initView() {
        Utils.setSwipeRefreshLayout(srl_refresh, this)
        if (mViewUserIdx == Comm_Prefs.getUserProfileIndex()) {
            iv_add_object.visibility = VISIBLE
        } else {
            iv_add_object.visibility = GONE
        }

        // 댓글 설정
        et_comment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                if (!et_comment.text.toString().isNullOrEmpty()) {
                    btn_commit_comment.visibility = VISIBLE
                    rl_comment.visibility = GONE
                } else {
                    btn_commit_comment.visibility = GONE
                    rl_comment.visibility = VISIBLE
                }
                btn_commit_comment.isSelected = !et_comment.text.toString().isNullOrEmpty()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    /**
     * Init RecyclerView Adapter
     */
    private fun initAdapter() {

        mAnOAdapter = BaseRecyclerViewAdapter(abilityAndOpportunityListener)
        mObjectAdapter = BaseRecyclerViewAdapter(objectListener)

        rv_ability_and_opportunity.run {
            adapter = mAnOAdapter
            layoutManager = LinearLayoutManager(context)
        }

        rv_object.run {
            adapter = mObjectAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    /**
     * 미리 있는 데이터 집어넣기
     */
    private fun bindData() {
        if (mBean != null) {
            val commentCount = mBean!!.comment_count

            tv_comment.text = Utils.getCommentView(commentCount)
            val image = mBean!!.user_image

            if (TextUtils.isEmpty(image))
                Glide.with(context!!).load(R.drawable.drawer_user).apply(
                    RequestOptions().circleCrop()
                ).into(iv_profile)
            else
                Glide.with(context!!).load(image).placeholder(R.drawable.drawer_user).apply(
                    RequestOptions().circleCrop()
                ).into(iv_profile)

            if (mAnOAdapter != null) {
                mAnOAdapter!!.clear()
                if (mBean!!.ability_and_opportunity.size > 0) {
                    tv_default_ability_opportunity.visibility = GONE
                    for (i in 0 until mBean!!.ability_and_opportunity.size) {
                        mAnOAdapter!!.add(mBean!!.ability_and_opportunity[i])
                    }
                } else {
                    tv_default_ability_opportunity.visibility = VISIBLE
                }
            } else {
                tv_default_ability_opportunity.visibility = VISIBLE
            }

            if (mObjectAdapter != null) {
                mObjectAdapter!!.clear()
                if (mBean!!.objects.size > 0) {
                    tv_default_object.visibility = GONE
                    for (i in 0 until mBean!!.objects.size) {
                        mObjectAdapter!!.add(mBean!!.objects[i])
                    }
                } else {
                    tv_default_object.visibility = VISIBLE
                }
            } else {
                tv_default_object.visibility = VISIBLE
            }
        }
    }

    /**
     * HTTP
     * 발전계획 페이지 조회
     */
    private fun getBlueprint() {
        // TW : 현재 조회하고 있는 Profile User Index 를 사용해야 합니다. +
        val profile_idx = mViewUserIdx
        DAClient.getBlueprint(profile_idx, object : DAHttpCallback {
            override fun onFailure(call: Call, e: IOException) {
                super.onFailure(call, e)
                if(srl_refresh != null) srl_refresh.isRefreshing = false
            }

            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if(srl_refresh != null) srl_refresh.isRefreshing = false
                if (context != null) {

                    if (code == DAClient.SUCCESS) {

                        val abilityList = ArrayList<BeanBlueprintAnO>()
                        val opportunityList = ArrayList<BeanBlueprintAnO>()

                        val json = JSONObject(body)

                        mBean = BeanBlueprint(0, "", ArrayList(), ArrayList())

                        try {
                            val commentCount = json.getInt("comment_count")
                            mBean!!.comment_count = commentCount

                            if (commentCount < 1000) {
                                tv_comment.text = commentCount.toString()
                            } else {
                                val k = commentCount / 1000
                                if (k < 1000) {
                                    tv_comment.text = "${k}K"
                                } else {
                                    val m = k / 1000
                                    tv_comment.text = "${m}M"
                                }
                            }

                            val image = json.getString("user_image")
                            mBean!!.user_image = image
                            if (TextUtils.isEmpty(image))
                                Glide.with(context!!).load(R.drawable.drawer_user).apply(
                                    RequestOptions().circleCrop()
                                ).into(iv_profile)
                            else
                                Glide.with(context!!).load(image).placeholder(R.drawable.drawer_user).apply(
                                    RequestOptions().circleCrop()
                                ).into(iv_profile)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                        try {
                            val abilities = json.getJSONArray("abilities")
                            for (i in 0 until abilities.length()) {
                                val ability = abilities.getJSONObject(i)
                                val idx = ability.getInt("idx")
                                val profile_index = ability.getInt("profile_idx")
                                val strAbility = ability.getString("ability")
                                abilityList.add(
                                    BeanBlueprintAnO(
                                        profile_index,
                                        idx,
                                        strAbility,
                                        0
                                    )
                                )
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }


                        try {
                            val opportunities = json.getJSONArray("opportunities")
                            for (i in 0 until opportunities.length()) {
                                val opportunity = opportunities.getJSONObject(i)
                                val idx = opportunity.getInt("idx")
                                val profile_index = opportunity.getInt("profile_idx")
                                val strOpportunity = opportunity.getString("opportunity")
                                opportunityList.add(
                                    BeanBlueprintAnO(
                                        profile_index,
                                        idx,
                                        strOpportunity,
                                        1
                                    )
                                )
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        mAnOAdapter!!.clear()
                        tv_default_ability_opportunity.visibility = GONE
                        if (abilityList.size > 0 && opportunityList.size > 0) { // 능력 및 기회가 둘다 1개이상 있을 시
                            if (abilityList.size > 1) { // 능력이 1개이상 있을 시
                                for (i in 0..1) {
                                    mAnOAdapter!!.add(abilityList[i])
                                    mBean!!.ability_and_opportunity.add(abilityList[i])
                                }

                                for (i in 0..0) {
                                    mAnOAdapter!!.add(opportunityList[i])
                                    mBean!!.ability_and_opportunity.add(opportunityList[i])
                                }
                            } else { // 능력이 1개일 시
                                if (opportunityList.size > 1) { // 기회가 1개 이상일 시
                                    for (i in 0..0) {
                                        mAnOAdapter!!.add(abilityList[i])
                                        mBean!!.ability_and_opportunity.add(abilityList[i])
                                    }

                                    for (i in 0..1) {
                                        mAnOAdapter!!.add(opportunityList[i])
                                        mBean!!.ability_and_opportunity.add(opportunityList[i])
                                    }
                                } else { // 능력 및 기회가 1개일 시
                                    for (i in abilityList.indices) {
                                        mAnOAdapter!!.add(abilityList[i])
                                        mBean!!.ability_and_opportunity.add(abilityList[i])
                                    }

                                    for (i in opportunityList.indices) {
                                        mAnOAdapter!!.add(opportunityList[i])
                                        mBean!!.ability_and_opportunity.add(opportunityList[i])
                                    }
                                }
                            }
                        } else {
                            if (abilityList.size > 0) { // 능력만 있을 시

                                var max: Int = 0

                                if (abilityList.size > 2) {
                                    max = 3
                                } else
                                    max = abilityList.size

                                for (i in 0 until max) {
                                    mAnOAdapter!!.add(abilityList[i])
                                    mBean!!.ability_and_opportunity.add(abilityList[i])
                                }

                            } else if (opportunityList.size > 0) { // 기회만 있을 시

                                var max = 0

                                if (opportunityList.size > 2) {
                                    max = 3
                                } else
                                    max = opportunityList.size

                                for (i in 0 until max) {
                                    mAnOAdapter!!.add(opportunityList[i])
                                    mBean!!.ability_and_opportunity.add(opportunityList[i])
                                }
                            } else { // 둘다 없을 시
                                tv_default_ability_opportunity.visibility = VISIBLE
                            }
                        }
                        mObjectAdapter!!.clear()
                        var objects: JSONArray? = null
                        try {
                            objects = json.getJSONArray("objects")
                            if (objects == null || objects.length() < 1)
                                tv_default_object.visibility = VISIBLE
                            else tv_default_object.visibility = GONE

                            for (i in 0 until objects!!.length()) {
                                val bean = Gson().fromJson(
                                    objects.getJSONObject(i).toString(),
                                    BeanBlueprintObject::class.java
                                )
                                mObjectAdapter!!.add(bean)
                                mBean!!.objects.add(bean)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        })
    }

    /**
     * Ability And Opportunity
     * RecyclerView Listener
     */
    private val abilityAndOpportunityListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() =
                if (mAnOAdapter != null) mAnOAdapter!!.mArray.size else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
            BaseViewHolder.newInstance(R.layout.listitem_dot_ano, parent, false)

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            val bean = mAnOAdapter!!.get(i) as BeanBlueprintAnO
            val tvContents = h.getItemView<TextView>(R.id.tv_contents)
            val ivCircle = h.getItemView<ImageView>(R.id.iv_circle)

            if (bean.view_type == BeanBlueprintAnO.VIEW_TYPE_ABILITY) {
                ivCircle.setImageDrawable(
                    ContextCompat.getDrawable(
                        context!!,
                        R.drawable.ic_circle_yellow
                    )
                )
            } else if (bean.view_type == BeanBlueprintAnO.VIEW_TYPE_OPPORTUNITY) {
                ivCircle.setImageDrawable(
                    ContextCompat.getDrawable(
                        context!!,
                        R.drawable.ic_circle_orange
                    )
                )
            }

            tvContents.text = bean.contents
            tvContents.setOnClickListener(View.OnClickListener {
                (activity as ActivityMain).replaceFragment(
                    FragmentAnO.newInstance(FragmentAnO.VIEW_TYPE_ABILITY, mViewUserIdx),
                    true
                )
            })

            if (isMyDreamMore) {
                tvContents.maxLines = 1000
            } else {
                tvContents.maxLines = 1
            }
        }

        override fun getItemViewType(i: Int): Int = 0
    }

    /**
     * Object
     * RecyclerView Listener
     */
    private val objectListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() =
                if (mObjectAdapter != null) mObjectAdapter!!.mArray.size else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
            BaseViewHolder.newInstance(R.layout.listitem_object, parent, false)

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            val bean = mObjectAdapter!!.get(i) as BeanBlueprintObject
            val tvObjectTitle = h.getItemView<TextView>(R.id.tv_object_title)
            val ivObject = h.getItemView<ImageView>(R.id.iv_object)
            val tvObjectCount = h.getItemView<TextView>(R.id.tv_object_count)
            val llComplete = h.getItemView<LinearLayout>(R.id.ll_complete)
            tvObjectTitle.text = bean.object_name

            if(bean.complete == 1){
                llComplete.visibility = VISIBLE
            }else{
                llComplete.visibility = GONE
            }

            Glide.with(context!!)
                .load(bean.thumbnail_image)
                .placeholder(R.drawable.ic_image_white)
                .centerCrop()
                .into(ivObject)

            tvObjectCount.text = "${bean.total_action_post_count}개"

            h.itemView.setOnClickListener(View.OnClickListener {
                (activity as ActivityMain).replaceFragment(
                    FragmentObjectStep.newInstance(bean, mViewUserIdx),
                    addToBack = true,
                    isMainRefresh = true
                )
            })
        }

        override fun getItemViewType(i: Int): Int = 0
    }

    override fun onRefresh() {
        // 여기서 서버 Refresh
        getBlueprint()
        Utils.downKeyBoard(activity!!)
    }

    override fun onStop() {
        super.onStop()
        Utils.downKeyBoard(activity!!)
    }
}