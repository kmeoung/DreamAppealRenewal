package com.truevalue.dreamappeal.fragment.timeline

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivitySearch
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.bean.BeanTimeline
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_timeline.*
import kotlinx.android.synthetic.main.bottom_post_view.*
import kotlinx.android.synthetic.main.fragment_post_detail.*
import kotlinx.android.synthetic.main.fragment_timeline.*
import okhttp3.Call
import org.json.JSONObject
import java.lang.Exception

class FragmentTimeline : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private var mAdatper: BaseRecyclerViewAdapter? = null

    private val RV_TYPE_TIMELINE = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_timeline, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init View
        initView()
        // Init Adapter
        initAdapter()
        // View OnClick Listener
        onClickView()
        // init Data
        getTimeLineData()
    }

    /**
     * View 초기화
     */
    private fun initView() {
        Utils.setSwipeRefreshLayout(srl_refresh, this)
    }

    /**
     * Init RecyclerView Adapter
     */
    private fun initAdapter() {
        mAdatper = BaseRecyclerViewAdapter(rvListener)
        rv_timeline.adapter = mAdatper
        rv_timeline.layoutManager = LinearLayoutManager(context!!)
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_search -> {
                    val intent = Intent(context!!, ActivitySearch::class.java)
                    startActivity(intent)
                }
            }
        }
        iv_search.setOnClickListener(listener)
    }

    /**
     * Http
     * TimeLine
     */
    private fun getTimeLineData() {
        DAClient.getTimeLine(object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                srl_refresh.isRefreshing = false
                if (context != null) {
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()

                    if (code == DAClient.SUCCESS) {
                        val json = JSONObject(body)
                        mAdatper!!.clear()
                        try {
                            val posts = json.getJSONArray("posts")

                            for (i in 0 until posts.length()) {

                                val post = posts.getJSONObject(i)
                                val images = post.getJSONArray("images")

                                val bean = Gson().fromJson<BeanTimeline>(
                                    post.toString(),
                                    BeanTimeline::class.java
                                )

                                var imageList = ArrayList<String>()

                                for (j in 0 until images.length()) {

                                    val image = images.getJSONObject(j)

                                    val url = image.getString("url")
                                    imageList.add(url)
                                }
                                bean.imageList = imageList
                                mAdatper!!.add(bean)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

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
            get() = if (mAdatper != null) mAdatper!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            if (RV_TYPE_TIMELINE == viewType) {
                return BaseViewHolder.newInstance(R.layout.listitem_timeline, parent, false)
            }
            return BaseViewHolder.newInstance(R.layout.listitem_timeline, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {

            if (RV_TYPE_TIMELINE == getItemViewType(i)) {
                onTimelineBindViewHolder(h, i)
            }
        }

        override fun getItemViewType(i: Int): Int {
            return RV_TYPE_TIMELINE
        }

        /**
         * Timeline Bind View Holder
         */
        private fun onTimelineBindViewHolder(h: BaseViewHolder, i: Int) {
            val bean = mAdatper!!.get(i) as BeanTimeline
            val ivProfile = h.getItemView<ImageView>(R.id.iv_dream_profile)
            val ivMore = h.getItemView<ImageView>(R.id.iv_action_more)
            val tvValueStyle = h.getItemView<TextView>(R.id.tv_value_style)
            val tvJob = h.getItemView<TextView>(R.id.tv_job)
            val llObjectStep = h.getItemView<LinearLayout>(R.id.ll_object_step)
            val ivCircle = h.getItemView<ImageView>(R.id.iv_circle)
            val tvObject = h.getItemView<TextView>(R.id.tv_object)
            val llStepLine = h.getItemView<LinearLayout>(R.id.ll_step_line)
            val tvArrow = h.getItemView<TextView>(R.id.tv_arrow)
            val tvStep = h.getItemView<TextView>(R.id.tv_step)
            val rlImages = h.getItemView<RelativeLayout>(R.id.rl_images)
            // todo : 여기는 썸네일이 아닌 모든 데이터가 나와야 하는게 아닌가 ?
            val pagerImages = h.getItemView<ViewPager>(R.id.pager_image)
            val llIndicator = h.getItemView<LinearLayout>(R.id.ll_indicator)
            val tvIndicator = h.getItemView<TextView>(R.id.tv_indicator)
            val tvContents = h.getItemView<TextView>(R.id.tv_contents)
            val ivSideImg = h.getItemView<ImageView>(R.id.iv_side_img)
            val tvCheering = h.getItemView<TextView>(R.id.tv_cheering)
            val ivComment = h.getItemView<ImageView>(R.id.iv_comment)
            val tvComment = h.getItemView<TextView>(R.id.tv_comment)
            // todo : 좋아요 및 나머지 추후 연동
            val llCheering = h.getItemView<LinearLayout>(R.id.ll_cheering)
            val ivCheering = h.getItemView<ImageView>(R.id.iv_cheering)
            val llComment = h.getItemView<LinearLayout>(R.id.ll_comment)
            val llShare = h.getItemView<LinearLayout>(R.id.ll_share)
            val tvTime = h.getItemView<TextView>(R.id.tv_time)

            val pagerAdapter = BasePagerAdapter<String>(context!!)
            pagerImages.adapter = pagerAdapter
            pagerImages.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tvIndicator.text = ((position + 1).toString() + " / " + pagerAdapter!!.getCount())
                }
            })

            tvIndicator.text = ((1).toString() + " / " + bean.imageList.size)
            for (j in 0 until bean.imageList.size) {
                pagerAdapter.add(bean.imageList[j])
            }
            pagerAdapter.notifyDataSetChanged()

            when (bean.post_type) {
                0 -> {
                    ivCircle.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_circle_blue
                        )
                    )
                    tvArrow.setTextColor(ContextCompat.getColor(context!!, R.color.main_blue))
                    ivSideImg.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_side_blue
                        )
                    )
                }
                1 -> {
                    ivCircle.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_circle_green
                        )
                    )
                    tvArrow.setTextColor(ContextCompat.getColor(context!!, R.color.asparagus))
                    ivSideImg.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_side_green
                        )
                    )
                }
                2 -> {
                    ivCircle.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_circle_yellow
                        )
                    )
                    tvArrow.setTextColor(ContextCompat.getColor(context!!, R.color.yellow_orange))
                    ivSideImg.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_side_yellow
                        )
                    )
                }
            }


            if (!bean.profile_image.isNullOrEmpty()) {
                Glide.with(context!!)
                    .load(bean.profile_image)
                    .placeholder(R.drawable.drawer_user)
                    .circleCrop()
                    .into(ivProfile)
            }

            ivMore.setOnClickListener(View.OnClickListener {
                // todo : More 기능 추가 필요
            })

            tvValueStyle.text = if (bean.value_style.isNullOrEmpty()) "" else bean.value_style
            tvJob.text = if (bean.job.isNullOrEmpty()) "" else bean.job
            if (bean.object_title.isNullOrEmpty() && bean.step_title.isNullOrEmpty()) {
                llObjectStep.visibility = GONE
            } else {
                llObjectStep.visibility = VISIBLE
                tvObject.text = bean.object_title

                if (bean.step_title.isNullOrEmpty()) {
                    llStepLine.visibility = GONE
                } else {
                    tvStep.text = bean.step_title
                    llStepLine.visibility = VISIBLE
                }
            }

            Utils.setImageViewSquare(context!!, rlImages)

            tvTime.text = Utils.convertFromDate(bean.register_date)

            tvContents.text = bean.content

            tvCheering.text = bean.like_count.toString()
            tvComment.text = bean.comment_count.toString()

            // todo : 여기 bean.post_type 에 맞게 해야함 서버관련 문제 때문에 상의 후 진행
            ivComment.setOnClickListener(View.OnClickListener {

            })
            llComment.setOnClickListener(View.OnClickListener {

            })


        }
    }

    /**
     * On Refresh
     */
    override fun onRefresh() {
        getTimeLineData()
    }
}