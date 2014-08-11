package com.rosshambrick.rainorshine.networking;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface RemoteWeatherStore {
    @GET("/weather")
    Observable<WeatherResponseDto> getWeatherByQuery(@Query("q") String place);

    @GET("/weather")
    Observable<WeatherResponseDto> getWeatherById(@Query("id") long id);
}
