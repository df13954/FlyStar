package com.demo.flystar;

import android.app.Application;

public class BaseApplication extends Application {
    private static BaseApplication mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static BaseApplication getContext() {
        return mContext;
    }

}
