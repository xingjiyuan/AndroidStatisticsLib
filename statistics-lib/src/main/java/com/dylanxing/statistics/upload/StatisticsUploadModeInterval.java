package com.dylanxing.statistics.upload;


public class StatisticsUploadModeInterval implements IStatisticsUploadChecker {

    private int intervalTime;

    public StatisticsUploadModeInterval(int intervalTime) {
        this.intervalTime = intervalTime;
    }

    @Override
    public boolean isCanUpload(String... args) {
        String lastUploadTime = args[0];
        try {
            Long lastUploadTimeMillis = Long.valueOf(lastUploadTime);
            return android.text.TextUtils.isEmpty(lastUploadTime) || System.currentTimeMillis() - lastUploadTimeMillis > intervalTime;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
