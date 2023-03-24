package com.example.studyApp.customView.test

import android.animation.ObjectAnimator
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import com.example.studyApp.R
import com.example.studyApp.utils.DensityUtil
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

        image.setImageResource(R.mipmap.img)
        //  image.scaleType = ImageView.ScaleType.MATRIX
        var i = 0;
        val len = ImageView.ScaleType.values().size

        btn.setOnClickListener {
            if (i < len) {
                image.scaleType = ImageView.ScaleType.values().get(i++)
            } else {
                i = 0
            }

            Toast.makeText(this, "${image.scaleType}", Toast.LENGTH_SHORT).show()
        }

        image_wight_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                image.layoutParams = image.layoutParams.apply {
                    width = ((progress / 100.0) * DensityUtil.getScreenWidth(this@TestActivity)).toInt()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        image_height_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                image.layoutParams = image.layoutParams.apply {
                    height = ((progress / 100.0) * DensityUtil.getScreenHeight(this@TestActivity)).toInt()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

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