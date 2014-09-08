package com.rosshambrick.rainorshine.networking

import retrofit.http.GET
import retrofit.http.Query
import rx.Observable

public trait RemoteWeatherStore {
    GET("/weather")
    public fun getWeatherByQuery(Query("q") place: String): Observable<WeatherResponseDto>

    GET("/weather")
    public fun getWeatherById(Query("id") id: Long): Observable<WeatherResponseDto>
}
