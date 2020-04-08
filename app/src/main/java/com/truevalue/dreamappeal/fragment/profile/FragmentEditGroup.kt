package com.truevalue.dreamappeal.fragment.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMyProfileContainer
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.bean.BeanProfileGroup
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
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

        (activity as ActivityMyProfileContainer).iv_check.isSelected = isCheck()
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

            val cal = Calendar.getInstance()
            cal.time = sDate
            tv_start_year.text = String.format("%04d", cal.get(Calendar.YEAR))
            tv_start_month.text = String.format("%02d", cal.get(Calendar.MONTH) + 1)

            mBean!!.end_date?.let {
                try {
                    val eDate = sdf.parse(it)
                    cal.time = eDate
                    tv_end_year.text = String.format("%04d", cal.get(Calendar.YEAR))
                    tv_end_month.text = String.format("%02d", cal.get(Calendar.MONTH) + 1)
                }catch (e : Exception){ }
            }

            et_detail_info.setText(mBean!!.description)
        }
    }

    /**
     * 체크 확인
     */
    private fun isCheck(): Boolean {
        return (!et_group_name.text.toString().isNullOrEmpty())
                && (!et_group_rank.text.toString().isNullOrEmpty())
                && (!tv_sort.text.toString().isNullOrEmpty())
                && (!tv_start_year.text.toString().isNullOrEmpty())
                && (!tv_start_month.text.toString().isNullOrEmpty())
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

        (activity as ActivityMyProfileContainer).tv_title.text =
            getString(R.string.str_add_group_info)

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                (activity as ActivityMyProfileContainer).iv_check.isSelected = isCheck()
            }
        }

        et_group_name.addTextChangedListener(textWatcher)
        et_group_rank.addTextChangedListener(textWatcher)
        tv_sort.addTextChangedListener(textWatcher)
        tv_start_month.addTextChangedListener(textWatcher)
        tv_start_year.addTextChangedListener(textWatcher)
        tv_end_month.addTextChangedListener(textWatcher)
        tv_end_year.addTextChangedListener(textWatcher)

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
     * 시작 설정
     */
    private fun setStart() {
        val yearSelected: Int
        val monthSelected: Int
        val customTitle = getString(R.string.str_start_date)
        val locale = Locale("ko")

        val calendar = Calendar.getInstance()
        yearSelected =
            if (tv_start_year.text.toString().isNullOrEmpty()) calendar.get(Calendar.YEAR) else tv_start_year.text.toString().toInt()
        monthSelected =
            if (tv_start_month.text.toString().isNullOrEmpty()) calendar.get(Calendar.MONTH) else tv_start_month.text.toString().toInt()

        val dialogFragment = MonthYearPickerDialogFragment
            .getInstance(monthSelected, yearSelected, customTitle, locale)

        dialogFragment.setOnDateSetListener { year, monthOfYear ->
            tv_start_year.text = year.toString()
            tv_start_month.text = (monthOfYear + 1).toString()
        }

        dialogFragment.show(fragmentManager!!, null)
    }

    /**
     * 종료 설정
     */
    private fun setEnd() {
        val yearSelected: Int
        val monthSelected: Int
        val customTitle = getString(R.string.str_end_date)
        val locale = Locale("ko")
//Set default values
        val calendar = Calendar.getInstance()
        yearSelected =
            if (tv_end_year.text.toString().isNullOrEmpty()) calendar.get(Calendar.YEAR) else tv_end_year.text.toString().toInt()
        monthSelected =
            if (tv_end_month.text.toString().isNullOrEmpty()) calendar.get(Calendar.MONTH) else tv_end_month.text.toString().toInt()

        val dialogFragment = MonthYearPickerDialogFragment
            .getInstance(monthSelected, yearSelected, customTitle, locale)

        dialogFragment.setOnDateSetListener { year, monthOfYear ->
            tv_end_year.text = year.toString()
            tv_end_month.text = (monthOfYear + 1).toString()
        }

        dialogFragment.show(fragmentManager!!, null)
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                (activity as ActivityMyProfileContainer).iv_close -> (activity as ActivityMyProfileContainer).onBackPressed()
                (activity as ActivityMyProfileContainer).iv_check -> {
                    if ((activity as ActivityMyProfileContainer).iv_check.isSelected) {
                        if (mBean != null) editGroupInfo()
                        else addGroupInfo()
                    }
                }
                tv_sort -> setClassView()// 뷴류 설정
                tv_start_year -> setStart()
                tv_end_year -> setEnd()
                tv_start_month -> setStart()
                tv_end_month -> setEnd()
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
        val start_date = String.format("%04d-%02d", start_year, start_month)
        var end_date: String? = null
        if (!tv_end_year.text.toString().isNullOrEmpty() && !tv_end_month.text.toString().isNullOrEmpty()) {
            val end_year = Integer.parseInt(tv_end_year.text.toString())
            val end_month = Integer.parseInt(tv_end_month.text.toString())
            end_date = String.format("%04d-%02d", end_year, end_month)
        }
        val description = et_detail_info.text.toString()

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
                            (activity as ActivityMyProfileContainer).onBackPressed()
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
        val start_date = String.format("%04d-%02d", start_year, start_month)
        var end_date: String? = null
        if (!tv_end_year.text.toString().isNullOrEmpty() && !tv_end_month.text.toString().isNullOrEmpty()) {
            val end_year = Integer.parseInt(tv_end_year.text.toString())
            val end_month = Integer.parseInt(tv_end_month.text.toString())
            end_date = String.format("%04d-%02d", end_year, end_month)
        }
        var description : String? = et_detail_info.text.toString()

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
                            (activity as ActivityMyProfileContainer).onBackPressed()
                        }
                    }
                }
            })
    }


}