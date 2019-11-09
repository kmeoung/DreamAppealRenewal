package com.truevalue.dreamappeal.activity

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.activity_follow.*
import okhttp3.Call

class ActivityFollow : BaseActivity() {

    private var mViewType : String? = null

    companion object {
        val EXTRA_VIEW_TYPE = "EXTRA_VIEW_TYPE"
        val VIEW_TYPE_FOLLOWING = "VIEW_TYPE_FOLLOWING"
        val VIEW_TYPE_FOLLOWER = "VIEW_TYPE_FOLLOWER"
    }

    private var mAdapter: BaseRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follow)
    }

    override fun onResume() {
        super.onResume()
        // action
        onAction()
    }

    /**
     * Action
     */
    private fun onAction() {
        // Init View
        initView()
        // RecyclerView 초기화
        initAdapter()
        // View 클릭 리스너
        onClickView()
        // Init Data
        initData()
    }

    /**
     * 데이터 초기화
     */
    private fun initData(){
        mViewType = intent.getStringExtra(EXTRA_VIEW_TYPE)
        if(!mViewType.isNullOrEmpty()){
            when(mViewType){
                VIEW_TYPE_FOLLOWER->{
                    tv_title.text = getString(R.string.str_follower)
                    getFollower()
                }
                VIEW_TYPE_FOLLOWING->{
                    tv_title.text = getString(R.string.str_menu_following)
                    getFollowing()
                }
            }
        }
    }

    /**
     * View 초기화
     */
    private fun initView() {
        iv_back_blue.visibility = VISIBLE
        iv_back_black.visibility = GONE

    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val clickListener = View.OnClickListener {
            when (it) {
                iv_back_blue -> finish()

            }
        }
        iv_back_blue.setOnClickListener(clickListener)
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        if (mAdapter == null) mAdapter = BaseRecyclerViewAdapter(listener)
        rv_follow.layoutManager = LinearLayoutManager(this@ActivityFollow)
        rv_follow.adapter = mAdapter
    }

    /**
     * Http
     * 나를 팔로우한 사람들 리스트 가져오기
     */
    private fun getFollower(){
        // todo : 현재 보고있는 프로필의 인덱스를 넣어야 합니다
        val profile_idx = Comm_Prefs.getUserProfileIndex()
        DAClient.getFollowerList(profile_idx, object : DAHttpCallback{
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()

                if(code == DAClient.SUCCESS){

                }
            }
        })
    }

    /**
     * Http
     * 내가 팔로우한 사람들 리스트 가져오기
     */
    private fun getFollowing(){
        DAClient.getFollowingList(object : DAHttpCallback{
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()

                if(code == DAClient.SUCCESS){

                }
            }
        })
    }


    /**
     * RecyclerView Listener
     */
    private val listener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_follower, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {

        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}