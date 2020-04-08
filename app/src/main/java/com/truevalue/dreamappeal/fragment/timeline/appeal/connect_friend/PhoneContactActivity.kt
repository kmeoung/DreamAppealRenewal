package com.truevalue.dreamappeal.fragment.timeline.appeal.connect_friend

import android.database.Cursor
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base_new.BaseActivity
import com.truevalue.dreamappeal.base_new.viewmodel.EmptyViewModel
import com.truevalue.dreamappeal.utils.checkPermission
import com.truevalue.dreamappeal.utils.visible
import kotlinx.android.synthetic.main.activity_phone_contact.*
import kotlinx.android.synthetic.main.white_text_toolbar.*
import java.util.jar.Manifest


class PhoneContactActivity : BaseActivity<PhoneContactViewModel>() {
    override val classViewModel: Class<PhoneContactViewModel> = PhoneContactViewModel::class.java
    override val layoutId: Int = R.layout.activity_phone_contact
    private var mAdapter: ContactAdapter? = null
    override fun init() {

        setupUI()
        val registerContacts = mutableListOf(
            Contact(
                "https://kprofiles.com/wp-content/uploads/2019/11/D6aGkQlUcAAgf_n-533x800.jpg",
                "원피스를 찾아 떠나는",
                "해적왕 정석헌",
                true
            ), Contact(
                "https://kprofiles.com/wp-content/uploads/2019/11/D6aGkQlUcAAgf_n-533x800.jpg",
                "원피스를 찾아 떠나는",
                "해적왕 정석헌",
                true
            ), Contact(
                "https://kprofiles.com/wp-content/uploads/2019/11/D6aGkQlUcAAgf_n-533x800.jpg",
                "원피스를 찾아 떠나는",
                "해적왕 정석헌",
                true
            ), Contact(
                "https://kprofiles.com/wp-content/uploads/2019/11/D6aGkQlUcAAgf_n-533x800.jpg",
                "원피스를 찾아 떠나는",
                "해적왕 정석헌",
                true
            )
        )

        checkPermission(android.Manifest.permission.READ_CONTACTS) {
            val contacts = getViewModel().getListPhone(this)
            registerContacts.addAll(contacts)
            mAdapter?.setDataSource(registerContacts)
        }
    }

    private fun setupUI() {
        tvTitle.text = getString(R.string.contact_a_friend)
        tvRight.setOnClickListener {

        }
        ivLeft.visible()
        ivLeft.setOnClickListener {
            finish()
        }

        rvContacts.run {
            layoutManager = LinearLayoutManager(this@PhoneContactActivity)
            mAdapter = ContactAdapter(layoutInflater, onClickFollow = {}, onClickInvite = {})
            adapter = mAdapter
        }
    }
}