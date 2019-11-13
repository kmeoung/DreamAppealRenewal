package com.truevalue.dreamappeal.activity

import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.fragment.profile.FragmentDreamNoteIdea
import com.truevalue.dreamappeal.fragment.profile.FragmentDreamNoteLife
import kotlinx.android.synthetic.main.activity_dream_note.*

class ActivityDreamNote : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dream_note)

        // Action
        onAction()
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
            FragmentDreamNoteLife(),false)

    }

    /**
     * Fragment에서 접근하는 Fragment 변경
     */
    fun replaceFragment(fragment: Fragment, addToBack: Boolean) {
        replaceFragment(R.id.dream_note_container, fragment, addToBack)
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
                        FragmentDreamNoteLife(),false)
                }
                ll_idea->{
                    setActionBar(false)
                    replaceFragment(R.id.dream_note_container,
                        FragmentDreamNoteIdea(),false)
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