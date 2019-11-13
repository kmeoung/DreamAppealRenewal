package com.truevalue.dreamappeal.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityDreamNote
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import okhttp3.Call
import org.json.JSONObject
import java.text.SimpleDateFormat

class FragmentDreamNoteLife : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_recyclerview, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // View 초기화
        initView()
        // View Click Listener
        onClickView()
        // RecyclerView Adapter 초기화
        initAdapter()
        // data bind
        getDreamNoteLife()
    }

    /**
     * View Init
     */
    private fun initView() {
        // Action Bar 설정
        (activity as ActivityDreamNote).iv_back_black.visibility = View.VISIBLE
        (activity as ActivityDreamNote).tv_title.text = getString(R.string.str_dream_note)
    }

    /**
     * View 클릭 리스너
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                (activity as ActivityDreamNote).iv_back_black -> activity!!.finish()
            }
        }
        (activity as ActivityDreamNote).iv_back_black.setOnClickListener(listener)
    }

    /**
     * Init RecyclerView Adapter
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(recyclerViewListener)
        rv_recycle.adapter = mAdapter
        rv_recycle.layoutManager = LinearLayoutManager(context!!)
    }

    /**
     * Http
     * 일상 겸험 가져오기
     */
    private fun getDreamNoteLife() {
        // todo : 현재 보고있는 프로필의 idx 를 넣어야 합니다
        val cur_profile_idx = Comm_Prefs.getUserProfileIndex()

        DAClient.getDreamNoteLife(cur_profile_idx,
            object : DAHttpCallback {
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    if (context != null) {
                        Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT)
                            .show()

                        if (code == DAClient.SUCCESS) {
                            if (mAdapter == null) return
                            val json = JSONObject(body)
                            mAdapter!!.clear()
                            try {
                                val lifes = json.getJSONArray("life_posts")
                                for (i in 0 until lifes.length()) {
                                    val bean = Gson().fromJson<BeanDreamNoteLife>(
                                        lifes.getJSONObject(i).toString(),
                                        BeanDreamNoteLife::class.java
                                    )
                                    mAdapter!!.add(bean)
                                }
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
            })
    }

    /**
     * RecyclerView Listener
     */
    private
    val recyclerViewListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): BaseViewHolder {
            return BaseViewHolder.newInstance(
                R.layout.listitem_lifestyle,
                parent,
                false
            )
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            if (mAdapter != null) {
                val bean = mAdapter!!.get(i) as BeanDreamNoteLife
                val tvTitle = h.getItemView<TextView>(R.id.tv_title)
                val ivThumbnail = h.getItemView<ImageView>(R.id.iv_thumbnail)
                val tvDate = h.getItemView<TextView>(R.id.tv_date)

                tvTitle.text = bean.content
                if (bean.image.isNullOrEmpty()) {
                    Glide.with(context!!).load(R.drawable.ic_image_white).load(ivThumbnail)
                } else Glide.with(context!!).load(bean.image).placeholder(R.drawable.ic_image_white).load(
                    ivThumbnail
                )
                // todo : Date 형식 맞춰야 합니다
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val sdf2 = SimpleDateFormat("yyyy. MM. dd")

                val date = sdf.parse(bean.register_date)
                tvDate.text = sdf2.format(date)

                h.itemView.setOnClickListener(View.OnClickListener {
                    // todo : 여기에 상세페이지 달아야 합니다
                })
            }
        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}