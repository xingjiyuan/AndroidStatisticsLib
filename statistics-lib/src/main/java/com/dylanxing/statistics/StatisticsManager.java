package com.dylanxing.statistics;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.dylanxing.statistics.upload.IStatisticsUploadChecker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 上传数据格式：
 * {
 * "user_id": "",
 * "channel": "",//渠道标识
 * "mac": "",//设备mac地址
 * "device_id": "",//设备编号，与设备mac地址一起区分设备
 * "event": [//统计事件
 * {
 * "key": "",//事件key
 * "value": "",//事件值，一般按次数统计的事件有该字段
 * "time": "",//该事件最后出现的时间
 * "extends": [//事件扩展字段，具体字段按统计需求定义
 * {}
 * ]
 * }
 * ]
 * }
 */
public class StatisticsManager {
    private static final int MSG_UPLOAD = 8001;
    private static final String TAG = "StatisticsManager";
    private static volatile StatisticsManager instance;
    private final Handler statisticsHandler = new StatisticsHandler(Looper.getMainLooper(), this);
    private volatile boolean canUpload = true;
    private volatile String lastUploadTime;
    private volatile String uploadUserId;
    private volatile List<StatisticsEvent> eventList = Collections.synchronizedList(new LinkedList<StatisticsEvent>());
    private String channel;
    private String mac;
    private String deviceId;
    private IStatisticsUploadChecker statisticsUploadChecker;
    private IOnUploadListener onUploadListener;
    private List<StatisticsEvent> uploadEventList;
    private JSONObject uploadJson;
    private StatisticsConfig config;

    private StatisticsManager() {
        canUpload = true;
    }

