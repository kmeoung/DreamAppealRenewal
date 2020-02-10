package com.truevalue.dreamappeal.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.NumberPicker
import com.truevalue.dreamappeal.R
import kotlinx.android.synthetic.main.dialog_object_success.*
import kotlinx.android.synthetic.main.dialog_year_month_picker.*
import java.util.*

class DialogObjectSuccess(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Dialog 뒷 배경 및 여러가지 설정
        var layoutParams = WindowManager.LayoutParams()
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.8f
        window!!.attributes = layoutParams
        window!!.setBackgroundDrawableResource(R.color.transparent)

        setContentView(R.layout.dialog_object_success)

        btn_ok.setOnClickListener {
            dismiss()
        }
    }


}