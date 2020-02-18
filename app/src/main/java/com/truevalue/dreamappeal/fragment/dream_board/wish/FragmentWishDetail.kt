package com.truevalue.dreamappeal.fragment.dream_board.wish

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityImgScaling
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BasePagerAdapter
import com.truevalue.dreamappeal.bean.BeanWishImages
import com.truevalue.dreamappeal.bean.BeanWishPost
import com.truevalue.dreamappeal.bean.BeanWriter
import com.truevalue.dreamappeal.bean.Image
import com.truevalue.dreamappeal.fragment.dream_board.FragmentAddBoard
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_wish_board_detail.*
import kotlinx.android.synthetic.main.fragment_wish_board_detail.rl_images
import kotlinx.android.synthetic.main.fragment_wish_board_detail.tv_contents
import kotlinx.android.synthetic.main.fragment_wish_board_detail.tv_indicator
import okhttp3.Call
import org.json.JSONObject

class FragmentWishDetail : BaseFragment() {

    private var mWishIdx: Int
    private var mAdapterImage: BasePagerAdapter?
    private var mBean: BeanWishPost?

    init {
        mWishIdx = -1
        mBean = null
        mAdapterImage = null
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
        // Pager Adapter 초기화
        initAdapter()
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
     * PagerAdapter 초기화
     */
    private fun initAdapter() {
        mAdapterImage = BasePagerAdapter(context, object : BasePagerAdapter.IOBasePagerListener {
            override fun onBindViewPager(any: Any, view: ImageView, position: Int, arrayList: ArrayList<Any>) {
                val bean = any as BeanWishImages
                context?.let {
                    Glide.with(it)
                        .load(bean.image_url)
                        .centerCrop()
                        .placeholder(R.drawable.ic_image_gray)
                        .into(view)

                    view.setOnClickListener {
                        val array = ArrayList<String>()
                        for (item in arrayList) {
                            val img = item as BeanWishImages
                            array.add(img.image_url)
                        }
                        val intent = Intent(context, ActivityImgScaling::class.java)
                        intent.putExtra(ActivityImgScaling.EXTRA_IMAGES,array)
                        intent.putExtra(ActivityImgScaling.EXTRA_IMAGE_POSITION,position)
                        startActivity(intent)
                    }
                }

            }
        })

        pager_image.run {
            adapter = mAdapterImage
            addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    mAdapterImage?.let {
                        if(it.count > 1) {
                            tv_indicator.text =
                                if (it.count > 0) ((position + 1).toString() + " / " + it.count) else "0 / 0"
                        }
                    }
                }
            })
        }
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

                        tv_cheering.text = count.toString()
                        iv_cheering.isSelected = status
                    } else {
                        context?.let {
                            Toast.makeText(it.applicationContext, message, Toast.LENGTH_SHORT)
                                .show()
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

                    val wishImages = json.getJSONArray("wish_images")

                    tv_indicator.text = "0 / 0"
                    rl_images.visibility = VISIBLE
                    if(wishImages.length() < 1) rl_images.visibility = GONE
                    else if(wishImages.length() < 2){
                        ll_indicator.visibility = GONE
                    }else {
                        ll_indicator.visibility = VISIBLE
                        tv_indicator.text = "1 / " + wishImages.length()
                    }

                    mAdapterImage?.let {
                        adapter->
                        adapter.clear()
                        for (i in 0 until wishImages.length()) {
                            val wishImage = wishImages.getJSONObject(i)
                            val beanImage = Gson().fromJson<BeanWishImages>(
                                wishImage.toString(),
                                BeanWishImages::class.java
                            )
                            adapter.add(beanImage)
                        }
                        adapter.notifyDataSetChanged()
                    }

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

                    tv_achievement_cnt.text = "성과 $achievement_post_count / 3"
                    tv_action_cnt.text = "실천 ${action_post_count}개"

                    tv_cheering.text = like_count.toString()
                    iv_cheering.isSelected = statusOfLike

                    tv_time.text = Utils.convertFromDate(beanWish.register_date)

                    if(beanWish.profile_idx == Comm_Prefs.getUserProfileIndex()){
                        iv_action_more.visibility = VISIBLE
                    }else{
                        iv_action_more.visibility = GONE
                    }
                } else {
                    context?.let {
                        Toast.makeText(it.applicationContext, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}