package com.truevalue.dreamappeal.fragment.dream_board

import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.*
import com.truevalue.dreamappeal.bean.BeanAttenAction
import com.truevalue.dreamappeal.bean.BeanAttenAppealer
import com.truevalue.dreamappeal.bean.BeanAttenIdea
import com.truevalue.dreamappeal.bean.BeanPopular
import com.truevalue.dreamappeal.fragment.profile.FragmentProfile
import com.truevalue.dreamappeal.fragment.profile.blueprint.FragmentActionPost
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.fragment_popular.*
import okhttp3.Call
import org.json.JSONException
import org.json.JSONObject


class FragmentPopular : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_popular, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // rv adapter 초기화
        initAdapter()
        // Data bind
        bindData()
    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {

        mAdapter = BaseRecyclerViewAdapter(rvPopularListener)
        rv_popular.adapter = mAdapter
        rv_popular.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context!!)
        layoutManager.isAutoMeasureEnabled = true
        rv_popular.isNestedScrollingEnabled = false
        rv_popular.layoutManager = layoutManager
    }

    /**
     * Popular Bind Data
     */
    private fun bindData() {

        DAClient.getDreamBoardPopular(object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (code == DAClient.SUCCESS) {
                    val json = JSONObject(body)

                    val appealer = ArrayList<Any>()
                    try {
                        val profile_rank = json.getJSONArray("profile_rank")
                        for (i in 0 until profile_rank.length()) {
                            val bean = Gson().fromJson<BeanAttenAppealer>(
                                profile_rank
                                    .getJSONObject(i)
                                    .toString(),
                                BeanAttenAppealer::class.java
                            )
                            appealer.add(bean)
                        }
                    } catch (e: JSONException) {
                    } finally {
                        mAdapter!!.add(
                            BeanPopular(
                                getString(R.string.str_attention_appealer),
                                appealer,
                                R.layout.listitem_atten_appealer
                            )
                        )
                    }
                    val action = ArrayList<Any>()
                    try {
                        val action_rank = json.getJSONArray("action_rank")
                        for (i in 0 until action_rank.length()) {
                            val bean = Gson().fromJson<BeanAttenAction>(
                                action_rank
                                    .getJSONObject(i)
                                    .toString(),
                                BeanAttenAction::class.java
                            )
                            action.add(bean)
                        }
                    } catch (e: JSONException) {
                    } finally {
                        mAdapter!!.add(
                            BeanPopular(
                                getString(R.string.str_attention_action),
                                action,
                                R.layout.listitem_atten_action
                            )
                        )
                    }
                    val idea = ArrayList<Any>()
                    try {
                        val idea_rank = json.getJSONArray("idea_rank")
                        for (i in 0 until idea_rank.length()) {
                            val bean = Gson().fromJson<BeanAttenIdea>(
                                idea_rank
                                    .getJSONObject(i)
                                    .toString(),
                                BeanAttenIdea::class.java
                            )
                            idea.add(bean)
                        }
                    } catch (e: JSONException) {
                    } finally {
                        mAdapter!!.add(
                            BeanPopular(
                                getString(R.string.str_attention_idea),
                                idea,
                                R.layout.listitem_atten_idea
                            )
                        )
                    }

                } else {
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private val rvPopularListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder.newInstance(R.layout.listitem_popular, parent, false)
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
            val bean = mAdapter!!.get(i) as BeanPopular

            val tvAttention = h.getItemView<TextView>(R.id.tv_attention)
            val pagerAttention = h.getItemView<ViewPager>(R.id.pager_attention)
            tvAttention.text = bean.title

            if (bean.images!!.size > 0) {

                val adapter = BaseBookPagerAdapter(
                    context!!,
                    bean.images!!,
                    bean.itemId,
                    object : BaseBookPagerAdapter.IOBookPagerListener {
                        override fun onBindViewPager(pagerBean: Any, view: View) {

                            when (pagerBean) {
                                is BeanAttenAppealer -> {
                                    val ivBookCover =
                                        view.findViewById<ImageView>(R.id.iv_book_cover)
                                    val tvValueStyle =
                                        view.findViewById<TextView>(R.id.tv_value_style)
                                    val tvJob = view.findViewById<TextView>(R.id.tv_job)
                                    val tvName = view.findViewById<TextView>(R.id.tv_name)
                                    val tvCheering = view.findViewById<TextView>(R.id.tv_cheering)

                                    Glide.with(context!!)
                                        .load(pagerBean.image)
                                        .centerCrop()
                                        .placeholder(R.drawable.ic_image_white)
                                        .into(ivBookCover)

                                    tvValueStyle.text = pagerBean.value_style
                                    tvJob.text = pagerBean.job
                                    tvName.text = pagerBean.nickname
                                    tvCheering.text = "${Utils.getCommentView(pagerBean.count)}개"

                                    view.setOnClickListener(View.OnClickListener {
                                        (activity as ActivityMain)
                                            .replaceFragment(FragmentProfile
                                                .newInstance(pagerBean.idx),
                                                addToBack = true,
                                                isMainRefresh = false)
                                    })
                                }
                                is BeanAttenAction -> {
                                    val ivBookCover =
                                        view.findViewById<ImageView>(R.id.iv_book_cover)
                                    val tvTitle = view.findViewById<TextView>(R.id.tv_title)
                                    val tvSubTitle = view.findViewById<TextView>(R.id.tv_sub_title)

                                    Glide.with(context!!)
                                        .load(pagerBean.thumbnail_image)
                                        .centerCrop()
                                        .placeholder(R.drawable.ic_image_white)
                                        .into(ivBookCover)

                                    tvTitle.text = pagerBean.object_name
                                    tvSubTitle.text = "${pagerBean.value_style} ${pagerBean.job}"

                                    view.setOnClickListener(View.OnClickListener {
                                        (activity as ActivityMain)
                                            .replaceFragment(FragmentActionPost
                                                .newInstance(pagerBean.idx,
                                                    Comm_Prefs.getUserProfileIndex()),
                                                addToBack = true,
                                                isMainRefresh = false)
                                    })
                                }
                                is BeanAttenIdea -> {
                                    val ivBookCover =
                                        view.findViewById<ImageView>(R.id.iv_book_cover)
                                    val tvTitle = view.findViewById<TextView>(R.id.tv_title)

                                    Glide.with(context!!)
                                        .load(pagerBean.thumbnail_image)
                                        .centerCrop()
                                        .placeholder(R.drawable.ic_image_white)
                                        .into(ivBookCover)

                                    tvTitle.text = "${pagerBean.value_style} ${pagerBean.job}"

                                    view.setOnClickListener(View.OnClickListener {
                                        (activity as ActivityMain)
                                            .replaceFragment(FragmentActionPost
                                                .newInstance(pagerBean.idx,
                                                    Comm_Prefs.getUserProfileIndex(),
                                                    FragmentActionPost.TYPE_DREAM_NOTE_IDEA),
                                                addToBack = true,
                                                isMainRefresh = false)
                                    })
                                }
                            }
                        }
                    })
                pagerAttention.adapter = adapter
            } else {
                pagerAttention.visibility = GONE
            }


//            pagerAttention.setOnTouchListener(touchListener)

        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}