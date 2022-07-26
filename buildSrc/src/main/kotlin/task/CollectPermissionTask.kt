package task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

//TODO 怎么配置在项目中
open class CollectPermissionTask : DefaultTask() {

//
//    @Input
//    lateinit var  sdkDirectory :File
//    @Input
//    lateinit var  compileSdkVersion:String

    @TaskAction
    fun collectPermission() {
        println("[RewritePlugin] Collect Permission task start")
        parseAndroidJar("/Users/a58/Library/Android/sdk/platforms/android-21/android.jar/")
        println("[RewritePlugin] Collect Permission task end")
    }

    private fun parseAndroidJar(path: String) {
        val androidSourceCodeDir = File(path)
        if (!androidSourceCodeDir.exists()) {
            println("[RewritePlugin] android source code dir doest exists; $androidSourceCodeDir")
            return
        }
        parseDirectory(androidSourceCodeDir)
    }

    private fun parseDirectory(file: File) {
        if (!file.exists()) {
            return
        }
        if (file.isFile) {
            parseFile(file)
            return
        }
        val listFiles = file.listFiles()
        listFiles?.forEach {
            if (it.isFile) {
                parseFile(it)
            } else {
                parseDirectory(it)
            }
        }
    }

    private fun parseFile(file: File) {
        println("[RewritePlugin] gzp_parseJavaFile" + file.name)

        if (file.name.endsWith(".java")) {
            parseJavaFile(file)
        }
    }

    private fun parseJavaFile(file: File) {
        println("[RewritePlugin] gzp_parseJavaFile" + file.name)
    }

}