package com.gzp.rewrite

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.gzp.rewrite.transform.ReWriteTransform

class RewritePlugin : Plugin<Project> {
    override fun apply(target: Project) {

        val appExtension = target.extensions.getByName("android") as AppExtension
        appExtension.registerTransform(ReWriteTransform())
        println("this is custom RewritePlugin")
        println("RewritePlugin")
    }
}