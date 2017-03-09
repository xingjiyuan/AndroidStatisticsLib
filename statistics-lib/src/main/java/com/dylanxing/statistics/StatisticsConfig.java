package com.dylanxing.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatisticsConfig {
    private List<String> uploadKeyList = new ArrayList<>();
    private String defaultChannel;
    private ConfigProvider configProvider;
    private String deviceInfo;

    /**
     * 添加立即上传的事件key
     *
     * @param keys
     */
    public void addUploadKey(String... keys) {
        Collections.addAll(uploadKeyList, keys);
    }

    public List<String> getUploadKeyList() {
        return uploadKeyList;
    }

    /**
     * 默认渠道信息
     *
     * @return
     */
    public String getDefaultChannel() {
        return defaultChannel;
    }

    public void setDefaultChannel(String defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    public void setConfigProvider(ConfigProvider configProvider) {
        this.configProvider = configProvider;
    }

    public String getUserId() {
        if (configProvider != null) {
            return configProvider.getUserId();
        }
        return null;
    }

    public String getChannel() {
        if (configProvider != null) {
            return configProvider.getChannel();
        }
        return null;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public interface ConfigProvider {
        String getUserId();

        String getChannel();
    }
}
