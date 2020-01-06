package com.truevalue.dreamappeal.fragment.profile.dream_present

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_dream_description.*
import okhttp3.Call
import org.json.JSONArray
import org.json.JSONObject


class FragmentDreamDescription : BaseFragment() {

    private var mBean: BeanDreamPresent? = null
    private var mAdapter: BasePagerAdapter<String>? = null

    companion object {

        /**
         * 데이터 미리 저장
         */
        fun newInstance(bean: BeanDreamPresent?): FragmentDreamDescription {
            val fragment = FragmentDreamDescription()
            fragment.mBean = bean
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dream_description, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // View 초기화
        initView()
        // View 클릭 리스너
        onClickView()
        // Pager Adapter 초기화
        initAdapter()
    }

    override fun onResume() {
        super.onResume()
        (activity as ActivityMain).bottom_view.visibility = GONE
    }

    override fun onPause() {
        super.onPause()
        (activity as ActivityMain).bottom_view.visibility = VISIBLE
    }

    /**
     * Pager Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BasePagerAdapter(context!!, true)
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
    private fun getExampleIamges() {
        DAClient.profileExampleImage(2, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (code == DAClient.SUCCESS) {
                    val json = JSONObject(body)
                    val exUrl = json.getJSONArray("ex_url")

                    tv_indicator.text = (1.toString() + " / " + exUrl.length())

                    mAdapter!!.clear()
                    for (i in 0 until exUrl.length()) {
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
        val default_dream_description = getString(R.string.str_default_dream_description)
        var spDreamDescription = Utils.replaceTextColor(
            context,
            default_dream_description,
            getString(R.string.str_explanation)
        )
        tv_title.text = spDreamDescription

        // 하이라이팅 설정
        val title_highlight = getString(R.string.str_dream_description_highlight)
        tv_dream_description_info.text =
            Utils.replaceTextColor(context, tv_dream_description_info, title_highlight)

        val detail_title_highlight = getString(R.string.str_dream_description_detail_highlight)
        tv_dream_description_detail_info.text =
            Utils.replaceTextColor(
                context,
                tv_dream_description_detail_info,
                detail_title_highlight
            )

        et_dream_description.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                initRightBtn()
            }
        })

        et_dream_description_1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                initRightBtn()
            }
        })

        et_dream_description_2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                initRightBtn()
            }
        })

        et_dream_description_3.addTextChangedListener(object : TextWatcher {
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
            if (!mBean!!.description.isNullOrEmpty()) et_dream_description.setText(mBean!!.description)
            if (mBean!!.descriptions.size > 0) {
                for (i in 0 until mBean!!.descriptions.size) {
                    val content = mBean!!.descriptions[i]
                    when (i) {
                        0 -> et_dream_description_1.setText(content)
                        1 -> et_dream_description_2.setText(content)
                        2 -> et_dream_description_3.setText(content)
                    }
                }
            }
        }
    }

    /**
     * 오른쪽 상단 아이콘 설정
     */
    private fun initRightBtn() {
        iv_check.isSelected = !isAllInput()
    }

    private fun isAllInput(): Boolean {
        return TextUtils.isEmpty(et_dream_description.text.toString())
                || (TextUtils.isEmpty(et_dream_description_1.text.toString())
                && TextUtils.isEmpty(et_dream_description_2.text.toString())
                && TextUtils.isEmpty(et_dream_description_3.text.toString()))
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
                        commitDreamDescription()
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
    private fun commitDreamDescription() {
        val dreamDescription = et_dream_description.text.toString()
        val dreamDescription1 = et_dream_description_1.text.toString()
        val dreamDescription2 = et_dream_description_2.text.toString()
        val dreamDescription3 = et_dream_description_3.text.toString()
        val jsonArray = JSONArray()

        if (!dreamDescription1.isNullOrEmpty()) {
            val json = JSONObject()
            json.put("content", dreamDescription1)
            jsonArray.put(json)
        }

        if (!dreamDescription2.isNullOrEmpty()) {
            val json = JSONObject()
            json.put("content", dreamDescription2)
            jsonArray.put(json)
        }

        if (!dreamDescription3.isNullOrEmpty()) {
            val json = JSONObject()
            json.put("content", dreamDescription3)
            jsonArray.put(json)
        }

        DAClient.updateProfiles(null,
            null,
            dreamDescription,
            jsonArray,
            null,
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

    override fun onStop() {
        super.onStop()
        Utils.downKeyBoard(activity!!)
    }
}