package com.example.studyApp.utils

import java.io.*

class FileParserUtil {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            parseFile2FixedFormat("/Users/a58/Desktop/跳转到系统的辅助功能界面.txt", "/Users/a58/Desktop/result2.txt")
        }

        fun parseString2FixedFormat(string: String) {

        }

        fun parseFile2FixedFormat(srcPath: String, dstPath: String) {
            val fileReader = FileReader(File(srcPath))
            val fileWriter = FileWriter(File(dstPath))
            val readLines = fileReader.readLines()

            val arrayListOf = arrayListOf<String>()
            readLines.forEach {

                if (it.contains("Intent")) {
                    arrayListOf.add(it)
                    println(it)
                    fileWriter.write(it)
                    fileWriter.write("\n")
                }
            }
            fileWriter.close()
            fileReader.close()

        }

    }
}