package com.truevalue.dreamappeal.fragment.profile.blueprint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanBlueprintObject
import com.truevalue.dreamappeal.bean.BeanObjectStepHeader
import kotlinx.android.synthetic.main.fragment_object_step.*

class FragmentObjectStep : BaseFragment() {
    private var mBean : BeanBlueprintObject? = null
    private var mAdapter: BaseRecyclerViewAdapter? = null

    companion object{
        fun newInstance(bean : BeanBlueprintObject) : FragmentObjectStep{
            val fragment = FragmentObjectStep()
            fragment.mBean = bean
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_object_step, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 데이터 초기화
        initData()
        // recyclerview adapter 초기화
        initAdapter()
    }

    private fun initData(){
        if(mBean != null){

        }
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvListener)
        rv_achivement_ing.adapter = mAdapter

        val glm = GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
        glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(i: Int): Int {
                return if (mAdapter!!.get(i) is BeanObjectStepHeader
                    || mAdapter!!.get(i) is String
                )
                    3
                else
                    1
            }
        }
        rv_achivement_ing.layoutManager = glm
    }

    private val rvListener = object : IORecyclerViewListener {
        private val TYPE_HEADER = 1
        private val TYPE_ITEM = 0
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            if (viewType == TYPE_HEADER) return BaseViewHolder.newInstance(
                R.layout.listitem_object_step_sub_header,
                parent,
                false
            )
            return BaseViewHolder.newInstance(R.layout.listitem_object_step_image, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {

        }

        override fun getItemViewType(i: Int): Int {
            if (mAdapter!!.get(i) is BeanObjectStepHeader
                || mAdapter!!.get(i) is String) return TYPE_HEADER
            return TYPE_ITEM
        }
    }
}