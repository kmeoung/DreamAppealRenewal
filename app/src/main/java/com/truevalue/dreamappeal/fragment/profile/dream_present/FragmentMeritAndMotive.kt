package com.truevalue.dreamappeal.fragment.profile.dream_present

import android.content.Context
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
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BasePagerAdapter
import com.truevalue.dreamappeal.bean.BeanDreamPresent
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_main.tv_title
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_dream_title.*
import kotlinx.android.synthetic.main.fragment_merit_and_motive.*
import kotlinx.android.synthetic.main.fragment_merit_and_motive.pager_image
import kotlinx.android.synthetic.main.fragment_merit_and_motive.tv_indicator
import okhttp3.Call
import org.json.JSONObject

class FragmentMeritAndMotive : BaseFragment() {

    private var mBean: BeanDreamPresent? = null
    private var mAdapter : BasePagerAdapter<String>? = null
    companion object {

        /**
         * 데이터 미리 저장
         */
        fun newInstance(bean: BeanDreamPresent?): FragmentMeritAndMotive {
            val fragment = FragmentMeritAndMotive()
            fragment.mBean = bean
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_merit_and_motive, container, false)

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
        DAClient.profileExampleImage(3,object : DAHttpCallback{
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
        val default_merit = getString(R.string.str_default_merit)
        val default_morive = getString(R.string.str_default_motive)

        var spMerit = Utils.replaceTextColor(context, default_merit, getString(R.string.str_merit))
        var spMotive =
            Utils.replaceTextColor(context, default_morive, getString(R.string.str_motive))

        tv_title.text = TextUtils.concat(spMerit, " ", spMotive)

        et_merit_and_motive.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                initRightBtn()
            }
        })

        // 처음 Hint 글자 안보이게 하고 Focus잡기
        tv_init_merit_and_motive.setOnClickListener(OnClickListener {
            tv_init_merit_and_motive.isFocusableInTouchMode = true
            tv_init_merit_and_motive.requestFocus()
            val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(tv_init_merit_and_motive, 0)
            tv_init_merit_and_motive.visibility = GONE
        })

        // 데이터 바인드
        if (mBean != null) {
            if (!mBean!!.meritNmotive.isNullOrEmpty()) et_merit_and_motive.setText(mBean!!.meritNmotive)
            tv_init_merit_and_motive.visibility = GONE
        }
    }

    /**
     * 오른쪽 상단 아이콘 설정
     */
    private fun initRightBtn() {
        iv_check.isSelected = !isAllInput()
        if(isAllInput()) tv_init_merit_and_motive.visibility = GONE
    }

    private fun isAllInput(): Boolean {
        return TextUtils.isEmpty(et_merit_and_motive.text.toString())
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
                        commitDreamMeritAndMotive()
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
    private fun commitDreamMeritAndMotive() {
        val meritAndMotive = et_merit_and_motive.text.toString()

        DAClient.updateProfiles(null,
            null,
            null,
           null,
            meritAndMotive,
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
}