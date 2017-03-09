package com.dylanxing.androidstatisticslib;

import android.content.Context;

import com.dylanxing.statistics.StatisticsManager;

import java.util.Map;

public class ClickAgent {

    public static void onEvent(Context context, String key) {
        StatisticsManager.getInstance().onCountEvent(context, key);
    }

    public static void onEventValue(Context context, String key, Map<String, String> value, int valueInt) {
        if (value == null) {
            StatisticsManager.getInstance().onEvent(context, key, String.valueOf(valueInt));
        } else {
            String rawValue = value.get(key);
            if (!android.text.TextUtils.isEmpty(rawValue)) {
                StatisticsManager.getInstance().onEvent(context, key, rawValue);
            } else {
                StatisticsManager.getInstance().onEvent(context, key, String.valueOf(valueInt));
            }
        }
    }

    public static void onEventValue(Context context, String key, String value) {
        StatisticsManager.getInstance().onEvent(context, key, value);
    }

}