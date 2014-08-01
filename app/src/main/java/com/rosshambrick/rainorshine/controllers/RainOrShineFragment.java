package com.rosshambrick.rainorshine.controllers;

import android.app.Fragment;
import android.os.Bundle;

import com.rosshambrick.rainorshine.Injector;

public class RainOrShineFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }
}
