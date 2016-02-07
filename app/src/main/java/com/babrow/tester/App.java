package com.babrow.tester;

import android.app.Application;
import android.content.Context;

/**
 * Created by babrow on 06.02.2016.
 */
public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
}
