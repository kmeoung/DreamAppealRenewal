package com.truevalue.dreamappeal.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanAnotherProfile
import com.truevalue.dreamappeal.bean.BeanAnotherProfileGroup
import com.truevalue.dreamappeal.bean.BeanAnotherProfileInfo
import kotlinx.android.synthetic.main.dialog_another_profile.*

class DialogAnotherProfile(context: Context,var bean : BeanAnotherProfile?) : Dialog(context) {

    private val LISTITEM_TYPE_INFO = 0
    private val LISTITEM_TYPE_GROUP = 1

    private var mAdapter: BaseRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Dialog 뒷 배경 및 여러가지 설정
        var layoutParams = WindowManager.LayoutParams()
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.8f
        window!!.attributes = layoutParams
        window!!.setBackgroundDrawableResource(R.color.transparent)

        setContentView(R.layout.dialog_another_profile)

        // Click View Listener
        onClickView()
        // RecyclerView Adapter 초기 설정
        initAdapter()
        // Data 생성
        initData()
    }

    /**
     * Data 생성
     */
    private fun initData() {
        if (mAdapter != null) {
            mAdapter!!.add(BeanAnotherProfileInfo("info"))
            for (i in 1..10) {
                mAdapter!!.add(BeanAnotherProfileGroup("group"))
            }
        }
    }

    /**
     * View CLick Listenre
     */
    private fun onClickView(){
        iv_close.setOnClickListener {
            dismiss()
        }
    }

    /**
     * RecyclerView Adapter 설정
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(listener)
        rv_another_profile.adapter = mAdapter
        rv_another_profile.layoutManager = LinearLayoutManager(context)
    }

    private val listener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            if (viewType == LISTITEM_TYPE_INFO) return BaseViewHolder.newInstance(
                R.layout.listitem_another_profile,
                parent,
                false
            )
            return BaseViewHolder.newInstance(
                R.layout.listitem_another_profile_group,
                parent,
                false
            )
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {

        }

        override fun getItemViewType(i: Int): Int {
            if (mAdapter != null) {
                when (mAdapter?.get(i)) {
                    is BeanAnotherProfileInfo -> return LISTITEM_TYPE_INFO
                    else -> return LISTITEM_TYPE_GROUP
                }
            }
            return LISTITEM_TYPE_INFO
        }
    }
}