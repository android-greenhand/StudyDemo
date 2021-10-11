package com.example.studyApp.demo.hotfix;

/**
 * 命令行中生成 dex包： dx
 */
public class HotfixException {
    public static String getStringWithException() {
        return "这里已经修复了";
       //throw new RuntimeException("HotfixException");
    }
}
