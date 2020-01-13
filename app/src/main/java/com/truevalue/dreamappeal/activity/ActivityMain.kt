package com.truevalue.dreamappeal.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.amazonaws.mobile.client.AWSMobileClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.kakao.util.helper.Utility
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.base.IOActionBarListener
import com.truevalue.dreamappeal.fragment.dream_board.FragmentDreamBoard
import com.truevalue.dreamappeal.fragment.notification.FragmentNotification
import com.truevalue.dreamappeal.fragment.profile.FragmentProfile
import com.truevalue.dreamappeal.fragment.profile.blueprint.FragmentBlueprint
import com.truevalue.dreamappeal.fragment.profile.dream_present.FragmentDreamPresent
import com.truevalue.dreamappeal.fragment.profile.performance.FragmentPerformance
import com.truevalue.dreamappeal.fragment.timeline.FragmentTimeline
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.service.ServiceFirebaseMsg
import com.truevalue.dreamappeal.utils.Comm_Param
import com.truevalue.dreamappeal.utils.Comm_Prefs
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_main_view.*
import kotlinx.android.synthetic.main.nav_view.*
import okhttp3.Call
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class ActivityMain : BaseActivity() {
    var mActionListener: IOActionBarListener? = null
    // 현재 나의 프로필을 보고 있고, 내가 다른 프로필을 선택하여
    // 변겅이 되었는지를 확인하여 MainProfile을 변경
    private var mCurrentUserIdx: Int = -1

    companion object {
        var isMainRefresh = false

        const val MAIN_TYPE_HOME = "MAIN_TYPE_HOME"
        const val MAIN_TYPE_TIMELINE = "MAIN_TYPE_TIMELINE"
        const val MAIN_TYPE_ADD_BOARD = "MAIN_TYPE_ADD_BOARD"
        const val MAIN_TYPE_NOTIFICATION = "MAIN_TYPE_NOTIFICATION"
        const val MAIN_TYPE_PROFILE = "MAIN_TYPE_PROFILE"

        const val ACTION_BAR_TYPE_PROFILE_MAIN = "ACTION_BAR_TYPE_PROFILE_MAIN"
        const val ACTION_BAR_TYPE_PROFILE_OTHER = "ACTION_BAR_TYPE_PROFILE_OTHER"
    }

    data class BeanDrawerData(var following: Int, var dream_point: Int)

    var mDrawerData: BeanDrawerData? = null

    var mMainViewType = ""

    init {
        // 회원가입시 PROFILE로 이동
        mMainViewType =
            if (Comm_Prefs.getUserProfileIndex() > -1) MAIN_TYPE_HOME else MAIN_TYPE_PROFILE
    }

    private var mActionBarType = ACTION_BAR_TYPE_PROFILE_MAIN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        intent.getStringExtra(ServiceFirebaseMsg.TYPE)?.let{
            mMainViewType = MAIN_TYPE_NOTIFICATION
        }

        // Action
        onAction()

        mCurrentUserIdx = Comm_Prefs.getUserProfileIndex()

        AWSMobileClient.getInstance().initialize(this) {
            Log.d("AWS_LOG", "AWS INITIALIZED")
        }.execute()
    }


    fun initAllView() {
        val fm = supportFragmentManager
        for (i in 0..fm.backStackEntryCount) {
            fm.popBackStack()
        }
        // Action
        onAction()

        mCurrentUserIdx = Comm_Prefs.getUserProfileIndex()

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
        // Drawer
        setDrawer()

    }

    /**
     * Drawer 설정
     */
    private fun setDrawer() {
        dl_drawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
                tv_following.text = if (mDrawerData != null) "${mDrawerData!!.following}" else "0"
                tv_dream_point.text = if (mDrawerData != null) String.format(
                    "%,d",
                    mDrawerData!!.dream_point
                ) else "0"
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerClosed(drawerView: View) {
                tv_following.text = if (mDrawerData != null) "${mDrawerData!!.following}" else "0"
                tv_dream_point.text = if (mDrawerData != null) String.format(
                    "%,d",
                    mDrawerData!!.dream_point
                ) else "0"
            }

            override fun onDrawerOpened(drawerView: View) {
                tv_following.text = if (mDrawerData != null) "${mDrawerData!!.following}" else "0"
                tv_dream_point.text = if (mDrawerData != null) String.format(
                    "%,d",
                    mDrawerData!!.dream_point
                ) else "0"
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
    fun replaceFragment(fragment: Fragment, addToBack: Boolean, isMainRefresh: Boolean) {
        replaceFragment(R.id.base_container, fragment, addToBack)
        ActivityMain.isMainRefresh = isMainRefresh
    }

    /**
     * Bottom View Click Listener
     */
    private fun onClickBottomView() {
        val onClickListener = View.OnClickListener {
            when (it) {
                iv_home -> {
                    mMainViewType = MAIN_TYPE_HOME
                }
                iv_timeline ->
                    mMainViewType = MAIN_TYPE_TIMELINE
                iv_add_board ->
                    mMainViewType = MAIN_TYPE_ADD_BOARD
                iv_notification -> {
                    mMainViewType = MAIN_TYPE_NOTIFICATION
                }
                iv_profile ->
                    mMainViewType = MAIN_TYPE_PROFILE
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
                    val intent = Intent(this@ActivityMain, ActivityFollowCheering::class.java)
                    intent.putExtra(
                        ActivityFollowCheering.EXTRA_VIEW_TYPE,
                        ActivityFollowCheering.VIEW_TYPE_FOLLOWING
                    )
                    startActivityForResult(intent, ActivityFollowCheering.REQUEST_REPLACE_USER_IDX)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_CODE) {
            if (requestCode == ActivityFollowCheering.REQUEST_REPLACE_USER_IDX) {
                val view_user_idx = data!!.getIntExtra(RESULT_REPLACE_USER_IDX, -1)
                replaceFragment(FragmentProfile.newInstance(view_user_idx), true)
            }
        }
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
                dl_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            MAIN_TYPE_TIMELINE -> {
                iv_home.isSelected = false
                iv_timeline.isSelected = true
                iv_notification.isSelected = false
                iv_profile.isSelected = false
                dl_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            MAIN_TYPE_ADD_BOARD -> {
            }
            MAIN_TYPE_NOTIFICATION -> {
                iv_home.isSelected = false
                iv_timeline.isSelected = false
                iv_notification.isSelected = true
                iv_profile.isSelected = false
                dl_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            MAIN_TYPE_PROFILE -> {
                iv_home.isSelected = false
                iv_timeline.isSelected = false
                iv_notification.isSelected = false
                iv_profile.isSelected = true
                dl_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
        }
    }

    /**
     * Init Fragment
     */
    private fun initFragment() {
        // todo : 처음 페이지 설정 시 변경 필요
        when (mMainViewType) {
            MAIN_TYPE_HOME -> replaceFragment(FragmentDreamBoard(), false)
            MAIN_TYPE_TIMELINE -> replaceFragment(FragmentTimeline(), false)
            MAIN_TYPE_ADD_BOARD -> {
                val intent = Intent(this@ActivityMain, ActivityCameraGallery::class.java)
                intent.putExtra(
                    ActivityCameraGallery.VIEW_TYPE,
                    ActivityCameraGallery.EXTRA_ACTION_POST
                )
                intent.putExtra(
                    ActivityCameraGallery.SELECT_TYPE,
                    ActivityCameraGallery.EXTRA_IMAGE_MULTI_SELECT
                )
                startActivity(intent)
            }
            MAIN_TYPE_NOTIFICATION -> replaceFragment(FragmentNotification(), false)
            MAIN_TYPE_PROFILE -> replaceFragment(
                FragmentProfile.newInstance(Comm_Prefs.getUserProfileIndex()),
                false
            )
        }

    }

    override fun onBackPressed() {
        if (dl_drawer.isDrawerOpen(Gravity.RIGHT)) {
            dl_drawer.closeDrawer(Gravity.RIGHT)
        } else {
            super.onBackPressed()
            if (isMainRefresh || mCurrentUserIdx != Comm_Prefs.getUserProfileIndex()) {
                if (mCurrentUserIdx != Comm_Prefs.getUserProfileIndex()) {
                    mCurrentUserIdx = Comm_Prefs.getUserProfileIndex()
                    if (mViewRefreshListener != null) mViewRefreshListener!!.OnRefreshAllView()
                } else {
                    if (mViewRefreshListener != null) mViewRefreshListener!!.OnRefreshView()
                }
            }
            isMainRefresh = false
        }
    }

    fun onBackPressed(isMainRefresh: Boolean) {
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

    var mViewRefreshListener: IOMainViewRefresh? = null

    /**
     * Main 페이지에서 View가 Refresh되지 않는 현상을 수정
     */
    interface IOMainViewRefresh {
        fun OnRefreshView()
        fun OnRefreshAllView() {}
    }
}
