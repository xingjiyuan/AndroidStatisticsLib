package com.dylanxing.statistics.upload;


import com.dylanxing.statistics.utils.DateUtils;

public class StatisticsUploadModeDay implements IStatisticsUploadChecker {

    @Override
    public boolean isCanUpload(String... args) {
        String lastUploadTime = args[0];
        return android.text.TextUtils.isEmpty(lastUploadTime) || isNewDay(lastUploadTime);
    }

    private boolean isNewDay(String lastUploadTime) {
        return !DateUtils.isSameDay(Long.parseLong(lastUploadTime));
    }
}
