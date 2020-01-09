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
import androidx.viewpager.widget.ViewPager
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BasePagerAdapter
import com.truevalue.dreamappeal.bean.BeanBlueprintAnO
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_page.*
import kotlinx.android.synthetic.main.fragment_add_page.pager_image
import kotlinx.android.synthetic.main.fragment_add_page.rl_images
import kotlinx.android.synthetic.main.fragment_add_page.tv_indicator
import kotlinx.android.synthetic.main.fragment_dream_title.*
import okhttp3.Call
import org.json.JSONObject

class FragmentAddPage : BaseFragment() {

    private var mViewType: String = ""
    private var mBeanAnO: BeanBlueprintAnO? = null
    private var mObjectIdx: Int = -1
    private var mStepIdx: Int = -1
    private var mAdapter: BasePagerAdapter<String>? = null
    private var mContents: String? = null

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

        /**
         * 실현 성과 수정
         * 상세 추가
         */
        fun newInstance(view_type: String, object_idx: Int): FragmentAddPage {
            val fragment = FragmentAddPage()
            fragment.mViewType = view_type
            fragment.mObjectIdx = object_idx
            return fragment
        }

        /**
         * 실현 성과 수정
         * 상세 추가
         */
        fun newInstance(view_type: String, object_idx: Int, contents: String): FragmentAddPage {
            val fragment = FragmentAddPage()
            fragment.mViewType = view_type
            fragment.mObjectIdx = object_idx
            fragment.mContents = contents
            return fragment
        }


        /**
         * 상세 수정 / 삭제
         */
        fun newInstance(view_type: String, object_idx: Int, step_idx: Int): FragmentAddPage {
            val fragment = FragmentAddPage()
            fragment.mViewType = view_type
            fragment.mObjectIdx = object_idx
            fragment.mStepIdx = step_idx
            return fragment
        }

        /**
         * 상세 수정 / 삭제
         */
        fun newInstance(
            view_type: String,
            object_idx: Int,
            step_idx: Int,
            contents: String
        ): FragmentAddPage {
            val fragment = FragmentAddPage()
            fragment.mViewType = view_type
            fragment.mObjectIdx = object_idx
            fragment.mStepIdx = step_idx
            fragment.mContents = contents
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
        val exListener = object : DAHttpCallback {
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
        }

        val ex_idx: Int = when (mViewType) {
            VIEW_TYPE_ADD_ABILITY -> 1
            VIEW_TYPE_ADD_OPPORTUNITY -> 1
            VIEW_TYPE_EDIT_ABILITY -> 1
            VIEW_TYPE_EDIT_OPPORTUNITY -> 1
            VIEW_TYPE_ADD_STEP -> 1
            VIEW_TYPE_EDIT_STEP -> 1
            VIEW_TYPE_ADD_STEP_DETAIL -> 2
            VIEW_TYPE_EDIT_STEP_DETAIL -> 2
            else -> 1
        }

        if (mViewType == VIEW_TYPE_ADD_ABILITY ||
            mViewType == VIEW_TYPE_EDIT_ABILITY ||
            mViewType == VIEW_TYPE_ADD_OPPORTUNITY ||
            mViewType == VIEW_TYPE_EDIT_OPPORTUNITY
        ) {

            if (mViewType == VIEW_TYPE_ADD_OPPORTUNITY ||
                mViewType == VIEW_TYPE_EDIT_OPPORTUNITY
            ) {
                DAClient.opportunityExampleImage(ex_idx, exListener)
            } else {
                DAClient.abilityExampleImage(ex_idx, exListener)
            }
        } else {
            DAClient.objectExampleImage(ex_idx, exListener)
        }

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
            imm.showSoftInput(et_contents, 0)
            tv_default.visibility = GONE
        })

        // 데이터 바인드
        if (mBeanAnO != null) {
            et_contents.setText(mBeanAnO!!.contents)
            tv_default.visibility = View.GONE
        }

        if (mContents != null) {
            et_contents.setText(mContents)
            tv_default.visibility = GONE
        }

        Utils.setImageViewSquare(context!!, rl_images, 3, 4)
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
                iv_back_black -> (activity as ActivityMain).onBackPressed(false)
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
                    addObjectStepDetail(mObjectIdx, contents)
                }
                VIEW_TYPE_EDIT_STEP -> {
                    updateObjectStep(mObjectIdx, contents)
                }
                VIEW_TYPE_EDIT_STEP_DETAIL -> {
                    updateObjectStepDetail(mStepIdx, mObjectIdx, contents)
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
                        (activity as ActivityMain).onBackPressed(false)
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
                        (activity as ActivityMain).onBackPressed(false)
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
                        (activity as ActivityMain).onBackPressed(false)
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
                        (activity as ActivityMain).onBackPressed(false)
                    }
                }
            }
        })
    }

    /**
     * Http
     * 실천 목표 추가
     */
    private fun addObjectStep(contents: String) {
        DAClient.addObject(contents, object : DAHttpCallback {
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
                        (activity as ActivityMain).onBackPressed(true)
                    }
                }
            }
        })
    }

    /**
     * Http
     * 실천 목표 수정
     */
    private fun updateObjectStep(object_idx: Int, contents: String) {
        DAClient.updateObject(object_idx,
            contents,
            null,
            null,
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
                            (activity as ActivityMain).onBackPressed(false)
                        }
                    }
                }
            })
    }

    /**
     * Http
     * 실천 목표 상세 추가
     */
    private fun addObjectStepDetail(object_idx: Int, title: String) {

        DAClient.addObjectStepDetail(object_idx, title, object : DAHttpCallback {
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
                        (activity as ActivityMain).onBackPressed(false)
                    }
                }
            }
        })
    }

    /**
     * Http
     * 실천 목표 상세 수정
     */
    private fun updateObjectStepDetail(step_idx: Int, object_idx: Int, title: String) {

        DAClient.updateObjectStepDetail(step_idx, object_idx, title, object : DAHttpCallback {
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
                        (activity as ActivityMain).onBackPressed(false)
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