package com.truevalue.dreamappeal.fragment.profile.performance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.fragment_best_post.*

class FragmentBestPost : BaseFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_best_post,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        initView()
    }

    /**
     * View 초기화
     */
    private fun initView(){
        // 상단 이미지 정사각형 설정
        Utils.setImageViewSquare(context,rl_images)
    }
}