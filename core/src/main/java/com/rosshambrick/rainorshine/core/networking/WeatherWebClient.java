package com.rosshambrick.rainorshine.core.networking;

import com.rosshambrick.rainorshine.core.networking.model.WeatherData;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface WeatherWebClient {
    @GET("/weather")
    Observable<WeatherData> getWeatherByQuery(@Query("q") String place);

    @GET("/weather")
    Observable<WeatherData> getWeatherById(@Query("id") long id);
}
