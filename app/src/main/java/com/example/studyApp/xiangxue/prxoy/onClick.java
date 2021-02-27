package com.example.studyApp.xiangxue.prxoy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author: gezongpan
 * data: 2021/2/26
 * desc:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface onClick {
   public int []ids = new int[10];
   String in = " ";
   int [] value() ;
}
