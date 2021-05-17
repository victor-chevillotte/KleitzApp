package com.senter.demo.uhf;

import android.app.Application;

public class App extends Application {
    
    private static App mSinglton;
    
    @Override
    public void onCreate() {
        super.onCreate();
        mSinglton = this;
    }
    
    public static App AppInstance() {
        return mSinglton;
    }
    
}











