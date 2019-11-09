package com.truevalue.dreamappeal.fragment.login

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityLoginContainer
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.bean.BeanRegister
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import com.truevalue.dreamappeal.utils.Comm_Param
import com.truevalue.dreamappeal.utils.Utils
import kotlinx.android.synthetic.main.action_bar_login.*
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.et_id
import kotlinx.android.synthetic.main.fragment_register.et_password
import okhttp3.Call
import java.util.*
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
        mAuthMap[AUTH_TERMS_OF_USE] = false
        mAuthMap[AUTH_PRIVACY_POLICY] = false
        mAuthMap[AUTH_RECEIVE_MARKETING] = false
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
        iv_back_blue.visibility = VISIBLE
        tv_text_btn.visibility = GONE

        val year = mCal.get(Calendar.YEAR)
        val month = mCal.get(Calendar.MONTH) + 1
        val date = mCal.get(Calendar.DAY_OF_MONTH)
        tv_year.text = String.format("%04d", year) + "년"
        tv_month.text = String.format("%02d", month) + "월"
        tv_date.text = String.format("%02d", date) + "일"
        tv_gender.text = "남"
        isGender = false

        // TODO : 테스트용
        if (!Comm_Param.REAL) {
            et_id.setText(Comm_Param.DEBUG_EMAIL)
            et_name.setText(Comm_Param.DEBUG_NAME)
            et_password.setText(Comm_Param.DEBUG_PASSWORD)
            et_re_password.setText(Comm_Param.DEBUG_PASSWORD)
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
                iv_back_blue -> if (activity != null) activity!!.onBackPressed()
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
                btn_next -> {
                    checkRegister()
                }
            }
        }
        tv_year.setOnClickListener(listener)
        tv_month.setOnClickListener(listener)
        tv_date.setOnClickListener(listener)
        tv_gender.setOnClickListener(listener)
        iv_back_blue.setOnClickListener(listener)
        ll_all_agree.setOnClickListener(listener)
        ll_terms_of_use.setOnClickListener(listener)
        ll_privacy_policy.setOnClickListener(listener)
        ll_receive_marketing.setOnClickListener(listener)
        btn_next.setOnClickListener(listener)
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
        val popupMenu = PopupMenu(context!!, tv_gender)
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
        popupMenu.show()
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

                tv_year.text = String.format("%04d", year) + "년"
                tv_month.text = String.format("%02d", month + 1) + "월"
                tv_date.text = String.format("%02d", dayOfMonth) + "일"
            }, mCal.get(Calendar.YEAR), mCal.get(Calendar.MONTH), mCal.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
        dialog.show()
    }

    /**
     * 회원가입 양식을 제대로 썻는지 확인
     * true 통과
     */
    fun checkRegister() {
        val name = et_name.text.toString()
        var email = et_id.text.toString().trim()
        email = email.replace(" ","")
        val password = et_password.text.toString()
        val rePassword = et_re_password.text.toString()

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(
                context!!.applicationContext,
                getString(R.string.str_input_name),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(
                context!!.applicationContext,
                getString(R.string.str_input_email),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (!Utils.isEmailValid(email)) {
            Toast.makeText(
                context!!.applicationContext,
                getString(R.string.str_input_email_type),
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(
                context!!.applicationContext,
                getString(R.string.str_input_password),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if(password.length < 8){
            Toast.makeText(
                context!!.applicationContext,
                getString(R.string.str_check_password_min_length),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (!TextUtils.equals(password, rePassword)) {
            Toast.makeText(
                context!!.applicationContext,
                getString(R.string.str_plz_match_password),
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }

        if (!iv_all_agree.isSelected) {
            Toast.makeText(
                context!!.applicationContext,
                getString(R.string.str_check_agreement),
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }

        val bean = BeanRegister(email, password, name, isGender, mCal.time)

        DAClient.sendEmail(email, name, object : DAHttpCallback {
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
                        (activity as ActivityLoginContainer).replaceFragment(
                            FragmentCheckEmail.newInstance(
                                bean
                            ), true
                        )
                    }
                }
            }
        })
    }
}