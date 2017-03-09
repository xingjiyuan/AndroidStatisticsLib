package com.dylanxing.androidstatisticslib;

import android.content.Context;
import android.util.Log;

import com.dylanxing.statistics.StatisticsConfig;
import com.dylanxing.statistics.StatisticsManager;
import com.dylanxing.statistics.upload.IStatisticsUploadChecker;
import com.dylanxing.statistics.upload.StatisticsUploadModeCount;

public class StatisticsInitHelper {
    private static final String TAG = "StatisticsInitHelper";
    private volatile static StatisticsInitHelper instance;

    private StatisticsInitHelper() {

    }

    public static StatisticsInitHelper getInstance() {
        if (instance == null) {
            synchronized (StatisticsInitHelper.class) {
                if (instance == null) {
                    instance = new StatisticsInitHelper();
                }
            }
        }
        return instance;
    }

    public void init(final Context context) {
        IStatisticsUploadChecker statisticsUploadChecker = new StatisticsUploadModeCount(2);

        StatisticsConfig statisticsConfig = new StatisticsConfig();
        statisticsConfig.addUploadKey(SampleDataStatistics.KEY_777777, SampleDataStatistics.KEY_666666);
        statisticsConfig.setDefaultChannel("test_channel_qq");
        statisticsConfig.setDeviceInfo(DeviceUtil.getDeviceInfo(context));
        statisticsConfig.setConfigProvider(new StatisticsConfig.ConfigProvider() {
            @Override
            public String getUserId() {
                /*获取用户名*/
                /*示例:
                return PreferencesManager.getInstance().getUserPreferences().getUserId();*/
                return "test_user_id";
            }

            @Override
            public String getChannel() {
                /*获取渠道信息*/
                /*示例:
                return PreferencesManager.getInstance().getChannelPreferences().getChannel();*/
                return "test_channel_qq";
            }
        });

        StatisticsManager.IOnUploadListener onUploadListener = new StatisticsManager.IOnUploadListener() {
            @Override
            public void onUpload(String uploadData) {
                Log.d(TAG, "uploadStatisticsData: " + uploadData);
                /*请求上传统计数据，根据返回的上传结果状态调用StatisticsManager中的onUploadSuccess()或onUploadFailure()方法*/
                // TODO: 上传成功是调用
                StatisticsManager.getInstance().onUploadSuccess(context);
                // TODO: 上传失败时调用
                StatisticsManager.getInstance().onUploadFailure();

                /*示例：
                ServerAPI.uploadEvent(uploadData, new INetworkListener() {
                    @Override
                    public <T> void onSuccess(ResponseInfo<T> responseInfo) {

                        try {
                            if (statisticsParser.parseUploadInfo(responseInfo.result.toString())) {
                                StatisticsManager.getInstance().onUploadSuccess(context);
                            }
                        } catch (Exception e) {
                            StatisticsManager.getInstance().onUploadFailure();
                        }
                    }

                    @Override
                    public void onFailure(HttpException httpException, String msg) {
                        StatisticsManager.getInstance().onUploadFailure();
                    }
                });*/
            }
        };
        StatisticsManager.getInstance().init(context, statisticsUploadChecker, onUploadListener, statisticsConfig);
    }

}
