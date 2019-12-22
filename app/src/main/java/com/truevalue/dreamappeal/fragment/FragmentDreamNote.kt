package com.truevalue.dreamappeal.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.fragment.profile.FragmentDreamNoteIdea
import com.truevalue.dreamappeal.fragment.profile.FragmentDreamNoteLife
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.activity_dream_note.*

class FragmentDreamNote : BaseFragment(){

    private var mViewUserIdx = -1
    companion object{
        val EXTRA_VIEW_USER_IDX = "EXTRA_VIEW_USER_IDX"
        fun newInstance(view_user_idx : Int) : FragmentDreamNote {
            val fragment = FragmentDreamNote()
            fragment.mViewUserIdx = view_user_idx
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.activity_dream_note,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        onAction()
    }

    /**
     * View Init
     */
    private fun initView() {
        // Action Bar 설정
        iv_back_black.visibility = View.VISIBLE
        tv_title.text = getString(R.string.str_dream_note)
    }


    /**
     * Normal Action
     */
    private fun onAction(){
        // View Click Listener
        onClickView()
        // 일상 / 경험 설정
        setActionBar(true)
        replaceFragment(R.id.dream_note_container,
            FragmentDreamNoteLife.newInstance(mViewUserIdx),false)

    }

    /**
     * View Click Listener
     */
    private fun onClickView(){
        val listener = View.OnClickListener{
            when(it){
                ll_life->{
                    setActionBar(true)
                    replaceFragment(R.id.dream_note_container,
                        FragmentDreamNoteLife.newInstance(mViewUserIdx),false)
                }
                ll_idea->{
                    setActionBar(false)
                    replaceFragment(R.id.dream_note_container,
                        FragmentDreamNoteIdea.newInstance(mViewUserIdx),false)
                }
            }
        }
        ll_idea.setOnClickListener(listener)
        ll_life.setOnClickListener(listener)
    }

    /**
     * ActionBar 설정
     */
    private fun setActionBar(islife : Boolean){
        if(islife){
            iv_idea.isSelected = false
            tv_idea.isSelected = false
            iv_under_idea.visibility = INVISIBLE

            iv_life.isSelected = true
            tv_life.isSelected = true
            iv_under_life.visibility = VISIBLE
        }else{
            iv_idea.isSelected = true
            tv_idea.isSelected = true
            iv_under_idea.visibility = VISIBLE

            iv_life.isSelected = false
            tv_life.isSelected = false
            iv_under_life.visibility = INVISIBLE
        }
    }
}