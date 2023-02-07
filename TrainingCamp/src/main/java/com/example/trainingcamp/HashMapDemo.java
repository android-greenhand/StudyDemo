package com.example.trainingcamp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class HashMapDemo {
    public static void main(String[] args) {
        test_toArray();
    }


    static void test_toArray() {
        HashMap<String, String> hashMap = new LinkedHashMap<String, String>();
        hashMap.put("pushImei", "11");
        hashMap.put("pushTownId", "pushTownId");
        hashMap.put("pushUserId", "pushUserId");
        hashMap.put("pushPlatform", "android");

        String[] customParamsString = hashMap.values().toArray(new String[]{});

        System.out.println(Arrays.toString(customParamsString));

    }
}
