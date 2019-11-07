package com.truevalue.dreamappeal.fragment.profile.dream_present

import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanDreamPresent
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.fragment_dream_present.*
import okhttp3.Call
import org.json.JSONException
import org.json.JSONObject
import kotlin.math.max

class FragmentDreamPresent : BaseFragment(), IORecyclerViewListener,
    SwipeRefreshLayout.OnRefreshListener {

    private var mAdapter: BaseRecyclerViewAdapter? = null
    private var mBean: BeanDreamPresent? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_dream_present, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        initView()
        // 클릭 Listener
        onClickView()
        // todo : 이 친구 간과할수가없음
        if (mAdapter == null) {
            if (Comm_Prefs.getUserProfileIndex() > -1) {
                getProfile()
            } else {
                addDreamProfile()
            }
        }
        // Init Adapter
        initAdapter()
        // bind Temp Data
        bindTempData()
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
     * 내 꿈 소개 등록
     */
    private fun addDreamProfile() {
        DAClient.addProfiles("",
            "",
            "",
            JSONObject(),
            "",
            object : DAHttpCallback {
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    if (context != null) {
                        Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
                            .show()

                        if (code == DAClient.SUCCESS) {
                            val json = JSONObject(body)
                            val token = json.getString("token")
                            val result = json.getJSONObject("result")
                            val profile_idx = result.getInt("insertId")
                            Comm_Prefs.setToken(token)
                            Comm_Prefs.setUserProfileIndex(profile_idx)
                            getProfile() // 내 꿈 소개 조회
                        }
                    }
                }
            })
    }


    /**
     * Http
     * 내 꿈 소개 조회
     */
    private fun getProfile() {
        DAClient.getProfiles(Comm_Prefs.getUserProfileIndex(), object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (context != null) {
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
                        .show()

                    srl_refresh.isRefreshing = false
                    if (code == DAClient.SUCCESS) {
                        val json = JSONObject(body)
                        val profile = json.getJSONObject("profile")
                        val gson = Gson()
                        mBean = gson.fromJson<BeanDreamPresent>(
                            profile.toString(),
                            BeanDreamPresent::class.java
                        )
                        if (mBean != null) {
                            val bean = mBean!!
                            tv_dream_level.text = String.format("LV.%02d", bean.level)
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

                            if (bean.value_style.isNullOrEmpty() && bean.job.isNullOrEmpty()) {
                                tv_init_dream_title.visibility = VISIBLE
                            } else {
                                tv_init_dream_title.visibility = GONE

                                tv_value_style.text = bean.value_style
                                tv_job.text = bean.job
                            }

                            if (bean.description.isNullOrEmpty()) {
                                tv_init_dream_description.visibility = GONE
                            } else tv_dream_description.text = bean.description

                            if (bean.meritNmotive.isNullOrEmpty()) tv_init_merit_and_motive.visibility =
                                VISIBLE
                            else tv_init_merit_and_motive.visibility = GONE

                            tv_merit_and_motive.text = bean.meritNmotive

                            try {
                                val description_spec = profile.getJSONArray("description_spec");
                                if (description_spec.length() < 1) {
                                    tv_init_dream_description.visibility = GONE
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                tv_init_dream_description.visibility = VISIBLE
                            }
                        }
                    }
                }
            }
        })
    }

    /**
     * VIew OnClick Listener
     */
    private fun onClickView() {
        var listener = View.OnClickListener {
            when (it) {
                ll_dreams -> {
                    // replace to Dream List
                    (activity as ActivityMain).replaceFragment(FragmentDreamList(), true)
                }
                ll_follower -> {
                    // replace to Follower
                }
                iv_dream_profile -> {
                    // replace to Gallery and Camera
                }
                ll_dream_title,
                tv_init_dream_title -> {
                    // replace to Dream Title
                    (activity as ActivityMain).replaceFragment(
                        FragmentDreamTitle.newInstance(mBean),
                        true
                    )
                }
                tv_dream_description,
                tv_init_dream_description -> {
                    // replace to Dream Description
                }
                tv_merit_and_motive,
                tv_init_merit_and_motive -> {
                    // replace to Merit and Motive
                }
                btn_dream_description_more -> {
                    // Expend Description View
                }
                btn_merit_and_motive_more -> {
                    // Expend Merit Motive View
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
            // todo : Data Class 파일
//            var any = mAdapter!!.mArray[i]
        }
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
        srl_refresh.isRefreshing = false
    }
}

