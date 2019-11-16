package com.truevalue.dreamappeal.fragment.profile.blueprint

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.bean.BeanBlueprintAnO
import com.truevalue.dreamappeal.bean.BeanBlueprintObject
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.bottom_comment_view.*
import kotlinx.android.synthetic.main.fragment_blueprint.*
import okhttp3.Call
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

class FragmentBlueprint : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private var mAnOAdapter: BaseRecyclerViewAdapter? = null // Ability & Opportunity Adapter
    private var mObjectAdapter: BaseRecyclerViewAdapter? = null // Object Adapter

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
        // 발전계획 페이지 조회
        getBlueprint()
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
     * Init View
     */
    private fun initView() {
        Utils.setSwipeRefreshLayout(srl_refresh, this)
    }

    /**
     * Init RecyclerView Adapter
     */
    private fun initAdapter() {

        mAnOAdapter = BaseRecyclerViewAdapter(abilityAndOpportunityListener)
        mObjectAdapter = BaseRecyclerViewAdapter(objectListener)

        rv_ability_and_opportunity.adapter = mAnOAdapter
        rv_object.adapter = mObjectAdapter

        rv_ability_and_opportunity.layoutManager =
            LinearLayoutManager(context)
        rv_object.layoutManager = LinearLayoutManager(context)
    }

    /**
     * HTTP
     * 발전계획 페이지 조회
     */
    private fun getBlueprint(){
        // todo : 현재 조회하고 있는 Profile User Index 를 사용해야 합니다. +
        val profile_idx = Comm_Prefs.getUserProfileIndex()
        DAClient.getBlueprint(profile_idx,object : DAHttpCallback{
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
                if(context != null){
                    Toast.makeText(context!!.applicationContext,message,Toast.LENGTH_SHORT).show()

                    if(code == DAClient.SUCCESS){

                        val abilityList = ArrayList<BeanBlueprintAnO>()
                        val opportunityList = ArrayList<BeanBlueprintAnO>()

                        val json = JSONObject(body)
                        try {
                            val commentCount = json.getInt("comment_count")
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
                                val profile_idx = ability.getInt("profile_idx")
                                val strAbility = ability.getString("ability")
                                abilityList.add(
                                    BeanBlueprintAnO(
                                        profile_idx,
                                        idx,
                                        strAbility
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
                                val profile_idx = opportunity.getInt("profile_idx")
                                val strOpportunity = opportunity.getString("opportunity")
                                opportunityList.add(
                                    BeanBlueprintAnO(
                                        profile_idx,
                                        idx,
                                        strOpportunity
                                    )
                                )
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                        tv_default_ability_opportunity.visibility = GONE
                        if (abilityList.size > 0 && opportunityList.size > 0) { // 능력 및 기회가 둘다 1개이상 있을 시
                            if (abilityList.size > 1) { // 능력이 1개이상 있을 시
                                for (i in 0..1) {
                                    mAnOAdapter!!.add(abilityList[i])
                                }

                                for (i in 0..0) {
                                    mAnOAdapter!!.add(opportunityList[i])
                                }
                            } else { // 능력이 1개일 시
                                if (opportunityList.size > 1) { // 기회가 1개 이상일 시
                                    for (i in 0..0) {
                                        mAnOAdapter!!.add(abilityList[i])
                                    }

                                    for (i in 0..1) {
                                        mAnOAdapter!!.add(opportunityList[i])
                                    }
                                } else { // 능력 및 기회가 1개일 시
                                    for (i in abilityList.indices) {
                                        mAnOAdapter!!.add(abilityList[i])
                                    }

                                    for (i in opportunityList.indices) {
                                        mAnOAdapter!!.add(opportunityList[i])
                                    }
                                }
                            }
                        } else {
                            if (abilityList.size > 0) { // 능력만 있을 시

                                var max = 0

                                if (abilityList.size > 2) {
                                    max = 3
                                } else
                                    max = abilityList.size

                                for (i in 0 until max) {
                                    mAnOAdapter!!.add(abilityList[i])
                                }

                            } else if (opportunityList.size > 0) { // 기회만 있을 시

                                var max = 0

                                if (opportunityList.size > 2) {
                                    max = 3
                                } else
                                    max = opportunityList.size

                                for (i in 0 until max) {
                                    mAnOAdapter!!.add(opportunityList[i])
                                }
                            } else { // 둘다 없을 시
                                tv_default_ability_opportunity.visibility = VISIBLE
                            }
                        }

                        var objects: JSONArray? = null
                        try {
                            objects = json.getJSONArray("objects")
                            if (objects == null || objects!!.length() < 1)
                                tv_default_object.visibility = VISIBLE
                            else tv_default_object.visibility = GONE

                            for (i in 0 until objects!!.length()) {
                                val bean = Gson().fromJson(
                                    objects!!.getJSONObject(i).toString(),
                                    BeanBlueprintObject::class.java
                                )
                                mObjectAdapter!!.add(bean)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()

                        }

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
            BaseViewHolder.newInstance(R.layout.listitem_dot_text, parent, false)

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
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
        }

        override fun getItemViewType(i: Int): Int = 0
    }

    override fun onRefresh() {
        // 여기서 서버 Refresh
        getBlueprint()
    }
}