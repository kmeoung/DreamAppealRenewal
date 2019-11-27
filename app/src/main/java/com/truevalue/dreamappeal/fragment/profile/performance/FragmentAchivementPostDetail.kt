package com.truevalue.dreamappeal.fragment.profile.performance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.bottom_post_view.*
import kotlinx.android.synthetic.main.fragment_best_post.*
import okhttp3.Call

class FragmentAchivementPostDetail : BaseFragment() {

    private var mPostIndx = -1

    companion object {
        fun newInstance(idx: Int): FragmentAchivementPostDetail {
            val fragment = FragmentAchivementPostDetail()
            fragment.mPostIndx = idx
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_post_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        initView()
        // View Click Listener
        onViewClicked()
        // 데이터 바인딩
        achivementPostDetail()
    }

    /**
     * View 초기화
     */
    private fun initView() {
        // 상단 이미지 정사각형 설정
        Utils.setImageViewSquare(context, rl_images)
    }

    /**
     * View Clicked
     */
    private fun onViewClicked() {
        val listener = View.OnClickListener {
            when (it) {
                iv_back_black -> activity!!.onBackPressed()
                ll_cheering -> {

                }
                ll_comment, ll_comment_detail -> {

                }
                ll_share -> {

                }
            }
        }
        iv_back_black.setOnClickListener(listener)
        ll_cheering.setOnClickListener(listener)
        ll_comment.setOnClickListener(listener)
        ll_comment_detail.setOnClickListener(listener)
        ll_share.setOnClickListener(listener)
    }

    /**
     * 상세 페이지 데이터 조회
     */
    private fun achivementPostDetail() {
        DAClient.achievementPostDetail(mPostIndx, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (context != null) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                    if (code == DAClient.SUCCESS) {

                    }
                }
            }
        })
    }
}