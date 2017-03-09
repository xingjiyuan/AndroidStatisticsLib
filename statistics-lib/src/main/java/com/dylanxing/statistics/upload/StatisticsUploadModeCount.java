package com.dylanxing.statistics.upload;


public class StatisticsUploadModeCount implements IStatisticsUploadChecker {

    private int maxCount;

    public StatisticsUploadModeCount(int maxCount) {
        this.maxCount = maxCount;
    }

    @Override
    public boolean isCanUpload(String... args) {
        try {
            int currentCount = Integer.valueOf(args[1]);
            return maxCount > 0 && currentCount >= maxCount;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
