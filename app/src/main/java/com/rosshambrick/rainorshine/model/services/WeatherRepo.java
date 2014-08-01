package com.rosshambrick.rainorshine.model.services;

import com.rosshambrick.rainorshine.model.WeatherData;
import com.rosshambrick.rainorshine.networking.WebClient;

import javax.inject.Inject;

import rx.Observable;

public class WeatherRepo {

    private WebClient mWebClient;
    private Observable<WeatherData> mCache;

    @Inject
    public WeatherRepo(WebClient webClient) {
        mWebClient = webClient;
    }

    public Observable<WeatherData> getCityById(long cityId) {
        if (mCache == null) {
            mCache = mWebClient.getWeatherById(cityId).cache();
        }
        return mCache;
    }
}
