package com.truevalue.dreamappeal.fragment.dream_board

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityCameraGallery
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanWish
import com.truevalue.dreamappeal.bean.BeanWishPost
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_board.*
import okhttp3.Call
import java.io.File

class FragmentAddBoard : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter? = null
    private val REQUEST_ADD_IMAGES = 1003

    private var mViewType : String?
    private var mBean : Any?
    init {
        mViewType = TYPE_ADD_WISH
        mBean = null
    }

    companion object{
        const val TYPE_ADD_WISH = "TYPE_ADD_WISH"
        const val TYPE_ADD_CONCERN = "TYPE_ADD_CONCERN"

        const val TYPE_EDIT_WISH = "TYPE_ADD_WISH"
        const val TYPE_EDIT_CONCERN = "TYPE_ADD_CONCERN"

        fun newInstance(view_type : String, bean : Any? = null) : FragmentAddBoard{
            val fragment = FragmentAddBoard()
            fragment.mViewType = view_type
            fragment.mBean = bean
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_board, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // View 초기화
        initView()
        // bind init Data
        bindInitData()
        // Adapter 초기화
        initAdapter()
        // View Click 초기화
        onClickView()
    }

    override fun onResume() {
        super.onResume()
        (activity as ActivityMain).bottom_view.visibility = GONE
    }

    override fun onPause() {
        super.onPause()
        (activity as ActivityMain).bottom_view.visibility = VISIBLE
    }

    /**
     * View 초기화
     */
    private fun initView() {
        when(mViewType){
            TYPE_ADD_WISH, TYPE_EDIT_WISH->{
                tv_title.text = getString(R.string.str_wish_add_title)
            }
            TYPE_ADD_CONCERN, TYPE_EDIT_CONCERN->{
                tv_title.text = getString(R.string.str_concern_add_title)
            }
        }
        iv_check.visibility = VISIBLE

        val watcher = object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                iv_check.isSelected = check()
            }
        }

        et_title.addTextChangedListener(watcher)
        et_contents.addTextChangedListener(watcher)
    }

    /**
     * Check 버튼 설정
     */
    private fun check() : Boolean{
        return (!et_title.text.toString().isNullOrEmpty()) &&
                (!et_contents.text.toString().isNullOrEmpty())
    }

    /**
     * 초기 데이터 셋팅
     */
    private fun bindInitData(){
        mBean?.let {
            when(mViewType){
                TYPE_EDIT_CONCERN->{

                }
                TYPE_EDIT_WISH->{
                    val bean = mBean as BeanWishPost
                    et_title.setText(bean.title)
                    et_contents.setText(bean.content)
                }
            }
        }
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvImageListener)
        rv_board_img.adapter = mAdapter
        rv_board_img.layoutManager =
            LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
    }

    /**
     * View Click 초기화
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_add_photo -> {
                    val intent = Intent(context!!, ActivityCameraGallery::class.java)
                    intent.putExtra(
                        ActivityCameraGallery.SELECT_TYPE,
                        ActivityCameraGallery.EXTRA_IMAGE_MULTI_SELECT
                    )
                    intent.putExtra(
                        ActivityCameraGallery.VIEW_TYPE,
                        ActivityCameraGallery.EXTRA_BOARD
                    )
                    startActivityForResult(intent, REQUEST_ADD_IMAGES)
                }
                iv_check->{
                    if(iv_check.isSelected){
                        when(mViewType){
                            TYPE_ADD_WISH->{
                                DAClient.addWish(et_title.text.toString(),
                                    et_contents.text.toString(),
                                    checkCallbackListener)
                            }
                            TYPE_ADD_CONCERN->{

                            }
                            TYPE_EDIT_WISH->{
                                val bean = mBean as BeanWishPost
                                DAClient.updateWish(bean.idx,et_title.text.toString(),
                                    et_contents.text.toString(),
                                    checkCallbackListener)
                            }
                            TYPE_EDIT_CONCERN->{

                            }
                        }
                    }
                }
                iv_back_black->{
                    (activity as ActivityMain).onBackPressed(false)
                }
            }
        }
        iv_add_photo.setOnClickListener(listener)
        iv_check.setOnClickListener(listener)
        iv_back_black.setOnClickListener(listener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_ADD_IMAGES) {
                val fileArray =
                    data!!.getSerializableExtra(ActivityCameraGallery.REQUEST_IMAGE_FILES) as ArrayList<File>

                if (fileArray != null) {
                    if (fileArray.size > 0) {
                        if (mAdapter != null) {
                            for (file in fileArray) {
                                mAdapter!!.add(file)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Check Callback Listener
     */
    private val checkCallbackListener = object : DAHttpCallback{
        override fun onResponse(
            call: Call,
            serverCode: Int,
            body: String,
            code: String,
            message: String
        ) {
            if(code == DAClient.SUCCESS){
                (activity as ActivityMain).onBackPressed(false)
            }else{
                context?.let {
                    Toast.makeText(it.applicationContext,message,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * RecyclerView 이미지 Listener
     */
    private val rvImageListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_achivement_list, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {

        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}