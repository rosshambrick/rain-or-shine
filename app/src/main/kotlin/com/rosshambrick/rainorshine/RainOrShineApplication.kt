package com.rosshambrick.rainorshine

import android.app.Application

import dagger.ObjectGraph

public class RainOrShineApplication() : Application() {
    override fun onCreate() {
        super.onCreate()

        //        FragmentManager.enableDebugLogging(BuildConfig.DEBUG);

        val module = RainOrShineModule()
        val objectGraph = ObjectGraph.create(module)
        Injector.create(objectGraph)

    }
}
