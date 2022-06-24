package com.gzp.rewrite.util

import org.apache.commons.io.IOUtils

object ClassUtil {
    private val MAGIC_CAFEBABE = byteArrayOf(-54, -2, -70, -66)
    @JvmStatic
    fun isValidClassBytes(classBytecode: ByteArray): Boolean {
        return if (classBytecode.size < MAGIC_CAFEBABE.size) {
            false
        } else MAGIC_CAFEBABE[0] == classBytecode[0] && MAGIC_CAFEBABE[1] == classBytecode[1] && MAGIC_CAFEBABE[2] == classBytecode[2] && MAGIC_CAFEBABE[3] == classBytecode[3]
    }

    fun getClassBytes(clazz: Class<*>): ByteArray {
        val classPath = "/" + clazz.name.replace('.', '/') + ".class"
        val classInput = clazz.javaClass.getResourceAsStream(classPath)
        return IOUtils.toByteArray(classInput)
    }

    fun convert(classDescSet: Set<String>): Set<String> {
        val classNameSet: MutableSet<String> = HashSet(classDescSet.size)
        for (s in classDescSet) {
            classNameSet.add(s.replace("/".toRegex(), "."))
        }
        return classNameSet
    }
}