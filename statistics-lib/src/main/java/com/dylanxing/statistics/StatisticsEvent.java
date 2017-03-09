package com.dylanxing.statistics;

import com.dylanxing.statistics.utils.JsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class StatisticsEvent {
    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String TIME = "time";
    public static final String EXTENDS = "extends";
    public static final String TYPE = "type";
    public static final String TYPE_COUNT = "0";
    public static final String TYPE_VALUE = "1";
    private String time;
    private String key;
    private String value;
    private String type;
    private List<Extend> extendList;

    public StatisticsEvent() {
    }

    public StatisticsEvent(String key) {
        this.key = key;
    }

    public StatisticsEvent(String key, String value, String time, String type) {
        this.key = key;
        this.value = value;
        this.time = time;
        this.type = type;
    }

    public static StatisticsEvent fromJSONObject(JSONObject eventObject) {
        StatisticsEvent statisticsEvent = new StatisticsEvent();
        statisticsEvent.setKey(eventObject.optString(KEY));
        statisticsEvent.setValue(eventObject.optString(VALUE));
        statisticsEvent.setTime(eventObject.optString(TIME));
        statisticsEvent.setType(eventObject.optString(TYPE));
        try {
            JSONArray jsonArray = eventObject.optJSONArray(EXTENDS);
            if (jsonArray != null) {
                statisticsEvent.setExtendList(JsonUtil.toList(jsonArray.toString(), Extend.class));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return statisticsEvent;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Extend> getExtendList() {
        return extendList;
    }

    public void setExtendList(List<Extend> extendList) {
        this.extendList = extendList;
    }

    public JSONObject toJSONObject(boolean isToFile) {
        JSONObject json = new JSONObject();
        try {
            json.put(KEY, key);
            json.put(VALUE, value);
            json.put(TIME, time);
            if (isToFile) {
                json.put(TYPE, type);
            }
            if (extendList != null) {
                JSONArray extendArray = new JSONArray();
                for (Extend extent : extendList) {
                    extendArray.put(JsonUtil.toJson(extent));
                }
                json.put(EXTENDS, extendArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof StatisticsEvent)) {
            return false;
        }
        StatisticsEvent statisticsEvent = (StatisticsEvent) obj;
        return this.key.equals(statisticsEvent.getKey());
    }

    public static class Extend {
    }
}
