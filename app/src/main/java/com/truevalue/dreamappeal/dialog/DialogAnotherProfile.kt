package com.truevalue.dreamappeal.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter2
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanAnotherProfile
import com.truevalue.dreamappeal.bean.BeanAnotherProfileGroup
import com.truevalue.dreamappeal.bean.BeanAnotherProfileInfo
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.dialog_another_profile.*
import java.text.SimpleDateFormat

class DialogAnotherProfile(context: Context, var bean: BeanAnotherProfile?) : Dialog(context) {

    private val LISTITEM_TYPE_INFO = 0
    private val LISTITEM_TYPE_GROUP = 1

    private var mAdapter: BaseRecyclerViewAdapter2<Any>? = null

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
        // initData
        initData()
    }


    /**
     * View CLick Listenre
     */
    private fun onClickView() {
        iv_close.setOnClickListener {
            dismiss()
        }
    }

    /**
     * RecyclerView Adapter 설정
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter2(listener)
        rv_another_profile.adapter = mAdapter
        rv_another_profile.layoutManager = LinearLayoutManager(context)
    }

    /**
     * RecyclerView Data 설정
     */
    private fun initData() {
        if (bean != null) {
            mAdapter!!.clear()
            mAdapter!!.add(bean!!)

            if (bean!!.group != null && bean!!.group!!.size > 0) {
                for (i in 0 until bean!!.group!!.size) {
                    mAdapter!!.add(bean!!.group!![i])
                }
            }
        }
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
            if (mAdapter != null) {
                if (getItemViewType(i) == LISTITEM_TYPE_INFO) {
                    val bean = mAdapter!!.get(i) as BeanAnotherProfile
                    val tvName = h.getItemView<TextView>(R.id.tv_name)
                    val tvNickName = h.getItemView<TextView>(R.id.tv_nickname)
                    val tvAge = h.getItemView<TextView>(R.id.tv_age)
                    val tvGender = h.getItemView<TextView>(R.id.tv_gender)
                    val tvAddress = h.getItemView<TextView>(R.id.tv_address)
                    val tvEmail = h.getItemView<TextView>(R.id.tv_email)

                    var privateBean = bean.private!!



                    tvName.text =
                        if (bean.name.isNullOrEmpty()) context!!.getString(R.string.str_none) else if(privateBean.name == 0) bean.name else
                            context.getString(R.string.str_private)
                    tvNickName.text =
                        if (bean.nickname.isNullOrEmpty()) context!!.getString(R.string.str_none) else
                            if(privateBean.nickname == 0) bean.nickname else context.getString(R.string.str_private)
                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                    tvAge.text = if(bean.birth.isNullOrEmpty()) context!!.getString(R.string.str_none)
                    else if(privateBean.birth == 0) Utils.dateToAge(sdf.parse(bean.birth)).toString() else
                        context.getString(R.string.str_private)
                    tvGender.text = if (bean.gender.isNullOrEmpty())
                        context!!.getString(R.string.str_none)
                    else if(privateBean.gender == 0) if(bean.gender == "0") context.getString(R.string.str_female) else context.getString(
                        R.string.str_male
                    )
                    else context.getString(R.string.str_private)

                    tvAddress.text =
                        if (bean.address.isNullOrEmpty()) context!!.getString(R.string.str_none) else if(privateBean.address == 0) bean.address else context.getString(R.string.str_private)
                    tvEmail.text =
                        if (bean.email.isNullOrEmpty()) context!!.getString(R.string.str_none) else if(privateBean.email == 0) bean.email else context.getString(R.string.str_private)


                } else {
                    val bean = mAdapter!!.get(i) as BeanAnotherProfileGroup
                    val tvGroup = h.getItemView<TextView>(R.id.tv_group)
                    val tvRank = h.getItemView<TextView>(R.id.tv_rank)

                    tvGroup.text = if(bean.groupName.isNullOrEmpty()) context.getString(R.string.str_none) else bean.groupName
                    tvRank.text = if(bean.position.isNullOrEmpty()) context.getString(R.string.str_none) else bean.position

                }
            }
        }

        override fun getItemViewType(i: Int): Int {
            if (mAdapter != null) {
                when (mAdapter?.get(i)) {
                    is BeanAnotherProfile -> return LISTITEM_TYPE_INFO
                    else -> return LISTITEM_TYPE_GROUP
                }
            }
            return LISTITEM_TYPE_INFO
        }
    }
}