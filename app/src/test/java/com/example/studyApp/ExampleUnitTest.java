package com.example.studyApp;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    void fun(List list) {

    }

    public static void main(String[] args) {

    }


    /**
     * 通配符
     *
     * @param genericType
     */
    void printlnFruitName(GenericType<? extends Fruit> genericType) {

    }

    /**
     * 泛型方法
     */

    <T> void printlnFr(T a) {

    }

    static class Fruit {

    }


    static class Apple extends Fruit {

    }

    static class GenericType<T extends Fruit> {

    }
}


