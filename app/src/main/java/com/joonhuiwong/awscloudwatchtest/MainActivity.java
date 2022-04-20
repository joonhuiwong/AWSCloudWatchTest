package com.joonhuiwong.awscloudwatchtest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainApplication.getInstance().log("TestGroup", "TestStream", "Test Log");
        MainApplication.getInstance().getCloudWatchLogger().log("TestGroup", "TestStream", "Test Log");
    }

}