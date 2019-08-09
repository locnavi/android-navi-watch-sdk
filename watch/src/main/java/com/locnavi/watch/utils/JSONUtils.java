package com.locnavi.watch.utils;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * author:chen
 * time:2017/8/21
 * desc:
 */
public class JSONUtils {

    private JSONUtils(){}

    /**
     * 对象转换成json字符串
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    /**
     * json字符串转成对象
     * @param str
     * @param type
     * @return
     */
    public static <T> T fromJson(String str, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(str, type);
    }

    /**
     * json字符串转成对象
     * @param str
     * @param type
     * @return
     */
    public static <T> T fromJson(String str, Class<T> type) {
        Gson gson = new Gson();
        return gson.fromJson(str, type);
    }

    public static <T> T fromJSONObject(JSONObject obj, Class<T> type) {
        return fromJson(toJson(obj),type);
    }

}
