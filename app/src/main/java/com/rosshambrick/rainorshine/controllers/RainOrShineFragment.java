package com.rosshambrick.rainorshine.controllers;

import android.app.Fragment;
import android.os.Bundle;

import com.rosshambrick.rainorshine.Injector;

import rx.subscriptions.CompositeSubscription;

public class RainOrShineFragment extends Fragment {
    protected CompositeSubscription mSubscriptions = new CompositeSubscription();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}
