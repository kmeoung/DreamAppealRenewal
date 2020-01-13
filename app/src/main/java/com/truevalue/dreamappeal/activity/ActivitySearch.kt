package com.truevalue.dreamappeal.activity

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.fragment.FragmentSearchAppealer
import com.truevalue.dreamappeal.fragment.FragmentSearchBoard
import com.truevalue.dreamappeal.fragment.FragmentSearchTag
import kotlinx.android.synthetic.main.activity_search.*

class ActivitySearch : BaseActivity() {

    private var mSearchType : Int
    var mSearchListener: IOSearchListener? = null
    private val handler: Handler
    companion object {
        private const val TYPE_APPEALER = 0
        private const val TYPE_BOARD = 1
        private const val TYPE_TAG = 2
        private const val SEARCH_DELAY = 1000L

        const val RESULT_REPLACE_BOARD_IDX = "RESULT_REPLACE_BOARD_IDX"
        const val RESULT_REPLACE_BOARD_TYPE = "RESULT_REPLACE_BOARD_TYPE"
        const val REQUEST_SEARCH = 3000

        const val RESULT_CODE_BOARD = 1005
        const val REQUEST_ADDR = 1015
    }

    init {
        mSearchType = TYPE_APPEALER
        mSearchListener = null
        handler = Handler(Handler.Callback {
            mSearchListener?.let {
                it.onSearch(et_search.text.toString())
            }
            false
        })
    }

    interface IOSearchListener {
        fun onSearch(keyword: String)
    }

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

                iv_cancel.visibility = if (et_search.text.toString().isNullOrEmpty()) GONE else VISIBLE

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
     * SearchActivity Replace Fragment
     */
    fun replaceFragment(fragment : Fragment,addToBack : Boolean){
        replaceFragment(R.id.search_container, fragment, addToBack)
    }

    /**
     * SearchActivity Replace Fragment (set Tag Keyword
     */
    fun replaceFragment(fragment : Fragment,addToBack : Boolean,tag_keyword : String){
        replaceFragment(fragment, addToBack)
        searchSetText(tag_keyword,false)
    }

    private fun searchSetText(keyword : String, send_handler : Boolean){
        et_search.setText(keyword)
        if(!send_handler) handler.removeMessages(0)
    }

    /**
     * Set Search View Type
     */
    private fun setSearchType(search_type: Int) {
        searchSetText("",false)
        when (search_type) {
            TYPE_APPEALER -> {
                tv_appealer.isSelected = true
                tv_board.isSelected = false
                tv_tag.isSelected = false
                replaceFragment(R.id.search_container, FragmentSearchAppealer(), false)
            }
            TYPE_BOARD -> {
                tv_appealer.isSelected = false
                tv_board.isSelected = true
                tv_tag.isSelected = false
                replaceFragment(R.id.search_container, FragmentSearchBoard(), false)
            }
            TYPE_TAG -> {
                tv_appealer.isSelected = false
                tv_board.isSelected = false
                tv_tag.isSelected = true
                replaceFragment(R.id.search_container, FragmentSearchTag(), false)
            }
        }
        mSearchType = search_type
    }


}