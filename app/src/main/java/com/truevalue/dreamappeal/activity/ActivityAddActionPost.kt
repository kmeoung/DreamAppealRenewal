package com.truevalue.dreamappeal.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.fragment.profile.blueprint.FragmentAddActionPost
import java.io.File

class ActivityAddActionPost : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_action_post)

        initView()
    }

    private fun initView() {
        if (intent.getSerializableExtra(ActivityCameraGallery.REQUEST_IMAGE_FILES) != null) {
            val fileArray =
                intent.getSerializableExtra(ActivityCameraGallery.REQUEST_IMAGE_FILES) as ArrayList<File>

            replaceFragment(FragmentAddActionPost.newInstance(fileArray),false)
        }
    }

    /**
     * Fragment에서 접근하는 Fragment 변경
     */
    fun replaceFragment(fragment: Fragment, addToBack: Boolean) {
        replaceFragment(R.id.action_container, fragment, addToBack)
    }


}