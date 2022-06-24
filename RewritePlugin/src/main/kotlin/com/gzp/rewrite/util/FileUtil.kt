package com.gzp.rewrite.util

import com.gzp.rewrite.util.ClassUtil.isValidClassBytes
import org.apache.commons.io.IOUtils
import org.gradle.internal.util.BiFunction
import java.io.*
import java.util.function.Consumer
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

object FileUtil {
    fun readFile(file: File?): ByteArray {
        return readFile(FileInputStream(file))
    }

    fun readFile(inputStream: InputStream): ByteArray {
        val bos = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        val fis: FileInputStream? = null
        return try {
            var size: Int
            while (inputStream.read(buffer).also { size = it } != -1) {
                bos.write(buffer, 0, size)
            }
            bos.toByteArray()
        } finally {
            if (fis != null) {
                try {
                    fis.close()
                } catch (ignroed: IOException) {
                }
            }
        }
    }

    fun traverseJarClass(
        inputJar: File,
        outputJar: File,
        bytecodeTransform: BiFunction<ByteArray?, String?, ByteArray?>
    ) {
        if (outputJar.exists()) {
            outputJar.delete()
        }
        var zipFileInput: ZipFile? = null
        var zipOutput: ZipOutputStream? = null
        try {
            zipFileInput = ZipFile(inputJar)
            zipOutput = ZipOutputStream(BufferedOutputStream(FileOutputStream(outputJar)))
            val originEntries = zipFileInput.entries()
            while (originEntries.hasMoreElements()) {
                val originEntry = originEntries.nextElement()
                val name = originEntry.name
                var bytecode: ByteArray? = ByteArray(originEntry.size.toInt())
                IOUtils.readFully(zipFileInput.getInputStream(originEntry), bytecode)
                if (name.endsWith(".class") && isValidClassBytes(bytecode!!)) {
                    bytecode = bytecodeTransform.apply(name, bytecode)
                }
                zipOutput.putNextEntry(ZipEntry(name))
                zipOutput.write(bytecode)
                zipOutput.closeEntry()
            }
        } catch (e: Throwable) {
            println("[RewritePlugin] fail processJar jar=$inputJar")
            throw e
        } finally {
            IOUtils.closeQuietly(zipFileInput)
            IOUtils.closeQuietly(zipOutput)
        }
    }

    fun traverseJarClass(inputJar: File, byteCodeConsumer: Consumer<ByteArray?>) {
        var zipFileInput: ZipFile? = null
        try {
            zipFileInput = ZipFile(inputJar)
            val originEntries = zipFileInput.entries()
            while (originEntries.hasMoreElements()) {
                val originEntry = originEntries.nextElement()
                val name = originEntry.name
                val bytecode = ByteArray(originEntry.size.toInt())
                IOUtils.readFully(zipFileInput.getInputStream(originEntry), bytecode)
                if (name.endsWith(".class") && isValidClassBytes(bytecode)) {
                    byteCodeConsumer.accept(bytecode)
                }
            }
        } catch (e: Throwable) {
            println("[RewritePlugin] fail processJar jar=$inputJar")
            throw e
        } finally {
            IOUtils.closeQuietly(zipFileInput)
        }
    }
}