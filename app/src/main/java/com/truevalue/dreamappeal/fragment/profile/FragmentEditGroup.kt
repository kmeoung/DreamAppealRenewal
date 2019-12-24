package com.truevalue.dreamappeal.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMyProfileContainer
import com.truevalue.dreamappeal.bean.BeanProfileGroup
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Param
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_edit_group_info.*
import okhttp3.Call
import java.text.SimpleDateFormat
import java.util.*

class FragmentEditGroup : BaseFragment() {

    private var mClass: Int = -1
    private var mBean: BeanProfileGroup? = null

    /**
     * class 각 숫자의 의미
     */

//    {
//        groupName: String,
//        position: String,
//        class: Number,
//            start_date: String,
//        end_date: String,
//        description: String,
//    }

    companion object {
        fun newInstance(bean: BeanProfileGroup?): FragmentEditGroup {
            var fragment = FragmentEditGroup()
            fragment.mBean = bean
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_edit_group_info, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Init View
        initView()
        // Init data
        initData()
        // View On Click Listener
        onClickView()

        // todo : 나중에 dropdown으로 변경이 필요함
        setSpinner()
    }


    /**
     * InitData
     */
    private fun initData() {
        if (mBean != null) {
            et_group_name.setText(mBean!!.groupName)

            et_group_rank.setText(mBean!!.position)

            tv_sort.text = getString(
                when (mBean!!.Class) {
                    0 -> R.string.str_class_company
                    1 -> R.string.str_class_school
                    2 -> R.string.str_class_circle
                    3 -> R.string.str_class_group
                    else -> R.string.str_class_group
                }
            )
            mClass = mBean!!.Class

            val sdf = SimpleDateFormat("yyyy-MM")
            val sDate = sdf.parse(mBean!!.start_date)
            val eDate = sdf.parse(mBean!!.end_date)

            val cal = Calendar.getInstance()
            cal.time = sDate
            tv_start_year.text = String.format("%04d", cal.get(Calendar.YEAR))
            tv_start_month.text = String.format("%02d", cal.get(Calendar.MONTH) + 1)
            cal.time = eDate
            tv_end_year.text = String.format("%04d", cal.get(Calendar.YEAR))
            tv_end_month.text = String.format("%02d", cal.get(Calendar.MONTH) + 1)

            et_detail_info.setText(mBean!!.description)

        }
    }

    /**
     * View 초기화
     */
    private fun initView() {
        // 상단 바 설정
        (activity as ActivityMyProfileContainer).iv_back_black.visibility = View.GONE
        (activity as ActivityMyProfileContainer).iv_back_blue.visibility = View.GONE
        (activity as ActivityMyProfileContainer).iv_check.visibility = View.VISIBLE
        (activity as ActivityMyProfileContainer).iv_close.visibility = View.VISIBLE
        // todo : 여기 추가 수정 따로 이씀 header도 다름
        (activity as ActivityMyProfileContainer).tv_title.text =
            getString(R.string.str_add_group_info)
    }

    /**
     * 분류 버튼 View 설정
     * class 각 숫자의 의미
     *  0 : 직장
     *  1 : 학교
     *  2 : 동아리
     *  3 : 단체
     */
    private fun setClassView() {
        val popupMenu = PopupMenu(context!!, tv_sort)
        popupMenu.menu.add(getString(R.string.str_class_company))
        popupMenu.menu.add(getString(R.string.str_class_school))
        popupMenu.menu.add(getString(R.string.str_class_circle))
        popupMenu.menu.add(getString(R.string.str_class_group))

        popupMenu.setOnMenuItemClickListener {
            mClass = when (it.title) {
                getString(R.string.str_class_company) -> 0
                getString(R.string.str_class_school) -> 1
                getString(R.string.str_class_circle) -> 2
                getString(R.string.str_class_group) -> 3
                else -> -1
            }
            tv_sort.text = it.title
            false
        }
        popupMenu.show()
    }

    /**
     * 년도 설정
     */
    private fun setYear(view: TextView) {
        val popupMenu = PopupMenu(context!!, view)
        for (i in Calendar.getInstance().get(Calendar.YEAR)..Calendar.getInstance().get(Calendar.YEAR) + 2) {
            popupMenu.menu.add(i.toString())
        }

        popupMenu.setOnMenuItemClickListener {
            view.text = it.title
            false
        }
        popupMenu.show()
    }

    /**
     * 월 설정
     */
    private fun setMonth(view: TextView) {
        val popupMenu = PopupMenu(context!!, view)
        for (i in 1..12) {
            popupMenu.menu.add(i.toString())
        }

        popupMenu.setOnMenuItemClickListener {
            view.text = it.title
            false
        }
        popupMenu.show()
    }

    // todo : 나중에 변경이 필요합니다.
    private fun setSpinner() {
//
//        val startYearList = ArrayList<String>()
//        for(i in Calendar.getInstance().get(Calendar.YEAR) .. Calendar.getInstance().get(Calendar.YEAR) + 2){
//            startYearList.add(i.toString())
//        }
//        sp_start_year.adapter = ArrayAdapter<String>(context,R.layout.support_simple_spinner_dropdown_item,startYearList)
//        sp_start_year.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {}
//        }
//
//        sp_end_year.adapter = ArrayAdapter<String>(context,R.layout.support_simple_spinner_dropdown_item,startYearList)
//        sp_end_year.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//
//            }
//            override fun onNothingSelected(p0: AdapterView<*>?) {}
//        }
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                (activity as ActivityMyProfileContainer).iv_close -> activity!!.onBackPressed()
                (activity as ActivityMyProfileContainer).iv_check -> {
                    if (iv_check.isSelected) {
                        // todo : 다시 설정해야 함
                        if (mBean != null) editGroupInfo()
                        else addGroupInfo()
                    }
                }
                tv_sort -> setClassView()// 뷴류 설정
                tv_start_year -> setYear(tv_start_year)
                tv_end_year -> setYear(tv_end_year)
                tv_start_month -> setMonth(tv_start_month)
                tv_end_month -> setMonth(tv_end_month)
            }
        }
        (activity as ActivityMyProfileContainer).iv_close.setOnClickListener(listener)
        (activity as ActivityMyProfileContainer).iv_check.setOnClickListener(listener)
        tv_sort.setOnClickListener(listener)
        tv_start_year.setOnClickListener(listener)
        tv_end_year.setOnClickListener(listener)
        tv_start_month.setOnClickListener(listener)
        tv_end_month.setOnClickListener(listener)
    }

    /**
     * Http
     * 소속정보 추가
     */
    private fun addGroupInfo() {

        val groupName = et_group_name.text.toString()
        val position = et_group_rank.text.toString()
        val Class = mClass
        val start_year = Integer.parseInt(tv_start_year.text.toString())
        val start_month = Integer.parseInt(tv_start_month.text.toString())
        val end_year = Integer.parseInt(tv_end_year.text.toString())
        val end_month = Integer.parseInt(tv_end_month.text.toString())
        val start_date = String.format("%04d-%02d", start_year, start_month)
        val end_date = String.format("%04d-%02d", end_year, end_month)
        val description = et_detail_info.text.toString()

        // todo : 페이지를 완성해야 합니다
        DAClient.addUsersGroup(
            groupName,
            position,
            Class,
            start_date,
            end_date,
            description,
            object : DAHttpCallback {
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    if (context != null) {
                        Toast.makeText(
                            context!!.applicationContext,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()

                        if (code == DAClient.SUCCESS) {
                            activity!!.onBackPressed()
                        }
                    }
                }
            })
    }


    /**
     * Http
     * 소속정보 수정
     */
    private fun editGroupInfo() {

        val idx = mBean!!.idx
        val groupName = et_group_name.text.toString()
        val position = et_group_rank.text.toString()
        val Class = mClass
        val start_year = Integer.parseInt(tv_start_year.text.toString())
        val start_month = Integer.parseInt(tv_start_month.text.toString())
        val end_year = Integer.parseInt(tv_end_year.text.toString())
        val end_month = Integer.parseInt(tv_end_month.text.toString())
        val start_date = String.format("%04d-%02d", start_year, start_month)
        val end_date = String.format("%04d-%02d", end_year, end_month)
        val description = et_detail_info.text.toString()

        // todo : 페이지를 완성해야 합니다
        DAClient.editUsersGroup(
            idx,
            groupName,
            position,
            Class,
            start_date,
            end_date,
            description,
            object : DAHttpCallback {
                override fun onResponse(
                    call: Call,
                    serverCode: Int,
                    body: String,
                    code: String,
                    message: String
                ) {
                    if (context != null) {
                        Toast.makeText(
                            context!!.applicationContext,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()

                        if (code == DAClient.SUCCESS) {
                            activity!!.onBackPressed()
                        }
                    }
                }
            })
    }


}