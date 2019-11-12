package com.truevalue.dreamappeal.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityDreamPoint
import com.truevalue.dreamappeal.base.BaseFragment
import kotlinx.android.synthetic.main.action_bar_other.*

class FragmentDreamPointCoupon : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dream_point_usage, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // OnClickListener
        onClickView()
    }

    /**
     * View Click Listener
     */
    private fun onClickView(){
        val listener = View.OnClickListener{
            when(it){
                (activity as ActivityDreamPoint).iv_back_blue-> activity!!.onBackPressed()
            }
        }

        (activity as ActivityDreamPoint).iv_back_blue.setOnClickListener(listener)
    }
}