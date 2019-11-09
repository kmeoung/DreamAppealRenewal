package com.truevalue.dreamappeal.fragment.profile

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMyProfileContainer
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_normal_profile.*
import kotlinx.android.synthetic.main.fragment_normal_profile_edit.*
import okhttp3.Call
import java.util.*

class FragmentEditGroup : BaseFragment() {

    private val mSCal: Calendar
    private val mECal: Calendar

    init {
        mSCal = Calendar.getInstance()
        mECal = Calendar.getInstance()
    }

    /**
     * class 각 숫자의 의미
     *  0 : 직장
     *  1 : 학교
     *  2 : 동아리
     *  3 : 단체
     */

//    {
//        groupName: String,
//        position: String,
//        class: Number,
//            start_date: String,
//        end_date: String,
//        description: String,
//    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_edit_group_info, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Init View
        initView()
        // View On Click Listener
        onClickView()
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
     * DatePickerDialog
     * 사용자에게 보여줄 시 혹은 데이터를 저장할시에는 Month에 + 1 을 해야합니다
     */
    private fun startDatePicker(calendar : Calendar){
        val dialog = DatePickerDialog(
            context!!,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                var year = String.format("%04d", year)
                var month = String.format("%02d", month + 1)
                var date = String.format("%02d", dayOfMonth)

                tv_date.text = "$year. $month. $date"
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
        dialog.show()
    }

    private fun endDatePicker(calendar : Calendar){
        val dialog = DatePickerDialog(
            context!!,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                var year = String.format("%04d", year)
                var month = String.format("%02d", month + 1)
                var date = String.format("%02d", dayOfMonth)

                tv_date.text = "$year. $month. $date"
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
        dialog.show()
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                (activity as ActivityMyProfileContainer).iv_close -> activity!!.onBackPressed()
                (activity as ActivityMyProfileContainer).iv_check -> {
                    // todo : 페이지를 완성해야 합니다
                    DAClient.addUsersGroup(
                        "테스트2",
                        "대빵2",
                        0,
                        "2019-11-10",
                        "2019-11-10",
                        "ㅎㅎ2",
                        object : DAHttpCallback {
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

                                    }
                                }
                            }
                        })
                }
            }
        }
        (activity as ActivityMyProfileContainer).iv_close.setOnClickListener(listener)
        (activity as ActivityMyProfileContainer).iv_check.setOnClickListener(listener)
    }


}