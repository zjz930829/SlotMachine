package com.zjz.slotmachine;

import android.app.Application;

/**
 * Description: .
 * Created by ZJZ on 2020/5/22.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(getApplicationContext());
    }
}
