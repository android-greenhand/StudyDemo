package com.example.trainingcamp

class KotlinTrainingCamp {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val s = "abc"
            val result = s.split("d")
            println(result.size)
            println(className())
        }

        fun className(): String {
            return "KotlinTrainingCamp"
        }

    }

}