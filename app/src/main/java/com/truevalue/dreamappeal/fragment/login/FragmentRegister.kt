package com.truevalue.dreamappeal.fragment.login

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.utils.Comm_Param
import kotlinx.android.synthetic.main.action_bar_profile_other.*
import kotlinx.android.synthetic.main.fragment_register.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FragmentRegister : BaseFragment() {
    // true 여성 false 남성
    private var isGender: Boolean
    private val mCal: Calendar
    private val mAuthMap: HashMap<String, Boolean>

    // 인증요청들
    private val AUTH_TERMS_OF_USE = "AUTH_TERMS_OF_USE"
    private val AUTH_PRIVACY_POLICY = "AUTH_PRIVACY_POLICY"
    private val AUTH_RECEIVE_MARKETING = "AUTH_RECEIVE_MARKETING"

    init {
        // 남성으로 초기화
        isGender = false
        mCal = Calendar.getInstance()
        mAuthMap = HashMap()
        // 인증요청들 초기화
        mAuthMap.put(AUTH_TERMS_OF_USE, false)
        mAuthMap.put(AUTH_PRIVACY_POLICY, false)
        mAuthMap.put(AUTH_RECEIVE_MARKETING, false)
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
        tv_title.text = getString(R.string.str_register)
        iv_menu.visibility = GONE
        iv_search.visibility = INVISIBLE
        iv_back.visibility = VISIBLE

        setGenderView()
        // todo : 시간 남으면
//        setCalendar()
        val year = mCal.get(Calendar.YEAR)
        val month = mCal.get(Calendar.MONTH) + 1
        val date = mCal.get(Calendar.DAY_OF_MONTH)
        tv_year.text = String.format("%04d", year)
        tv_month.text = String.format("%02d", month)
        tv_date.text = String.format("%02d", date)
        tv_gender.text = "남"
        isGender = false

        // TODO : 테스트용
        if (!Comm_Param.REAL) {
            et_id.setText("test@gmail.com")
            et_name.setText("test")
            et_password.setText("test")
            et_re_password.setText("test")
        }
    }

    /**
     * Custom 캘린더
     */
    private fun setCalendar() {
        // Year 설정
        var popupMenu = PopupMenu(context!!, tv_year)
        for (i in 1900..mCal.get(Calendar.YEAR)) {
            popupMenu.menu.add(i.toString())
        }
        popupMenu.setOnMenuItemClickListener {
            var selected = it.title.toString()
            mCal.set(Calendar.YEAR, Integer.parseInt(selected))
            tv_year.setText(selected)
            false
        }
    }

    /**
     * View Click Listener
     */
    private fun onClickView() {
        val listener = OnClickListener {
            when (it) {
                tv_year, tv_month, tv_date -> {
                    showDatePickerDialog()
                }
                tv_gender -> {
                    setGenderView()
                }
                iv_back -> if (activity != null) activity!!.onBackPressed()
                ll_all_agree -> {
                    for (key: String in mAuthMap.keys) {
                        mAuthMap[key] = !iv_all_agree.isSelected
                    }
                    initAuthIcon()
                }
                ll_terms_of_use -> {
                    mAuthMap[AUTH_TERMS_OF_USE] = !iv_terms_of_use.isSelected
                    initAuthIcon()
                }
                ll_privacy_policy -> {
                    mAuthMap[AUTH_PRIVACY_POLICY] = !iv_privacy_policy.isSelected
                    initAuthIcon()
                }
                ll_receive_marketing -> {
                    mAuthMap[AUTH_RECEIVE_MARKETING] = !iv_receive_marketing.isSelected
                    initAuthIcon()
                }
            }
        }
        tv_year.setOnClickListener(listener)
        tv_month.setOnClickListener(listener)
        tv_date.setOnClickListener(listener)
        tv_gender.setOnClickListener(listener)
        iv_back.setOnClickListener(listener)
        ll_all_agree.setOnClickListener(listener)
        ll_terms_of_use.setOnClickListener(listener)
        ll_privacy_policy.setOnClickListener(listener)
        ll_receive_marketing.setOnClickListener(listener)
    }

    /**
     * 인증 아이콘들 설정
     */
    private fun initAuthIcon() {
        if (mAuthMap.isNotEmpty()) {
            iv_terms_of_use.isSelected = mAuthMap[AUTH_TERMS_OF_USE]!!
            iv_privacy_policy.isSelected = mAuthMap[AUTH_PRIVACY_POLICY]!!
            iv_receive_marketing.isSelected = mAuthMap[AUTH_RECEIVE_MARKETING]!!

            iv_all_agree.isSelected =
                iv_terms_of_use.isSelected &&
                        iv_privacy_policy.isSelected &&
                        iv_receive_marketing.isSelected
        }

    }


    /**
     * Gender 버튼 View 설정
     */
    private fun setGenderView() {
        var popupMenu = PopupMenu(context!!, tv_year)
        popupMenu.menu.add("남")
        popupMenu.menu.add("여")

        popupMenu.setOnMenuItemClickListener {
            when (it.title) {
                "남" -> isGender = false
                "여" -> isGender = true
            }
            tv_gender.text = it.title
            false
        }

    }

    /**
     * DatePickerDialog
     * 사용자에게 보여줄 시 혹은 데이터를 저장할시에는 Month에 + 1 을 해야합니다
     */
    private fun showDatePickerDialog() {
        val dialog = DatePickerDialog(
            context!!,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                mCal.set(Calendar.YEAR, year)
                mCal.set(Calendar.MONTH, month)
                mCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                tv_year.text = String.format("%04d", year)
                tv_month.text = String.format("%02d", month + 1)
                tv_date.text = String.format("%02d", dayOfMonth)
            }, mCal.get(Calendar.YEAR), mCal.get(Calendar.MONTH), mCal.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
        dialog.show()
    }
}