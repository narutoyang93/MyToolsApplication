package com.naruto.mytoolsapplication;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

/**
 * @Purpose
 * @Author Naruto Yang
 * @CreateDate 2018/10/15
 * @Note
 */
public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
