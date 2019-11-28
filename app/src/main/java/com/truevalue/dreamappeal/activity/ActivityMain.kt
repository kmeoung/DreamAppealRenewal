package com.truevalue.dreamappeal.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.amazonaws.mobile.client.AWSMobileClient
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.base.IOActionBarListener
import com.truevalue.dreamappeal.fragment.profile.FragmentProfile
import com.truevalue.dreamappeal.fragment.profile.blueprint.FragmentBlueprint
import com.truevalue.dreamappeal.fragment.profile.dream_present.FragmentDreamPresent
import com.truevalue.dreamappeal.fragment.profile.performance.FragmentPerformance
import com.truevalue.dreamappeal.utils.Comm_Prefs
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_main_view.*
import kotlinx.android.synthetic.main.nav_view.*

class ActivityMain : BaseActivity() {
    var mActionListener: IOActionBarListener? = null
    // todo : 현재 나의 프로필을 보고 있고, 내가 다른 프로필을 선택하여
    // todo : 변겅이 되었는지를 확인하여 MainProfile을 변경
    private var mCurrentUserIdx : Int = -1

    companion object {
        var isMainRefresh = false

        val MAIN_TYPE_HOME = "MAIN_TYPE_HOME"
        val MAIN_TYPE_TIMELINE = "MAIN_TYPE_TIMELINE"
        val MAIN_TYPE_ADD_BOARD = "MAIN_TYPE_ADD_BOARD"
        val MAIN_TYPE_NOTIFICATION = "MAIN_TYPE_NOTIFICATION"
        val MAIN_TYPE_PROFILE = "MAIN_TYPE_PROFILE"

        val ACTION_BAR_TYPE_PROFILE_MAIN = "ACTION_BAR_TYPE_PROFILE_MAIN"
        val ACTION_BAR_TYPE_PROFILE_OTHER = "ACTION_BAR_TYPE_PROFILE_OTHER"
    }

    var mMainViewType = MAIN_TYPE_HOME
    private var mActionBarType = ACTION_BAR_TYPE_PROFILE_MAIN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Action
        onAction()

        mCurrentUserIdx = Comm_Prefs.getUserProfileIndex()

