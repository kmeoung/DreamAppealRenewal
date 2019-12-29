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
        val EDIT_ACHIEVEMENT_POST = "EDIT_ACHIEVEMENT_POST"
        val EDIT_ACTION_POST = "EDIT_ACTION_POST"
        val EDIT_CHANGE_CATEGORY = "EDIT_CHANGE_CATEGORY"

        val EDIT_VIEW_TYPE = "EDIT_VIEW_TYPE"

        val EDIT_POST_IDX = "EDIT_POST_IDX"

        val ACHIEVEMENT_POST_IDX = "ACHIEVEMENT_POST_IDX"
        val REQUEST_IAMGE_FILES = "REQUEST_IAMGE_FILES"
        val REQUEST_TITLE = "REQUEST_TITLE"
        val REQUEST_CONTENTS = "REQUEST_CONTENTS"
        val REQUEST_TAGS = "REQUEST_TAGS"

        val REQUEST_CATEOGORY_IDX = "REQUEST_CATEOGORY_IDX"
        val REQUEST_CATEOGORY_DETAIL_IDX = "REQUEST_CATEOGORY_DETAIL_IDX"
    }

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

        }else if(intent.getStringExtra(EDIT_VIEW_TYPE) != null){
            val viewType = intent.getStringExtra(EDIT_VIEW_TYPE)
            var imageFiles = ArrayList<String>()
            if(intent.getSerializableExtra(REQUEST_IAMGE_FILES) != null){
                imageFiles = intent.getSerializableExtra(REQUEST_IAMGE_FILES) as ArrayList<String>
            }
            val postIdx = intent.getIntExtra(EDIT_POST_IDX,-1)

            when(viewType){
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

    /**
     * Fragment에서 접근하는 Fragment 변경
     */
    fun replaceFragment(fragment: Fragment, addToBack: Boolean) {
        replaceFragment(R.id.post_container, fragment, addToBack)
    }


}