package com.truevalue.dreamappeal.fragment.profile.blueprint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanBlueprintAnO
import com.truevalue.dreamappeal.fragment.profile.FragmentAddPage
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_ano.*
import okhttp3.Call
import org.json.JSONObject

class FragmentAnO : BaseFragment() {

    private var mViewType: Int?
    private var mAdapter: BaseRecyclerViewAdapter? = null

    init {
        mViewType = VIEW_TYPE_ABILITY
    }

    companion object {
        val VIEW_TYPE_ABILITY = 0
        val VIEW_TYPE_OPPORTUNITY = 1

        fun newInstance(view_type: Int): FragmentAnO {
            val fragment = FragmentAnO()
            fragment.mViewType = view_type
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_ano, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰 초기화
        initView()
        // View Click Listener
        onClickView()
        // RecyclerView 초기화
        initAdapter()
        // init data
        setTabView(mViewType!!)
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_back_black -> activity!!.onBackPressed()
                ll_add -> {
                    val type = when (mViewType) {
                        VIEW_TYPE_ABILITY -> FragmentAddPage.VIEW_TYPE_ADD_ABILITY
                        VIEW_TYPE_OPPORTUNITY -> FragmentAddPage.VIEW_TYPE_ADD_OPPORTUNITY
                        else -> ""
                    }
                    (activity as ActivityMain).replaceFragment(
                        FragmentAddPage.newInstance(
                            type
                        ), true
                    )
                }
                ll_ability -> setTabView(VIEW_TYPE_ABILITY)
                ll_opportunity -> setTabView(VIEW_TYPE_OPPORTUNITY)
            }
        }
        ll_add.setOnClickListener(listener)
        ll_ability.setOnClickListener(listener)
        ll_opportunity.setOnClickListener(listener)
        iv_back_black.setOnClickListener(listener)
    }

    /**
     * 상단 Tab 설정
     */
    private fun setTabView(view_type: Int) {
        mViewType = view_type
        when (view_type) {
            VIEW_TYPE_ABILITY -> {
                iv_ability.isSelected = true
                tv_ability.isSelected = true
                iv_under_ability.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.yellow_orange
                    )
                )
                iv_add.setImageDrawable(
                    ContextCompat.getDrawable(
                        context!!,
                        R.drawable.ic_add_yellow
                    )
                )
                tv_add.text = getString(R.string.str_add_ability)
                tv_add.setTextColor(ContextCompat.getColor(context!!,R.color.yellow_orange))

                iv_opportunity.isSelected = false
                tv_opportunity.isSelected = false
                iv_under_opportunity.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.light_peach
                    )
                )
                getAbilities()
            }
            VIEW_TYPE_OPPORTUNITY -> {
                iv_ability.isSelected = false
                tv_ability.isSelected = false
                iv_under_ability.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.light_peach
                    )
                )
                iv_add.setImageDrawable(
                    ContextCompat.getDrawable(
                        context!!,
                        R.drawable.ic_add_orange
                    )
                )
                tv_add.text = getString(R.string.str_add_opportunity)
                tv_add.setTextColor(ContextCompat.getColor(context!!,R.color.faded_orange))

                iv_opportunity.isSelected = true
                tv_opportunity.isSelected = true
                iv_under_opportunity.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.faded_orange
                    )
                )
                getOpportunities()
            }
        }
    }

    /**
     * Http
     * 갖출 능력 조회
     */
    private fun getAbilities() {
        val profile_idx: Int =
            Comm_Prefs.getUserProfileIndex() // todo : 여기에는 현재 사용중인 프로필의 idx를 넣어야 합니다

        DAClient.getAbilities(profile_idx, object : DAHttpCallback {
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

                        mAdapter!!.clear()
                        val abilities = json.getJSONArray("abilities")
                        for (i in 0 until abilities.length()) {
                            val ability = abilities.getJSONObject(i)
                            val profile_idx = ability.getInt("profile_idx")
                            val idx = ability.getInt("idx")
                            val contents = ability.getString("ability")

                            val bean = BeanBlueprintAnO(profile_idx, idx, contents, 0)
                            mAdapter!!.add(bean)
                        }
                    }
                }
            }
        })
    }

    /**
     * Http
     * 민들고픈 기회 조회
     */
    private fun getOpportunities() {
        val profile_idx: Int =
            Comm_Prefs.getUserProfileIndex() // todo : 여기에는 현재 사용중인 프로필의 idx를 넣어야 합니다

        DAClient.getOpportunity(profile_idx, object : DAHttpCallback {
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
                        mAdapter!!.clear()
                        val opportunities = json.getJSONArray("opportunities")
                        for (i in 0 until opportunities.length()) {
                            val opportunity = opportunities.getJSONObject(i)
                            val profile_idx = opportunity.getInt("profile_idx")
                            val idx = opportunity.getInt("idx")
                            val contents = opportunity.getString("opportunity")

                            val bean = BeanBlueprintAnO(profile_idx, idx, contents, 1)
                            mAdapter!!.add(bean)
                        }
                    }
                }
            }
        })
    }

    /**
     * Http
     * 갖출 능력 삭제
     */
    private fun deleteAbility(ability_idx: Int) {
        DAClient.deleteAbility(ability_idx, object : DAHttpCallback {
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
                        // todo : 서버 호출을 굳이 해야하는지 확인
                        getAbilities()
                    }
                }
            }
        })
    }

    /**
     * Http
     * 민들고픈 기회 삭제
     */
    private fun deleteOpportunity(opportunity_idx: Int) {
        DAClient.deleteOpportunity(opportunity_idx, object : DAHttpCallback {
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
                        // todo : 서버 호출을 굳이 해야하는지 확인
                        getOpportunities()
                    }
                }
            }
        })
    }


    /**
     * View init
     */
    private fun initView() {
        tv_title.text = getText(R.string.str_default_ability_and_opportunity)
    }

    /**
     * RecyclerView 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvListener)
        rv_ano.adapter = mAdapter
        rv_ano.layoutManager = LinearLayoutManager(context)
    }

    /**
     * 임시 데이터 바인딩
     */
    private fun bindTempData() {
        for (i in 1..10) {
            mAdapter!!.add("")
        }
    }

    private val LISTITEM_VIEW_TYPE_COLOR = 0
    private val LISTITEM_VIEW_TYPE_NONE_COLOR = 1

    /**
     * RecyclerView Ability Adapter
     */
    private val rvListener = object : IORecyclerViewListener {

        /**
         * Ability 팝업 메뉴 띄우기
         */
        private fun showPopupMenu(view: View, bean: BeanBlueprintAnO, viewType: Int) {
            val popupMenu = PopupMenu(context!!, view)
            popupMenu.menu.add(getString(R.string.str_edit))
            popupMenu.menu.add(getString(R.string.str_delete))

            popupMenu.setOnMenuItemClickListener {
                when (it.title) {
                    getString(R.string.str_edit) -> {
                        val type = when (viewType) {
                            VIEW_TYPE_ABILITY -> FragmentAddPage.VIEW_TYPE_EDIT_ABILITY
                            VIEW_TYPE_OPPORTUNITY -> FragmentAddPage.VIEW_TYPE_EDIT_OPPORTUNITY
                            else -> ""
                        }

                        (activity as ActivityMain).replaceFragment(
                            FragmentAddPage.newInstance(
                                type
                                , bean
                            ), true
                        )
                    }
                    getString(R.string.str_delete) -> {
                        when (viewType) {
                            VIEW_TYPE_ABILITY -> deleteAbility(bean.idx)
                            VIEW_TYPE_OPPORTUNITY -> deleteOpportunity(bean.idx)
                        }
                    }
                }
                false
            }
            popupMenu.show()
        }

        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_detail_ano, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            val bean = mAdapter!!.get(i) as BeanBlueprintAnO
            val llItem = h.getItemView<LinearLayout>(R.id.ll_item)
            val tvContents = h.getItemView<TextView>(R.id.tv_contents)

            if (mViewType == VIEW_TYPE_ABILITY) {
                if (LISTITEM_VIEW_TYPE_NONE_COLOR == getItemViewType(i)) {
                    llItem.setBackgroundColor(ContextCompat.getColor(context!!, R.color.white))
                } else {
                    llItem.setBackgroundColor(ContextCompat.getColor(context!!, R.color.off_white))
                }
            } else if (mViewType == VIEW_TYPE_OPPORTUNITY) {
                if (LISTITEM_VIEW_TYPE_NONE_COLOR == getItemViewType(i)) {
                    llItem.setBackgroundColor(ContextCompat.getColor(context!!, R.color.white))
                } else {
                    llItem.setBackgroundColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.very_light_pink
                        )
                    )
                }
            }

            tvContents.text = bean.contents
            tvContents.setOnLongClickListener(View.OnLongClickListener {
                showPopupMenu(tvContents, bean, mViewType!!)
                false
            })
        }

        override fun getItemViewType(i: Int): Int {
            if (i % 2 == 0) return LISTITEM_VIEW_TYPE_COLOR
            return LISTITEM_VIEW_TYPE_NONE_COLOR
        }
    }
}