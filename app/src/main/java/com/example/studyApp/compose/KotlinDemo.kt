package com.example.studyApp.compose


/**
 * 扩展函数练习
 */
class ExpendFunTest {
    val text: String = "ExpendFunTest"
    fun printText() = println(text)

}

class KotlinDemo {

    fun ExpendFunTest.expendFun() {
        println("this is ExpendFunTest's expend Function")
        printText()

    }

    val ExpendFunTest.expendText: String
        get() = "ExpendText"


    fun useExpendFunTest() {
        println("开始扩展函数")
        println(ExpendFunTest().expendText)
        ExpendFunTest().expendFun()
    }
}


fun main() {
    KotlinDemo().useExpendFunTest()
}