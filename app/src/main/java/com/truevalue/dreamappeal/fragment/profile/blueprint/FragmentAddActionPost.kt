package com.truevalue.dreamappeal.fragment.profile.blueprint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import kotlinx.android.synthetic.main.fragment_add_action_post.*

class FragmentAddActionPost : BaseFragment() {

    private var mAdapter : BaseRecyclerViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_action_post, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // init rv Adapter
        initAdapter()
    }

    /**
     * RecyclerView Adpater 초기화
     */
    private fun initAdapter(){
        mAdapter = BaseRecyclerViewAdapter(rvListener)
        rv_image.adapter = mAdapter
        rv_image.layoutManager = LinearLayoutManager(context)
    }

    /**
     * RecyclerView Listener
     */
    val rvListener = object : IORecyclerViewListener{
        override val itemCount: Int
            get() = if(mAdapter != null)mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {

        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }


}