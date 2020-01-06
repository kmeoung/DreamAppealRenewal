package com.truevalue.dreamappeal.fragment.dream_board

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityCameraGallery
import com.truevalue.dreamappeal.base.BaseFragment
import com.truevalue.dreamappeal.base.BaseRecyclerViewAdapter
import com.truevalue.dreamappeal.base.BaseViewHolder
import com.truevalue.dreamappeal.base.IORecyclerViewListener
import kotlinx.android.synthetic.main.fragment_add_board.*
import kotlinx.android.synthetic.main.fragment_ano.*
import java.io.File

class FragmentAddBoard : BaseFragment() {

    private var mAdapter: BaseRecyclerViewAdapter? = null
    private val REQUEST_ADD_IMAGES = 1003

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_board, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // View 초기화
        initView()
        // Adapter 초기화
        initAdapter()
        // View Click 초기화
        onClickView()
    }

    /**
     * View 초기화
     */
    private fun initView() {

    }

    /**
     * RecyclerView Adapter 초기화
     */
    private fun initAdapter() {
        mAdapter = BaseRecyclerViewAdapter(rvImageListener)
        rv_board_img.adapter = mAdapter
        rv_board_img.layoutManager =
            LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
    }

    /**
     * View Click 초기화
     */
    private fun onClickView() {
        val listener = View.OnClickListener {
            when (it) {
                iv_add_photo -> {
                    val intent = Intent(context!!, ActivityCameraGallery::class.java)
                    intent.putExtra(
                        ActivityCameraGallery.SELECT_TYPE,
                        ActivityCameraGallery.EXTRA_IMAGE_MULTI_SELECT
                    )
                    intent.putExtra(
                        ActivityCameraGallery.VIEW_TYPE,
                        ActivityCameraGallery.EXTRA_BOARD
                    )
                    startActivityForResult(intent, REQUEST_ADD_IMAGES)
                }
            }
        }
        iv_add_photo.setOnClickListener(listener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_ADD_IMAGES) {
                val fileArray =
                    data!!.getSerializableExtra(ActivityCameraGallery.REQUEST_IMAGE_FILES) as ArrayList<File>

                if (fileArray != null) {
                    if (fileArray.size > 0) {
                        if (mAdapter != null) {
                            for (file in fileArray) {
                                mAdapter!!.add(file)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * RecyclerView 이미지 Listener
     */
    private val rvImageListener = object : IORecyclerViewListener {
        override val itemCount: Int
            get() = if (mAdapter != null) mAdapter!!.size() else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onBindViewHolder(h: BaseViewHolder, i: Int) {

        }

        override fun getItemViewType(i: Int): Int {
            return 0
        }
    }
}