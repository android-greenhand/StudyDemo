package com.example.studyApp.JNI;

public class JNITools {
    static {
        System.loadLibrary("JNIdemo");
    }

    public  native String StringFromJNI();
}
