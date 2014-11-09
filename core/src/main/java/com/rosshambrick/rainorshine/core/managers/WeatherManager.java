package com.rosshambrick.rainorshine.core.managers;

import com.rosshambrick.rainorshine.core.entities.WeatherReport;

import rx.Observable;

public interface WeatherManager {
    Observable<WeatherReport> getCityById(long cityId);

    Observable<WeatherReport> getCityByName(String name);

    Observable<WeatherReport> getCitiesWithWeather();

    Observable<WeatherReport> getTop(int count);
}
