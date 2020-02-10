package com.truevalue.dreamappeal.fragment.profile.blueprint

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanActionPost
import com.truevalue.dreamappeal.bean.BeanActionPostHeader
import com.truevalue.dreamappeal.bean.BeanBlueprintObject
import com.truevalue.dreamappeal.bean.BeanObjectStep
import com.truevalue.dreamappeal.dialog.DialogObjectSuccess
import com.truevalue.dreamappeal.fragment.profile.FragmentAddPage
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_object_step.*
import kotlinx.android.synthetic.main.layout_object_step_header.*
import okhttp3.Call
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FragmentObjectStep : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {
    private var mBean: BeanBlueprintObject? = null
    private var mAdapter: BaseRecyclerViewAdapter? = null
    private var mObjectBean: BeanObjectStep? = null

    private var mViewUserIdx: Int = -1

    companion object {
        fun newInstance(bean: BeanBlueprintObject, view_user_idx: Int): FragmentObjectStep {
            val fragment = FragmentObjectStep()
            fragment.mViewUserIdx = view_user_idx
            fragment.mBean = bean
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_object_step, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // init View
        initView()
        // View Click Listener
        onClickView()
        // recyclerview adapter 초기화
        initAdapter()
        // 데이터 초기화
        initData()
    }

    /**
     * View 초기화
     */
    private fun initView() {

        tv_title.text = getString(R.string.str_object_step)

        if (mViewUserIdx == Comm_Prefs.getUserProfileIndex()) {
            iv_object_step_more.visibility = VISIBLE
            tv_detail_step.visibility = VISIBLE
        } else {
            iv_object_step_more.visibility = GONE
            tv_detail_step.visibility = GONE
        }

        Utils.setSwipeRefreshLayout(srl_refresh,this)

        tv_default_step.text = if(mViewUserIdx == Comm_Prefs.getUserProfileIndex()){
            Utils.replaceTextType(context,tv_default_step,"세부단계를 추가해 작은 것부터 실천해보세요")
        }
        else{
            "상대방이 만든 세부단계가\n여기에 표시됩니다"
        }
    }

    /**
     * VIew OnClick Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_back_black -> (activity as ActivityMain).onBackPressed()
                iv_object_step_more -> {
                    showMoreDialog()
                }
                tv_detail_step -> {
                    if (mObjectBean != null && mObjectBean!!.complete == 0) {
                        (activity as ActivityMain).replaceFragment(
                            FragmentAddPage.newInstance(
                                FragmentAddPage.VIEW_TYPE_ADD_STEP_DETAIL, mObjectBean!!.idx
                            ), addToBack = true, isMainRefresh = false
                        )
                    }
                }
            }
        }

        iv_back_black.setOnClickListener(listener)
        iv_object_step_more.setOnClickListener(listener)
        tv_detail_step.setOnClickListener(listener)

    }

    /**
     * 더보기 Dialog 띄우기
     */
    private fun showMoreDialog() {
        val list =
            arrayOf(
                if (mObjectBean!!.complete == 1) getString(R.string.str_do_not_success) else getString(R.string.str_do_success),
                getString(R.string.str_edit),
                getString(R.string.str_delete)
            )
        val builder =
            AlertDialog.Builder(context)
        builder.setItems(list) { _, i ->
            when (list[i]) {
                getString(R.string.str_do_not_success) -> {
                    updateComplete(mBean!!.idx, 0)
                }
                getString(R.string.str_do_success) -> {
                    updateComplete(mBean!!.idx, 1)
                }
                getString(R.string.str_edit) -> if (mObjectBean != null) {
                    (activity as ActivityMain).replaceFragment(
                        FragmentAddPage.newInstance(
                            FragmentAddPage.VIEW_TYPE_EDIT_STEP,
                            mObjectBean!!.idx,
                            mObjectBean!!.object_name
                        ), addToBack = true,isMainRefresh = false
                    )
                }
                getString(R.string.str_delete) -> {
                    val builder =
                        AlertDialog.Builder(context)
                            .setTitle(getString(R.string.str_delete_object_title))
                            .setMessage(getString(R.string.str_delete_object_contents))
                            .setPositiveButton(
                                getString(R.string.str_yes)
                            ) { dialog, _ ->
                                if (mObjectBean != null) {
                                    deleteObjectStep(mObjectBean!!.idx)
                                }
                                dialog.dismiss()
                            }
                            .setNegativeButton(
                                getString(R.string.str_no)
                            ) { dialog, _ -> dialog.dismiss() }
                    val dialog = builder.create()
                    dialog.show()
                }
            }
        }
        builder.create().show()
    }

    /**
     * Http
     * 삭제
     */
    private fun deleteObjectStep(object_idx: Int){
        DAClient.deleteObject(object_idx,object : DAHttpCallback{
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
                        (activity as ActivityMain).onBackPressed(true)
                    }
                }
            }
        })
    }

    /**
     * Http
     * complete 설정
     */
    private fun updateComplete(object_idx: Int, complete: Int) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        DAClient.updateObject(
            object_idx,
            null,
            null,
            complete,
            if (complete == 1) sdf.format(Date()) else null,
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
                            if(complete == 1) {
                                val dialog = DialogObjectSuccess(context!!)
                                dialog.show()
                            }

                            getObjects(object_idx)
                        }else{
                            Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            })
    }

    /**
     * 받아 온 데이터 초기화
     */
    private fun initData() {
        if (mBean != null) {
            getObjects(mBean!!.idx)
        }
    }

    /**
     * Http
     * 실천 목표 조회
     */
    private fun getObjects(object_idx: Int) {
        DAClient.getObjects(object_idx, object : DAHttpCallback {
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

                    if (code == DAClient.SUCCESS) {
                        if (mAdapter == null) return

                        mAdapter!!.clear()

                        val json = JSONObject(body)
                        val `object` = json.getJSONObject("object")
                        val bean = Gson().fromJson<BeanObjectStep>(
                            `object`.toString(),
                            BeanObjectStep::class.java
                        )

                        mObjectBean = bean
                        tv_object_step_title.text = bean.object_name
                        val postCount = json.getInt("total_action_post_count")
                        tv_total_count.text = "총 인증 ${postCount}개"

                        if (mObjectBean!!.complete == 0) {
                            ll_complete.visibility = GONE
                        } else {
                            ll_complete.visibility = VISIBLE
                        }

                        try {
                            val noneStepActionPost = json.getJSONArray("none_step_action_post")
                            for (i in 0 until noneStepActionPost.length()) {
                                val `object` = noneStepActionPost.getJSONObject(i)
                                val actionPost = Gson().fromJson<BeanActionPost>(
                                    `object`.toString(),
                                    BeanActionPost::class.java
                                )
                                mAdapter!!.add(actionPost)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                        try {
                            val objectSteps = json.getJSONArray("object_steps")
                            for (i in 0 until objectSteps.length()) {
                                val objectStep = objectSteps.getJSONObject(i)
                                val beanObjectHeader = Gson().fromJson<BeanActionPostHeader>(
                                    objectStep.toString(),
                                    BeanActionPostHeader::class.java
                                )
                                beanObjectHeader.position = (i + 1)
                                mAdapter!!.add(beanObjectHeader)
                                val actionPosts = objectStep.getJSONArray("action_posts")
                                for (j in 0 until actionPosts.length()) {
                                    val actionPost = actionPosts.getJSONObject(j)
                                    val beanActionPost = Gson().fromJson<BeanActionPost>(
                                        actionPost.toString(),
                                        BeanActionPost::class.java
                                    )
                                    mAdapter!!.add(beanActionPost)
                                }
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        if(mAdapter!!.size() > 0){
                            tv_default_step.visibility = GONE
                        }else{
                            tv_default_step.visibility = VISIBLE
                        }

                    }else{
                        Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvListener)
        rv_achivement_ing.adapter = mAdapter

        val glm = GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
        glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(i: Int): Int {
                return if (mAdapter!!.get(i) is BeanActionPostHeader
                    || mAdapter!!.get(i) is String
                )
                    3
                else
                    1
            }
        }
        rv_achivement_ing.layoutManager = glm
    }

    private val rvListener = object : IORecyclerViewListener {
        private val TYPE_HEADER = 1
        private val TYPE_ITEM = 0
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            if (viewType == TYPE_HEADER) return BaseViewHolder.newInstance(
                R.layout.listitem_object_step_sub_header,
                parent,
                false
            )
            return BaseViewHolder.newInstance(R.layout.listitem_object_step_image, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {

            if (getItemViewType(i) == TYPE_HEADER) {
                val bean = mAdapter!!.get(i) as BeanActionPostHeader

                val tvPosition = h.getItemView<TextView>(R.id.tv_position)
                val tvTitle = h.getItemView<TextView>(R.id.tv_title)
                val ivMore = h.getItemView<ImageView>(R.id.iv_more)

                tvPosition.text = bean.position.toString()
                tvTitle.text = bean.title
                if (mViewUserIdx == Comm_Prefs.getUserProfileIndex()) {
                    ivMore.visibility = VISIBLE
                } else ivMore.visibility = GONE

                ivMore.setOnClickListener{
                    showPopupMenu(ivMore,bean)
                }

            } else if (getItemViewType(i) == TYPE_ITEM) {
                val bean = mAdapter!!.get(i) as BeanActionPost

                val ivImage = h.getItemView<ImageView>(R.id.iv_image)
                Utils.setImageItemViewSquare(context!!,ivImage)
                Glide.with(context!!)
                    .load(bean.thumbnail_image)
                    .placeholder(R.drawable.ic_image_white)
                    .into(ivImage)

                h.itemView.setOnClickListener{
                    (activity as ActivityMain).replaceFragment(
                        FragmentActionPost.newInstance(bean.idx, mViewUserIdx),
                        addToBack = true,
                        isMainRefresh = false
                    )
                }
            }
        }

        /**
         * Show PopupMenu
         */
        private fun showPopupMenu(ivMore: View, bean: BeanActionPostHeader) {
            val popupMenu = PopupMenu(context!!, ivMore)
            popupMenu.menu.add(getString(R.string.str_edit))
            popupMenu.menu.add(getString(R.string.str_delete))

            popupMenu.setOnMenuItemClickListener {
                when (it.title) {
                    getString(R.string.str_edit) -> {
                            (activity as ActivityMain).replaceFragment(
                                FragmentAddPage.newInstance(
                                    FragmentAddPage.VIEW_TYPE_EDIT_STEP_DETAIL, bean.object_idx,bean.idx,bean.title
                                ), addToBack = true, isMainRefresh = false
                            )
                    }
                    getString(R.string.str_delete) -> {
                        val builder =
                            AlertDialog.Builder(context!!)
                                .setTitle(getString(R.string.str_delete_object_step_title))
                                .setMessage(getString(R.string.str_delete_object_step_contents))
                                .setPositiveButton(
                                    getString(R.string.str_yes)
                                ) { dialog, _ ->
                                    deleteObjectStepDetail(bean.idx,bean.object_idx)
                                    dialog.dismiss()
                                }
                                .setNegativeButton(
                                    getString(R.string.str_no)
                                ) { dialog, _ -> dialog.dismiss() }
                        val dialog = builder.create()
                        dialog.show()
                    }
                }
                false
            }
            popupMenu.show()
        }

        /**
         * Http
         * Object Step Detail 제거
         */
        private fun deleteObjectStepDetail(step_idx : Int, object_idx: Int){
            DAClient.deleteObjectStepDetail(step_idx, object_idx, object : DAHttpCallback {
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
                            getObjects(object_idx)
                        }
                    }
                }
            })
        }

        override fun getItemViewType(i: Int): Int {
            if (mAdapter!!.get(i) is BeanActionPostHeader
                || mAdapter!!.get(i) is String
            ) return TYPE_HEADER
            return TYPE_ITEM
        }
    }

    override fun onRefresh() {
        if(mBean != null) {
            getObjects(mBean!!.idx)
        }else srl_refresh.isRefreshing = false
    }
}