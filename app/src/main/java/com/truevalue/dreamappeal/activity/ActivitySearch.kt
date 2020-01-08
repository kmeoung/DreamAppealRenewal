package com.truevalue.dreamappeal.activity

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.fragment.FragmentSearchAppealer
import com.truevalue.dreamappeal.fragment.FragmentSearchBoard
import com.truevalue.dreamappeal.fragment.FragmentSearchTag
import kotlinx.android.synthetic.main.activity_search.*

class ActivitySearch : BaseActivity() {

    private val TYPE_APPEALER = 0
    private val TYPE_BOARD = 1
    private val TYPE_TAG = 2

    private var mSearchType = TYPE_APPEALER

    private val SEARCH_DELAY = 1000L

    companion object {
        var RESULT_REPLACE_USER_IDX = "RESULT_REPLACE_USER_IDX"
        var REQUEST_REPLACE_USER_IDX = 3000
    }

    interface IOSearchListener {
        fun onSearch(keyword: String)
    }

    var mSearchListener: IOSearchListener? = null
    val handler: Handler = Handler(Handler.Callback {
        mSearchListener!!.onSearch(et_search.text.toString())
        false
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // View Init
        initView()
        // view click listener
        onClickView()
    }

    /**
     * Init View
     */
    private fun initView() {
        iv_cancel.visibility = GONE
        setSearchType(TYPE_APPEALER)

        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                handler.removeMessages(0)

                if (et_search.text.toString().isNullOrEmpty()) {
                    iv_cancel.visibility = GONE
                } else iv_cancel.visibility = VISIBLE

                handler.sendEmptyMessageDelayed(0, SEARCH_DELAY)
            }
        })
    }

    override fun onDestroy() {
        handler.removeMessages(0)
        super.onDestroy()
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                btn_cancel -> finish()
                tv_appealer -> {
                    if (mSearchType != TYPE_APPEALER)
                        setSearchType(TYPE_APPEALER)
                }
                tv_board -> {
                    if (mSearchType != TYPE_BOARD)
                        setSearchType(TYPE_BOARD)
                }
                tv_tag -> {
                    if (mSearchType != TYPE_TAG)
                        setSearchType(TYPE_TAG)
                }
                iv_cancel -> {
                    et_search.setText("")
                }
            }
        }
        btn_cancel.setOnClickListener(listener)
        tv_appealer.setOnClickListener(listener)
        tv_board.setOnClickListener(listener)
        tv_tag.setOnClickListener(listener)
        iv_cancel.setOnClickListener(listener)
    }

    /**
     * Set Search View Type
     */
    private fun setSearchType(search_type: Int) {

        when (search_type) {
            TYPE_APPEALER -> {
                tv_appealer.isSelected = true
                tv_board.isSelected = false
                tv_tag.isSelected = false
                replaceFragment(R.id.search_container, FragmentSearchAppealer(), false)
            }
            TYPE_BOARD -> {
//                Toast.makeText(applicationContext,getString(R.string.str_not_ready_yet),Toast.LENGTH_SHORT).show()
                tv_appealer.isSelected = false
                tv_board.isSelected = true
                tv_tag.isSelected = false
                replaceFragment(R.id.search_container, FragmentSearchBoard(), false)
            }
            TYPE_TAG -> {
//                Toast.makeText(applicationContext,getString(R.string.str_not_ready_yet),Toast.LENGTH_SHORT).show()
                tv_appealer.isSelected = false
                tv_board.isSelected = false
                tv_tag.isSelected = true
                replaceFragment(R.id.search_container, FragmentSearchTag(), false)
            }
        }
        mSearchType = search_type
    }


}