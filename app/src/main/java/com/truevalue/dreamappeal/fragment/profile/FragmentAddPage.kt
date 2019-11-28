package com.truevalue.dreamappeal.fragment.profile

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.bean.BeanBlueprintAnO
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_add_page.*
import kotlinx.android.synthetic.main.fragment_merit_and_motive.*
import okhttp3.Call

class FragmentAddPage : BaseFragment() {

    private var mViewType: String = ""
    private var mBeanAnO: BeanBlueprintAnO? = null

    companion object {
        val VIEW_TYPE_ADD_ABILITY = "VIEW_TYPE_ADD_ABILITY"
        val VIEW_TYPE_ADD_OPPORTUNITY = "VIEW_TYPE_ADD_OPPORTUNITY"
        val VIEW_TYPE_EDIT_ABILITY = "VIEW_TYPE_EDIT_ABILITY"
        val VIEW_TYPE_EDIT_OPPORTUNITY = "VIEW_TYPE_EDIT_OPPORTUNITY"
        val VIEW_TYPE_ADD_STEP = "VIEW_TYPE_ADD_STEP"
        val VIEW_TYPE_EDIT_STEP = "VIEW_TYPE_EDIT_STEP"
        val VIEW_TYPE_ADD_STEP_DETAIL = "VIEW_TYPE_ADD_STEP_DETAIL"
        val VIEW_TYPE_EDIT_STEP_DETAIL = "VIEW_TYPE_EDIT_STEP_DETAIL"

        fun newInstance(view_type: String, bean: BeanBlueprintAnO): FragmentAddPage {
            val fragment = FragmentAddPage()
            fragment.mViewType = view_type
            fragment.mBeanAnO = bean
            return fragment
        }

        fun newInstance(view_type: String): FragmentAddPage {
            val fragment = FragmentAddPage()
            fragment.mViewType = view_type
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_page, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰 초기화
        initView()
        // View Click Listener
        onClickView()
    }

    /**
     * View 초기화
     */
    private fun initView() {
        var title = ""
        var default_text = ""
        when (mViewType) {
            VIEW_TYPE_ADD_ABILITY -> {
                title = getString(R.string.str_add_ability)
                default_text = getString(R.string.str_default_add_ability)
            }
            VIEW_TYPE_ADD_OPPORTUNITY -> {
                title = getString(R.string.str_add_opportunity)
                default_text = getString(R.string.str_default_add_opportunity)
            }
            VIEW_TYPE_EDIT_ABILITY -> {
                title = getString(R.string.str_edit_ability)
                default_text = getString(R.string.str_default_add_ability)
            }
            VIEW_TYPE_EDIT_OPPORTUNITY -> {
                title = getString(R.string.str_edit_opportunity)
                default_text = getString(R.string.str_default_add_opportunity)
            }
            VIEW_TYPE_ADD_STEP -> {
                title = getString(R.string.str_add_oject_step)
                default_text = getString(R.string.str_default_object_step)
            }
            VIEW_TYPE_ADD_STEP_DETAIL -> {
                title = getString(R.string.str_add_object_step_detail_title)
                default_text = getString(R.string.str_default_object_step_detail)
            }
            VIEW_TYPE_EDIT_STEP -> {
                title = getString(R.string.str_edit_object_step)
                default_text = getString(R.string.str_default_object_step)
            }
            VIEW_TYPE_EDIT_STEP_DETAIL -> {
                title = getString(R.string.str_edit_object_step_detail)
                default_text = getString(R.string.str_default_object_step_detail)
            }
        }
        tv_title.text = title
        tv_default.text = default_text
        iv_check.visibility = VISIBLE

        et_contents.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                initRightBtn()
            }
        })

        // 처음 Hint 글자 안보이게 하고 Focus잡기
        tv_default.setOnClickListener(View.OnClickListener {
            et_contents.isFocusableInTouchMode = true
            et_contents.requestFocus()
            val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(tv_init_merit_and_motive, 0)
            tv_default.visibility = GONE
        })

        // 데이터 바인드
        if (mBeanAnO != null) {
            et_contents.setText(mBeanAnO!!.contents)
            tv_default.visibility = View.GONE
        }
    }

    /**
     * 오른쪽 상단 아이콘 설정
     */
    private fun initRightBtn() {
        iv_check.isSelected = !isAllInput()
        if (isAllInput()) tv_default.visibility = View.GONE
    }

    private fun isAllInput(): Boolean {
        return TextUtils.isEmpty(et_contents.text.toString())
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_check -> {
                    clickCheck()
                }
                iv_back_black -> (activity as ActivityMain).onBackPressed()
            }
        }
        iv_check.setOnClickListener(listener)
        iv_back_black.setOnClickListener(listener)
    }

    private fun clickCheck() {
        val contents = et_contents.text.toString()
        if (iv_check.isSelected) {
            when (mViewType) {
                VIEW_TYPE_ADD_ABILITY -> {
                    addAbility(contents)
                }
                VIEW_TYPE_ADD_OPPORTUNITY -> {
                    addOpportunity(contents)
                }
                VIEW_TYPE_EDIT_ABILITY -> {
                    updateAbility(mBeanAnO!!.idx, contents)
                }
                VIEW_TYPE_EDIT_OPPORTUNITY -> {
                    updateOpportunity(mBeanAnO!!.idx, contents)
                }
                VIEW_TYPE_ADD_STEP -> {
                    addObjectStep(contents)
                }
                VIEW_TYPE_ADD_STEP_DETAIL -> {

                }
                VIEW_TYPE_EDIT_STEP -> {

                }
                VIEW_TYPE_EDIT_STEP_DETAIL -> {

                }
            }
        }
    }

    /**
     * Http
     * 갖출 능력 등록
     */
    private fun addAbility(contents: String) {
        DAClient.addAbility(contents, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (context != null) {
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()

                    if (code == DAClient.SUCCESS) {
                        activity!!.onBackPressed()
                    }
                }
            }
        })
    }

    /**
     * Http
     * 민들고픈 기회 등록
     */
    private fun addOpportunity(contents: String) {
        DAClient.addOpportunity(contents, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (context != null) {
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()

                    if (code == DAClient.SUCCESS) {
                        activity!!.onBackPressed()
                    }
                }
            }
        })
    }


    /**
     * Http
     * 갖출 능력 수정
     */
    private fun updateAbility(idx: Int, contents: String) {
        DAClient.updateAbility(idx, contents, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (context != null) {
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()

                    if (code == DAClient.SUCCESS) {
                        activity!!.onBackPressed()
                    }
                }
            }
        })
    }

    /**
     * Http
     * 민들고픈 기회 수정
     */
    private fun updateOpportunity(idx: Int, contents: String) {
        DAClient.updateOpportunity(idx, contents, object : DAHttpCallback {
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (context != null) {
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()

                    if (code == DAClient.SUCCESS) {
                        activity!!.onBackPressed()
                    }
                }
            }
        })
    }

    /**
     * Http
     * 실천 목표 추가
     */
    private fun addObjectStep(contents : String){
        DAClient.addObject(contents,object : DAHttpCallback{
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (context != null) {
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()

                    if (code == DAClient.SUCCESS) {
                        activity!!.onBackPressed()
                    }
                }
            }
        })
    }

    /**
     * Http
     * 실천 목표 수정
     */
    private fun updateObjectStep(object_idx : Int,contents : String){
        DAClient.updateObject(object_idx,
            contents,
            null,
            null,
            null,
            object : DAHttpCallback{
            override fun onResponse(
                call: Call,
                serverCode: Int,
                body: String,
                code: String,
                message: String
            ) {
                if (context != null) {
                    Toast.makeText(context!!.applicationContext, message, Toast.LENGTH_SHORT).show()

                    if (code == DAClient.SUCCESS) {
                        activity!!.onBackPressed()
                    }
                }
            }
        })
    }


}