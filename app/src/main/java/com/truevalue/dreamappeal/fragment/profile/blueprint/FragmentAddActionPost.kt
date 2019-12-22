package com.truevalue.dreamappeal.fragment.profile.blueprint

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityAddPost
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_add_action_post.*
import okhttp3.Call
import java.io.File

class FragmentAddActionPost : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter? = null
    private var mImages: ArrayList<File>? = null
    private var mUrls : ArrayList<String>? = null
    private var postIdx = -1
    private var mContents : String? = null
    companion object {
        fun newInstance(images: ArrayList<File>): FragmentAddActionPost {
            val fragment = FragmentAddActionPost()
            fragment.mImages = images
            return fragment
        }

        fun newInstance(url: ArrayList<String>, post_idx : Int,contents : String): FragmentAddActionPost {
            val fragment = FragmentAddActionPost()
            fragment.mUrls = url
            fragment.postIdx = post_idx
            fragment.mContents = contents
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_action_post, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // init View
        initView()
        // init rv Adapter
        initAdapter()
        // init Data
        initData()
        // Click Listener
        onClickView()
    }

    /**
     * View 초기화
     */
    private fun initView() {

        et_comment.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                tv_text_btn.isSelected = !et_comment.text.toString().isNullOrEmpty()
                iv_check.isSelected = !et_comment.text.toString().isNullOrEmpty()
            }
        })

        if(postIdx > -1){
            tv_text_btn.visibility = GONE
            tv_title.text = getString(R.string.str_edit_post)
            iv_check.visibility = VISIBLE

            et_comment.setText(mContents)
            et_comment.setSelection(et_comment.length())
        }else{
            tv_text_btn.visibility = VISIBLE
            tv_title.text = getString(R.string.str_new_post)
            iv_check.visibility = GONE
        }

    }

    /**
     * RecyclerView Adpater 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvListener)
        rv_image.adapter = mAdapter
        rv_image.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    /**
     * RecyclerView Init Data
     */
    private fun initData() {
        if (mImages != null && mImages!!.size > 0) {
            for (i in 0 until mImages!!.size) {
                mAdapter!!.add(mImages!![i])
            }
        }else if(mUrls != null && mUrls!!.size > 0){
            for (i in 0 until mUrls!!.size) {
                mAdapter!!.add(mUrls!![i])
            }
        }
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_back_black -> activity!!.onBackPressed()
                tv_text_btn -> if (tv_text_btn.isSelected)
                    (activity as ActivityAddPost)
                        .replaceFragment(
                            FragmentLevelChoice.newInstance(
                                mImages,
                                et_comment.text.toString()
                            ), true
                        )
                iv_check->{
                    if(iv_check.isSelected){
                        updateActionPost()
                    }
                }
            }
        }
        tv_text_btn.setOnClickListener(listener)
        iv_back_black.setOnClickListener(listener)
        iv_check.setOnClickListener(listener)
    }

    /**
     * Post 수정
     */
    private fun updateActionPost(){
        DAClient.updateActionPosts(postIdx,null,null,null,et_comment.text.toString(),object : DAHttpCallback{
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if(context != null){
                    Toast.makeText(context!!.applicationContext,message,Toast.LENGTH_SHORT).show()

                    if(code == DAClient.SUCCESS){
                        activity!!.onBackPressed()
                    }
                }
            }
        })
    }

    /**
     * RecyclerView Listener
     */
    private val rvListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_add_action_post_img, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            if (mAdapter != null) {
                val ivImage = h.getItemView<ImageView>(R.id.iv_image)
                val image = mAdapter!!.get(i)

                Glide.with(context!!)
                    .load(image)
                    .placeholder(R.drawable.ic_image_black)
                    .centerCrop()
                    .into(ivImage)

            }
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }


}