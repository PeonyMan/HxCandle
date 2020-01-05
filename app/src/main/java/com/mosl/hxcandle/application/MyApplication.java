package com.mosl.hxcandle.application;

import android.app.Application;
import android.content.Context;

import com.mosl.hxcandle.database.manager.DBManager;

public class MyApplication extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = getApplicationContext();
        DBManager.getInstance();
    }

    public static Context getAppContext(){
        return appContext;
    }
}
