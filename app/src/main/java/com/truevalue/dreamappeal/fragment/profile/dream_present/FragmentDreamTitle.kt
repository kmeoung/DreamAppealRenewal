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
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.bean.BeanDreamPresent
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_main.*
import kotlinx.android.synthetic.main.action_bar_main.tv_title
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_dream_present.*
import kotlinx.android.synthetic.main.fragment_dream_title.*
import okhttp3.Call

class FragmentDreamTitle : BaseFragment() {

    private var mBean : BeanDreamPresent? = null

    companion object{

        fun newInstance(bean : BeanDreamPresent?) : FragmentDreamTitle{
            val fragment = FragmentDreamTitle()
            fragment.mBean = bean
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
        tv_text_btn.text = getString(R.string.str_commit)
        tv_title.text = getString(R.string.str_title_dream_list)

        // 하이라이팅 설정
        val title_highlight = getString(R.string.str_dream_title_info_highlight)
        tv_dream_title_info.text =
            Utils.replaceTextColor(context, tv_dream_title_info, title_highlight)

        et_value_style.addTextChangedListener(object : TextWatcher{

            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                initRightBtn()
            }
        })

        et_job.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                initRightBtn()
            }
        })

        // 데이터 바인드
        if(mBean != null){
            if(!mBean!!.job.isNullOrEmpty()) et_job.setText(mBean!!.job)
            if(!mBean!!.value_style.isNullOrEmpty()) et_value_style.setText(mBean!!.value_style)
        }
    }

    /**
     * 오른쪽 상단 아이콘 설정
     */
    private fun initRightBtn() {
        iv_check.isSelected = if (isAllInput()) false else true
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
                    if(iv_check.isSelected){
                        commitDreamTitle()
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
    private fun commitDreamTitle(){
        val job = et_job.text.toString()
        val valueStyle = et_value_style.text.toString()
        DAClient.updateProfiles(job,valueStyle,null,null,null,
            object : DAHttpCallback{
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
                            if(activity != null) activity!!.onBackPressed()
                        }
                    }
                }
            })
    }
}