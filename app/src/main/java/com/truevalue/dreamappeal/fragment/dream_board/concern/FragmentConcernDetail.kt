package com.truevalue.dreamappeal.fragment.dream_board.concern

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.bean.AdoptedRePost
import com.truevalue.dreamappeal.bean.BeanConcernDetail
import com.truevalue.dreamappeal.bean.Image
import com.truevalue.dreamappeal.bean.RePost
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_concern_detail.*
import kotlinx.android.synthetic.main.fragment_concern_detail.pager_image
import kotlinx.android.synthetic.main.fragment_concern_detail.rl_images
import kotlinx.android.synthetic.main.fragment_concern_detail.tv_contents
import kotlinx.android.synthetic.main.fragment_concern_detail.tv_indicator
import kotlinx.android.synthetic.main.fragment_post_detail.*
import okhttp3.Call
import org.json.JSONObject

class FragmentConcernDetail : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter?
    private var mAdapterImage: BasePagerAdapter?
    private var mConcernIdx: Int?
    private var mBean : BeanConcernDetail?

    init {
        mAdapter = null
        mAdapterImage = null
        mConcernIdx = -1
        mBean = null
    }

    companion object {

        private const val RV_TYPE_ADOPTED = 0
        private const val RV_TYPE_ITEM = 1


        fun newInstance(concern_idx: Int): FragmentConcernDetail {
            val fragment = FragmentConcernDetail()
            fragment.mConcernIdx = concern_idx
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_concern_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // view 초기화
        initView()
        // adapter 초기화
        initAdapter()
        // View Click Listener
        onClickView()
        // 게시글 조회
        getConcernDetail()
    }

    /**
     * View 초기화
     */
    private fun initView() {
        // 상단 이미지 정사각형 설정
        Utils.setImageViewSquare(context, rl_images)
        tv_title.text = getString(R.string.str_concern_title)
    }

    /**
     * http
     * 게시글 조회
     */
    private fun getConcernDetail() {
        mConcernIdx?.let {
            DAClient.getConcern(it, object : DAHttpCallback {
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    if (code == DAClient.SUCCESS) {
                        val json = JSONObject(body)
                        val bean = Gson().fromJson<BeanConcernDetail>(json.toString(),BeanConcernDetail::class.java)
                        setConcernDetail(bean)
                    } else {
                        context?.let { context ->
                            Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            })
        }
    }

    private fun setConcernDetail(bean : BeanConcernDetail){
        mBean = bean
        tv_like_cnt.text = bean.post.votes
        tv_concern_title.text = bean.post.title

        tv_indicator.text = "0 / 0"
        bean.images?.let {
            tv_indicator.text = "1 / " + it.size
            for (i in it.indices) {
                mAdapterImage!!.add(it[i])
            }
            mAdapterImage!!.notifyDataSetChanged()
        }

        tv_contents.text = bean.post.content
        // todo : profile image 넘어오지 않음
//        iv_profile
        tv_user.text = "${bean.post_writer.value_style} ${bean.post_writer.job} ${bean.post_writer.nickname}"
        tv_fame.text = bean.post_writer.reputation
        // todo : 아직 지정되지 않음
//        tv_gold
//        tv_silver
//        tv_bronze

        bean.re_posts?.let {
            tv_comment_cnt.text = bean.re_posts?.size.toString()
        }?: kotlin.run {
            tv_comment_cnt.text = 0.toString()
        }

        mAdapter?.let {adapter->
            adapter.clear()
            bean.adopted_re_post?.let {
                adopted->
                adapter.add(adopted)
            }

            bean.re_posts?.let {
                for(i in it.iterator()){
                    adapter.add(it)
                }
            }


        }

    }

    /**
     * Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvListener)
        rv_re_concern.run {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }

        mAdapterImage = BasePagerAdapter(context, object : BasePagerAdapter.IOBasePagerListener {
            override fun onBindViewPager(any: Any, view: ImageView, position: Int) {
                val url = any as Image
                context?.let {
                    Glide.with(it)
                        .load(url.image_url)
                        .centerCrop()
                        .placeholder(R.drawable.ic_image_gray)
                        .into(view)
                }

            }
        })

        pager_image.run {
            adapter = mAdapterImage
            addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    mAdapterImage?.let {
                        tv_indicator.text =
                            if (it.count > 0) ((position + 1).toString() + " / " + it.count) else "0 / 0"
                    }
                }
            })
        }
    }

    /**
     * View Click Listener
     */
    private fun onClickView(){
        val listener = View.OnClickListener{
            when(it){
                iv_back_black->(activity as ActivityMain).onBackPressed(false)
                iv_like_up->{

                }
                iv_like_down->{

                }
            }
        }
        iv_back_black.setOnClickListener(listener)
        iv_like_up.setOnClickListener(listener)
        iv_like_down.setOnClickListener(listener)
    }



    /**
     * 답글 RecyclerView Listener
     */
    private val rvListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = mAdapter?.size() ?: 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_re_concern, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            val ivLikeUp = h.getItemView<ImageView>(R.id.iv_like_up)
            val tvLikeCnt = h.getItemView<TextView>(R.id.tv_like_cnt)
            val ivLikeDown = h.getItemView<ImageView>(R.id.iv_like_down)
            val tvAdoption = h.getItemView<TextView>(R.id.tv_adoption)
            val ivProfile = h.getItemView<ImageView>(R.id.iv_profile)
            val tvUser = h.getItemView<TextView>(R.id.tv_user)
            val tvFame = h.getItemView<TextView>(R.id.tv_fame)
            val tvContents = h.getItemView<TextView>(R.id.tv_contents)
            // todo : 아래 3가지는 정의되지 않음
            val tvGold = h.getItemView<TextView>(R.id.tv_gold)
            val tvSilver = h.getItemView<TextView>(R.id.tv_silver)
            val tvBronze = h.getItemView<TextView>(R.id.tv_bronze)
            val llBg = h.getItemView<LinearLayout>(R.id.ll_bg)

            if(getItemViewType(i) == RV_TYPE_ADOPTED){
                val bean = mAdapter!!.get(i) as AdoptedRePost
                tvAdoption.visibility = GONE
                llBg.setBackgroundColor(ContextCompat.getColor(context!!,R.color.off_white_two))

                tvLikeCnt.text = bean.votes
                tvContents.text = bean.content
                // todo : 프로필 이미지가 없음
                //ivProfile.
                tvUser.text = "${bean.value_style} ${bean.job} ${bean.nickname}"
                tvFame.text = bean.reputation

            }else{
                val bean = mAdapter!!.get(i) as RePost
                tvAdoption.visibility = if(mBean?.adopted_re_post == null) VISIBLE else GONE
                llBg.setBackgroundColor(ContextCompat.getColor(context!!,R.color.white))

                tvLikeCnt.text = bean.votes
                // todo : 프로필 이미지가 없음
                //ivProfile.
                tvUser.text = "${bean.value_style} ${bean.job} ${bean.nickname}"
                tvFame.text = bean.reputation
            }
        }

        override fun getItemViewType(i: Int): Int {
            if(mAdapter!!.get(i) is AdoptedRePost) return RV_TYPE_ADOPTED

            return RV_TYPE_ITEM
        }
    }
}