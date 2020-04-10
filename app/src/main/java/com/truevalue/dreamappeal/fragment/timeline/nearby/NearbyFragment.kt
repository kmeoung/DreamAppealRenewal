package com.truevalue.dreamappeal.fragment.timeline.nearby

import android.content.Intent
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base_new.fragment.BaseTabFragment
import com.truevalue.dreamappeal.base_new.viewmodel.EmptyViewModel
import com.truevalue.dreamappeal.fragment.timeline.adapter.TimeLineData
import com.truevalue.dreamappeal.fragment.timeline.adapter.TimeLineDataAdapter
import com.truevalue.dreamappeal.fragment.timeline.nearby.search_address.AddressAdapter
import com.truevalue.dreamappeal.fragment.timeline.nearby.search_address.SearchAddressActivity
import com.truevalue.dreamappeal.utils.Constants
import com.truevalue.dreamappeal.utils.gone
import com.truevalue.dreamappeal.utils.visible
import kotlinx.android.synthetic.main.fragment_nearby.*

class NearbyFragment : BaseTabFragment<EmptyViewModel>() {

    override val classViewModel: Class<EmptyViewModel> = EmptyViewModel::class.java
    override val layoutId: Int = R.layout.fragment_nearby

    companion object {
        fun newInstance(): NearbyFragment {
            return NearbyFragment()
        }
    }

    private var mAdapter: TimeLineDataAdapter? = null

    override fun onFirstRender() {
        groupListData.gone()
        tvSetupPlace.setOnClickListener {
            startActivityForResult(
                Intent(context, SearchAddressActivity::class.java),
                Constants.ADDRESS_SEARCH_CODE
            )
        }

        tvChosenDistrict.setOnClickListener { }
        tvSearchAddress.setOnClickListener { tvSetupPlace.performClick() }

        rvPosts.apply {
            mAdapter = TimeLineDataAdapter(layoutInflater)
            adapter = mAdapter
            setHasFixedSize(true)
        }
        mAdapter?.setDataSource(
            listOf(
                TimeLineData(),
                TimeLineData(1),
                TimeLineData(2),
                TimeLineData(3),
                TimeLineData(1),
                TimeLineData()
            )
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Constants.ADDRESS_SEARCH_CODE -> {
                val address = data?.getStringExtra(Constants.ADDRESS_SEARCH_DATA)
                tvChosenCity.text = address
                tvChosenDistrict.text = address
                groupListData.visible()
                llEmpty.gone()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }

    }
}