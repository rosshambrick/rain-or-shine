package com.rosshambrick.rainorshine;

import dagger.ObjectGraph;

public class Injector {
    private static ObjectGraph sObjectGraph;

    public static void init() {
        RainOrShineModule module = new RainOrShineModule();
        sObjectGraph = ObjectGraph.create(module);
    }

    public static <T> T inject(T object) {
        return sObjectGraph.inject(object);
    }

    public static void reset() {
        init();
    }
}
