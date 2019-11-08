package com.truevalue.dreamappeal.fragment.profile

import android.os.Bundle
import android.view.Gravity
import androidx.annotation.Nullable
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentStatePagerAdapter
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.dialog.DialogAnotherProfile
import com.truevalue.dreamappeal.fragment.profile.blueprint.FragmentBlueprint
import com.truevalue.dreamappeal.fragment.profile.dream_present.FragmentDreamPresent
import com.truevalue.dreamappeal.fragment.profile.performance.FragmentPerformance
import kotlinx.android.synthetic.main.action_bar_main.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*

class FragmentProfile : BaseFragment() {

    private var mFragments: Array<BaseFragment>?
    private var mTabs: Array<String>?
    private var pagerAdapter: ViewPagerAdapter?

    init {
        mFragments = null
        mTabs = null
        pagerAdapter = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (pagerAdapter == null) {
            // Tabs 초기화
            initTabs()
            // Fragments 초기화
            initFragments()
        }
        // View Adapter 초기화
        initAdapter()
        // View 클릭 리스너
        onClickView()
    }

    private fun initAdapter() {
        if(pagerAdapter == null) pagerAdapter = ViewPagerAdapter(childFragmentManager)
        var viewpager: ViewPager = vp_profile
        viewpager.adapter = pagerAdapter
        var tabs: TabLayout = tl_profile
        tabs.setupWithViewPager(viewpager)
        viewpager.offscreenPageLimit = 2
    }

    private fun initTabs() {
        mTabs = arrayOf(
            getString(R.string.str_dream_present),
            getString(R.string.str_performance),
            getString(R.string.str_blueprint)
        )
    }

    private fun initFragments() {
        mFragments = arrayOf(
            FragmentDreamPresent(),
            FragmentPerformance(),
            FragmentBlueprint()
        )
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_menu -> (activity as ActivityMain).dl_drawer.openDrawer(Gravity.RIGHT)
                tv_title -> {
                    val dialog = DialogAnotherProfile(context!!)
                    dialog.show()
                }
            }
        }
        iv_menu.setOnClickListener(listener)
        tv_title.setOnClickListener(listener)
    }

    /**
     * ViewPager Adapter
     * 페이지 넘어갈 때 마다 서버 호출 필요
     */
    private inner class ViewPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int = mFragments!!.size
        override fun getItem(position: Int): Fragment = mFragments!![position]
        @Nullable
        override fun getPageTitle(position: Int): CharSequence = mTabs!![position]
    }


}

