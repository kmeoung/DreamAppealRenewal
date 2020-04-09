package com.truevalue.dreamappeal.fragment.timeline

import android.util.Log
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base_new.fragment.BaseFragment
import com.truevalue.dreamappeal.base_new.viewmodel.EmptyViewModel
import com.truevalue.dreamappeal.fragment.timeline.appeal.FragmentTimeline
import com.truevalue.dreamappeal.fragment.timeline.nearby.NearbyFragment
import com.truevalue.dreamappeal.fragment.timeline.subject_interest.SubjectInterestFragment
import com.truevalue.dreamappeal.utils.visible
import com.truevalue.dreamappeal.widgets.MainPagerAdapter
import kotlinx.android.synthetic.main.fragment_timeline_container.*
import kotlinx.android.synthetic.main.white_toolbar.*


class TimeLineFragment : BaseFragment<EmptyViewModel>() {
    override val classViewModel: Class<EmptyViewModel>
        get() = EmptyViewModel::class.java
    override val layoutId: Int = R.layout.fragment_timeline_container

    companion object {
        fun newInstance(): TimeLineFragment {
            val fragment = TimeLineFragment()
            return fragment
        }
    }

    override fun init() {
        tvTitle.text = getString(R.string.timeline)
        ivRight.visible()
        ivRight.setImageResource(R.drawable.ic_blue_message)
        ivRight.setOnClickListener {

        }



        setUpViewPager()
    }
    private val TAG = "TimeLineFragment";
    private fun setUpViewPager() {
        val fragments =
            arrayListOf(
                FragmentTimeline.newInstance(),
                SubjectInterestFragment.newInstance(),
                NearbyFragment.newInstance()
            )
        val mainPagerAdapter = MainPagerAdapter(childFragmentManager, fragments)
        vpMain.adapter = mainPagerAdapter
        vpMain.setPagingEnabled(false)
        vpMain.offscreenPageLimit = fragments.size - 1
        tlMain.setupWithViewPager(vpMain)
        tlMain.getTabAt(0)?.text = getString(R.string.appeal)
        tlMain.getTabAt(1)?.text = getString(R.string.subject_of_interest)
        tlMain.getTabAt(2)?.text = getString(R.string.hometown)
//        for (i in 0 until tlMain.tabCount) {
//            val tabItem = tlMain.getTabAt(i)
//            tabItem?.text = getString(R.string.appeal)
//            tabItem?.text = getString(R.string.subject_of_interest)
//            tabItem?.text = getString(R.string.hometown)
//        }
    }
}