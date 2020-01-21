package com.truevalue.dreamappeal.fragment.profile.dream_present

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.bean.BeanDreamList
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_dream_list.*
import okhttp3.Call
import org.json.JSONObject

class FragmentDreamList : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter? = null
    private var isEdit = false

    private var mViewUserIdx: Int = -1

    companion object {
        fun newInstance(view_user_idx: Int): FragmentDreamList {
            val fragment = FragmentDreamList()
            fragment.mViewUserIdx = view_user_idx
            return fragment
        }
    }

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

    override fun onResume() {
        super.onResume()
        (activity as ActivityMain).bottom_view.visibility = GONE
    }

    override fun onPause() {
        super.onPause()
        (activity as ActivityMain).bottom_view.visibility = VISIBLE
    }

    /**
     * Init View
     */
    private fun initView() {
        // action Bar 설정
        tv_title.text = getString(R.string.str_title_dream_list)
        iv_back_black.visibility = GONE
        iv_back_blue.visibility = VISIBLE
        if (mViewUserIdx == Comm_Prefs.getUserProfileIndex()) {
            ll_edit_list.visibility = VISIBLE
        } else ll_edit_list.visibility = GONE
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
                iv_back_blue -> {
                    activity!!.onBackPressed()
                }
                iv_edit -> {
                    isEdit = !isEdit
                    mAdapter!!.notifyDataSetChanged()
                }
                ll_add_dream -> { // 새로운 프로필 추가
                    (activity as ActivityMain).replaceFragment(
                        FragmentDreamTitle.newInstance(
                            FragmentDreamTitle.MODE_NEW_PROFILE
                        ), true
                    )
                }
            }
        }

        iv_edit.setOnClickListener(listener)
        iv_back_blue.setOnClickListener(listener)
        ll_add_dream.setOnClickListener(listener)
    }

    /**
     * HTTP
     * 꿈 목록 조회
     */
    private fun getDreamList() {

        DAClient.profilesList(
            mViewUserIdx,
            object : DAHttpCallback {
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
                            val profile = json.getJSONArray("profiles")

                            if (mAdapter == null) return
                            mAdapter!!.clear()
                            for (i in 0 until profile.length()) {
                                val bean = Gson().fromJson<BeanDreamList>(
                                    profile[i].toString(),
                                    BeanDreamList::class.java
                                )
                                mAdapter!!.add(bean)
                            }
                        }else{
                            Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            })
    }

    /**
     * Http
     * 프로필 변경
     */
    private fun changeProfile(idx: Int) {

        DAClient.profileChange(
            idx,
            object : DAHttpCallback {
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
                            val token = json.getString("token")
                            val profile_idx = json.getInt("profile_idx")
                            Comm_Prefs.setUserProfileIndex(profile_idx)
                            Comm_Prefs.setToken(token)

                            (activity as ActivityMain).initAllView()
                        }else{
                            Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        )
    }

    /**
     * HTTP
     * 꿈 목록 삭제
     */
    private fun deleteProfile(idx: Int) {
        DAClient.deleteProfiles(idx, object : DAHttpCallback {
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
                        getDreamList()
                    }
                }
            }
        })
    }

    /**
     * 꿈 목록 삭제 팝업
     */
    private fun showDeleteProfileDialog(idx: Int) {
        val builder = AlertDialog.Builder(context)
            .setTitle(getString(R.string.str_delete_profile_dialog_title))
            .setMessage(getString(R.string.str_delete_profile_dialog_contents))
            .setPositiveButton(getString(R.string.str_yes)) { dialog, which ->
                if (mAdapter != null) {
                    if (mAdapter!!.size() > 1) {
                        if (idx == mViewUserIdx) {
                            Toast.makeText(
                                context!!.applicationContext,
                                getString(R.string.str_error_delete_using_profile),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            deleteProfile(idx)
                        }
                    } else {
                        Toast.makeText(
                            context!!.applicationContext,
                            getString(R.string.str_error_min_profile),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton(
                getString(R.string.str_no)
            ) { dialog, which -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }

    /**
     * 꿈 목록 변경 팝업
     */
    private fun showChangeProfileDialog(idx: Int) {
        val builder = AlertDialog.Builder(context)
            .setTitle(getString(R.string.str_change_profile_dialog_title))
            .setMessage(getString(R.string.str_change_profile_dialog_contents))
            .setPositiveButton(getString(R.string.str_yes)) { dialog, which ->
                if (mAdapter != null) {
                    changeProfile(idx)
                    dialog.dismiss()
                }
            }
            .setNegativeButton(
                getString(R.string.str_no)
            ) { dialog, which -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }

    private val recyclerViewListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
            BaseViewHolder.newInstance(R.layout.listitem_dream_list, parent, false)

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            if (mAdapter != null) {
                val bean: BeanDreamList = mAdapter!!.get(i) as BeanDreamList
                val pbExp = h.getItemView<ProgressBar>(R.id.pb_exp)
                val ivDelete = h.getItemView<ImageView>(R.id.iv_delete)
                val ctlDreamListItem = h.getItemView<ConstraintLayout>(R.id.ctl_dream_list_item)
                val ivProfile = h.getItemView<ImageView>(R.id.iv_profile)
                val tvValueStyle = h.getItemView<TextView>(R.id.tv_value_style)
                val tvJob = h.getItemView<TextView>(R.id.tv_job)
                val tvLevel = h.getItemView<TextView>(R.id.tv_level)
                val tvAchievement = h.getItemView<TextView>(R.id.tv_achievement)
                val tvAction = h.getItemView<TextView>(R.id.tv_action)
                val tvExp = h.getItemView<TextView>(R.id.tv_exp)

                Glide.with(context!!)
                    .load(bean.image)
                    .placeholder(R.drawable.drawer_user)
                    .circleCrop()
                    .into(ivProfile)

                if (bean.idx == mViewUserIdx) {
                    ctlDreamListItem.background =
                        resources.getDrawable(R.drawable.bg_empty_rectangle_blue_2)
                    ivDelete.visibility = GONE
                } else {
                    if (isEdit) ivDelete.visibility = VISIBLE
                    else ivDelete.visibility = GONE

                    ctlDreamListItem.background =
                        resources.getDrawable(R.drawable.bg_dream_list)

                    if (mViewUserIdx == Comm_Prefs.getUserProfileIndex()) {
                        h.itemView.setOnClickListener(OnClickListener {
                            showChangeProfileDialog(bean.idx)
                        })
                    }
                }

                if (mViewUserIdx == Comm_Prefs.getUserProfileIndex()) ivDelete.visibility = VISIBLE
                else ivDelete.visibility = GONE

                ivDelete.setOnClickListener(OnClickListener {
                    showDeleteProfileDialog(bean.idx)
                })

                pbExp.progress = bean.exp
                pbExp.max = bean.max_exp
                tvValueStyle.text = bean.value_style
                tvJob.text = bean.job
                tvLevel.text = String.format("Lv.%02d", bean.level)
                tvAchievement.text = bean.achievement_post_count.toString() + " / 3"
                tvAction.text = bean.action_post_count.toString() + "회"
                tvExp.text = String.format("%d / %d", bean.exp, bean.max_exp)
            }
        }

        override fun getItemViewType(i: Int): Int = 0
    }
}