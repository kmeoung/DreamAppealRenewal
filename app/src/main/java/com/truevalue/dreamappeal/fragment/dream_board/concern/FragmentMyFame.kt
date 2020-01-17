package com.truevalue.dreamappeal.fragment.dream_board.concern

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanMyFame
import com.truevalue.dreamappeal.bean.ConcernHistory
import com.truevalue.dreamappeal.bean.ReConcernHistory
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_my_fame.*
import okhttp3.Call
import org.json.JSONObject

class FragmentMyFame : BaseFragment() {
    private var mAdapter : BaseRecyclerViewAdapter?
    private var mBean : BeanMyFame?

    companion object{

        private const val RV_TYPE_ITEM = 0
        private const val RV_TYPE_HEADER = 1
    }

    init {
        mAdapter = null
        mBean = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_my_fame, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // view 초기화
        initView()
        // Adapter 초기화
        initAdapter()
        // View Click Listener
        onClickView()
        // 활동내역 조회
        getStatus()
    }


    /**
     * Http
     * 내 활동내역 조회
     */
    private fun getStatus(){
        DAClient.getConcernStatus(object : DAHttpCallback{
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if(code == DAClient.SUCCESS){
                    val json = JSONObject(body)
                    val bean = Gson().fromJson<BeanMyFame>(json.toString(), BeanMyFame::class.java)
                    mBean = bean
                    // todo : User Profile 없음
//                    iv_profile
                    tv_user.text = bean.user.nickname
                    tv_fame.text = bean.user.point.toString()

                    mAdapter?.let {adapter->
                        adapter.clear()

                        adapter.add(getString(R.string.str_fame_text))
                        for(i in bean.concern_history.indices){
                            val bean = bean.concern_history[i]
                            adapter.add(bean)
                        }
                        adapter.add(getString(R.string.str_fame_text))
                        for(i in bean.concern_history.indices){
                            val bean = bean.re_concern_history[i]
                            adapter.add(bean)
                        }
                    }
                }else{
                    context?.let {
                        Toast.makeText(it.applicationContext,message,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    /**
     * View 초기화
     */
    private fun initView() {
        iv_back_blue.visibility = VISIBLE
        iv_back_black.visibility = GONE
        tv_title.text = getString(R.string.str_my_fame)
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter(){
        mAdapter = BaseRecyclerViewAdapter(rvListener)
        rv_cycle.run {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_back_blue -> {
                    (activity as ActivityMain).onBackPressed(false)
                }
            }
        }
        iv_back_blue.setOnClickListener(listener)
    }

    private val rvListener = object : IORecyclerViewListener{
        override val itemCount: Int
            get() = mAdapter?.size() ?: 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            if(viewType == RV_TYPE_HEADER) return BaseViewHolder.newInstance(R.layout.listitem_my_fame_header,parent,false)
            return BaseViewHolder.newInstance(R.layout.listitem_my_fame,parent,false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {

            if(getItemViewType(i) == RV_TYPE_HEADER){
                val tvTitle = h.getItemView<TextView>(R.id.tv_title)
                val bean = mAdapter?.get(i) as String
                tvTitle.text = bean
            }else{
                val tvcontents = h.getItemView<TextView>(R.id.tv_contents)

                if(mAdapter?.get(i) is ConcernHistory){
                    val bean = mAdapter?.get(i) as ConcernHistory
                    tvcontents.text = bean.title
                }else if(mAdapter?.get(i) is ReConcernHistory){
                    val bean = mAdapter?.get(i) as ReConcernHistory
                    tvcontents.text = bean.title
                }
            }
        }

        override fun getItemViewType(i: Int): Int {
            if(mAdapter?.get(i) is String)
            return RV_TYPE_HEADER
            return RV_TYPE_ITEM
        }
    }
}