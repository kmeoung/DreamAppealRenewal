package com.truevalue.dreamappeal.fragment.timeline.appeal.connect_friend.facebook

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.stackoverflowuser.base.viewmodel.BaseViewModel
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.truevalue.dreamappeal.fragment.timeline.appeal.connect_friend.Contact

class FacebookContactViewModel : BaseViewModel() {
    private var contacts = MutableLiveData<List<Contact>>()

    fun contacts() = contacts
    private val TAG = "FacebookC";
    fun getFbFriends() {
        GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "/me/friends",
            null,
            HttpMethod.GET,
            GraphRequest.Callback {
                Log.d(TAG, "init: $it")
            }
        ).executeAsync()
    }
}