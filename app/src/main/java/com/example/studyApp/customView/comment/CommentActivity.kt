package com.example.studyApp.customView.comment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.studyApp.R
import kotlinx.android.synthetic.main.activity_comment.*


class CommentActivity : AppCompatActivity() {
    private val mViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(CommentActivityViewModel::class.java)
    }
    private val mBottomSheetFragment = BottomSheetFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        bottom_sheet_dialog_fragment_text.setOnClickListener {
            mBottomSheetFragment.show(supportFragmentManager, "test")
        }



        mViewModel.mCommentStatus.observe(this, Observer {
            bottom_sheet_dialog_fragment_text.text = if (it) "隐藏" else "展开"
        })
    }

    override fun onResume() {
        super.onResume()
    }
}