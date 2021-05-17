package com.example.studyApp.demo.other

import android.app.Activity
import android.app.Application
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Bundle
import android.view.View

import java.util.*

object GreyEffect : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentActivity = activity

        if (activity != null && greyEffect) {
            applyGreyEffect(activity, greyEffect)
        }
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        if (activity != null) {
            currentActivity = activity
            applyGreyEffect(activity, greyEffect)
        }
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null

    }
    private const val KEY_GREY_CONFIG = "key:grey_config"

    private var greyEffect: Boolean = false
        set(value) {
            field = value
            applyGreyEffect(currentActivity, value)
        }

    private val activityPaintMap = WeakHashMap<Activity, GreyEffectInfo>()

    private var currentActivity: Activity? = null

    private fun applyGreyEffect(activity: Activity?, greyEffect: Boolean) {
        if (activity == null) {
            return
        }

        var greyEffectInfo = activityPaintMap[activity]
        if (greyEffectInfo == null) {
            if (!greyEffect) {
                return
            }

            greyEffectInfo = GreyEffectInfo(Paint(), false)
            activityPaintMap[activity] = greyEffectInfo
        }

        if (greyEffect == greyEffectInfo.greyEffect) {
            return
        }

        greyEffectInfo.greyEffect = greyEffect
        if (greyEffect) {
            val colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(0.0f)
            greyEffectInfo.paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        } else {
            greyEffectInfo.paint = Paint()
        }

        activity.window.decorView.setLayerType(View.LAYER_TYPE_HARDWARE, greyEffectInfo.paint)
    }

    class GreyEffectInfo(
            var paint: Paint,
            var greyEffect: Boolean
    )
}