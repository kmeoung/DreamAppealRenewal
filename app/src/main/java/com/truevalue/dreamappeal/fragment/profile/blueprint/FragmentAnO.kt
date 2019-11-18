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
import kotlinx.android.synthetic.main.fragment_ability_opportunity.*
import okhttp3.Call
import org.json.JSONObject

class FragmentAnO : BaseFragment() {


    private var mAbilityAdapter: BaseRecyclerViewAdapter? = null
    private var mOpportunityAdapter: BaseRecyclerViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_ability_opportunity, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰 초기화
        initView()
        // RecyclerView 초기화
        initAdapter()
        // View Click Listener
        onClickView()
        // 임시 데이터 바인딩
//        bindTempData()
        // 갖출 능력 조회
        getAbilities()
        // 민들고픈 기회 조회
        getOpportunities()
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_add_ability -> {
                    (activity as ActivityMain).replaceFragment(
                        FragmentAddPage.newInstance(
                            FragmentAddPage.VIEW_TYPE_ADD_ABILITY
                        ), true
                    )
                }
                iv_add_opportunity -> {
                    (activity as ActivityMain).replaceFragment(
                        FragmentAddPage.newInstance(
                            FragmentAddPage.VIEW_TYPE_ADD_OPPORTUNITY
                        ), true
                    )
                }
                iv_back_black -> activity!!.onBackPressed()
            }
        }
        iv_add_ability.setOnClickListener(listener)
        iv_add_opportunity.setOnClickListener(listener)
        iv_back_black.setOnClickListener(listener)
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

                        mAbilityAdapter!!.clear()
                        val abilities = json.getJSONArray("abilities")
                        for (i in 0 until abilities.length()) {
                            val ability = abilities.getJSONObject(i)
                            val profile_idx = ability.getInt("profile_idx")
                            val idx = ability.getInt("idx")
                            val contents = ability.getString("ability")

                            val bean = BeanBlueprintAnO(profile_idx, idx, contents)
                            mAbilityAdapter!!.add(bean)
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
                        mOpportunityAdapter!!.clear()
                        val opportunities = json.getJSONArray("opportunities")
                        for (i in 0 until opportunities.length()) {
                            val opportunity = opportunities.getJSONObject(i)
                            val profile_idx = opportunity.getInt("profile_idx")
                            val idx = opportunity.getInt("idx")
                            val contents = opportunity.getString("opportunity")

                            val bean = BeanBlueprintAnO(profile_idx, idx, contents)
                            mOpportunityAdapter!!.add(bean)
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
        mAbilityAdapter = BaseRecyclerViewAdapter(abilityListener)
        rv_ability.adapter = mAbilityAdapter
        rv_ability.layoutManager = LinearLayoutManager(context)

        mOpportunityAdapter = BaseRecyclerViewAdapter(opportunityListener)
        rv_opportunity.adapter = mOpportunityAdapter
        rv_opportunity.layoutManager = LinearLayoutManager(context)
    }

    /**
     * 임시 데이터 바인딩
     */
    private fun bindTempData() {
        for (i in 1..10) {
            mAbilityAdapter!!.add("")
            mOpportunityAdapter!!.add("")
        }
    }

    private val LISTITEM_VIEW_TYPE_COLOR = 0
    private val LISTITEM_VIEW_TYPE_NONE_COLOR = 1

    /**
     * RecyclerView Ability Adapter
     */
    private val abilityListener = object : IORecyclerViewListener {

        /**
         * Ability 팝업 메뉴 띄우기
         */
        private fun showPopupMenu(view: View, bean: BeanBlueprintAnO) {
            val popupMenu = PopupMenu(context!!, view)
            popupMenu.menu.add(getString(R.string.str_edit))
            popupMenu.menu.add(getString(R.string.str_delete))

            popupMenu.setOnMenuItemClickListener {
                when (it.title) {
                    getString(R.string.str_edit) -> {
                        (activity as ActivityMain).replaceFragment(
                            FragmentAddPage.newInstance(
                                FragmentAddPage.VIEW_TYPE_EDIT_ABILITY
                                , bean
                            ), true
                        )
                    }
                    getString(R.string.str_delete) -> {
                        deleteAbility(bean.idx)
                    }
                }
                false
            }
            popupMenu.show()
        }

        override val itemCount: Int
            get() = if (mAbilityAdapter != null) mAbilityAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_detail_ano, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            val bean = mAbilityAdapter!!.get(i) as BeanBlueprintAnO
            val llItem = h.getItemView<LinearLayout>(R.id.ll_item)
            val tvContents = h.getItemView<TextView>(R.id.tv_contents)
            if (LISTITEM_VIEW_TYPE_NONE_COLOR == getItemViewType(i)) {
                llItem.setBackgroundColor(ContextCompat.getColor(context!!, R.color.white))
            } else {
                llItem.setBackgroundColor(ContextCompat.getColor(context!!, R.color.off_white))
            }

            tvContents.text = bean.contents
            tvContents.setOnLongClickListener(View.OnLongClickListener {
                showPopupMenu(tvContents, bean)
                false
            })
        }

        override fun getItemViewType(i: Int): Int {
            if (i % 2 == 0) return LISTITEM_VIEW_TYPE_COLOR
            return LISTITEM_VIEW_TYPE_NONE_COLOR
        }
    }

    /**
     * RecyclerView Opportunity Adapter
     */
    private val opportunityListener = object : IORecyclerViewListener {

        /**
         * Opportunity 팝업 메뉴 띄우기
         */
        private fun showPopupMenu(view: View, bean: BeanBlueprintAnO) {
            val popupMenu = PopupMenu(context!!, view)
            popupMenu.menu.add(getString(R.string.str_edit))
            popupMenu.menu.add(getString(R.string.str_delete))

            popupMenu.setOnMenuItemClickListener {
                when (it.title) {
                    getString(R.string.str_edit) -> {
                        (activity as ActivityMain).replaceFragment(
                            FragmentAddPage.newInstance(
                                FragmentAddPage.VIEW_TYPE_EDIT_OPPORTUNITY
                                , bean
                            ), true
                        )
                    }
                    getString(R.string.str_delete) -> {
                        deleteOpportunity(bean.idx)
                    }
                }
                false
            }
            popupMenu.show()
        }

        override val itemCount: Int
            get() = if (mOpportunityAdapter != null) mOpportunityAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_detail_ano, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            val bean = mOpportunityAdapter!!.get(i) as BeanBlueprintAnO
            val llItem = h.getItemView<LinearLayout>(R.id.ll_item)
            val tvContents = h.getItemView<TextView>(R.id.tv_contents)
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

            tvContents.text = bean.contents
            tvContents.setOnLongClickListener(View.OnLongClickListener {
                showPopupMenu(tvContents, bean)
                false
            })
        }

        override fun getItemViewType(i: Int): Int {
            if (i % 2 == 0) return LISTITEM_VIEW_TYPE_COLOR
            return LISTITEM_VIEW_TYPE_NONE_COLOR
        }
    }
}