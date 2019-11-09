package com.truevalue.dreamappeal.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.activity.ActivityMyProfileContainer
import com.truevalue.dreamappeal.activity.BeanProfileGroup
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.bean.BeanProfileUser
import com.truevalue.dreamappeal.bean.BeanProfileUserPrivates
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_normal_profile.*
import okhttp3.Call
import org.json.JSONObject
import java.text.SimpleDateFormat

class FragmentMyProfile : BaseFragment() {

    // Recyclerview Adapter
    private var mAdapter: BaseRecyclerViewAdapter? = null
    private var mBean : BeanProfileUser? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_normal_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initView
        initView()
        // View Click
        onClickView()
        // 내 유저 데이터 가져오기
        getUserProfile()
        // RecyclerView Adatpr init
        initAdapter()
        // bind Temp Data
        bindTempData()
    }

    /**
     * View 초기화
     */
    private fun initView() {
        // 상단 바 설정
        (activity as ActivityMyProfileContainer).iv_back_black.visibility = View.GONE
        (activity as ActivityMyProfileContainer).iv_back_blue.visibility = View.VISIBLE
        (activity as ActivityMyProfileContainer).iv_check.visibility = View.GONE
        (activity as ActivityMyProfileContainer).tv_title.text = getString(R.string.str_normal_profile)
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                (activity as ActivityMyProfileContainer).iv_back_blue -> activity!!.onBackPressed()
                iv_edit_normal_profile -> {
                    (activity as ActivityMyProfileContainer).replaceFragment(FragmentNormalProfileEdit.newInstance(mBean), true)
                }
                iv_add_group -> {

                }
            }
        }

        (activity as ActivityMyProfileContainer).iv_back_blue.setOnClickListener(listener)
        iv_edit_normal_profile.setOnClickListener(listener)
        iv_add_group.setOnClickListener(listener)
    }

    /**
     * HTTP
     * 개인정보 조회
     */
    private fun getUserProfile(){
        DAClient.getMyUserData(object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if(context != null){
                    Toast.makeText(context!!.applicationContext,message, Toast.LENGTH_SHORT).show()

                    if(code == DAClient.SUCCESS){
                        // todo : 데이터 바인딩 필요
                        val json = JSONObject(body)
                        val user = json.getJSONObject("user")
                        val profileUser = Gson().fromJson<BeanProfileUser>(user.toString(),BeanProfileUser::class.java)
                        profileUser.private = Gson().fromJson(user.getJSONObject("privates").toString(),BeanProfileUserPrivates::class.java)
                        mBean = profileUser
                        val bean = profileUser
                        tv_name.text = bean.name
                        tv_nickname.text = if(bean.nickname.isNullOrEmpty()) getString(R.string.str_none) else bean.nickname
                        val sdf = SimpleDateFormat("yyyy-MM-dd")
                        val date = sdf.parse(bean.birth)
                        // todo : 추후 나이계산
                        tv_age.text = 1.toString()
                        tv_gender.text = if(bean.gender == 0) "여" else "남"
                        tv_address.text = if(bean.address.isNullOrEmpty()) getString(R.string.str_none) else bean.address
                        tv_email.text = bean.email
                        val groups = json.getJSONArray("groups")
                        for (i in 0 until groups.length()){
                            val Object = groups.getJSONObject(i)
                            val group : BeanProfileGroup = Gson().fromJson(Object.toString(),BeanProfileGroup::class.java)
                            group.Class = Object.getInt("class")
                            mAdapter!!.add(group)
                        }
                    }
                }
            }
        })
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(listener)
        rv_normal_profile_group.adapter = mAdapter
        rv_normal_profile_group.layoutManager = LinearLayoutManager(context)
    }

    /**
     * Bind Temp Data
     */
    private fun bindTempData(){
        for(i in 1 .. 10){
            mAdapter!!.add("")
        }
    }

    private val listener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_normal_profile_group, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {

        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}