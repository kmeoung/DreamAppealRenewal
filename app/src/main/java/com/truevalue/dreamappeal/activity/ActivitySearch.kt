package com.truevalue.dreamappeal.activity

import android.os.Bundle
import android.view.View
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import kotlinx.android.synthetic.main.activity_search.*

class ActivitySearch : BaseActivity() {

    private val TYPE_APPEALER = 0
    private val TYPE_BOARD = 1
    private val TYPE_TAG = 2

    private var mSearchType = TYPE_APPEALER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // view click listener
        onClickView()
    }

    /**
     * Init View
     */
    private fun initView(){

    }

    /**
     * View Click Listener
     */
    private fun onClickView(){
        val listener = View.OnClickListener{
            when(it){
                btn_cancel->finish()
                tv_appealer->{
                    setSearchType(TYPE_APPEALER)
                }
                tv_board->{
                    setSearchType(TYPE_BOARD)
                }
                tv_tag->{
                    setSearchType(TYPE_TAG)
                }
            }
        }
        btn_cancel.setOnClickListener(listener)
        tv_appealer.setOnClickListener(listener)
        tv_board.setOnClickListener(listener)
        tv_tag.setOnClickListener(listener)
    }

    /**
     * Set Search View Type
     */
    private fun setSearchType(search_type : Int){
        mSearchType = search_type

        when(mSearchType){
            TYPE_APPEALER->{

            }
            TYPE_BOARD->{

            }
            TYPE_TAG->{

            }
        }
    }




}