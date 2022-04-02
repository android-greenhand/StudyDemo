package com.example.studyApp.utils;

import com.github.jsonzou.jmockdata.JMockData;
import com.github.jsonzou.jmockdata.MockConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class JSONCamp {

    public static void main(String[] args) {

        String s = "[{\"title\":\"标题\",\"pic\":\"https://seopic.699pic.com/photo/50045/1021.jpg_wh1200.jpg?w=300\"},{\"title\":\"标题\",\"pic\":\"https://seopic.699pic.com/photo/50045/1021.jpg_wh1200.jpg?w=300\"},{\"title\":\"标题\",\"pic\":\"https://seopic.699pic.com/photo/50045/1021.jpg_wh1200.jpg?w=300\"}]";



         List<TagInfo> tagInfoList;

        tagInfoList  = new Gson().fromJson(s, new MyTypeToken<List<TagInfo>>(){}.getType());

        System.out.println(tagInfoList.size());
        System.out.println(tagInfoList.get(0).title);


        Type superclassTypeParameter = MyTypeToken.getSuperclassTypeParameter( tagInfoList.getClass());


        System.out.println(superclassTypeParameter.getTypeName());





      

    }






    static public class MyTypeToken<T> {
        final Type type;
        protected MyTypeToken() {
            this.type = getSuperclassTypeParameter(this.getClass());
        }

       public static Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class) {
                throw new RuntimeException("Missing type parameter.");
            } else {
                ParameterizedType parameterized = (ParameterizedType)superclass;
                return parameterized.getActualTypeArguments()[0];
            }
        }

        public Type getType() {
            return type;
        }
    }
    static public class TagInfo {
        public String title = "";
        public String textColor = "#ffffff";
        public String backgroundColor = "#ffffff";
        public String pic = "";
    }


    public static class ItemLocalInfo {
        public String localName = "";
        public String jumpAction = "";
        public String localId = "";

        @Override
        public String toString() {
            return "ItemLocalInfo{" +
                    "localName='" + localName + '\'' +
                    ", jumpAction='" + jumpAction + '\'' +
                    ", localId='" + localId + '\'' +
                    '}';
        }
    }
}
