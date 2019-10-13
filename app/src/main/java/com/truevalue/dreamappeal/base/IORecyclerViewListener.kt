package com.truevalue.dreamappeal.base

import androidx.annotation.NonNull
import android.view.ViewGroup

interface IORecyclerViewListener {

    val itemCount: Int

    fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): BaseViewHolder

    fun onBindViewHolder(@NonNull h: BaseViewHolder, i: Int)

    fun getItemViewType(i: Int): Int
}