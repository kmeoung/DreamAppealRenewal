package com.truevalue.dreamappeal.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.fragment.profile.blueprint.FragmentAddActionPost
import com.truevalue.dreamappeal.fragment.profile.performance.FragmentNewAddAchievementPost
import java.io.File

class ActivityAddPost : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        initView()
    }

    private fun initView() {
        if(intent.getStringExtra(ActivityCameraGallery.VIEW_TYPE) != null) {
            val viewType = intent.getStringExtra(ActivityCameraGallery.VIEW_TYPE)

            when(viewType){
                ActivityCameraGallery.EXTRA_ACTION_POST->{
                    if (intent.getSerializableExtra(ActivityCameraGallery.REQUEST_IMAGE_FILES) != null) {
                        val fileArray =
                            intent.getSerializableExtra(ActivityCameraGallery.REQUEST_IMAGE_FILES) as ArrayList<File>

                        replaceFragment(FragmentAddActionPost.newInstance(fileArray), false)
                    }
                }
                ActivityCameraGallery.EXTRA_ACHIVEMENT_POST->{
                    if (intent.getSerializableExtra(ActivityCameraGallery.REQUEST_IMAGE_FILES) != null) {
                        val fileArray =
                            intent.getSerializableExtra(ActivityCameraGallery.REQUEST_IMAGE_FILES) as ArrayList<File>
                        val bestIdx = intent.getIntExtra(ActivityCameraGallery.REQUEST_BEST_IDX,-1)

                        replaceFragment(FragmentNewAddAchievementPost.newInstance(fileArray,bestIdx), false)
                    }
                }
            }

        }
    }

    /**
     * Fragment에서 접근하는 Fragment 변경
     */
    fun replaceFragment(fragment: Fragment, addToBack: Boolean) {
        replaceFragment(R.id.post_container, fragment, addToBack)
    }


}