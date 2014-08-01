package com.rosshambrick.rainorshine.networking;

import com.rosshambrick.rainorshine.model.WeatherData;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface WebClient {
    @GET("/weather")
    Observable<WeatherData> getWeatherByQuery(@Query("q") String place);

    @GET("/weather")
    Observable<WeatherData> getWeatherById(@Query("id") long id);
}
