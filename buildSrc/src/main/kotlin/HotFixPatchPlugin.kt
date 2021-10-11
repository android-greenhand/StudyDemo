import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke

class HotFixPatchPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        println("this is custom HotFixPatchPlugin")

        /**
         * 创建扩展属性
         */
        val patchExtension = target.extensions.create("patch", PatchExtension::class.java)
        /**
         * 注意：在这里获取属性为null
         */
        patchExtension?.let {
            println(patchExtension.toString())
        }

        target.afterEvaluate {
            projectAfterEvaluate(this)
        }

        val tasks = target.tasks
        val taskre = tasks.findByName("processReleaseManifest");
        val taskde = tasks.findByName("processDeBugManifest");


    }

    private fun projectAfterEvaluate(project: Project) {
        val patchExtension = project.extensions.findByName("patch")
        patchExtension?.let {
            println(patchExtension.toString())
        }

        /**
         * 处理debug为false的场景
         *
         * 1.得到每个class文件的md5值
         *
         */

        project.tasks.forEach {  }

     }





}