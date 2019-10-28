package com.truevalue.dreamappeal.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.base.IOActionBarListener
import com.truevalue.dreamappeal.fragment.profile.FragmentProfile
import com.truevalue.dreamappeal.fragment.profile.blueprint.FragmentBlueprint
import com.truevalue.dreamappeal.fragment.profile.dream_present.FragmentDreamList
import com.truevalue.dreamappeal.fragment.profile.dream_present.FragmentDreamPresent
import com.truevalue.dreamappeal.fragment.profile.performance.FragmentPerformance
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_main_view.*

class ActivityMain : BaseActivity() {

    var mActionListener : IOActionBarListener? = null
    companion object {
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
    }

    /**
     * ActivityMain Action
     */
    private fun onAction() {
        // Fragment 초기화
        initFragment()
        // Action Bar 초기화
        initActionBar(mActionBarType)
        // Bottom View Click Listener
        onClickBottomView()
        // bottom 이미지 초기화
        initBottomView()

        val ivMenu: ImageView = findViewById(R.id.iv_menu)
        ivMenu.setOnClickListener(View.OnClickListener {
            var intent = Intent(this, ActivityLoginContainer::class.java)
            startActivity(intent)
            finish()
        })
    }

    /**
     * Drawer 설정
     */
    private fun setDrawer(){
        dl_drawer.addDrawerListener(object : DrawerLayout.DrawerListener{
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
    private fun isOpenDrawer() : Boolean{
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
     * Bottom View Click Listener
     */
    private fun onClickBottomView() {
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
    private fun initBottomView() {
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

    /**
     * Action Bar 초기화
     */
    fun initActionBar(action_bar_type: String?){
//        if (!action_bar_type.isNullOrEmpty()) {
//            mActionBarType = action_bar_type
//            when (action_bar_type) {
//                ACTION_BAR_TYPE_PROFILE_MAIN -> {
//                    profile_main.visibility = VISIBLE
//                    profile_other.visibility = GONE
//                }
//                ACTION_BAR_TYPE_PROFILE_OTHER -> {
//                    profile_main.visibility = GONE
//                    profile_other.visibility = VISIBLE
//                }
//            }
//        }
    }

    /**
     * Action Bar 설정
     */
    fun setActionBar(data_class : Any,listener : IOActionBarListener?){
//        this.mActionListener = listener
//        var action_bar_type = when(data_class){ // Action Bar Main
//            data_class as BeanActionBarMain->{
//
//                profile_main.findViewById<ImageView>(R.id.iv_menu).visibility = if(data_class.isMenu) VISIBLE else GONE
//                profile_main.findViewById<ImageView>(R.id.iv_back).visibility = if(data_class.isBack) VISIBLE else GONE
//                profile_main.findViewById<ImageView>(R.id.iv_search).visibility = if(data_class.isSearch) VISIBLE else GONE
//                profile_main.findViewById<TextView>(R.id.tv_text_btn).visibility = if(data_class.isTextBtn) VISIBLE else GONE
//                // 가운데 대칭 설정
//                if(!data_class.isSearch && !data_class.isTextBtn) profile_main.findViewById<ImageView>(R.id.iv_search).visibility = INVISIBLE
//
//                ACTION_BAR_TYPE_PROFILE_MAIN
//            }
//            data_class as BeanActionBarOther->{ // Action Bar 나머지
//
//                profile_other.findViewById<ImageView>(R.id.iv_menu).visibility = if(data_class.isMenu) VISIBLE else GONE
//                profile_other.findViewById<ImageView>(R.id.iv_back).visibility = if(data_class.isBack) VISIBLE else GONE
//                profile_other.findViewById<ImageView>(R.id.iv_close).visibility = if(data_class.isClose) VISIBLE else GONE
//                profile_other.findViewById<ImageView>(R.id.iv_search).visibility = if(data_class.isSearch) VISIBLE else GONE
//                profile_other.findViewById<TextView>(R.id.tv_text_btn).visibility = if(data_class.isTextBtn) VISIBLE else GONE
//                // 가운데 대칭 설정
//                if(!data_class.isSearch && !data_class.isTextBtn) profile_other.findViewById<ImageView>(R.id.iv_search).visibility = INVISIBLE
//
//                ACTION_BAR_TYPE_PROFILE_OTHER
//            }
//            else->ACTION_BAR_TYPE_PROFILE_MAIN
//        }
//        initActionBar(action_bar_type)
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
