package com.dylanxing.statistics.utils;


import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class JsonUtil<T> {

    public static <T> T toObj(String json, Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(json, clazz);
    }

    public static String toJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static <T> ArrayList<T> toList(String json, Class<T> clazz) throws JSONException {
        Gson gson = new Gson();
        ArrayList<T> list = new ArrayList<T>();
        JSONArray jArr = new JSONArray(json);
        for (int i = 0; i < jArr.length(); i++) {
            T t = gson.fromJson(jArr.getString(i), clazz);
            list.add(t);
        }
        return list;
    }
}
