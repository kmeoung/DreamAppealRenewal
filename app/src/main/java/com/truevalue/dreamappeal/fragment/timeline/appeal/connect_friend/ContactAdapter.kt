package com.truevalue.dreamappeal.fragment.timeline.appeal.connect_friend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base_new.adapter.BaseAdapter
import com.truevalue.dreamappeal.base_new.adapter.BaseHolder
import com.truevalue.dreamappeal.utils.gone
import com.truevalue.dreamappeal.utils.load
import com.truevalue.dreamappeal.utils.value
import com.truevalue.dreamappeal.utils.visible
import kotlinx.android.synthetic.main.item_contact.*
import kotlinx.android.synthetic.main.item_registerd_contact.*

class ContactAdapter(
    inflater: LayoutInflater
    , var onClickFollow: (Contact) -> Unit
    , var onClickInvite: (Contact) -> Unit
) :
    BaseAdapter<Contact, BaseHolder<Contact>>(inflater) {
    override fun getItemViewType(position: Int): Int {
        return when {
            dataSource[position].isRegistered -> R.layout.item_registerd_contact
            else -> R.layout.item_contact
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Contact> {
        return when (viewType) {
            R.layout.item_registerd_contact -> ViewHolderRegister(
                inflater.inflate(viewType, parent, false)
            )
            else -> ViewHolderContact(inflater.inflate(viewType, parent, false))
        }
    }

    inner class ViewHolderRegister(view: View) : BaseHolder<Contact>(view) {
        init {
            tvFollow.setOnClickListener {
                onClickFollow.invoke(dataSource[adapterPosition])
                tvFollowing.visible()
                tvFollow.gone()
            }
        }

        override fun bind(data: Contact, position: Int) {
            civAvatar.load(data.image.value())
            tvName.text = data.name
            tvIntro.text = data.intro
        }
    }

    inner class ViewHolderContact(view: View) : BaseHolder<Contact>(view) {
        init {
            tvInvite.setOnClickListener {
                onClickInvite.invoke(dataSource[adapterPosition])
            }
        }

        override fun bind(data: Contact, position: Int) {
            tvUsername.text = data.name
            tvContact.text = data.intro
        }
    }
}

data class Contact(
    var image: String? = null,
    var name: String? = null,
    var intro: String? = null,
    var isRegistered: Boolean = false
) {

}