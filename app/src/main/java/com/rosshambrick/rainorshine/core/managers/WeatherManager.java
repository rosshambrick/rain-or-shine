package com.rosshambrick.rainorshine.core.managers;

import com.rosshambrick.rainorshine.core.entities.WeatherReport;

import java.util.List;

import rx.Observable;
/*
     A facade for which Fragments and Activities can use to
     get the data needed to display without understanding
     how the data is retrieved
 */
public interface WeatherManager {
    Observable<WeatherReport> getByCityId(final int cityId);

    Observable<WeatherReport> searchWeatherForCity(String name);

    Observable<List<WeatherReport>> getAll();
}
