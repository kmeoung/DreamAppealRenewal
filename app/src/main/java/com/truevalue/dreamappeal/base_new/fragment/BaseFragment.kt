package com.example.stackoverflowuser.base.fragment


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.stackoverflowuser.base.repository.ErrorModel
import com.example.stackoverflowuser.base.viewmodel.BaseViewModel
import com.example.stackoverflowuser.base.viewmodel.ClassUtils
import com.example.stackoverflowuser.base.viewmodel.ShareViewModel
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.utils.toast


abstract class BaseFragment<T : BaseViewModel> : Fragment() {
    private lateinit var viewModel: T
    fun getViewModel() = viewModel
    protected abstract val classViewModel: Class<T>

    protected abstract val layoutId: Int


    private var progressDialogLoading: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val shareViewMode = ClassUtils.getAnnotation(this, ShareViewModel::class.java)
        if (shareViewMode != null)
            viewModel = ViewModelProvider(activity!!).get(classViewModel)
        else {
            viewModel = ViewModelProvider(this).get(classViewModel)
            viewModel.error().observe(this, onError)
            viewModel.loading().observe(this, onLoading)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    abstract fun init()

    protected var onError = Observer<ErrorModel> {
        if (it.msg != null) {
            context?.toast(it.msg)
        }
    }

    protected var onLoading = Observer<Boolean> {
        if (it) {
            showProgressLoading()
        } else {
            hideProgressLoading()
        }
    }

    fun showProgressLoading() {

    }

    fun hideProgressLoading() {
        if (progressDialogLoading != null && progressDialogLoading!!.isShowing)
            progressDialogLoading!!.dismiss()
    }

    fun addFragment(
        fragment: Fragment,
        id: Int,
        backStack: Boolean = true
    ) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.enter_from_right, R.anim.exit_to_left,
            R.anim.enter_from_left, R.anim.exit_to_right
        )
        val currentFragment = parentFragmentManager.fragments.lastOrNull()
        currentFragment?.let {
            transaction.hide(it)
        }

        if (backStack) {
            transaction.addToBackStack(fragment.javaClass.simpleName)
        }
        transaction.add(id, fragment, fragment.javaClass.simpleName).commit()
    }

    fun replaceFragment(
        fragment: Fragment,
        id: Int,
        backStack: Boolean = false
    ) {
        val transaction = parentFragmentManager.beginTransaction()
        if (backStack) {
            transaction.addToBackStack(fragment.javaClass.simpleName)
        }
        transaction.replace(id, fragment, fragment.javaClass.simpleName).commit()
    }
}


