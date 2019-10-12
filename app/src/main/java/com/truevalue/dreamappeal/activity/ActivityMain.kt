package com.truevalue.dreamappeal.activity

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.fragment.profile.FragmentProfile
import kotlinx.android.synthetic.main.action_bar.*
import kotlinx.android.synthetic.main.bottom_main_view.*

class ActivityMain : BaseActivity() {

    companion object {
        val MAIN_TYPE_HOME = "MAIN_TYPE_HOME"
        val MAIN_TYPE_TIMELINE = "MAIN_TYPE_TIMELINE"
        val MAIN_TYPE_ADD_BOARD = "MAIN_TYPE_ADD_BOARD"
        val MAIN_TYPE_NOTIFICATION = "MAIN_TYPE_NOTIFICATION"
        val MAIN_TYPE_PROFILE = "MAIN_TYPE_PROFILE"

        val ACTION_BAR_TYPE_PROFILE_MAIN = "ACTION_BAR_TYPE_PROFILE_MAIN"
        val ACTION_BAR_TYPE_PROFILE_OTHER = "ACTION_BAR_TYPE_PROFILE_OTHER"
    }

    private var mMainViewType = MAIN_TYPE_HOME
    private var mActionBarType = ACTION_BAR_TYPE_PROFILE_MAIN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Action
        onAction()
    }

    fun onAction() {
        // Fragment 초기화
        initFragment()
        // Action Bar 초기화
        initActionBar(mActionBarType)
        // Bottom View Click Listener
        onClickBottomView()
        // bottom 이미지 초기화
        initBottomView()
    }

    /**
     * Bottom View Click Listener
     */
    fun onClickBottomView() {
        var onClickListener = View.OnClickListener {
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
     * 하단 View 클릭 이미지 설정
     */
    fun initBottomView() {
        when (mMainViewType) {
            MAIN_TYPE_HOME -> {
                iv_home.isSelected = true
                iv_timeline.isSelected = false
                iv_notification.isSelected = false
                iv_profile.isSelected = false
                FragmentProfile()
            }
            MAIN_TYPE_TIMELINE -> {
                iv_home.isSelected = false
                iv_timeline.isSelected = true
                iv_notification.isSelected = false
                iv_profile.isSelected = false
                FragmentProfile()
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
            }
            MAIN_TYPE_PROFILE -> {
                iv_home.isSelected = false
                iv_timeline.isSelected = false
                iv_notification.isSelected = false
                iv_profile.isSelected = true
                FragmentProfile()
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

    fun initActionBar(action_bar_type: String?) {
        if (!action_bar_type.isNullOrEmpty()) {
            mActionBarType = action_bar_type
            when (action_bar_type) {
                ACTION_BAR_TYPE_PROFILE_MAIN -> {
                    profile_main.visibility = VISIBLE
                    profile_other.visibility = GONE
                }
                ACTION_BAR_TYPE_PROFILE_OTHER -> {
                    profile_main.visibility = GONE
                    profile_other.visibility = VISIBLE
                }
            }
        }
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
}
