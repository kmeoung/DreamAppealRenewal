package com.truevalue.dreamappeal.fragment.login

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.DatePicker
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.utils.Comm_Param
import kotlinx.android.synthetic.main.action_bar_profile_other.*
import kotlinx.android.synthetic.main.fragment_register.*
import java.util.*

class FragmentRegister : BaseFragment() {
    // true 여성 false 남성
    var isGender: Boolean
    val mCal : Calendar

    init {
        // 남성으로 초기화
        isGender = false
        mCal = Calendar.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_register, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        initView()
        // Click View Listener
        onClickView()
    }

    /**
     * View 초기화
     */
    private fun initView() {
        tv_title.setText(getString(R.string.str_register))
        iv_menu.visibility = GONE
        iv_search.visibility = INVISIBLE
        iv_back.visibility = VISIBLE

        setGenderView()
        val year = mCal.get(Calendar.YEAR)
        val month = mCal.get(Calendar.MONTH) + 1
        val date = mCal.get(Calendar.DAY_OF_MONTH)
        tv_year.setText(String.format("%04d", year))
        tv_month.setText(String.format("%02d", month))
        tv_date.setText(String.format("%02d", date))

        // TODO : 테스트용
        if (!Comm_Param.REAL) {
            et_id.setText("test@gmail.com")
            et_name.setText("test")
            et_password.setText("test")
            et_re_password.setText("test")
        }
    }

    /**
     * View Click Listener
     */
    fun onClickView() {
        val listener = OnClickListener {
            when (it) {
                tv_year, tv_month, tv_date -> {
                    showDatePickerDialog()
                }
                btn_gender_man -> {
                    isGender = false
                    setGenderView()
                }
                btn_gender_woman -> {
                    isGender = true
                    setGenderView()
                }
                btn_register -> {
                    // 서버 연동 작업
                }
                iv_back-> if(activity != null) activity!!.onBackPressed()
            }
        }
        tv_year.setOnClickListener(listener)
        tv_month.setOnClickListener(listener)
        tv_date.setOnClickListener(listener)
        btn_gender_man.setOnClickListener(listener)
        btn_gender_woman.setOnClickListener(listener)
        btn_register.setOnClickListener(listener)
        iv_back.setOnClickListener(listener)
    }

    /**
     * Gender 버튼 View 설정
     */
    fun setGenderView() {
        btn_gender_woman.isSelected = isGender
        btn_gender_man.isSelected = !isGender
    }

    /**
     * DatePickerDialog
     * 사용자에게 보여줄 시 혹은 데이터를 저장할시에는 Month에 + 1 을 해야합니다
     */
    private fun showDatePickerDialog() {
        val dialog = DatePickerDialog(context!!,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                mCal.set(Calendar.YEAR, year)
                mCal.set(Calendar.MONTH, month)
                mCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                tv_year.setText(String.format("%04d", year))
                tv_month.setText(String.format("%02d", month + 1))
                tv_date.setText(String.format("%02d", dayOfMonth))
            }, mCal.get(Calendar.YEAR), mCal.get(Calendar.MONTH), mCal.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
        dialog.show()
    }
}