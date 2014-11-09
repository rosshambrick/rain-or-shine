package com.rosshambrick.rainorshine;

import dagger.ObjectGraph;

public class Injector {
    private static ObjectGraph sObjectGraph;

    public static void create(ObjectGraph objectGraph) {
        sObjectGraph = objectGraph;
    }

    public static void inject(Object object) {
        sObjectGraph.inject(object);
    }
}
