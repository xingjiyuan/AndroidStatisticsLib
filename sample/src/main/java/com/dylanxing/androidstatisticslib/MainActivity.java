package com.dylanxing.androidstatisticslib;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.clickable_1).setOnClickListener(this);
        findViewById(R.id.clickable_2).setOnClickListener(this);
        findViewById(R.id.clickable_3).setOnClickListener(this);
        findViewById(R.id.clickable_4).setOnClickListener(this);
    }

    private Context getContext() {
        return MainActivity.this;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clickable_1:
                ClickAgent.onEvent(getContext(), SampleDataStatistics.KEY_111111);
                break;
            case R.id.clickable_2:
                ClickAgent.onEvent(getContext(), SampleDataStatistics.KEY_111112);
                break;
            case R.id.clickable_3:
                ClickAgent.onEvent(getContext(), SampleDataStatistics.KEY_666666);
                break;
            case R.id.clickable_4:
                ClickAgent.onEvent(getContext(), SampleDataStatistics.KEY_777777);
                break;
        }
    }
}
