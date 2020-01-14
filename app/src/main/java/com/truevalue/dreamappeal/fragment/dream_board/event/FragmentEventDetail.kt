package com.truevalue.dreamappeal.fragment.dream_board.event

import android.os.Bundle
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
import com.truevalue.dreamappeal.bean.BeanPromotion
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_event_detail.*
import okhttp3.Call
import org.json.JSONObject

class FragmentEventDetail : BaseFragment() {

    private var mPromotionIdx: Int = -1

    companion object {
        fun newInstance(promotion_idx: Int): FragmentEventDetail {
            val fragment =
                FragmentEventDetail()
            fragment.mPromotionIdx = promotion_idx
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_event_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        initView()
        // View Click Listener
        onClickView()
        // 프로모션 가져오기
        getPromotions()

    }

    /**
     * View 초기화
     */
    private fun initView() {
        tv_title.text = getString(R.string.str_event)
        iv_back_black.visibility = GONE
        iv_back_blue.visibility = VISIBLE
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_back_blue -> {
                    (activity as ActivityMain).onBackPressed(false)
                }
            }
        }
        iv_back_blue.setOnClickListener(listener)
    }

    /**
     * Http
     * 기본 Event 전체 조회
     */
    private fun getPromotions() {
        DAClient.getBoardEventDetail(mPromotionIdx, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (code == DAClient.SUCCESS) {
                    val json = JSONObject(body)
                    val promotions = json.getJSONArray("promotion")

                    if (promotions.length() < 0) tv_default.visibility = VISIBLE
                    else {
                        val promotion = promotions.getJSONObject(0)
                        val bean = Gson().fromJson<BeanPromotion>(
                            promotion.toString(),
                            BeanPromotion::class.java
                        )
                        context?.let {
                            if (bean.url.isNullOrEmpty()) tv_default.visibility = VISIBLE
                            else {
                                tv_default.visibility = GONE
                                Glide.with(it)
                                    .load(bean.url)
                                    .placeholder(R.drawable.ic_image_white)
                                    .centerCrop()
                                    .thumbnail(0.1f)
                                    .into(iv_event)
                            }
                        }
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