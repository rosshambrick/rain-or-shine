package com.rosshambrick.rainorshine.core.managers;

import com.rosshambrick.rainorshine.core.entities.WeatherReport;

import rx.Observable;
/*
     A facade for which Fragments and Activities can use to
     get the data needed to display without understanding
     how the data is retrieved
 */
public interface WeatherManager {
    Observable<WeatherReport> getByCityId(long cityId);

    Observable<WeatherReport> getByCityName(String name);

    Observable<WeatherReport> getAll();

    Observable<WeatherReport> getTop(int count);
}
