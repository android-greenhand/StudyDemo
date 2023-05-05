package com.example.trainingcamp

fun main() {
    println("顶层方法")
    KotlinDemo().test()
}

class KotlinDemo {
    var temp = 0;
    private var a: Int = 0
        get() {
            temp++
            return temp
        }

    fun test() {
        if (a == 1 && a == 2 && a == 3) {
            println("success")
        }
    }
}