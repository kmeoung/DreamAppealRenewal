package com.truevalue.dreamappeal.fragment.dream_board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.fragment.dream_board.concern.FragmentConcern
import com.truevalue.dreamappeal.fragment.dream_board.event.FragmentEvent
import kotlinx.android.synthetic.main.fragment_dream_board.*

class FragmentDreamBoard : BaseFragment() {

    companion object{
        private const val TAB_TYPE_EVENT = 0
        private const val TAB_TYPE_POPULAR = 1
        private const val TAB_TYPE_CONCERN = 2
    }

    private var mTabViewType = TAB_TYPE_POPULAR

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dream_board, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        initView()
        // View Click Listener
        onClickView()
        // Tab View 설정
        setTabView(mTabViewType)
    }

    /**
     * View 초기화
     */
    private fun initView() {

    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                tv_event -> {
                    setTabView(TAB_TYPE_EVENT)
                }
                tv_popular -> {
                    setTabView(TAB_TYPE_POPULAR)
                }
                tv_concern -> {
                    setTabView(TAB_TYPE_CONCERN)
                }
            }
        }
        tv_event.setOnClickListener(listener)
        tv_popular.setOnClickListener(listener)
        tv_concern.setOnClickListener(listener)
    }

    /**
     * 상단 Tab View 설정
     */
    private fun setTabView(tab_type : Int){
        mTabViewType = tab_type
        when(tab_type){
            TAB_TYPE_EVENT->{
                tv_event.isSelected = true
                tv_popular.isSelected = false
                tv_concern.isSelected = false
                replaceFragment(R.id.board_container,
                    FragmentEvent(),false)
            }
            TAB_TYPE_POPULAR->{
                tv_event.isSelected = false
                tv_popular.isSelected = true
                tv_concern.isSelected = false
                replaceFragment(R.id.board_container,FragmentPopular(),false)
            }
            TAB_TYPE_CONCERN->{
                tv_event.isSelected = false
                tv_popular.isSelected = false
                tv_concern.isSelected = true
                replaceFragment(R.id.board_container,
                    FragmentConcern(),false)
            }
        }
    }
}