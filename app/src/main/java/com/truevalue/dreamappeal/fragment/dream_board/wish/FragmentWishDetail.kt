package com.truevalue.dreamappeal.fragment.dream_board.wish

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityAddPost
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.bean.BeanWish
import com.truevalue.dreamappeal.bean.BeanWishPost
import com.truevalue.dreamappeal.bean.BeanWriter
import com.truevalue.dreamappeal.fragment.dream_board.FragmentAddBoard
import com.truevalue.dreamappeal.fragment.profile.blueprint.FragmentActionPost
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_wish_board_detail.*
import kotlinx.android.synthetic.main.fragment_wish_board_detail.tv_contents
import kotlinx.android.synthetic.main.fragment_wish_board_detail.tv_value_style
import okhttp3.Call
import org.json.JSONObject

class FragmentWishDetail : BaseFragment() {

    private var mWishIdx: Int
    private var mBean: BeanWishPost?

    init {
        mWishIdx = -1
        mBean = null
    }

    companion object {
        fun newInstance(wish_idx: Int): FragmentWishDetail {
            val fragment = FragmentWishDetail()
            fragment.mWishIdx = wish_idx
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_wish_board_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init View
        initView()
        // View Click Listener
        onClickView()
        // 소원 게시글 가져오기
        getWishDetail()
    }

    /**
     * View 초기화
     */
    private fun initView() {
        iv_back_black.visibility = GONE
        iv_back_blue.visibility = VISIBLE
        tv_title.text = getString(R.string.str_wish_detail_title)
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                ll_cheering -> {
                    wishLike()
                }
                iv_action_more -> {
                    showMoreDialog()
                }
                iv_back_blue -> {
                    (activity as ActivityMain).onBackPressed(false)
                }
            }
        }
        ll_cheering.setOnClickListener(listener)
        iv_action_more.setOnClickListener(listener)
        iv_back_blue.setOnClickListener(listener)
    }

    /**
     * 더보기 Dialog 띄우기
     */
    private fun showMoreDialog() {
        val list = arrayOf(
            getString(R.string.str_edit),
            getString(R.string.str_delete)
        )
        val builder =
            AlertDialog.Builder(context)
        builder.setItems(list) { _, i ->
            when (list[i]) {
                getString(R.string.str_edit) -> {
                    mBean?.let {
                        (activity as ActivityMain).replaceFragment(
                            FragmentAddBoard.newInstance(
                                FragmentAddBoard.TYPE_EDIT_WISH,
                                it
                            ), addToBack = true, isMainRefresh = false
                        )
                    }

                }

                getString(R.string.str_delete) -> {
                    val builder =
                        AlertDialog.Builder(context)
                            .setTitle(getString(R.string.str_delete_post_title))
                            .setMessage(getString(R.string.str_delete_post_contents))
                            .setPositiveButton(
                                getString(R.string.str_yes)
                            ) { dialog, _ ->
                                deleteWish()
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
     * 소원 게시물 삭제
     */
    private fun deleteWish() {
        DAClient.deleteWish(mWishIdx,
            object : DAHttpCallback {
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    context?.let {
                        Toast.makeText(it.applicationContext, message, Toast.LENGTH_SHORT).show()
                    }

                    if (code == DAClient.SUCCESS) {
                        (activity as ActivityMain).onBackPressed(false)
                    }
                }
            })
    }

    /**
     * Http
     * 소원 게시물 좋아요
     */
    private fun wishLike() {
        DAClient.likeWish(mWishIdx,
            object : DAHttpCallback {
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    if (code == DAClient.SUCCESS) {
                        val json = JSONObject(body)
                        val status = json.getBoolean("status")
                        val count = json.getInt("count")

                        tv_cheering.text = "${count}개"
                        iv_cheering.isSelected = status
                    } else {
                        context?.let {
                            Toast.makeText(it.applicationContext, message, Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            })
    }

    /**
     * Http
     * 소원 게시글 가져오기
     */
    private fun getWishDetail() {
        DAClient.getWish(mWishIdx, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (code == DAClient.SUCCESS) {
                    val json = JSONObject(body)
                    val wish_post = json.getJSONObject("wish_post")
                    val writer = json.getJSONObject("writer")
                    val achievement_post_count = json.getInt("achievement_post_count")
                    val action_post_count = json.getInt("action_post_count")
                    val like_count = json.getInt("like_count")
                    val statusOfLike = json.getBoolean("statusOfLike")

                    val beanWish = Gson().fromJson<BeanWishPost>(
                        wish_post.toString()
                        , BeanWishPost::class.java
                    )
                    val beanWriter = Gson().fromJson<BeanWriter>(
                        writer.toString(),
                        BeanWriter::class.java
                    )
                    mBean = beanWish
                    tv_wish_title.text = beanWish.title
                    tv_contents.text = beanWish.content

                    tv_value_style.text = beanWriter.value_style
                    tv_job_name.text = "${beanWriter.job} ${beanWriter.nickname}"

                    tv_achievement_cnt.text = achievement_post_count.toString()
                    tv_action_cnt.text = action_post_count.toString()

                    tv_cheering.text = "${like_count}개"
                    iv_cheering.isSelected = statusOfLike

                    tv_time.text = Utils.convertFromDate(beanWish.register_date)
                } else {
                    context?.let {
                        Toast.makeText(it.applicationContext, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}