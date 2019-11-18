package com.truevalue.dreamappeal.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityDreamPoint
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanDreamPoint
import com.truevalue.dreamappeal.bean.BeanPointTimer
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_dream_point.*
import okhttp3.Call
import org.json.JSONObject


class FragmentDreamPoint : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter? = null

    private val DAILY_SELECT_TYPE_DAILY = 0
    private val DAILY_SELECT_TYPE_WEEKLY = 1
    private val DAILY_SELECT_TYPE_MONTHLY = 2

    private var mDailyType = DAILY_SELECT_TYPE_DAILY
    private var mPointType: HashMap<Int, ArrayList<BeanDreamPoint>?>? = null
    private var mBeanPointTimer : BeanPointTimer? = null

    private val mTimer = object : Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            setPointTimer()
            this.sendEmptyMessageDelayed(0,100)
        }
    }

    init {

        // Map 초기화
        mPointType = HashMap()
        if (mPointType != null) {
            mPointType!![DAILY_SELECT_TYPE_DAILY] = ArrayList()
            mPointType!![DAILY_SELECT_TYPE_WEEKLY] = ArrayList()
            mPointType!![DAILY_SELECT_TYPE_MONTHLY] = ArrayList()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dream_point, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        initView()
        // 처음 뷰 설정
        selectDaily(mDailyType)
        // recyclerview adapter init
        initAdatper()
        // View Click Listener
        onClickView()
        // 임시 데이터 Bind
//        bindTempData()
        // 데이서 Bind
        getDreamPoint()

    }

    /**
     * Bind Temp Data
     */
    private fun bindTempData() {
        for (i in 1..10) {
            mAdapter!!.add("")
        }
    }


    override fun onResume() {
        super.onResume()
        mTimer.sendEmptyMessage(0)
    }

    override fun onPause() {
        super.onPause()
        mTimer.removeMessages(0)
    }

    /**
     * View Init
     */
    private fun initView() {
        // Action Bar 설정
        (activity as ActivityDreamPoint).iv_back_black.visibility = GONE
        (activity as ActivityDreamPoint).iv_back_blue.visibility = VISIBLE
        (activity as ActivityDreamPoint).iv_check.visibility = GONE
        (activity as ActivityDreamPoint).iv_close.visibility = GONE
        (activity as ActivityDreamPoint).tv_title.text = getString(R.string.str_dream_point)

        val mission = getString(R.string.str_dream_point_info_mission)
        val getPoint = getString(R.string.str_dream_point_info_get_point)
        val missionColor =
            Utils.replaceTextColor(context, mission, getString(R.string.str_success_mission))
        val getPointColor = Utils.replaceTextColor(context, getPoint, getString(R.string.str_get))
        tv_dream_point_info.text = TextUtils.concat(missionColor, " ", getPointColor)
    }

    /**
     * Adapter Init
     */
    private fun initAdatper() {
        mAdapter = BaseRecyclerViewAdapter(recyclerViewListener)
        rv_mission.adapter = mAdapter
        rv_mission.layoutManager = LinearLayoutManager(context)
    }

    /**
     * View OnClick Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                (activity as ActivityDreamPoint).iv_back_blue -> {
                    activity!!.finish()
                }
                ll_daily -> {
                    selectDaily(DAILY_SELECT_TYPE_DAILY)
                }
                ll_weekly -> {
                    selectDaily(DAILY_SELECT_TYPE_WEEKLY)
                }
                ll_monthly -> {
                    selectDaily(DAILY_SELECT_TYPE_MONTHLY)
                }
                tv_get_and_usage -> {
                    (activity as ActivityDreamPoint).replaceFragment(FragmentDreamPointUsage(),true)
                }
                iv_coupon -> {
                    (activity as ActivityDreamPoint).replaceFragment(FragmentDreamPointCoupon(),true)
                }
            }
        }
        (activity as ActivityDreamPoint).iv_back_blue.setOnClickListener(listener)
        ll_daily.setOnClickListener(listener)
        ll_weekly.setOnClickListener(listener)
        ll_monthly.setOnClickListener(listener)
        tv_get_and_usage.setOnClickListener(listener)
        iv_coupon.setOnClickListener(listener)
    }

    /**
     * 일간 / 주간 / 월간 선택
     */
    private fun selectDaily(daily_type: Int) {
        mDailyType = daily_type

        iv_daily.isSelected = false
        tv_daily.isSelected = false
        tv_str_daily.isSelected = false
        iv_line_daily.visibility = VISIBLE
        iv_weekly.isSelected = false
        tv_weekly.isSelected = false
        tv_str_weekly.isSelected = false
        iv_line_weekly.visibility = VISIBLE
        iv_monthly.isSelected = false
        tv_monthly.isSelected = false
        tv_str_monthly.isSelected = false
        iv_line_monthly.visibility = VISIBLE

        when (daily_type) {
            DAILY_SELECT_TYPE_DAILY -> {
                iv_daily.isSelected = true
                tv_daily.isSelected = true
                tv_str_daily.isSelected = true
                iv_line_daily.visibility = INVISIBLE
            }
            DAILY_SELECT_TYPE_WEEKLY -> {
                iv_weekly.isSelected = true
                tv_weekly.isSelected = true
                tv_str_weekly.isSelected = true
                iv_line_weekly.visibility = INVISIBLE
            }
            DAILY_SELECT_TYPE_MONTHLY -> {
                iv_monthly.isSelected = true
                tv_monthly.isSelected = true
                tv_str_monthly.isSelected = true
                iv_line_monthly.visibility = INVISIBLE
            }
        }

        if (mAdapter != null) {
            if (mPointType!![daily_type]!!.size > 0) {
                mAdapter!!.clear()
                for (i in 0 until mPointType!![daily_type]!!.size) {
                    mAdapter!!.add(mPointType!![daily_type]!![i])
                }
            }
        }
    }

    /**
     * Http
     * 드림포인트 메인 조회
     */
    private fun getDreamPoint() {

        DAClient.getDreamPoint(object : DAHttpCallback {
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
                        val point = json.getInt("point")
                        tv_point.text = String.format("%,d", point)

                        if (mAdapter == null) return
                        mAdapter!!.clear()
                        mPointType!![DAILY_SELECT_TYPE_DAILY]!!.clear()
                        try {
                            val daily = json.getJSONArray("daily")
                            for (i in 0 until daily.length()) {
                                val bean = Gson().fromJson<BeanDreamPoint>(
                                    daily[i].toString(),
                                    BeanDreamPoint::class.java
                                )
                                if (mDailyType == DAILY_SELECT_TYPE_DAILY) mAdapter!!.add(bean)
                                mPointType!![DAILY_SELECT_TYPE_DAILY]!!.add(bean)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        mPointType!![DAILY_SELECT_TYPE_WEEKLY]!!.clear()
                        try {
                            val daily = json.getJSONArray("weekly")

                            for (i in 0 until daily.length()) {
                                val bean = Gson().fromJson<BeanDreamPoint>(
                                    daily[i].toString(),
                                    BeanDreamPoint::class.java
                                )
                                if (mDailyType == DAILY_SELECT_TYPE_WEEKLY) mAdapter!!.add(bean)
                                mPointType!![DAILY_SELECT_TYPE_WEEKLY]!!.add(bean)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        mPointType!![DAILY_SELECT_TYPE_MONTHLY]!!.clear()
                        try {
                            val daily = json.getJSONArray("monthly")

                            for (i in 0 until daily.length()) {
                                val bean = Gson().fromJson<BeanDreamPoint>(
                                    daily[i].toString(),
                                    BeanDreamPoint::class.java
                                )
                                if (mDailyType == DAILY_SELECT_TYPE_MONTHLY) mAdapter!!.add(bean)
                                mPointType!![DAILY_SELECT_TYPE_MONTHLY]!!.add(bean)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        try{
                            val left_time = json.getJSONObject("left_time")
                            val day = left_time.getString("day")
                            val week = left_time.getString("week")
                            val month = left_time.getString("month")
                            mBeanPointTimer = BeanPointTimer(day,week, month)

                        }catch (e: Exception){
                            e.printStackTrace()
                        }
                    }
                }
            }
        })
    }

    /**
     * Point Timer 설정
     */
    private fun setPointTimer(){
        if(mBeanPointTimer != null){
            var maximum = when(mDailyType){
                DAILY_SELECT_TYPE_DAILY->{
                    mBeanPointTimer!!.day
                }
                DAILY_SELECT_TYPE_WEEKLY->{
                    mBeanPointTimer!!.week
                }
                DAILY_SELECT_TYPE_MONTHLY->{
                    mBeanPointTimer!!.month
                }
                else-> ""
            }

            tv_time.text = Utils.getTimerTime(maximum)
        }
    }


    /**
     * RecyclerView Listener
     */
    private val recyclerViewListener = object : IORecyclerViewListener {

        private val BTN_TYPE_SUCCESS = 0
        private val BTN_TYPE_BEFORE_SUCCESS = 1
        private val BTN_TYPE_BEFORE_GET = 2

        /**
         * setting Dream Point Btn
         */
        private fun setDreamPointBtn(
            btn_type: Int,
            ll_bg: LinearLayout,
            iv_point: ImageView,
            tv_point: TextView
        ) {
            val context = context!!
            iv_point.visibility = VISIBLE

            when (btn_type) {
                BTN_TYPE_SUCCESS -> {
                    ll_bg.background =
                        ContextCompat.getDrawable(context, R.drawable.ic_mission_btn_outline)
                    iv_point.visibility = GONE
                    tv_point.setTextColor(ContextCompat.getColor(context, R.color.main_blue))
                }
                BTN_TYPE_BEFORE_SUCCESS -> {
                    ll_bg.background =
                        ContextCompat.getDrawable(context, R.drawable.ic_mission_btn_outline)
                    iv_point.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_point_blue
                        )
                    )
                    iv_point.visibility = VISIBLE
                    tv_point.setTextColor(ContextCompat.getColor(context, R.color.main_blue))
                }
                BTN_TYPE_BEFORE_GET -> {
                    ll_bg.background = ContextCompat.getDrawable(context, R.drawable.ic_mission_btn)
                    iv_point.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_point_white
                        )
                    )
                    iv_point.visibility = VISIBLE
                    tv_point.setTextColor(ContextCompat.getColor(context, R.color.white))
                }
                else -> {
                    ll_bg.background = ContextCompat.getDrawable(context, R.drawable.ic_mission_btn)
                    iv_point.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_point_white
                        )
                    )
                    tv_point.setTextColor(ContextCompat.getColor(context, R.color.white))
                }
            }
        }

        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_mission, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            if (context != null) {
                if (mAdapter != null) {
                    val bean: BeanDreamPoint = mAdapter!!.get(i) as BeanDreamPoint

                    val llBtn = h.getItemView<LinearLayout>(R.id.ll_point)
                    val ivBtn = h.getItemView<ImageView>(R.id.iv_point)
                    val tvBtn = h.getItemView<TextView>(R.id.tv_get_point)
                    val tvTitle = h.getItemView<TextView>(R.id.tv_title)
                    val pbExp = h.getItemView<ProgressBar>(R.id.pb_exp)
                    val tvStrPoint = h.getItemView<TextView>(R.id.tv_str_point)

                    var status: Int
                    status =
                        if (bean.status == 0) {if (bean.limit <= bean.curState) BTN_TYPE_BEFORE_GET else BTN_TYPE_BEFORE_SUCCESS} else BTN_TYPE_SUCCESS

                    tvTitle.text = bean.missionName
                    pbExp.max = bean.limit
                    pbExp.progress = bean.curState
                    tvBtn.text = if(status == BTN_TYPE_SUCCESS) getString(R.string.str_success) else bean.point.toString()
                    tvStrPoint.text = "${bean.curState}/${bean.limit}"
                    setDreamPointBtn(status, llBtn, ivBtn, tvBtn)

                    if(status == BTN_TYPE_SUCCESS){
                        tvBtn.textSize = 15.0f
                        tvBtn.setPadding(0,0,0,0)
                    }else{
                        tvBtn.textSize = 18.0f

                        if(tvBtn.text.toString().length > 1){
                            tvBtn.setPadding(2,0,0,0)
                        }else tvBtn.setPadding(6,0,0,0)
                    }

                    llBtn.gravity = Gravity.CENTER

                    if(status == BTN_TYPE_BEFORE_GET){
                        llBtn.setOnClickListener(OnClickListener {
                            // todo : 여기서 포인트 얻는 서버요청이 필요합니다
                            getMissionPoint(bean.idx)
                        })
                    }
                }
            }
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }

    /**
     * Http
     * 이벤트 드림포인트 받기
     */
    private fun getMissionPoint(idx : Int){

        DAClient.getMissionPoint(idx, object : DAHttpCallback{
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if(context != null){
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()

                    if (code == DAClient.SUCCESS) {
                        getDreamPoint()
                    }
                }
            }
        })

    }
}