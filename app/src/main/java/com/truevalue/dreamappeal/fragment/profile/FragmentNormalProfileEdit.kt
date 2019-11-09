package com.truevalue.dreamappeal.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMyProfileContainer
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.bean.BeanProfileUser
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_normal_profile.*
import kotlinx.android.synthetic.main.fragment_normal_profile_edit.*

class FragmentNormalProfileEdit : BaseFragment() {

    private var mBean : BeanProfileUser? = null

    companion object{
        fun newInstance(bean : BeanProfileUser?) : FragmentNormalProfileEdit{
            var fragment = FragmentNormalProfileEdit()
            fragment.mBean = bean
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_normal_profile_edit, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    /**
     * View 초기화
     */
    private fun initView() {
        // 상단 바 설정
        (activity as ActivityMyProfileContainer).iv_back_black.visibility = View.VISIBLE
        (activity as ActivityMyProfileContainer).tv_title.text = getString(R.string.str_normal_edit_prifile)
        (activity as ActivityMyProfileContainer).iv_check.visibility = View.VISIBLE

        if(mBean != null) {
            et_name.setText(if(mBean!!.name.isNullOrEmpty()) "" else mBean!!.name)
            et_nickname.setText(if(mBean!!.nickname.isNullOrEmpty()) "" else mBean!!.nickname)
            tv_date.setText(mBean!!.name)
//            tvge.setText(mBean!!.name)
            et_name.setText(mBean!!.name)
        }
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                (activity as ActivityMyProfileContainer).iv_back_black -> activity!!.onBackPressed()

            }
        }

        (activity as ActivityMyProfileContainer).iv_back_blue.setOnClickListener(listener)
        (activity as ActivityMyProfileContainer).iv_check.setOnClickListener(listener)
    }
}