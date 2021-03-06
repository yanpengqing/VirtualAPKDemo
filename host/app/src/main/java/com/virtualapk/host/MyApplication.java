package com.virtualapk.host;

import com.didi.virtualapk.PluginManager;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        PluginManager.getInstance(base).init();
    }
}
