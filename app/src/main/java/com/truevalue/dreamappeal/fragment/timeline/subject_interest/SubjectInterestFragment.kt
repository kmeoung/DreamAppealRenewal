package com.truevalue.dreamappeal.fragment.timeline.subject_interest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base_new.fragment.BaseTabFragment
import com.truevalue.dreamappeal.base_new.viewmodel.EmptyViewModel
import kotlinx.android.synthetic.main.fragment_subject_interest.*

class SubjectInterestFragment : BaseTabFragment<EmptyViewModel>() {

    override val classViewModel: Class<EmptyViewModel> = EmptyViewModel::class.java
    override val layoutId: Int = R.layout.fragment_subject_interest

    companion object {
        fun newInstance(): SubjectInterestFragment {
            return SubjectInterestFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onFirstRender() {
        tvLable.text = "dsdsdsds"
    }
}