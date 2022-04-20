package com.joonhuiwong.awscloudwatchtest;

import android.app.Application;

public class MainApplication extends Application {
    public static MainApplication instance;

    private CloudWatchLogger cloudWatchLogger;

    public static MainApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        this.cloudWatchLogger = new CloudWatchLogger();

        // Call this when you have your credentials. You can access it from anywhere by
        // MainApplication.getInstance().getCloudWatchLogger().connect();
        this.cloudWatchLogger.connect();
    }

    public void log(String logGroupName, String logStreamName, String message) {
        this.cloudWatchLogger.log(logGroupName, logStreamName, message);
    }

    public CloudWatchLogger getCloudWatchLogger() {
        return cloudWatchLogger;
    }

}
