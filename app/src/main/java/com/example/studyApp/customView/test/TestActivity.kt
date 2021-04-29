package com.example.studyApp.customView.test

import android.animation.ObjectAnimator
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.studyApp.R
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        gradient_text.text = "你好世界"
        gradient_text.mAboveTextColor = Color.RED
        gradient_text.mBelowTextColor = Color.CYAN
        Handler().postDelayed({
            startViewAnimation()
        }, 1000)
    }

    private fun startViewAnimation() {
        ObjectAnimator
                .ofFloat(gradient_text, "mPercent", 0f, 1f)
                .apply {
                    duration = 3000
                }
                .start()
    }
}