package com.truevalue.dreamappeal.fragment.timeline.appeal.connect_friend.kakao

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import com.example.stackoverflowuser.base.viewmodel.BaseViewModel
import com.truevalue.dreamappeal.fragment.timeline.appeal.connect_friend.Contact

class KakaoContactViewModel : BaseViewModel() {

    fun getListPhone(context: Context): List<Contact> {
        val contactList = mutableListOf<Contact>()
        val phones: Cursor? = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
            null, null, null
        )
        if (phones != null) {
            while (phones.moveToNext()) {
                val name: String = phones.getString(
                    phones.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                    )
                )
                val phoneNumber: String = phones.getString(
                    phones.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    )
                )
                contactList.add(
                    Contact(
                        null,
                        name,
                        phoneNumber
                    )
                )
            }
            phones.close()
        }
        return contactList
    }
}