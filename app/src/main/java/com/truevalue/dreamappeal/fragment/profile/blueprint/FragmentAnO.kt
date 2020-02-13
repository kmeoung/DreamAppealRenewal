package com.truevalue.dreamappeal.fragment.profile.blueprint

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.bean.BeanBlueprintAnO
import com.truevalue.dreamappeal.fragment.profile.FragmentAddPage
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_ano.*
import kotlinx.android.synthetic.main.fragment_merit_and_motive.*
import okhttp3.Call
import org.json.JSONObject

class FragmentAnO : BaseFragment() {

    private var mViewType: Int?
    private var mAdapter: BaseRecyclerViewAdapter? = null

    init {
        mViewType = VIEW_TYPE_ABILITY
    }

    private var mViewUserIdx: Int = -1

    companion object {
        val VIEW_TYPE_ABILITY = 0
        val VIEW_TYPE_OPPORTUNITY = 1

        fun newInstance(view_type: Int, view_user_idx: Int): FragmentAnO {
            val fragment = FragmentAnO()
            fragment.mViewType = view_type
            fragment.mViewUserIdx = view_user_idx
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
        // init Adpater
        initAdapter()
        // init data
        setTabView(mViewType!!)
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
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_back_black -> activity!!.onBackPressed()
                ll_add -> {
                    if (Comm_Prefs.getUserProfileIndex() == mViewUserIdx) {
                        val type = when (mViewType) {
                            VIEW_TYPE_ABILITY -> FragmentAddPage.VIEW_TYPE_ADD_ABILITY
                            VIEW_TYPE_OPPORTUNITY -> FragmentAddPage.VIEW_TYPE_ADD_OPPORTUNITY
                            else -> ""
                        }
                        (activity as ActivityMain).replaceFragment(
                            FragmentAddPage.newInstance(
                                type
                            ), true, isMainRefresh = false
                        )
                    }
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

        if (mViewUserIdx == Comm_Prefs.getUserProfileIndex()) {
            iv_add.visibility = VISIBLE
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
                    tv_add.text = "등록하기"
                    ll_bg_add.setBackgroundColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.yellow_orange
                        )
                    )

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
                    tv_add.text = "등록하기"
                    ll_bg_add.setBackgroundColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.faded_orange
                        )
                    )

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
        } else {
            iv_add.visibility = GONE
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
                    tv_add.text = getString(R.string.str_ability)
                    ll_bg_add.setBackgroundColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.yellow_orange
                        )
                    )

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
                    tv_add.text = getString(R.string.str_opportunity)
                    ll_bg_add.setBackgroundColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.faded_orange
                        )
                    )

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
    }

    /**
     * Http
     * 갖출 능력 조회
     */
    private fun getAbilities() {
        val profile_idx: Int = mViewUserIdx

        DAClient.getAbilities(profile_idx, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (context != null) {

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
                    } else {
                        Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
                            .show()
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
            mViewUserIdx

        DAClient.getOpportunity(profile_idx, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (context != null) {

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
                } else {
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()
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
                        //  서버 호출을 굳이 해야하는지 확인
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
                        //  서버 호출을 굳이 해야하는지 확인
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
     * 임시 데이터 바인딩
     */
    private fun bindTempData() {
        for (i in 1..10) {
            mAdapter!!.add("")
        }
    }

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
                            ), true, isMainRefresh = false
                        )
                    }
                    getString(R.string.str_delete) -> {
                        when (viewType) {
                            VIEW_TYPE_ABILITY -> {
                                val builder =
                                    AlertDialog.Builder(context!!)
                                        .setMessage(getString(R.string.str_delete_ability_contents))
                                        .setPositiveButton(
                                            getString(R.string.str_yes)
                                        ) { dialog, _ ->
                                            deleteAbility(bean.idx)
                                            dialog.dismiss()
                                        }
                                        .setNegativeButton(
                                            getString(R.string.str_no)
                                        ) { dialog, _ -> dialog.dismiss() }
                                val dialog = builder.create()
                                dialog.show()
                            }
                            VIEW_TYPE_OPPORTUNITY -> {
                                val builder =
                                    AlertDialog.Builder(context!!)
                                        .setMessage(getString(R.string.str_delete_opportunity_contents))
                                        .setPositiveButton(
                                            getString(R.string.str_yes)
                                        ) { dialog, _ ->
                                            deleteOpportunity(bean.idx)
                                            dialog.dismiss()
                                        }.setNegativeButton(
                                            getString(R.string.str_no)
                                        ) { dialog, _ -> dialog.dismiss() }
                                val dialog = builder.create()
                                dialog.show()
                            }
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
            val ivMore = h.getItemView<ImageView>(R.id.iv_more)
            llItem.setBackgroundColor(ContextCompat.getColor(context!!, R.color.white))

            tvContents.text = bean.contents

            if (mViewUserIdx == Comm_Prefs.getUserProfileIndex()) ivMore.visibility = VISIBLE
            else ivMore.visibility = GONE

            ivMore.setOnClickListener {
                showPopupMenu(ivMore, bean, mViewType!!)
            }
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}