package com.example.studyApp

import kotlinx.coroutines.*

class KotlinTest {
    private val scope = MainScope()
    fun run() {
        scope.launch(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            println(this.toString())
            val res = getResult()
            val res2 = getResult()
            println(res + res2)
            val endTime = System.currentTimeMillis()
            println("总计：\t" + (endTime - startTime))
        }
    }

    fun runAsync() {
        scope.launch(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            println(this.toString())
            val res = async { getResult() }
            val res2 = async { getResult() }
            println(res.await() + res2.await())
            val endTime = System.currentTimeMillis()
            println("Async 总计：\t" + (endTime - startTime))
        }
    }

    private suspend fun getResult(): String {
        delay(1000)
        return "111";
    }
}

fun main() {
    KotlinTest().run()
    KotlinTest().runAsync()

    while (true) {

    }


}