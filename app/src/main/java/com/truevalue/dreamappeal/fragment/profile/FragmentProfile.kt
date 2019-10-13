package com.truevalue.dreamappeal.fragment.profile

import android.os.Bundle
import androidx.annotation.Nullable
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.fragment.profile.blueprint.FragmentBlueprint
import com.truevalue.dreamappeal.fragment.profile.dream_present.FragmentDreamPresent
import com.truevalue.dreamappeal.fragment.profile.performance.FragmentPerformance
import kotlinx.android.synthetic.main.fragment_profile.*

class FragmentProfile : BaseFragment() {
    private var mFragments: Array<BaseFragment>? = null
    private var mTabs: Array<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tabs 초기화
        initTabs()
        // Fragments 초기화화
        initFragments()
        // View Adapter 초기화
        initAdapter()
    }

    private fun initAdapter() {
        var pagerAdapter = ViewPagerAdapter(childFragmentManager)
        var viewpager: ViewPager = vp_profile
        viewpager.adapter = pagerAdapter
        var tabs: TabLayout = tl_profile
        tabs.setupWithViewPager(viewpager)
    }

    private fun initTabs() {
        mTabs = arrayOf(
            getString(R.string.str_dream_present),
            getString(R.string.str_performance),
            getString(R.string.str_blueprint)
        )
    }

    private fun initFragments() {
        // todo : 조금 더 좋은 방법이 있는지 확인 필요
        mFragments = arrayOf(
            FragmentDreamPresent(),
            FragmentPerformance(),
            FragmentBlueprint()
        )
    }

    /**
     * ViewPager Adapter
     * 페이지 넘어갈 때 마다 서버 호출 필요
     */
    private inner class ViewPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int = mFragments!!.size
        override fun getItem(position: Int): Fragment = mFragments!!.get(position)
        @Nullable
        override fun getPageTitle(position: Int): CharSequence = mTabs!![position]
    }


}

