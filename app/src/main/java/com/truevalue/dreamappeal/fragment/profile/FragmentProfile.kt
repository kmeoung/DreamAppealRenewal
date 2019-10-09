package com.truevalue.dreamappeal.fragment.profile

import android.os.Bundle
import android.support.annotation.Nullable
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.fragment.profile.dream_present.FragmentDreamPresent
import kotlinx.android.synthetic.main.fragment_profile.*

class FragmentProfile : BaseFragment() {

    var mFragments: Array<Fragment>? = null
    var mTabs: Array<String>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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

    fun initAdapter() {
        var PagerAdapter = ViewPagerAdapter(childFragmentManager)
        var viewpager: ViewPager = vp_profile
        viewpager.adapter = PagerAdapter
        var tabs: TabLayout = tl_profile
        tabs.setupWithViewPager(viewpager)
    }

    fun initTabs() {
        mTabs = arrayOf(
            getString(R.string.str_dream_present),
            getString(R.string.str_performance),
            getString(R.string.str_blueprint)
        )
    }

    fun initFragments() {
        mFragments = arrayOf(
            FragmentDreamPresent(),
            FragmentDreamPresent(),
            FragmentDreamPresent()
        )
    }

    /**
     * ViewPager Adapter
     * 페이지 넘어갈 때 마다 서버 호출 필요
     */
    inner class ViewPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int = mFragments!!.size
        override fun getItem(position: Int): Fragment = mFragments!!.get(position)
        @Nullable
        override fun getPageTitle(position: Int): CharSequence = mTabs!![position]
    }
}