        // todo : AWS Mobile Init
        AWSMobileClient.getInstance().initialize(this) {
            Log.d("AWS_LOG", "AWS INITIALIZED")
        }.execute()
    }

    /**
     * ActivityMain Action
     */
    private fun onAction() {
        // Fragment 초기화
        initFragment()
        // Bottom View Click Listener
        onClickBottomView()
        // bottom 이미지 초기화
        initBottomView()
        // Drawer View Click Listener
        onClickDrawerView()
//
//        val ivMenu: ImageView = findViewById(R.id.iv_menu)
//        ivMenu.setOnClickListener(View.OnClickListener {
//            var intent = Intent(this, ActivityLoginContainer::class.java)
//            startActivity(intent)
//            finish()
//        })
    }

    /**
     * Drawer 설정
     */
    private fun setDrawer() {
        dl_drawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {

            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerClosed(drawerView: View) {

            }

            override fun onDrawerOpened(drawerView: View) {

            }
        })
    }

    /**
     * Drawer 열기 여부 관리
     */
    private fun isOpenDrawer(): Boolean {
        val fragment = supportFragmentManager.findFragmentById(R.id.base_container)
        return (fragment is FragmentProfile)
                || (fragment is FragmentBlueprint)
                || (fragment is FragmentPerformance)
                || (fragment is FragmentDreamPresent)
    }

    /**
     * Fragment에서 접근하는 Fragment 변경
     */
    fun replaceFragment(fragment: Fragment, addToBack: Boolean) {
        replaceFragment(R.id.base_container, fragment, addToBack)
    }

    /**
     * Fragment에서 접근하는 Fragment 변경
     * IsMainRefresh = 메인 프로필 조회할 것인지
     */
    fun replaceFragment(fragment: Fragment, addToBack: Boolean, isMainRefresh : Boolean) {
        replaceFragment(R.id.base_container, fragment, addToBack)
        ActivityMain.isMainRefresh = isMainRefresh
    }

    /**
     * Bottom View Click Listener
     */
    private fun onClickBottomView() {
        val onClickListener = View.OnClickListener {
            mMainViewType = when (it) {
                iv_home ->
                    MAIN_TYPE_HOME
                iv_timeline ->
                    MAIN_TYPE_TIMELINE
                iv_add_board ->
                    MAIN_TYPE_ADD_BOARD
                iv_notification ->
                    MAIN_TYPE_NOTIFICATION
                iv_profile ->
                    MAIN_TYPE_PROFILE
                else -> {
                    Toast.makeText(this, getString(R.string.str_error), Toast.LENGTH_SHORT).show()
                    MAIN_TYPE_HOME
                }
            }
            initFragment()
            initBottomView()
        }

        iv_home.setOnClickListener(onClickListener)
        iv_timeline.setOnClickListener(onClickListener)
        iv_add_board.setOnClickListener(onClickListener)
        iv_notification.setOnClickListener(onClickListener)
        iv_profile.setOnClickListener(onClickListener)
    }

    /**
     * Drawer View Click Listener
     */
    private fun onClickDrawerView() {
        val listener = View.OnClickListener {
            when (it) {
                ll_logout -> {
                    Comm_Prefs.setUserProfileIndex(-1)
                    Comm_Prefs.setToken(null)

                    val intent = Intent(this@ActivityMain, ActivityLoginContainer::class.java)
                    startActivity(intent)
                    finish()
                }
                ll_profile -> {
                    val intent = Intent(this@ActivityMain, ActivityMyProfileContainer::class.java)
                    startActivity(intent)
                    dl_drawer.closeDrawer(Gravity.RIGHT)
                }
                ll_following -> {
                    val intent = Intent(this@ActivityMain, ActivityFollow::class.java)
                    intent.putExtra(
                        ActivityFollow.EXTRA_VIEW_TYPE,
                        ActivityFollow.VIEW_TYPE_FOLLOWING
                    )
                    startActivity(intent)
                    dl_drawer.closeDrawer(Gravity.RIGHT)
                }
                ll_dream_point -> {
                    val intent = Intent(this@ActivityMain, ActivityDreamPoint::class.java)
                    startActivity(intent)
                    dl_drawer.closeDrawer(Gravity.RIGHT)
                }
            }
        }
        ll_logout.setOnClickListener(listener)
        ll_profile.setOnClickListener(listener)
        ll_following.setOnClickListener(listener)
        ll_dream_point.setOnClickListener(listener)
    }

    /**
     * 하단 View 클릭 이미지 설정
     * TODO : 이동이 따로 설정이 필요합니다
     */
    private fun initBottomView() {
        when (mMainViewType) {
            MAIN_TYPE_HOME -> {
                iv_home.isSelected = true
                iv_timeline.isSelected = false
                iv_notification.isSelected = false
                iv_profile.isSelected = false
                FragmentProfile()
                dl_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            MAIN_TYPE_TIMELINE -> {
                iv_home.isSelected = false
                iv_timeline.isSelected = true
                iv_notification.isSelected = false
                iv_profile.isSelected = false
                FragmentProfile()
                dl_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            MAIN_TYPE_ADD_BOARD -> {
                FragmentProfile()
            }
            MAIN_TYPE_NOTIFICATION -> {
                iv_home.isSelected = false
                iv_timeline.isSelected = false
                iv_notification.isSelected = true
                iv_profile.isSelected = false
                FragmentProfile()
                dl_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            MAIN_TYPE_PROFILE -> {
                iv_home.isSelected = false
                iv_timeline.isSelected = false
                iv_notification.isSelected = false
                iv_profile.isSelected = true
                FragmentProfile()
                dl_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
            else -> FragmentProfile()
        }
    }

    /**
     * Init Fragment
     */
    fun initFragment() {
        // todo : 처음 페이지 설정 시 변경 필요
        var fragment = when (mMainViewType) {
            MAIN_TYPE_HOME -> FragmentProfile()
            MAIN_TYPE_TIMELINE -> FragmentProfile()
            MAIN_TYPE_ADD_BOARD -> FragmentProfile()
            MAIN_TYPE_NOTIFICATION -> FragmentProfile()
            MAIN_TYPE_PROFILE -> FragmentProfile()
            else -> FragmentProfile()
        }
        replaceFragment(R.id.base_container, fragment, false)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if(isMainRefresh || mCurrentUserIdx != Comm_Prefs.getUserProfileIndex()){
            isMainRefresh = false
            if(mCurrentUserIdx != Comm_Prefs.getUserProfileIndex()){
                mCurrentUserIdx = Comm_Prefs.getUserProfileIndex()
                if(mViewRefreshListener != null) mViewRefreshListener!!.OnRefreshAllView()
            }else{
                if(mViewRefreshListener != null) mViewRefreshListener!!.OnRefreshView()
            }
        }
    }

    fun onBackPressed(isMainRefresh: Boolean){
        ActivityMain.isMainRefresh = isMainRefresh
        onBackPressed()
    }
    /**
     * Init Fragment Stack
     */
    fun initPage() {
        val fm = supportFragmentManager
        for (i in 0..fm.backStackEntryCount) {
            fm.popBackStack()
        }
    }

    var mViewRefreshListener : IOMainViewRefresh? = null

    /**
     * Main 페이지에서 View가 Refresh되지 않는 현상을 수정
     */
    interface IOMainViewRefresh {
        fun OnRefreshView()
        fun OnRefreshAllView(){}
    }
}
