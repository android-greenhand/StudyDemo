package com.example.studyApp.github

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.studyApp.R

class ViewMoreTextViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_more_text_view)

        val viewMore =findViewById<ViewMoreTextView>(R.id.viewMore)

        viewMore
            .setAnimationDuration(500)
            .setEllipsizedText("展开")
            .setVisibleLines(3)
            .setIsExpanded(false)
            .setEllipsizedTextColor(ContextCompat.getColor(this, R.color.colorAccent))

        viewMore.text =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua." +
                    "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat." +
                    "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur." +
                    "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."

        viewMore.setOnClickListener {
            viewMore.toggle()
        }
    }
}
