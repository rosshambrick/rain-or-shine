package com.rosshambrick.rainorshine;

import android.app.Application;

public class RainOrShineApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Injector.init();

    }
}
