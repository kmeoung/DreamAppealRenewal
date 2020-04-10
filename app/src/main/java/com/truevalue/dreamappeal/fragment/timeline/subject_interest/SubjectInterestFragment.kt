package com.truevalue.dreamappeal.fragment.timeline.subject_interest

import com.google.android.material.bottomsheet.BottomSheetDialog
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base_new.fragment.BaseTabFragment
import com.truevalue.dreamappeal.base_new.viewmodel.EmptyViewModel
import com.truevalue.dreamappeal.fragment.timeline.adapter.GridOptionAdapter
import com.truevalue.dreamappeal.fragment.timeline.adapter.TimeLineData
import com.truevalue.dreamappeal.fragment.timeline.adapter.TimeLineDataAdapter
import com.truevalue.dreamappeal.utils.gone
import com.truevalue.dreamappeal.utils.toPx
import com.truevalue.dreamappeal.utils.visible
import com.truevalue.dreamappeal.widgets.GridItemDecoration
import kotlinx.android.synthetic.main.dialog_bottom_string.*
import kotlinx.android.synthetic.main.fragment_subject_interest.*

class SubjectInterestFragment : BaseTabFragment<EmptyViewModel>() {

    override val classViewModel: Class<EmptyViewModel> = EmptyViewModel::class.java
    override val layoutId: Int = R.layout.fragment_subject_interest

    companion object {
        fun newInstance(): SubjectInterestFragment {
            return SubjectInterestFragment()
        }
    }

    private var mAdapter: TimeLineDataAdapter? = null

    override fun onFirstRender() {
        setupUI()

    }

    private fun setupUI() {
        groupListData.gone()

        tvSeeOtherTopic.setOnClickListener {
            tvChooseTopic.performClick()
        }

        tvChooseTopic.setOnClickListener {
            val dialog = BottomSheetDialog(context!!, R.style.AppBottomSheetDialogTheme)
            val onClick: (String) -> Unit = {
                tvChosenTopic.text = it
                groupListData.visible()
                llEmpty.gone()
                dialog.dismiss()
            }
            dialog.apply {
                setContentView(R.layout.dialog_bottom_string)
                tvTitle.text = getString(R.string.please_select_interest_topic)
                val optionAdapter = GridOptionAdapter(layoutInflater, onClick)
                rvOptions.apply {
                    adapter = optionAdapter
                    addItemDecoration(GridItemDecoration(20.toPx(), 3))
                }

                val list = mutableListOf<String>()
                for (i in 0..20) {
                    list.add("#IT/로봇")
                }
                optionAdapter.setDataSource(list)
            }.show()
        }

        rvPosts.apply {
            mAdapter = TimeLineDataAdapter(layoutInflater)
            adapter = mAdapter
            setHasFixedSize(true)
        }
        mAdapter?.setDataSource(
            listOf(
                TimeLineData(),
                TimeLineData(1),
                TimeLineData(2),
                TimeLineData(3),
                TimeLineData(1),
                TimeLineData()
            )
        )
    }


}