    public static StatisticsManager getInstance() {
        if (instance == null) {
            synchronized (StatisticsManager.class) {
                if (instance == null) {
                    instance = new StatisticsManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context, IStatisticsUploadChecker statisticsUploadChecker, IOnUploadListener onUploadListener, StatisticsConfig config) {
        this.config = config;
        this.statisticsUploadChecker = statisticsUploadChecker;
        setOnUploadListener(onUploadListener);
        upload(context, null);
    }

    public void setOnUploadListener(IOnUploadListener onUploadListener) {
        this.onUploadListener = onUploadListener;
    }

    /**
     * 上传成功后必须调用该方法
     *
     * @param context
     */
    public void onUploadSuccess(Context context) {
        for (StatisticsEvent eventKey : uploadEventList) {
            eventList.remove(eventKey);
        }
        uploadEventList.clear();
        lastUploadTime = String.valueOf(System.currentTimeMillis());
        uploadUserId = config == null ? null : config.getUserId();
        JSONObject afterJson = new JSONObject();
        try {
            afterJson.put(StatisticsConstants.KEY_LAST_UPLOAD_TIME, lastUploadTime);
            afterJson.put(StatisticsConstants.KEY_USER_ID, uploadUserId);
            String mac = uploadJson.optString(StatisticsConstants.KEY_MAC);
            if (!android.text.TextUtils.isEmpty(mac)) {
                afterJson.put(StatisticsConstants.KEY_MAC, mac);
            }
            String deviceId = uploadJson.optString(StatisticsConstants.KEY_DEVICE_ID);
            if (!android.text.TextUtils.isEmpty(deviceId)) {
                afterJson.put(StatisticsConstants.KEY_DEVICE_ID, deviceId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StatisticsFileHelper.getInstance().clear(context, afterJson.toString());
        canUpload = true;
    }

    public void onUploadFailure() {
        canUpload = true;
    }

    private synchronized void uploadStatisticsData(final Context context) {
        if (canUpload) {
            canUpload = false;
            uploadJson = toJSONObject(context, false);
            if (android.text.TextUtils.isEmpty(uploadJson.optJSONArray(StatisticsConstants.KEY_EVENT).toString()) || "[]".equals(uploadJson.optJSONArray(StatisticsConstants.KEY_EVENT).toString())) {
                canUpload = true;
                asyncReadFromStatisticsFile(context);
                return;
            }
            if (uploadEventList == null) {
                uploadEventList = new ArrayList<>();
            }
            uploadEventList.addAll(eventList);
            uploadJson.remove(StatisticsConstants.KEY_LAST_UPLOAD_TIME);
            if (onUploadListener != null) {
                onUploadListener.onUpload(uploadJson.toString());
            }

        }
    }

    private synchronized JSONObject toJSONObject(Context context, boolean isToFile) {
        JSONObject json = new JSONObject();
        if (android.text.TextUtils.isEmpty(mac) || android.text.TextUtils.isEmpty(deviceId)) {
            try {
                if (config != null) {
                    json = new JSONObject(config.getDeviceInfo());
                    mac = json.optString(StatisticsConstants.KEY_MAC);
                    deviceId = json.optString(StatisticsConstants.KEY_DEVICE_ID);
                }
            } catch (JSONException ignored) {
            }
        } else {
            try {
                json.put(StatisticsConstants.KEY_MAC, mac);
                json.put(StatisticsConstants.KEY_DEVICE_ID, deviceId);
            } catch (JSONException ignored) {
            }
        }

        if (android.text.TextUtils.isEmpty(channel)) {
            channel = config == null ? null : config.getChannel();
        }
        if (android.text.TextUtils.isEmpty(uploadUserId)) {
            uploadUserId = config == null ? null : config.getUserId();
        }

        try {
            json.put(StatisticsConstants.KEY_LAST_UPLOAD_TIME, lastUploadTime == null ? "" : lastUploadTime);
            json.put(StatisticsConstants.KEY_USER_ID, uploadUserId);
            if (android.text.TextUtils.isEmpty(channel)) {
                json.put(StatisticsConstants.KEY_CHANNEL, config == null ? null : config.getDefaultChannel());
            } else {
                json.put(StatisticsConstants.KEY_CHANNEL, channel);
            }
            JSONArray events = new JSONArray();
            synchronized (eventList) {
                for (StatisticsEvent statisticsEvent : eventList) {
                    if (statisticsEvent != null) {
                        events.put(statisticsEvent.toJSONObject(isToFile));
                    }
                }
            }
            json.put(StatisticsConstants.KEY_EVENT, events);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private synchronized void initFromJSONObject(String jsonStr) {
        if (android.text.TextUtils.isEmpty(jsonStr)) {
            return;
        }
        JSONObject json;
        try {
            json = new JSONObject(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        lastUploadTime = json.optString(StatisticsConstants.KEY_LAST_UPLOAD_TIME);
        uploadUserId = json.optString(StatisticsConstants.KEY_USER_ID);
        JSONArray eventArray = json.optJSONArray(StatisticsConstants.KEY_EVENT);
        if (eventArray != null) {
            for (int index = 0; index < eventArray.length(); index++) {
                JSONObject eventObject = eventArray.optJSONObject(index);
                StatisticsEvent statisticsEvent = StatisticsEvent.fromJSONObject(eventObject);
                if (statisticsEvent == null || android.text.TextUtils.isEmpty(statisticsEvent.getKey())) {
                    continue;
                }
                eventList.add(statisticsEvent);
            }
        }
    }

    /*按点打点，统计次数*/
    public void onCountEvent(final Context outerContext, final String eventKey) {
        final Context context = outerContext.getApplicationContext();
        new AsyncTask<Context, Void, String>() {

            @Override
            protected String doInBackground(Context... params) {
                if (eventList.size() == 0) {
                    readFromStatisticsFile(params[0]);
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                checkIfChangedUser(context);
                String time = String.valueOf(System.currentTimeMillis());
                int index = eventList.indexOf(new StatisticsEvent(eventKey));
                if (index != -1) {
                    StatisticsEvent statisticsEvent = eventList.get(index);
                    String value = statisticsEvent.getValue();
                    statisticsEvent.setTime(time);
                    statisticsEvent.setType(StatisticsEvent.TYPE_COUNT);
                    if (!android.text.TextUtils.isEmpty(value)) {
                        int valueInt = Integer.valueOf(value);
                        valueInt++;
                        statisticsEvent.setValue(String.valueOf(valueInt));
                    } else {
                        statisticsEvent.setValue(String.valueOf("1"));
                    }
                } else {
                    eventList.add(new StatisticsEvent(eventKey, String.valueOf("1"), time, StatisticsEvent.TYPE_COUNT));
                }
                asyncWriteFileAndCheckUpload(context, eventKey);
            }
        }.execute(context);
    }

    /*按值打点，值为0*/
    public void onEvent(final Context outerContext, final String eventKey) {
        final Context context = outerContext.getApplicationContext();
        new AsyncTask<Context, Void, String>() {

            @Override
            protected String doInBackground(Context... params) {
                if (eventList.size() == 0) {
                    readFromStatisticsFile(params[0]);
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                checkIfChangedUser(context);
                String time = String.valueOf(System.currentTimeMillis());
                eventList.add(new StatisticsEvent(eventKey, String.valueOf("0"), time, StatisticsEvent.TYPE_VALUE));
                asyncWriteFileAndCheckUpload(context, eventKey);
            }
        }.execute(context);
    }

    /*按值打点，统计数值*/
    public void onEvent(final Context outerContext, final String eventKey, final String value) {
        final Context context = outerContext.getApplicationContext();
        new AsyncTask<Context, Void, String>() {

            @Override
            protected String doInBackground(Context... params) {
                if (eventList.size() == 0) {
                    readFromStatisticsFile(params[0]);
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                checkIfChangedUser(context);
                String time = String.valueOf(System.currentTimeMillis());
                eventList.add(new StatisticsEvent(eventKey, value, time, StatisticsEvent.TYPE_VALUE));
                asyncWriteFileAndCheckUpload(context, eventKey);
            }
        }.execute(context);
    }

    /**
     * 如果检测到更改用户，就把之前打点的数据先上传出去
     *
     * @param context
     */
    private void checkIfChangedUser(Context context) {
        if (canUpload && isChangedUser()) {
            if (getStatisticsHandler().hasMessages(MSG_UPLOAD)) {
                return;
            }
            Message msg = getStatisticsHandler().obtainMessage(MSG_UPLOAD);
            msg.obj = context;
            getStatisticsHandler().handleMessage(msg);
        }
    }

    private void asyncWriteFileAndCheckUpload(final Context context, final String eventKey) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                writeToStatisticsFile(context);
                upload(context, eventKey);
            }
        }).start();
    }

    private void upload(Context context, String eventKey) {
        if (canUpload && (isUploadKey(eventKey) || checkUpload(context))) {
            if (getStatisticsHandler().hasMessages(MSG_UPLOAD)) {
                return;
            }
            Message msg = getStatisticsHandler().obtainMessage(MSG_UPLOAD);
            msg.obj = context;
            getStatisticsHandler().handleMessage(msg);
        }
    }

    private boolean isUploadKey(String eventKey) {
        return config != null && config.getUploadKeyList() != null && config.getUploadKeyList().contains(eventKey);
    }

    public void uploadManually(Context context) {
        if (canUpload) {
            if (getStatisticsHandler().hasMessages(MSG_UPLOAD)) {
                return;
            }
            Message msg = getStatisticsHandler().obtainMessage(MSG_UPLOAD);
            msg.obj = context;
            getStatisticsHandler().handleMessage(msg);
        }
    }

    private synchronized void asyncReadFromStatisticsFile(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                readFromStatisticsFile(context);
            }
        }).start();
    }

    private synchronized void readFromStatisticsFile(final Context context) {
        if (eventList.size() == 0) {
            String readJsonStr = StatisticsFileHelper.getInstance().read(context);
            initFromJSONObject(readJsonStr);
        }
    }

    private synchronized void writeToStatisticsFile(final Context context) {
        JSONObject jsonObject = toJSONObject(context, true);
        StatisticsFileHelper.getInstance().write(context, jsonObject.toString());
    }

    private boolean isChangedUser() {
        String userId = config == null ? null : config.getUserId();
        return !android.text.TextUtils.isEmpty(userId) && !userId.equals(uploadUserId);
    }

    private boolean checkUpload(Context context) {
        if (statisticsUploadChecker == null) {
            return true;
        }
        return statisticsUploadChecker.isCanUpload(lastUploadTime, String.valueOf(eventList.size()));
    }

    private Handler getStatisticsHandler() {
        return statisticsHandler;
    }

    /**
     * 需要上传时会通过此接口回调
     */
    public interface IOnUploadListener {
        void onUpload(String uploadData);
    }

    static class StatisticsHandler extends Handler {
        private final WeakReference<StatisticsManager> outerReference;

        public StatisticsHandler(Looper looper, StatisticsManager outerReference) {
            super(looper);
            this.outerReference = new WeakReference<>(outerReference);
        }

        @Override
        public void handleMessage(Message msg) {
            StatisticsManager statisticsManager = outerReference.get();
            if (statisticsManager == null) {
                return;
            }
            switch (msg.what) {
                case MSG_UPLOAD:
                    statisticsManager.uploadStatisticsData((Context) msg.obj);
                    break;
            }
        }
    }
}
