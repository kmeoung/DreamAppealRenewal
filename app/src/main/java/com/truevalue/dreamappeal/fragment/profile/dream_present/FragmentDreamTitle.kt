package com.truevalue.dreamappeal.fragment.profile.dream_present

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BasePagerAdapter
import com.truevalue.dreamappeal.bean.BeanDreamPresent
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_main.tv_title
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_dream_title.*
import kotlinx.android.synthetic.main.fragment_dream_title.pager_image
import kotlinx.android.synthetic.main.fragment_dream_title.tv_indicator
import kotlinx.android.synthetic.main.fragment_post_detail.*
import okhttp3.Call
import org.json.JSONObject

class FragmentDreamTitle : BaseFragment() {

    private var mBean: BeanDreamPresent? = null
    private var mViewType: String? = null
    private var mAdapter: BasePagerAdapter<String>? = null
    companion object {

        val MODE_NEW_PROFILE = "MODE_NEW_PROFILE"

        /**
         * 데이터 미리 저장
         */
        fun newInstance(bean: BeanDreamPresent?): FragmentDreamTitle {
            val fragment = FragmentDreamTitle()
            fragment.mBean = bean
            return fragment
        }

        /**
         * View 모드 설정
         */
        fun newInstance(mode: String): FragmentDreamTitle {
            val fragment = FragmentDreamTitle()
            fragment.mViewType = mode
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dream_title, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // View 초기화
        initView()
        // View 클릭 리스너
        onClickView()
        // Pager Adapter 초기화
        initAdapter()
    }

    /**
     * Pager Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BasePagerAdapter(context!!)
        pager_image.adapter = mAdapter
        pager_image.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tv_indicator.text = ((position + 1).toString() + " / " + mAdapter!!.getCount())
            }
        })

        getExampleIamges()
    }

    /**
     * Http
     * 예시 이미지 가져오기
     */
    private fun getExampleIamges(){
        DAClient.profileExampleImage(1,object : DAHttpCallback{
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if(code == DAClient.SUCCESS){
                    val json = JSONObject(body)
                    val exUrl = json.getJSONArray("ex_url")

                    tv_indicator.text = (1.toString() + " / " + exUrl.length())

                    mAdapter!!.clear()
                    for(i in 0 until exUrl.length()){
                        val image = exUrl.getJSONObject(i)
                        val url = image.getString("url")
                        mAdapter!!.add(url)
                    }
                    mAdapter!!.notifyDataSetChanged()
                }
            }
        })
    }

    /**
     * Init View
     */
    private fun initView() {
        // action Bar 설정
        iv_back_black.visibility = GONE
        tv_text_btn.visibility = GONE
        iv_close.visibility = VISIBLE
        iv_check.visibility = VISIBLE

        // Default View 키워드 색상 변경
        val default_dream_title = getString(R.string.str_default_dream_title)

        var spDreamTitle = Utils.replaceTextColor(
            context,
            default_dream_title,
            getString(R.string.str_designation)
        )

        tv_title.text = spDreamTitle

        // 하이라이팅 설정
        val title_highlight = getString(R.string.str_dream_title_info_highlight)
        tv_dream_title_info.text =
            Utils.replaceTextColor(context, tv_dream_title_info, title_highlight)

        et_value_style.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                initRightBtn()
            }
        })

        et_job.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                initRightBtn()
            }
        })

        // 데이터 바인드
        if (mBean != null) {
            if (!mBean!!.job.isNullOrEmpty()) et_job.setText(mBean!!.job)
            if (!mBean!!.value_style.isNullOrEmpty()) et_value_style.setText(mBean!!.value_style)
        }
    }

    /**
     * 오른쪽 상단 아이콘 설정
     */
    private fun initRightBtn() {
        iv_check.isSelected = !isAllInput()
    }

    private fun isAllInput(): Boolean {
        return TextUtils.isEmpty(et_job.text.toString()) || TextUtils.isEmpty(et_value_style.text.toString())
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = OnClickListener {
            when (it) {
                iv_close -> activity?.onBackPressed()
                iv_check -> {
                    if (iv_check.isSelected) {

                        if (mViewType.isNullOrEmpty())
                            commitDreamTitle()
                        else if(mViewType.equals(MODE_NEW_PROFILE)){
                            newProfile()
                        }
                    }
                }
            }
        }
        iv_close.setOnClickListener(listener)
        iv_check.setOnClickListener(listener)
    }

    /**
     * 꿈 / 꿈소개 업데이트
     */
    private fun commitDreamTitle() {
        val job = et_job.text.toString()
        val valueStyle = et_value_style.text.toString()
        DAClient.updateProfiles(job, valueStyle, null, null, null,
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
                            (activity as ActivityMain).onBackPressed(true)
                        }
                    }
                }
            })
    }

    /**
     * Profile 생성
     */
    private fun newProfile(){
        val job = et_job.text.toString()
        val valueStyle = et_value_style.text.toString()
        DAClient.addProfiles(job,valueStyle,"", JSONObject(),"",object : DAHttpCallback{
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if(context != null){
                    Toast.makeText(context!!.applicationContext,message,Toast.LENGTH_SHORT).show()

                    if(code == DAClient.SUCCESS){
                        (activity as ActivityMain).onBackPressed(false)
                    }
                }
            }
        })
    }
}