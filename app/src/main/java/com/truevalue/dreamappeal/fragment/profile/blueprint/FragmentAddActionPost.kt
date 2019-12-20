package com.truevalue.dreamappeal.fragment.profile.blueprint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityAddActionPost
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_add_action_post.*
import java.io.File

class FragmentAddActionPost : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter? = null
    private var mImages: ArrayList<File>? = null

    companion object {
        fun newInstance(images: ArrayList<File>): FragmentAddActionPost {
            val fragment = FragmentAddActionPost()
            fragment.mImages = images
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
    private fun initView(){
        tv_text_btn.visibility = VISIBLE
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
        }
    }

    /**
     * View Click Listener
     */
    private fun onClickView(){
        val listener = View.OnClickListener{
            when(it){
                tv_text_btn->(activity as ActivityAddActionPost).replaceFragment(FragmentLevelChoice(),true)
            }
        }
        tv_text_btn.setOnClickListener(listener)
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
                val file = mAdapter!!.get(i)

                Glide.with(context!!)
                    .load(file)
                    .placeholder(R.drawable.ic_image_black)
                    .into(ivImage)

            }
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }


}