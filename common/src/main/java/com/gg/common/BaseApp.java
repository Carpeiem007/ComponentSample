package com.gg.common;

import android.app.Application;

public class BaseApp extends Application {

    public static BaseApp app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
}
