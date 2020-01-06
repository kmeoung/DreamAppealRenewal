package com.truevalue.dreamappeal.activity

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import com.truevalue.dreamappeal.bean.BeanAddress
import com.truevalue.dreamappeal.http.*
import com.truevalue.dreamappeal.utils.Comm_Param
import kotlinx.android.synthetic.main.activity_address_search.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_search.btn_cancel
import kotlinx.android.synthetic.main.activity_search.et_search
import kotlinx.android.synthetic.main.activity_search.iv_cancel
import kotlinx.android.synthetic.main.fragment_add_action_post.*
import okhttp3.Call
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class ActivityAddrSearch : BaseActivity() {

    private val SEARCH_DELAY = 1000L
    private var mAdapter: BaseRecyclerViewAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_search)

        // View Init
        initView()
        // Rv Adapter 초기화
        initAdapter()
        // view click listener
        onClickView()
    }

    /**
     * Init View
     */
    private fun initView() {
        iv_cancel.visibility = GONE

        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (et_search.text.toString().isNullOrEmpty()) {
                    iv_cancel.visibility = GONE
                } else iv_cancel.visibility = VISIBLE
            }
        })
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvListener)
        rv_search.adapter = mAdapter
        rv_search.layoutManager = LinearLayoutManager(this)
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                btn_cancel -> finish()
                iv_cancel -> {
                    et_search.setText("")
                }
            }
        }
        btn_cancel.setOnClickListener(listener)
        iv_cancel.setOnClickListener(listener)

        et_search.setOnEditorActionListener(TextView.OnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                if (!et_search.text.toString().isNullOrEmpty()) {
                    getAddrPost(et_search.text.toString())
                    et_search.setText("")
                }
            } else
                false
            true
        })

    }

    /**
     * Http
     * GET
     * 주소정보 가져오기
     */
    private fun getAddrPost(addr: String) {
        val header = DAHttpHeader()
        header.put("Authorization", " KakaoAK ${getString(R.string.kakao_rest_api_key)}")
        val params = DAHttpParams()
        params.put("query", addr)
        BaseOkhttpClient.request(
            HttpType.GET,
            Comm_Param.KAKAO_ADDRESS_API,
            header,
            params,
            object : DAHttpCallback {
                override fun onFailure(call: Call, e: IOException) {
                    super.onFailure(call, e)
                    e.printStackTrace()
                }

                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    val json = JSONObject(body)
                    mAdapter!!.clear()
                    try {
                        val documents = json.getJSONArray("documents")
                        for (i in 0 until documents.length()) {
                            val document = documents.getJSONObject(i)
                            var address : JSONObject
                            try{
                                address = document.getJSONObject("address")
                            }catch (e: Exception){
                                address = document.getJSONObject("road_address")
                            }
                            val addrName = address.getString("address_name")

                            val region_1depth_name = address.getString("region_1depth_name")
                            val region_2depth_name = address.getString("region_2depth_name")
                            val region_3depth_name = address.getString("region_3depth_name")
                            var region_3depth_h_name : String?
                            try {
                                region_3depth_h_name = address.getString("region_3depth_h_name")
                            }catch (e : Exception){
                                region_3depth_h_name = null
                            }

                            val x = address.getDouble("x")
                            val y = address.getDouble("y")

                            var zip_code : String?
                            try{
                                zip_code = address.getString("zip_code")
                            }catch (e : Exception){
                                zip_code = null
                            }

                            val bean = BeanAddress(
                                addrName,
                                region_1depth_name,
                                region_2depth_name,
                                region_3depth_name,
                                region_3depth_h_name,
                                x,
                                y,
                                zip_code
                            )
                            mAdapter!!.add(bean)
                        }
                    }catch (e : JSONException){
                        e.printStackTrace()
                    }
                }
            }
        )
    }

    /**
     * RecyclerView Listener
     */
    private val rvListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_addr_search, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            val tvAddr = h.getItemView<TextView>(R.id.tv_addr)
            val bean = mAdapter!!.get(i) as BeanAddress
            tvAddr.text = bean.addres_name
            tvAddr.setOnClickListener(View.OnClickListener {
                setUserAddress(bean)
            })
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }


    /**
     * Http
     * 유저 주소 등록
     */
    private fun setUserAddress(bean : BeanAddress){
        DAClient.setUserAddress(bean.addres_name,
            bean.region_1depth_name,
            bean.region_2depth_name,
            bean.region_3depth_name,
            bean.region_3depth_h_name,
            bean.x,
            bean.y,
            bean.zipcode,
            object : DAHttpCallback{
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
                }
            })
    }

}