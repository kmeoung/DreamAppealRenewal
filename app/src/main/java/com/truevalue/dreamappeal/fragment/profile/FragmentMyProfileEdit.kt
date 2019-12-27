package com.truevalue.dreamappeal.fragment.profile

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMyProfileContainer
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.bean.BeanProfileUser
import com.truevalue.dreamappeal.http.DAClient
import com.truevalue.dreamappeal.http.DAHttpCallback
import kotlinx.android.synthetic.main.action_bar_other.*
import kotlinx.android.synthetic.main.fragment_edit_group_info.*
import kotlinx.android.synthetic.main.fragment_normal_profile_edit.*
import okhttp3.Call
import java.text.SimpleDateFormat
import java.util.*

class FragmentMyProfileEdit : BaseFragment() {

    private var mBean: BeanProfileUser? = null
    private var isGender: Boolean
    private var mCal: Calendar

    /**
     * privates data
     *
     * 0 : public
     * 1 : private
     */

    init {
        isGender = false
        mCal = Calendar.getInstance()
    }

    companion object {
        fun newInstance(bean: BeanProfileUser?): FragmentMyProfileEdit {
            var fragment = FragmentMyProfileEdit()
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
        // View init
        initView()
        // View Onclick Listener
        onClickView()
    }

    private fun isCheck() : Boolean{

        val check = (!et_name.text.toString().isNullOrEmpty())
                && (!et_nickname.text.toString().isNullOrEmpty())
                && (!tv_gender.text.toString().isNullOrEmpty())
                && (!tv_date.text.toString().isNullOrEmpty())
        // todo : 지금은 주소 비활성화

        return check
    }

    /**
     * View 초기화
     */
    private fun initView() {
        // todo : 현재는 거주지 비활성화
        et_address.isEnabled = false
        // 상단 바 설정
        (activity as ActivityMyProfileContainer).iv_back_black.visibility = View.VISIBLE
        (activity as ActivityMyProfileContainer).tv_title.text =
            getString(R.string.str_normal_edit_prifile)
        (activity as ActivityMyProfileContainer).iv_check.visibility = View.VISIBLE

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                initRightBtn()
            }
        }
        et_name.addTextChangedListener(textWatcher)
        et_nickname.addTextChangedListener(textWatcher)
        et_address.addTextChangedListener(textWatcher)
        tv_gender.addTextChangedListener(textWatcher)
        tv_date.addTextChangedListener(textWatcher)

        if (mBean != null) {
            et_name.setText(mBean!!.name)
            et_nickname.setText(if (mBean!!.nickname.isNullOrEmpty()) "" else mBean!!.nickname)
            mCal.time = SimpleDateFormat("yyyy-MM-dd").parse(mBean!!.birth)
            tv_date.text = SimpleDateFormat("yyyy. MM. dd").format(mCal.time)
            tv_gender.text =
                getString(if (mBean!!.gender == 0) R.string.str_female else R.string.str_male)
            et_address.setText(if (mBean!!.nickname.isNullOrEmpty()) "" else mBean!!.address)

            if (mBean!!.private != null) {
                /**
                 * privates data
                 *
                 * 0 : public
                 * 1 : private
                 */
                var privates = mBean!!.private!!
                iv_lock_name.isSelected = (privates.name == 1)
                iv_lock_address.isSelected = (privates.address == 1)
                iv_lock_birth.isSelected = (privates.birth == 1)
                iv_lock_gender.isSelected = (privates.gender == 1)
                iv_lock_nickname.isSelected = (privates.nickname == 1)
            }
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

                var year = String.format("%04d", year)
                var month = String.format("%02d", month + 1)
                var date = String.format("%02d", dayOfMonth)

                tv_date.text = "$year. $month. $date"
            }, mCal.get(Calendar.YEAR), mCal.get(Calendar.MONTH), mCal.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
        dialog.show()
    }

    /**
     * Gender 버튼 View 설정
     */
    private fun setGenderView() {
        val popupMenu = PopupMenu(context!!, tv_gender)
        popupMenu.menu.add(getString(R.string.str_male))
        popupMenu.menu.add(getString(R.string.str_female))

        popupMenu.setOnMenuItemClickListener {
            when (it.title) {
                getString(R.string.str_male) -> isGender = false
                getString(R.string.str_female) -> isGender = true
            }
            tv_gender.text = it.title
            false
        }
        popupMenu.show()
    }

    /**
     * 오른쪽 상단 아이콘 설정
     */
    private fun initRightBtn() {
        (activity as ActivityMyProfileContainer).iv_check.isSelected = isCheck()
    }
    /**
     * Http
     * 내 정보 수정
     */
    private fun updateMyUserData(){
        val bean = mBean!!
        bean.name = et_name.text.toString()
        bean.nickname = et_nickname.text.toString()
        bean.birth = SimpleDateFormat("yyyy-MM-dd").format(mCal.time)
        bean.gender = if(isGender) 0 else 1
        bean.address = et_address.text.toString()
        bean.private!!.name = if(iv_lock_name.isSelected) 1 else 0
        bean.private!!.birth = if(iv_lock_birth.isSelected) 1 else 0
        bean.private!!.address = if(iv_lock_address.isSelected) 1 else 0
        bean.private!!.gender = if(iv_lock_gender.isSelected) 1 else 0
        bean.private!!.nickname = if(iv_lock_nickname.isSelected) 1 else 0

        DAClient.updateMyUserData(bean, object : DAHttpCallback {
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
     * View Click Listener
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                (activity as ActivityMyProfileContainer).iv_back_black -> activity!!.onBackPressed()
                tv_date -> showDatePickerDialog()
                iv_lock_address -> iv_lock_address.isSelected = !iv_lock_address.isSelected
                iv_lock_birth -> iv_lock_birth.isSelected = !iv_lock_birth.isSelected
                iv_lock_gender -> iv_lock_gender.isSelected = !iv_lock_gender.isSelected
                iv_lock_nickname -> iv_lock_nickname.isSelected = !iv_lock_nickname.isSelected
                iv_lock_name -> iv_lock_name.isSelected = !iv_lock_name.isSelected
                tv_gender -> setGenderView()
                (activity as ActivityMyProfileContainer).iv_check -> {
                    if ((activity as ActivityMyProfileContainer).iv_check.isSelected) {
                        updateMyUserData()
                    }
                }
            }
        }

        (activity as ActivityMyProfileContainer).iv_back_black.setOnClickListener(listener)
        (activity as ActivityMyProfileContainer).iv_check.setOnClickListener(listener)
        tv_date.setOnClickListener(listener)
        iv_lock_address.setOnClickListener(listener)
        iv_lock_birth.setOnClickListener(listener)
        iv_lock_gender.setOnClickListener(listener)
        iv_lock_nickname.setOnClickListener(listener)
        iv_lock_name.setOnClickListener(listener)
        tv_gender.setOnClickListener(listener)
    }
}