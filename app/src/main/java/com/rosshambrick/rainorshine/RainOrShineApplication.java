package com.rosshambrick.rainorshine;

import android.app.Application;
import android.app.FragmentManager;

import dagger.ObjectGraph;

public class RainOrShineApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

//        FragmentManager.enableDebugLogging(BuildConfig.DEBUG);

        RainOrShineModule module = new RainOrShineModule();
        ObjectGraph objectGraph = ObjectGraph.create(module);
        Injector.create(objectGraph);
    }
}
