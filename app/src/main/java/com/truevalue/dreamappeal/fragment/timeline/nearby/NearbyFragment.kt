package com.truevalue.dreamappeal.fragment.timeline.nearby

import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base_new.fragment.BaseTabFragment
import com.truevalue.dreamappeal.base_new.viewmodel.EmptyViewModel
import com.truevalue.dreamappeal.utils.gone
import kotlinx.android.synthetic.main.fragment_nearby.*

class NearbyFragment : BaseTabFragment<EmptyViewModel>() {

    override val classViewModel: Class<EmptyViewModel> = EmptyViewModel::class.java
    override val layoutId: Int = R.layout.fragment_nearby

    companion object {
        fun newInstance(): NearbyFragment {
            return NearbyFragment()
        }
    }

    override fun onFirstRender() {
        srlRefresh.gone()
        tvSetupPlace.setOnClickListener {

        }
    }
}