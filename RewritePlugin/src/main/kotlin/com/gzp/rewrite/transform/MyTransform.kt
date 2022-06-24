package com.gzp.rewrite.transform

import com.gzp.rewrite.util.ClassUtil
import com.gzp.rewrite.util.FileUtil
import com.android.build.api.transform.*
import org.apache.commons.io.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MyTransform : Transform() {
    override fun getName(): String {
        return "MyTranfrom"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return mutableSetOf(QualifiedContent.DefaultContentType.CLASSES, QualifiedContent.DefaultContentType.RESOURCES)
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return mutableSetOf(QualifiedContent.Scope.PROJECT, QualifiedContent.Scope.SUB_PROJECTS, QualifiedContent.Scope.EXTERNAL_LIBRARIES)
    }

    override fun isIncremental(): Boolean {
        return true
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        if (transformInvocation == null) {
            return
        }
        if (transformInvocation.isIncremental) {
            incrementalTransform(transformInvocation)
        } else {
            allTransform(transformInvocation)
        }
    }

    private fun incrementalTransform(transformInvocation: TransformInvocation) {
        transformInvocation.inputs.forEach {input ->
            input.jarInputs.forEach {jarInput ->
                val outputFile = transformInvocation.outputProvider.getContentLocation(
                    jarInput.file.absolutePath,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )
                when (jarInput.status) {
                    Status.ADDED, Status.CHANGED -> {
                        FileUtils.deleteQuietly(outputFile)
                        transformJar(
                            jarInput.file, outputFile)
                    }
                    Status.REMOVED -> {
                        FileUtils.deleteQuietly(outputFile)
                    }
                    Status.NOTCHANGED -> {

                    }
                }
            }
            input.directoryInputs.forEach {directoryInput ->
                val inputDir = directoryInput.file
                val outputDir: File = transformInvocation.outputProvider.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes,
                    directoryInput.scopes,
                    Format.DIRECTORY
                )

                directoryInput.changedFiles.forEach {
                    val inputFile = it.key
                    val outputFile = File(inputFile.absolutePath.replace(inputDir.absolutePath, outputDir.absolutePath))

                    when (it.value) {
                        Status.ADDED, Status.CHANGED -> {
                            FileUtils.deleteQuietly(outputFile)
                            transformClassFile(inputFile, outputFile)
                        }
                        Status.REMOVED -> {
                            FileUtils.deleteQuietly(outputFile)
                        }
                        Status.NOTCHANGED -> {

                        }
                    }
                }
            }
        }
    }

    private fun allTransform(transformInvocation: TransformInvocation) {
        transformInvocation.outputProvider.deleteAll()
        transformInvocation.inputs.forEach {input ->
            input.jarInputs.forEach {jarInput ->
                val outputFile = transformInvocation.outputProvider.getContentLocation(
                    jarInput.file.absolutePath,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )
                transformJar(
                    jarInput.file, outputFile)
            }
            input.directoryInputs.forEach {directoryInput ->
                val outputFile = transformInvocation.outputProvider.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes,
                    directoryInput.scopes,
                    Format.DIRECTORY
                )

                transformDirectory(
                    directoryInput.file,
                    outputFile
                )
            }
        }
    }

    private fun transformJar(input: File, output: File) {
        FileUtils.copyFile(input, output)
        FileUtil.traverseJarClass(input, output
        ) { _, inputBytes -> transformClassBytes(inputBytes) }
    }

    private fun transformDirectory(input: File, output: File) {
        FileUtils.forceMkdir(output)
        val srcDirPath = input.absolutePath
        val destDirPath = output.absolutePath
        input.listFiles()?.forEach {subFile ->
            val destFilePath = subFile.absolutePath.replace(srcDirPath, destDirPath)
            val destFile = File(destFilePath)
            if (subFile.isDirectory) {
                transformDirectory(subFile, destFile)
            } else {
                transformClassFile(subFile, destFile)
            }
        }
    }

    private fun transformClassFile(inputFile: File, outputFile: File) {
        try {
            val fis = FileInputStream(inputFile)
            val fos = FileOutputStream(outputFile)
            if (inputFile.name.endsWith(".class")) {
                fos.write(transformClassBytes(FileUtil.readFile(fis)))
            } else {
                val buffer = ByteArray(1024)
                var length: Int
                while (true) {
                    length = fis.read(buffer)
                    if (length != -1) {
                        fos.write(buffer, 0, length)
                    } else {
                        break
                    }
                }
            }
            fis.close()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun transformClassBytes(classBytes: ByteArray?): ByteArray {
        if (classBytes == null) {
            return ByteArray(0)
        }
        if (!ClassUtil.isValidClassBytes(classBytes)) {
            return classBytes
        }
        val cr = ClassReader(classBytes)

        val cn = ClassNode()
        cr.accept(cn, ClassReader.EXPAND_FRAMES)

        transformClassNode(cn)

        val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
        cn.accept(cw)

        return cw.toByteArray()
    }

    private fun transformClassNode(classNode: ClassNode) {
        if (classNode.name.contains("MainActivity")) {
            println(">>>>>>>>>>>>>>>> ${classNode.name}")
            classNode.methods.forEach { methodNode ->
                methodNode.instructions.iterator().forEach {
                    if (it is MethodInsnNode && it.opcode == Opcodes.INVOKESTATIC && it.owner == "android/util/Log") {
                        println("${classNode.name.replace("/", ".")}.${methodNode.name}")
                    }
                }
            }
        }

    }
}