package com.truevalue.dreamappeal.fragment.timeline.nearby.search_address

import android.app.Activity
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base_new.BaseActivity
import com.truevalue.dreamappeal.fragment.timeline.appeal.connect_friend.Contact
import com.truevalue.dreamappeal.fragment.timeline.appeal.connect_friend.ContactAdapter
import com.truevalue.dreamappeal.utils.Constants
import com.truevalue.dreamappeal.utils.checkPermission
import kotlinx.android.synthetic.main.activity_phone_contact.*
import kotlinx.android.synthetic.main.activity_search_address.*
import kotlinx.android.synthetic.main.search_toolbar.*
import kotlinx.android.synthetic.main.white_text_toolbar.*


class SearchAddressActivity : BaseActivity<SearchAddressViewModel>() {
    override val classViewModel: Class<SearchAddressViewModel> = SearchAddressViewModel::class.java
    override val layoutId: Int = R.layout.activity_search_address
    private var mAdapter: AddressAdapter? = null
    override fun init() {

        setupUI()
    }

    private fun setupUI() {
        tvCancelSearch.setOnClickListener {
            etSearch.text?.clear()
        }
        mAdapter = AddressAdapter(layoutInflater) {
            setResult(Activity.RESULT_OK, Intent().putExtra(Constants.ADDRESS_SEARCH_DATA,it))
            finish()
        }
        rvAddress.adapter = mAdapter
        mAdapter?.setDataSource(
            listOf(
                "전북 완주군 삼례읍 과학로",
                "전북 완주군 삼례읍 과학로",
                "전북 완주군 삼례읍 과학로",
                "전북 완주군 삼례읍 과학로",
                "전북 완주군 삼례읍 과학로",
                "전북 완주군 삼례읍 과학로"
            )
        )
    }

}