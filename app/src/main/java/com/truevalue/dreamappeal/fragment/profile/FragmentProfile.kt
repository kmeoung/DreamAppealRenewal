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
import android.widget.Toast
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.bean.BeanAnotherProfile
import com.truevalue.dreamappeal.bean.BeanAnotherProfileGroup
import com.truevalue.dreamappeal.dialog.DialogAnotherProfile
import com.truevalue.dreamappeal.fragment.profile.blueprint.FragmentBlueprint
import com.truevalue.dreamappeal.fragment.profile.dream_present.FragmentDreamPresent
import com.truevalue.dreamappeal.fragment.profile.performance.FragmentNewPerformance
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import kotlinx.android.synthetic.main.action_bar_main.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import okhttp3.Call
import org.json.JSONObject

class FragmentProfile : BaseFragment(), ActivityMain.IOMainViewRefresh {

    private var mFragments: Array<BaseFragment>?
    private var mTabs: Array<String>?
    private var pagerAdapter: ViewPagerAdapter?

    private var mViewUserIdx : Int = -1

    companion object{
        fun newInstance(view_user_idx : Int) : FragmentProfile{
            val fragment = FragmentProfile()
            fragment.mViewUserIdx = view_user_idx
            return fragment
        }
    }
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
        // Refresh Listener 연결
        (activity as ActivityMain).mViewRefreshListener = this
        // View Adapter 초기화
        initAdapter()
        // View 클릭 리스너
        onClickView()
    }

    /**
     * Adapter 초기화
     */
    private fun initAdapter() {
        if(pagerAdapter == null) pagerAdapter = ViewPagerAdapter(childFragmentManager)
        var viewpager: ViewPager = vp_profile
        viewpager.adapter = pagerAdapter
        var tabs: TabLayout = tl_profile
        tabs.setupWithViewPager(viewpager)
        viewpager.offscreenPageLimit = 2
    }

    /**
     * Tab 초기화
     */
    private fun initTabs() {
        mTabs = arrayOf(
            getString(R.string.str_dream_present),
            getString(R.string.str_performance),
            getString(R.string.str_blueprint)
        )
    }

    /**
     * Fragment 초기화
     */
    private fun initFragments() {
        mFragments = arrayOf(
            FragmentDreamPresent.newInstance(mViewUserIdx),
            FragmentNewPerformance.newInstance(mViewUserIdx),
            FragmentBlueprint.newInstance(mViewUserIdx)
        )
    }

    /**
     * Dialog 띄우기
     */
    private fun showDialog(){
        val profile_idx = mViewUserIdx

        DAClient.getAnotherUserData(profile_idx,object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if(context != null) {
                    if(code == DAClient.SUCCESS){
                        val json = JSONObject(body)
                        val user = json.getJSONObject("user")
                        val groups = json.getJSONArray("groups")

                        val bean = Gson().fromJson<BeanAnotherProfile>(user.toString(),BeanAnotherProfile::class.java)

                        val groupList = ArrayList<BeanAnotherProfileGroup>()
                        if(groups.length() > 0){
                            for(i in 0 until groups.length()){
                                val group = groups.getJSONObject(i)
                                val groupBean = Gson().fromJson<BeanAnotherProfileGroup>(group.toString(),BeanAnotherProfileGroup::class.java)
                                groupList.add(groupBean)
                            }
                        }
                        if(groupList.size > 0){
                            bean.group = groupList
                        }

                        val dialog = DialogAnotherProfile(context!!, bean)
                        dialog.show()
                    }else{
                        Toast.makeText(context!!.applicationContext,message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })


    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_menu -> (activity as ActivityMain).dl_drawer.openDrawer(Gravity.RIGHT)
                tv_title -> {
                    if(mViewUserIdx != Comm_Prefs.getUserProfileIndex()) {
                        // todo : 추후 상대방 프로필 클릭
//                        showDialog()
                    }
                }
                iv_dream_note->{
                    (activity as ActivityMain).replaceFragment(FragmentDreamNote.newInstance(mViewUserIdx),addToBack = true,isMainRefresh = false)
                }
            }
        }
        iv_menu.setOnClickListener(listener)
        tv_title.setOnClickListener(listener)
        iv_dream_note.setOnClickListener(listener)
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

    /**
     * Refresh Listener
     */
    override fun OnRefreshView() {
        if(mFragments != null){
            mFragments!![vp_profile.currentItem].OnServerRefresh()
        }
    }

    /**
     * Refresh All Listener
     */
    override fun OnRefreshAllView() {
        if(mFragments != null) {
            if (mFragments != null) {
                for (mFragment in mFragments!!) {
                    mFragment.OnServerRefresh()
                }
            }
        }
    }
}

