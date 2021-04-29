package com.example.studyApp.customView.test

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.studyApp.R
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

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