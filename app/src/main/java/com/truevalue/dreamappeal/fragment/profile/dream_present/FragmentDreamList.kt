package com.truevalue.dreamappeal.fragment.profile.dream_present

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanDreamList
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import kotlinx.android.synthetic.main.action_bar_main.*
import kotlinx.android.synthetic.main.action_bar_main.tv_title
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_dream_list.*
import okhttp3.Call
import org.json.JSONObject

class FragmentDreamList : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter? = null
    private var isEdit = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dream_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        initView()
        // RecyclerView Adapter 초기화
        initAdapter()
        // View 클릭 리스너
        onClickView()
        // 꿈 목록 조회
        getDreamList()
    }

    /**
     * Init View
     */
    private fun initView() {
        // activityMain 가져오기
        val activityMain = (activity as ActivityMain)
        // action Bar 설정
        activityMain.mMainViewType = ActivityMain.ACTION_BAR_TYPE_PROFILE_OTHER
        activityMain.tv_title.text = getString(R.string.str_title_dream_list)
        iv_back_black.visibility = GONE
        iv_back_blue.visibility = VISIBLE
    }

    /**
     * Init Adapter
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(recyclerViewListener)
        rv_dream_list.layoutManager = LinearLayoutManager(context)
        rv_dream_list.adapter = mAdapter
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = OnClickListener {
            when (it) {
                iv_back_blue->{ activity!!.onBackPressed() }
                iv_edit -> {
                    isEdit = !isEdit
                    // todo : 추가 작업 필요
                }
                (activity as ActivityMain).iv_back_blue -> {
                    activity?.onBackPressed()
                }
            }
        }

        iv_edit.setOnClickListener(listener)
        iv_back_blue.setOnClickListener(listener)
        (activity as ActivityMain).iv_back_blue.setOnClickListener(listener)
    }

    /**
     * HTTP
     * 꿈 목록 조회
     */
    private fun getDreamList(){

        DAClient.profilesList(
            Comm_Prefs.getUserProfileIndex(),
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
                            val profile = json.getJSONArray("profiles")

                            if(mAdapter == null) return

                            for (i in 0 until profile.length()) {
                                val bean = Gson().fromJson<BeanDreamList>(profile[i].toString(),BeanDreamList::class.java)
                                mAdapter!!.add(bean)
                            }
                        }
                    }
                }
            })
    }

    private val recyclerViewListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
            BaseViewHolder.newInstance(R.layout.listitem_dream_list, parent, false)

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            if(mAdapter != null) {
                val bean: BeanDreamList = mAdapter!!.get(i) as BeanDreamList
                val pbExp = h.getItemView<ProgressBar>(R.id.pb_exp)
                val ivDelete = h.getItemView<ImageView>(R.id.iv_delete)
                val ctlDreamListItem =  h.getItemView<ConstraintLayout>(R.id.ctl_dream_list_item)
                val ivProfile = h.getItemView<ImageView>(R.id.iv_profile)
                val tvValueStyle = h.getItemView<TextView>(R.id.tv_value_style)
                val tvJob = h.getItemView<TextView>(R.id.tv_job)
                val tvLevel = h.getItemView<TextView>(R.id.tv_level)
                val tvAchivement = h.getItemView<TextView>(R.id.tv_achivement)
                val tvAction = h.getItemView<TextView>(R.id.tv_action)
                val tvExp = h.getItemView<TextView>(R.id.tv_exp)

                if(bean.idx == Comm_Prefs.getUserProfileIndex()){
                    ctlDreamListItem.background = resources.getDrawable(R.drawable.bg_empty_rectangle_blue_2)
                }else{
                    ctlDreamListItem.background = resources.getDrawable(R.drawable.bg_dream_list)
                }

                if(isEdit) ivDelete.visibility = VISIBLE
                else ivDelete.visibility = GONE

                pbExp.progress = bean.exp
                pbExp.max = bean.max_exp
                tvValueStyle.text = bean.value_style
                tvJob.text = bean.job
                tvLevel.text = String.format("Lv.%02d",bean.level)
                tvAchivement.text = bean.achievement_post_count.toString() + "회"
                tvAction.text = bean.action_post_count.toString() + "회"
                tvExp.text = String.format("%d / %d",bean.exp,bean.max_exp)
            }
        }

        override fun getItemViewType(i: Int): Int = 0
    }
}