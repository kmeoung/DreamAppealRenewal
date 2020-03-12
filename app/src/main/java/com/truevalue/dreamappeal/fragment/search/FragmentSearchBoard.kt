package com.truevalue.dreamappeal.fragment.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivitySearch
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.bean.BeanSearchBoard
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.fragment_search_board.*
import okhttp3.Call
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class FragmentSearchBoard : BaseFragment(), ActivitySearch.IOSearchListener {

    private var mAdapter: BaseRecyclerViewAdapter?
    private var mInputTag : String?
    private var mSearchTag : String?
    init {
        mAdapter = null
        mInputTag = null
        mSearchTag = null
    }

    companion object{
        /**
         * Tag 게시물 검색용
         */
        fun newInstance(tag_keyword :String) : FragmentSearchBoard {
            val fragment = FragmentSearchBoard()
            fragment.mSearchTag = tag_keyword
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_search_board, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Rv Adapter 초기화
        initAdapter()
        // 데이터 초기화
        initData()
        // 초반 데이터 가져오기
        if(mSearchTag.isNullOrEmpty()) getBoardSearch()
        else getTagBoardSearch()
    }

    /**
     * Data 초기화
     */
    private fun initData() {
        (activity as ActivitySearch).mSearchListener = this
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvListener)
        rv_board.adapter = mAdapter
        rv_board.layoutManager = GridLayoutManager(context!!, 2)
        rv_board.addItemDecoration(BaseGridItemDecorate(context!!, 2.0f, 2))
    }

    /**
     * Http
     * 게시글 추천
     */
    private fun getBoardSearch() {

        DAClient.searchBoard(object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {

                if (code == DAClient.SUCCESS) {

                    val json = JSONObject(body)
                    try {

                        val posts: JSONArray = json.getJSONArray("posts")
                        mAdapter?.let {
                            it.clear()
                            for (i in 0 until posts.length()) {
                                val post: JSONObject = posts.getJSONObject(i)
                                val bean = Gson().fromJson<BeanSearchBoard>(
                                    post.toString(),
                                    BeanSearchBoard::class.java
                                )

                                // 사용자가 검색한 태그를 사용자 화면에 표시하는 과정
                                if (!bean.tags.isNullOrEmpty() && !mInputTag.isNullOrEmpty()) bean.tags =
                                    mInputTag
                                it.add(bean)
                            }
                        }
                    }catch (e : JSONException){
                        e.printStackTrace()
                    }
                } else {
                    context?.let {
                        Toast.makeText(it.applicationContext, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    /**
     * Http
     * 태그 게시물 검색
     */
    private fun getTagBoardSearch() {

        mSearchTag?.let {
            DAClient.searchTagPost(it,object : DAHttpCallback {
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {

                    if (code == DAClient.SUCCESS) {

                        val json = JSONObject(body)
                        val posts = json.getJSONArray("posts")
                        mAdapter?.let {
                            it.clear()
                            for (i in 0 until posts.length()) {
                                val post = posts.getJSONObject(i)
                                val bean = Gson().fromJson<BeanSearchBoard>(
                                    post.toString(),
                                    BeanSearchBoard::class.java
                                )

                                // 사용자가 검색한 태그를 사용자 화면에 표시하는 과정
                                if(!bean.tags.isNullOrEmpty() && !mInputTag.isNullOrEmpty()) bean.tags = mInputTag
                                it.add(bean)
                            }
                        }

                    } else {
                        context?.let {
                            Toast.makeText(it.applicationContext, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })

        }
    }


    /**
     * Http
     * 게시글 검색
     */
    private fun getBoardSearch(keyword: String) {

        DAClient.searchBoard(keyword, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (code == DAClient.SUCCESS) {

                    val json = JSONObject(body)
                    val posts = json.getJSONArray("posts")
                    mAdapter?.let {
                        it.clear()
                        for (i in 0 until posts.length()) {
                            val post = posts.getJSONObject(i)
                            val bean = Gson().fromJson<BeanSearchBoard>(
                                post.toString(),
                                BeanSearchBoard::class.java
                            )

                            // 사용자가 검색한 태그를 사용자 화면에 표시하는 과정
                            if(!bean.tags.isNullOrEmpty() && !mInputTag.isNullOrEmpty()) bean.tags = mInputTag
                            it.add(bean)
                        }
                    }

                } else {
                    context?.let {
                        Toast.makeText(it.applicationContext, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    /**
     * RecyclerView Listener
     */
    private val rvListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_search_board, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            val ivImage = h.getItemView<ImageView>(R.id.iv_image)
            val llTitle = h.getItemView<LinearLayout>(R.id.ll_title)
            val tvTitle = h.getItemView<TextView>(R.id.tv_title)
            val tvSubTitle = h.getItemView<TextView>(R.id.tv_sub_title)
            val llBg = h.getItemView<LinearLayout>(R.id.ll_bg)

            Utils.setImageItemViewSquare(context,llBg,2)

            mAdapter?.let {
                val bean = it.get(i) as BeanSearchBoard

                if(bean.object_name.isNullOrEmpty()){ // 태그
                    llTitle.visibility = GONE
                    tvSubTitle.text = "#${bean.tags}"
                }else{ // 게시글
                    llTitle.visibility = VISIBLE
                    tvTitle.text = bean.object_name
                    tvSubTitle.text = "${bean.value_style} ${bean.job}"
                }

                Glide.with(context!!)
                    .load(bean.thumbnail_image)
                    .centerCrop()
                    .into(ivImage)

                h.itemView.setOnClickListener {
                    val intent = Intent()
                    intent.putExtra(ActivitySearch.RESULT_REPLACE_BOARD_IDX,bean.idx)
                    intent.putExtra(ActivitySearch.RESULT_REPLACE_BOARD_TYPE,bean.post_type)
                    intent.putExtra(ActivitySearch.RESULT_REPLACE_PROFILE_IDX,bean.profile_idx)
                    activity!!.setResult(ActivitySearch.RESULT_CODE_BOARD,intent)
                    activity!!.finish()
                }
            }


        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }

    /**
     * 검색 Listener
     */
    override fun onSearch(keyword: String) {
        if (keyword.isNullOrEmpty()) {
            if(mSearchTag.isNullOrEmpty()) getBoardSearch()
            else activity!!.onBackPressed()
        } else {
            mInputTag = keyword
            getBoardSearch(keyword)
        }
    }
}