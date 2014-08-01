package com.rosshambrick.rainorshine.model.services;

import com.rosshambrick.rainorshine.core.networking.WeatherWebClient;
import com.rosshambrick.rainorshine.core.networking.model.WeatherData;

import javax.inject.Inject;

import rx.Observable;

public class WeatherRepo {

    private WeatherWebClient mWeatherWebClient;
    private Observable<WeatherData> mCache;

    @Inject
    public WeatherRepo(WeatherWebClient weatherWebClient) {
        mWeatherWebClient = weatherWebClient;
    }

    public Observable<WeatherData> getCityById(long cityId) {
        if (mCache == null) {
            mCache = mWeatherWebClient.getWeatherById(cityId).cache();
        }
        return mCache;
    }
}
