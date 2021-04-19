package com.example.studyApp.animation

import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.LinearInterpolator
import com.example.studyApp.R

class ValueAnimatorActivity : AppCompatActivity() {
    companion object {
        const val TAG: String = "ValueAnimator"
        const val CASE_0_300: String = "0..300"
        const val CASE_0_100_300: String = "0..100..300"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_value_animator)
        var case = CASE_0_100_300
        when (case) {
            CASE_0_300 -> {
                var startTime: Long = 0
                var endTime: Long = 0
                var flag = true
                val valueAnimator = ValueAnimator.ofInt(0, 100, 300).apply {
                    duration = 1000
                    interpolator = LinearInterpolator()
                    addUpdateListener {
                        when (it.animatedValue as Int) {
                            in 0..100 -> {
                                if (flag) {
                                    flag = false
                                    startTime = System.currentTimeMillis()

                                }
                                Log.d(TAG, it.currentPlayTime.toString())
                                Log.d(TAG, it.animatedFraction.toString())
                            }
                            in 100..300 -> {
                                if (!flag) {
                                    flag = true
                                    endTime = System.currentTimeMillis()
                                    Log.d(TAG + "时间", (endTime - startTime).toString())
                                }
                                Log.d(TAG + "100..300", it.currentPlayTime.toString())
                                Log.d(TAG + "100..300", it.animatedFraction.toString())
                            }
                        }
                    }
                }
                valueAnimator.start()
            }
            CASE_0_100_300 -> {
                var startTime: Long = 0
                var endTime: Long = 0
                var flag = true
                val valueAnimator = ValueAnimator.ofInt(0, 300).apply {
                    duration = 1000
                    interpolator = LinearInterpolator()
                    addUpdateListener {
                        if (flag && it.animatedValue as Int <= 100) {
                            flag = false
                            startTime = System.currentTimeMillis()

                        }

                        if (!flag && it.animatedValue as Int >= 100) {
                            flag = true
                            endTime = System.currentTimeMillis()
                            Log.d(TAG + "时间", (endTime - startTime).toString())
                        }
                    }
                }
                valueAnimator.start()
            }
        }



    }
}