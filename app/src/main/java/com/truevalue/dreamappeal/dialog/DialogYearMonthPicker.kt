package com.truevalue.dreamappeal.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.NumberPicker
import com.truevalue.dreamappeal.R
import kotlinx.android.synthetic.main.dialog_year_month_picker.*
import java.util.*

class DialogYearMonthPicker(context: Context, min_year: Int, min_month: Int) : Dialog(context) {

    private val minYear : Int
    private val minMonth : Int

    private val cal : Calendar

    init {
        cal = Calendar.getInstance()
        minYear = min_year
        minMonth = min_month
    }

    companion object{
        const val INIT_NUM = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Dialog 뒷 배경 및 여러가지 설정
        var layoutParams = WindowManager.LayoutParams()
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.8f
        window!!.attributes = layoutParams
        window!!.setBackgroundDrawableResource(R.color.transparent)

        setContentView(R.layout.dialog_year_month_picker)

        initData()
    }

    /**
     * View 초기화
     */
    private fun initView(){
        val yearFormat = NumberPicker.Formatter {
            String.format("%d년",it)
        }

        val monthFormat = NumberPicker.Formatter {
            String.format("%d월",it)
        }
    }

    /**
     * 초기 데이터 설정
     */
    private fun initData(){
        var minYear = 1900


        if(minYear != INIT_NUM){
            np_year.minValue = minYear
            if(minMonth != INIT_NUM){
                np_month.minValue = minMonth
            }
        }
    }
}