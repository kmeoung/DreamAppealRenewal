package com.truevalue.dreamappeal.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.bean.BeanBlueprintAnO
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_add_page.*
import okhttp3.Call

class FragmentAddPage : BaseFragment() {

    private var mViewType: String = ""
    private var mBeanAnO: BeanBlueprintAnO? = null

    companion object {
        val VIEW_TYPE_ADD_ABILITY = "VIEW_TYPE_ADD_ABILITY"
        val VIEW_TYPE_ADD_OPPORTUNITY = "VIEW_TYPE_ADD_OPPORTUNITY"
        val VIEW_TYPE_EDIT_ABILITY = "VIEW_TYPE_EDIT_ABILITY"
        val VIEW_TYPE_EDIT_OPPORTUNITY = "VIEW_TYPE_EDIT_OPPORTUNITY"

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
        // todo : 여기서 나머지 분기처리 필요
        iv_check.visibility = VISIBLE

        if (mBeanAnO != null) {
            et_contents.setText(mBeanAnO!!.contents)
        }
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
                iv_back_black -> (activity as ActivityMain).onBackPressed(true)
            }
        }
        iv_check.setOnClickListener(listener)
        iv_back_black.setOnClickListener(listener)
    }

    private fun clickCheck() {
        val contents = et_contents.text.toString()
        when (mViewType) {
            VIEW_TYPE_ADD_ABILITY -> {
                addAbility(contents)
            }
            VIEW_TYPE_ADD_OPPORTUNITY -> {
                addOpportunity(contents)
            }
            VIEW_TYPE_EDIT_ABILITY -> {
                updateAbility(mBeanAnO!!.idx,contents)
            }
            VIEW_TYPE_EDIT_OPPORTUNITY -> {
                updateOpportunity(mBeanAnO!!.idx,contents)
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


}