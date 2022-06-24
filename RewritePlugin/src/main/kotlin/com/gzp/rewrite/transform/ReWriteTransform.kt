package com.gzp.rewrite.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager.CONTENT_CLASS
import com.android.build.gradle.internal.pipeline.TransformManager.SCOPE_FULL_PROJECT
import com.android.utils.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import com.gzp.rewrite.Visitor.RewriteVisitor
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class ReWriteTransform : Transform() {
    override fun getName(): String {
        return "ReWriteTransform"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return SCOPE_FULL_PROJECT // QualifiedContent.Scope.SUB_PROJECTS, QualifiedContent.Scope.EXTERNAL_LIBRARIES
    }

    override fun isIncremental(): Boolean {
        return false
    }

    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)

        // 非增量编译先清除之前所有的输出
        if (!transformInvocation.isIncremental()) {
            transformInvocation.outputProvider.deleteAll()
        }

        transformInvocation.inputs?.forEach { it ->


            it.directoryInputs.forEach { it ->
                println("directoryInputs:absolutePath: ${it.file.absolutePath}")
                if (it.file.isDirectory) {
                    it.file.listFiles().forEach {
                        performDirectory(it)
                    }
                }
                val dest = transformInvocation.outputProvider.getContentLocation(it.name, it.contentTypes, it.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(it.file, dest)
            }


            it.jarInputs.forEach {

                println("jarInput:absolutePath: ${it.toString()}")
                val jarFile = JarFile(it.file)

                var jarName = it.name
                val md5Name = DigestUtils.md5Hex(it.file.absolutePath)
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length - 4)
                }
                val dest = transformInvocation.outputProvider.getContentLocation(jarName+md5Name, it.contentTypes, it.scopes, Format.JAR)
                FileUtils.copyFile(it.file, dest)

                val fileOutputStream = FileOutputStream(dest)
                val jarOutputStream = JarOutputStream(fileOutputStream)
                jarFile.entries().iterator().forEach { jarEntry ->
                    val name = jarEntry.name
                    println("jarInput::jarEntry$name")
                    try {
                        var byteArray = ByteArray(jarEntry.size.toInt())
                        val inputStream = jarFile.getInputStream(jarEntry)
                        IOUtils.readFully(inputStream, byteArray)
                        if (name.endsWith(".class") && isValidClassBytes(byteArray!!)) {
                            println("jarInputs:class: ${name}")
                            byteArray = modifyJarClass(byteArray,name)
                        }
                        val zipEntry = ZipEntry(jarEntry.name)
                        jarOutputStream.putNextEntry(zipEntry)
                        jarOutputStream.write(byteArray)
                        jarOutputStream.closeEntry()
                    }catch (e:Exception){
                        println(e.message)
                    }
                }
                IOUtils.closeQuietly(jarFile)
                IOUtils.closeQuietly(jarOutputStream)
            }
        }


    }

    fun performJar(jar: File) {
        if (jar.isDirectory) {
            jar.listFiles().forEach {
                println("jarInputs:directory: ${it.absolutePath}")
                performJar(it)
            }
        } else if (jar.isFile) {
            performJarFile(jar)
        }
    }

    fun performJarFile(jarFile: File) {
        println("jarInputs:file: ${jarFile.path}")

        if (jarFile.name == "MainActivity2.class") {
            println("performFile MainActivity2")
            val byteArray = modifyClass(jarFile)
            if (byteArray != null) {
                val path = jarFile.parent;
                val name = jarFile.name;
                jarFile.delete()
                val newFile = File(path, name)
                FileOutputStream(newFile).write(byteArray)
                println(byteArray)
            }
        }
    }

    fun performDirectory(directory: File) {
        if (directory.isDirectory) {
            directory.listFiles().forEach {
                println("directoryInputs:directory: ${it.absolutePath}")
                performDirectory(it)
            }
        } else if (directory.isFile) {
          //  performFile(directory)
        }
    }


    fun performFile(file: File) {
        println("directoryInputs:file: ${file.path}")

        if (file.name == "MainActivity2.class") {
            println("performFile MainActivity2")
            val byteArray = modifyClass(file)
            if (byteArray != null) {
                val path = file.parent;
                val name = file.name;
                file.delete()
                val newFile = File(path, name)
                FileOutputStream(newFile).write(byteArray)
                println(byteArray)
            }
        }

    }

    fun modifyJarClass(byteArray: ByteArray,name:String): ByteArray{
        if(name == "androidx/appcompat/app/AppCompatActivity.class"){
            val classReader = ClassReader(byteArray)
            val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
            classReader.accept(RewriteVisitor(Opcodes.ASM7, classWriter), Opcodes.ASM7)
            return classWriter.toByteArray()
        }
        return byteArray
    }


    fun modifyClass(file: File): ByteArray? {
        val classReader = ClassReader(file.readBytes())
        val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
        classReader.accept(RewriteVisitor(Opcodes.ASM7, classWriter), Opcodes.ASM7)
        return classWriter.toByteArray()
    }

    private val MAGIC_CAFEBABE = byteArrayOf(-54, -2, -70, -66)
    fun isValidClassBytes(classBytecode: ByteArray): Boolean {
        return if (classBytecode.size < MAGIC_CAFEBABE.size) {
            false
        } else MAGIC_CAFEBABE[0] == classBytecode[0] && MAGIC_CAFEBABE[1] == classBytecode[1] && MAGIC_CAFEBABE[2] == classBytecode[2] && MAGIC_CAFEBABE[3] == classBytecode[3]
    }
}