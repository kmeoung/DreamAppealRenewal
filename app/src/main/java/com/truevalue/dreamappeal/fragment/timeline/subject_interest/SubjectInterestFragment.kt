package com.truevalue.dreamappeal.fragment.timeline.subject_interest

import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base_new.fragment.BaseTabFragment
import com.truevalue.dreamappeal.base_new.viewmodel.EmptyViewModel

class SubjectInterestFragment : BaseTabFragment<EmptyViewModel>() {

    override val classViewModel: Class<EmptyViewModel> = EmptyViewModel::class.java
    override val layoutId: Int = R.layout.fragment_subject_interest

    companion object {
        fun newInstance(): SubjectInterestFragment {
            return SubjectInterestFragment()
        }
    }

    override fun onFirstRender() {

    }
}