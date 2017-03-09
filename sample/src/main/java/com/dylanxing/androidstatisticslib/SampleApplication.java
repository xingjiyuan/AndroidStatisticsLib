package com.dylanxing.androidstatisticslib;

import android.app.Application;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        onInit();
    }

    private void onInit() {
        StatisticsInitHelper.getInstance().init(getApplicationContext());
    }
}
