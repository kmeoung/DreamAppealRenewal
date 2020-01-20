package com.truevalue.dreamappeal.fragment.dream_board.concern

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
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
import com.truevalue.dreamappeal.fragment.dream_board.FragmentAddBoard
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Prefs
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.bottom_comment_view.*
import kotlinx.android.synthetic.main.fragment_concern_detail.*
import okhttp3.Call
import org.json.JSONObject

class FragmentConcernDetail : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter?
    private var mAdapterImage: BasePagerAdapter?
    private var mConcernIdx: Int?
    private var mBean: BeanConcernDetail?
    private var mIsEdit: Boolean
    private var mUpdateIdx: Int

    init {
        mUpdateIdx = -1
        mAdapter = null
        mAdapterImage = null
        mConcernIdx = -1
        mBean = null
        mIsEdit = false
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

        rl_comment.visibility = GONE
        btn_commit_comment.visibility = VISIBLE
        iv_profile.visibility = GONE
        et_comment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {}

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                btn_commit_comment.isSelected = !et_comment.text.toString().isNullOrEmpty()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
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
                        val bean = Gson().fromJson<BeanConcernDetail>(
                            json.toString(),
                            BeanConcernDetail::class.java
                        )
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

    /**
     * 질문 게시글 데이터 bind
     */
    private fun setConcernDetail(bean: BeanConcernDetail) {
        mBean = bean
        tv_like_cnt.text = bean.post.votes
        tv_concern_title.text = bean.post.title

        tv_indicator.text = "0 / 0"
        bean.images?.let {
            if (it.isNotEmpty()) {
                rl_images.visibility = VISIBLE
                tv_indicator.text = "1 / " + it.size
                for (i in it.indices) {
                    mAdapterImage!!.add(it[i])
                }
                mAdapterImage!!.notifyDataSetChanged()
            } else {
                rl_images.visibility = GONE
            }
        }

        if(bean.post.vote_type == DAClient.VOTE_UP){
            iv_like_up.isSelected = true
            iv_like_down.isSelected = false
        }else if(bean.post.vote_type == DAClient.VOTE_DOWN){
            iv_like_up.isSelected = false
            iv_like_down.isSelected = true
        }else{
            iv_like_up.isSelected = false
            iv_like_down.isSelected = false
        }

        tv_contents.text = bean.post.content
        bean.post_writer.image?.let { image ->
            Glide.with(context!!)
                .load(image)
                .circleCrop()
                .placeholder(R.drawable.drawer_user)
                .into(iv_post_profile)
        }
        tv_user.text =
            "${bean.post_writer.value_style} ${bean.post_writer.job} ${bean.post_writer.nickname}"
        tv_fame.text = bean.post_writer.reputation
        // todo : 아직 지정되지 않음
//        tv_gold
//        tv_silver
//        tv_bronze

        bean.re_posts?.let {
            tv_comment_cnt.text = bean.re_posts?.size.toString()
        } ?: kotlin.run {
            tv_comment_cnt.text = 0.toString()
        }

        mAdapter?.let { adapter ->
            adapter.clear()
            bean.adopted_re_post?.let { adopted ->
                adopted.idx?.let {
                    adapter.add(adopted)
                }

            }

            bean.re_posts?.let {
                for (i in it.indices) {
                    adapter.add(it[i])
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
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_back_black -> (activity as ActivityMain).onBackPressed(false)
                iv_like_up -> {
                    if(mBean!!.post.vote_type != DAClient.VOTE_UP) {
                        updateConcernVote(DAClient.VOTE_UP)
                    }
                }
                iv_like_down -> {
                    if(mBean!!.post.vote_type != DAClient.VOTE_DOWN) {
                        updateConcernVote(DAClient.VOTE_DOWN)
                    }
                }
                iv_post_more -> {
                    showMoreDialog()
                }
                btn_commit_comment -> {
                    if (btn_commit_comment.isSelected) if (mIsEdit) updateReConcern(mUpdateIdx) else addReConcern()
                }
                iv_writer_reply_close -> {
                    initComment()
                }
            }
        }
        iv_back_black.setOnClickListener(listener)
        iv_like_up.setOnClickListener(listener)
        iv_like_down.setOnClickListener(listener)
        iv_post_more.setOnClickListener(listener)
        btn_commit_comment.setOnClickListener(listener)
        iv_writer_reply_close.setOnClickListener(listener)
    }

    /**
     * Http
     * 질문 게시글 추천 / 비추천
     */
    private fun updateConcernVote(side: String) {
        mBean?.let {
            DAClient.updateConcernVote(it.post.idx, side, object : DAHttpCallback {
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    if (code == DAClient.SUCCESS) {
                        getConcernDetail()
                    } else {
                        context?.let { context ->
                            Toast.makeText(
                                context.applicationContext, message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            })
        }
    }

    /**
     * Http
     * 질문 게시글 답글 추천 / 비추천
     */
    private fun updateReConcernVote(side: String, re_idx: Int) {
        DAClient.updateReConcernVote(re_idx, side, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (code == DAClient.SUCCESS) {
                    getConcernDetail()
                } else {
                    context?.let { context ->
                        Toast.makeText(
                            context.applicationContext, message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    /**
     * Http
     * 질문 게시글 답글 채택 / 미채택
     */
    private fun updateReConcernAdopt(re_idx: Int) {
        DAClient.updateReConcernAdoptVote(re_idx, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (code == DAClient.SUCCESS) {
                    getConcernDetail()
                } else {
                    context?.let { context ->
                        Toast.makeText(
                            context.applicationContext, message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    /**
     * Http
     * 답글 생성
     */
    private fun addReConcern() {
        mBean?.let {
            DAClient.addReConcern(it.post.idx, et_comment.text.toString(), object : DAHttpCallback {
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    if (code == DAClient.SUCCESS) {
                        initComment()
                        getConcernDetail()
                    } else {
                        context?.let {context->
                            Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            })
        }
    }

    /**
     * Http
     * 답글 수정
     */
    private fun updateReConcern(re_idx: Int) {
        mBean?.let {
            DAClient.updateReConcern(re_idx, et_comment.text.toString(), object : DAHttpCallback {
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    if (code == DAClient.SUCCESS) {
                        initComment()
                        getConcernDetail()
                    } else {
                        context?.let {context->
                            Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            })
        }
    }

    /**
     * Http
     * 답글 삭제
     */
    private fun deleteReConcern(re_idx: Int) {
        mBean?.let {
            DAClient.deleteConcern(re_idx, object : DAHttpCallback {
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    if (code == DAClient.SUCCESS) {
                        getConcernDetail()
                    } else {
                        context?.let {context->
                            Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            })
        }
    }

    /**
     * 더보기 Dialog 띄우기
     */
    private fun showMoreDialog() {
        val list = arrayOf(
            getString(R.string.str_edit),
            getString(R.string.str_delete)
        )
        val builder =
            AlertDialog.Builder(context)
        builder.setItems(list) { _, i ->
            when (list[i]) {
                getString(R.string.str_edit) -> {
                    mBean?.let {
                        (activity as ActivityMain).replaceFragment(
                            FragmentAddBoard.newInstance(
                                FragmentAddBoard.TYPE_EDIT_CONCERN,
                                it
                            ), addToBack = true, isMainRefresh = false
                        )
                    }
                }

                getString(R.string.str_delete) -> {
                    val builder =
                        AlertDialog.Builder(context)
                            .setTitle(getString(R.string.str_delete_post_title))
                            .setMessage(getString(R.string.str_delete_post_contents))
                            .setPositiveButton(
                                getString(R.string.str_yes)
                            ) { dialog, _ ->
                                deleteConcern()
                                dialog.dismiss()
                            }
                            .setNegativeButton(
                                getString(R.string.str_no)
                            ) { dialog, _ -> dialog.dismiss() }
                    val dialog = builder.create()
                    dialog.show()
                }
            }
        }
        builder.create().show()
    }

    /**
     * Show PopupMenu
     */
    private fun showReConcernPopup(ivMore: View, re_idx: Int,writer: String, contents: String) {
        val popupMenu = PopupMenu(context!!, ivMore)
        popupMenu.menu.add(getString(R.string.str_edit))
        popupMenu.menu.add(getString(R.string.str_delete))

        popupMenu.setOnMenuItemClickListener {
            when (it.title) {
                getString(R.string.str_edit) -> {
                    mIsEdit = true
                    setReplyComment(re_idx,writer, contents)
                }
                getString(R.string.str_delete) -> {
                    val builder =
                        AlertDialog.Builder(context!!)
                            .setTitle(getString(R.string.str_delete_comment_title))
                            .setMessage(getString(R.string.str_delete_comment_contents))
                            .setPositiveButton(
                                getString(R.string.str_yes)
                            ) { dialog, _ ->
                                deleteReConcern(re_idx)
                                dialog.dismiss()
                            }
                            .setNegativeButton(
                                getString(R.string.str_no)
                            ) { dialog, _ -> dialog.dismiss() }
                    val dialog = builder.create()
                    dialog.show()
                }
            }
            false
        }
        popupMenu.show()
    }

    /**
     * Comment Reply 설정
     */
    private fun setReplyComment(re_idx: Int,writer : String, contents: String) {
        ll_writer.visibility = VISIBLE
        tv_writer.text = writer
        et_comment.setText(contents)
        mUpdateIdx = re_idx
    }

    /**
     * 질문 게시글 제거
     */
    private fun deleteConcern() {
        mBean?.let {
            DAClient.deleteConcern(it.post.idx, object : DAHttpCallback {
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    if (code == DAClient.SUCCESS) {
                        (activity as ActivityMain).onBackPressed(false)
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

    /**
     * Comment 초기화
     */
    private fun initComment() {
        mUpdateIdx = -1
        tv_writer.text = ""
        et_comment.setText("")
        mIsEdit = false
        ll_writer.visibility = GONE
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

            context?.let {
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

                if (getItemViewType(i) == RV_TYPE_ADOPTED) {
                    val bean = mAdapter!!.get(i) as AdoptedRePost

                    if (Comm_Prefs.getUserProfileIndex() == mBean?.post?.profile_idx) {
                        tvAdoption.visibility = VISIBLE
                    } else {
                        tvAdoption.visibility = GONE
                    }


                    llBg.setBackgroundColor(ContextCompat.getColor(it, R.color.off_white_two))

                    tvLikeCnt.text = bean.votes
                    tvContents.text = bean.content

                    bean.image?.let { image ->
                        Glide.with(it)
                            .load(image)
                            .circleCrop()
                            .placeholder(R.drawable.drawer_user)
                            .into(ivProfile)
                    }

                    if(bean.auth) {
                        h.itemView.setOnLongClickListener {
                            showReConcernPopup(tvContents, bean.idx!!, bean.nickname, bean.content)
                            true
                        }
                    }

                    tvUser.text = "${bean.value_style} ${bean.job} ${bean.nickname}"
                    tvFame.text = bean.reputation


                    if(bean.vote_type == DAClient.VOTE_UP){
                        ivLikeDown.isSelected = false
                        ivLikeUp.isSelected = true
                    }else if(bean.vote_type == DAClient.VOTE_DOWN){
                        ivLikeDown.isSelected = true
                        ivLikeUp.isSelected = false
                    }else{
                        ivLikeDown.isSelected = false
                        ivLikeUp.isSelected = false
                    }
                    if(bean.vote_type != DAClient.VOTE_UP) {
                        ivLikeUp.setOnClickListener {
                            updateReConcernVote(DAClient.VOTE_UP, bean.idx!!)
                        }
                    }
                    if(bean.vote_type != DAClient.VOTE_DOWN) {
                        ivLikeDown.setOnClickListener {
                            updateReConcernVote(DAClient.VOTE_DOWN, bean.idx!!)
                        }
                    }

                    tvAdoption.setOnClickListener {
                        // 채택 / 채택해제 해제
                        updateReConcernAdopt(bean.idx!!)
                    }

                } else {
                    val bean = mAdapter!!.get(i) as RePost

                    if (Comm_Prefs.getUserProfileIndex() == mBean?.post?.profile_idx) {
                        tvAdoption.visibility =
                            if (mBean?.adopted_re_post?.idx == null) VISIBLE else GONE
                    } else {
                        tvAdoption.visibility = GONE
                    }

                    llBg.setBackgroundColor(ContextCompat.getColor(it, R.color.white))

                    tvLikeCnt.text = bean.votes
                    tvContents.text = bean.content

                    bean.image?.let { image ->
                        Glide.with(it)
                            .load(image)
                            .circleCrop()
                            .placeholder(R.drawable.drawer_user)
                            .into(ivProfile)
                    }

                    if(bean.auth) {
                        h.itemView.setOnLongClickListener {
                            showReConcernPopup(tvContents, bean.idx, bean.nickname, bean.content)
                            true
                        }
                    }

                    if(bean.vote_type == DAClient.VOTE_UP){
                        ivLikeDown.isSelected = false
                        ivLikeUp.isSelected = true
                    }else if(bean.vote_type == DAClient.VOTE_DOWN){
                        ivLikeDown.isSelected = true
                        ivLikeUp.isSelected = false
                    }else{
                        ivLikeDown.isSelected = false
                        ivLikeUp.isSelected = false
                    }
                    if(bean.vote_type != DAClient.VOTE_UP) {
                        ivLikeUp.setOnClickListener {
                            updateReConcernVote(DAClient.VOTE_UP, bean.idx)
                        }
                    }
                    if(bean.vote_type != DAClient.VOTE_DOWN) {
                        ivLikeDown.setOnClickListener {
                            updateReConcernVote(DAClient.VOTE_DOWN, bean.idx)
                        }
                    }

                    tvUser.text = "${bean.value_style} ${bean.job} ${bean.nickname}"
                    tvFame.text = bean.reputation

                    tvAdoption.setOnClickListener {
                        // 채택 / 채택해제 해제
                        updateReConcernAdopt(bean.idx)
                    }
                }
            }


        }

        override fun getItemViewType(i: Int): Int {
            if (mAdapter!!.get(i) is AdoptedRePost) return RV_TYPE_ADOPTED

            return RV_TYPE_ITEM
        }
    }
}