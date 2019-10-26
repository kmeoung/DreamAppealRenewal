package com.truevalue.dreamappeal.fragment.profile.dream_present

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_main.*
import kotlinx.android.synthetic.main.fragment_dream_title.*

class FragmentDreamTitle : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dream_title, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // View 초기화
        initView()
        // View 클릭 리스너
        onClickView()
    }

    /**
     * Init View
     */
    private fun initView() {
        // action Bar 설정
        iv_menu.visibility = GONE
        iv_back.visibility = VISIBLE
        iv_search.visibility = GONE
        tv_text_btn.text = getString(R.string.str_commit)
        tv_title.text = getString(R.string.str_title_dream_list)

        // 하이라이팅 설정
        val title_highlight = getString(R.string.str_dream_title_info_highlight)
        tv_dream_title_info.text =
            Utils.replaceTextColor(context, tv_dream_title_info, title_highlight)
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = OnClickListener {
            when (it) {
                iv_back -> activity?.onBackPressed()
                tv_text_btn -> {

                }
            }
        }
        iv_back.setOnClickListener(listener)
        tv_text_btn.setOnClickListener(listener)
    }
}