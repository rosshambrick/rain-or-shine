package com.rosshambrick.rainorshine.app;

import android.os.Bundle;
import android.view.View;

import com.rosshambrick.rainorshine.BuildConfig;
import com.rosshambrick.rainorshine.Injector;
import com.rosshambrick.standardlib.RxFragment;

import butterknife.ButterKnife;

public class RainOrShineFragment extends RxFragment {
    @Override
    protected boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }
}
