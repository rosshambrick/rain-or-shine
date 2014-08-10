package com.rosshambrick.rainorshine.core.networking;

import com.rosshambrick.rainorshine.core.domain.entities.CityWeather;
import com.rosshambrick.rainorshine.core.networking.entities.WeatherResponseDto;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface RemoteWeatherStore {
    @GET("/weather")
    Observable<WeatherResponseDto> getWeatherByQuery(@Query("q") String place);

    @GET("/weather")
    Observable<WeatherResponseDto> getWeatherById(@Query("id") long id);
}
