package com.truevalue.dreamappeal.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.base.BaseActivity
import com.truevalue.dreamappeal.fragment.profile.blueprint.FragmentAddActionPost
import com.truevalue.dreamappeal.fragment.profile.blueprint.FragmentLevelChoice
import com.truevalue.dreamappeal.fragment.profile.performance.FragmentNewAddAchievementPost
import java.io.File

class ActivityAddPost : BaseActivity() {

    companion object{
        const val EDIT_ACHIEVEMENT_POST = "EDIT_ACHIEVEMENT_POST"
        const val EDIT_ACTION_POST = "EDIT_ACTION_POST"
        const val EDIT_CHANGE_CATEGORY = "EDIT_CHANGE_CATEGORY"

        const val EDIT_VIEW_TYPE = "EDIT_VIEW_TYPE"

        const val EDIT_POST_IDX = "EDIT_POST_IDX"

        const val ACHIEVEMENT_POST_IDX = "ACHIEVEMENT_POST_IDX"
        const val REQUEST_IAMGE_FILES = "REQUEST_IAMGE_FILES"
        const val REQUEST_TITLE = "REQUEST_TITLE"
        const val REQUEST_CONTENTS = "REQUEST_CONTENTS"
        const val REQUEST_TAGS = "REQUEST_TAGS"

        const val REQUEST_CATEOGORY_IDX = "REQUEST_CATEOGORY_IDX"
        const val REQUEST_CATEOGORY_DETAIL_IDX = "REQUEST_CATEOGORY_DETAIL_IDX"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        initView()
    }

    private fun initView() {
        intent.getStringExtra(ActivityCameraGallery.VIEW_TYPE)?.let {
            when(it){
                ActivityCameraGallery.EXTRA_ACTION_POST->{
                    intent.getSerializableExtra(ActivityCameraGallery.REQUEST_IMAGE_FILES)?.let { files->
                        val fileArray = files as ArrayList<File>
                        replaceFragment(FragmentAddActionPost.newInstance(fileArray), false)
                    }
                }
                ActivityCameraGallery.EXTRA_ACHIVEMENT_POST->{
                    intent.getSerializableExtra(ActivityCameraGallery.REQUEST_IMAGE_FILES)?.let { files->
                        val fileArray = files as ArrayList<File>
                        val bestIdx = intent.getIntExtra(ActivityCameraGallery.REQUEST_BEST_IDX,-1)
                        replaceFragment(FragmentNewAddAchievementPost.newInstance(fileArray,bestIdx), false)
                    }
                }
                else->{ }
            }
        }?:kotlin.run {
            intent.getStringExtra(EDIT_VIEW_TYPE)?.let {
                var imageFiles = ArrayList<String>()

                intent.getSerializableExtra(REQUEST_IAMGE_FILES)?.let { files->
                    imageFiles = files as ArrayList<String>
                }

                val postIdx = intent.getIntExtra(EDIT_POST_IDX,-1)

                when(it){
                    EDIT_ACHIEVEMENT_POST->{
                        val contents = intent.getStringExtra(REQUEST_CONTENTS)
                        val bestIdx = intent.getIntExtra(ACHIEVEMENT_POST_IDX,-1)
                        val title = intent.getStringExtra(REQUEST_TITLE)
                        replaceFragment(FragmentNewAddAchievementPost.newInstance(imageFiles, bestIdx,postIdx,title,contents), false)
                    }
                    EDIT_ACTION_POST->{
                        val contents = intent.getStringExtra(REQUEST_CONTENTS)
                        val tags = intent.getStringExtra(REQUEST_TAGS)
                        replaceFragment(FragmentAddActionPost.newInstance(imageFiles,postIdx,contents,tags), false)
                    }
                    EDIT_CHANGE_CATEGORY->{
                        val categoryIdx = intent.getIntExtra(REQUEST_CATEOGORY_IDX,-1)
                        val categoryDetailIdx = intent.getIntExtra(REQUEST_CATEOGORY_DETAIL_IDX,-1)
                        replaceFragment(FragmentLevelChoice.newInstance(postIdx,categoryIdx,categoryDetailIdx), false)
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