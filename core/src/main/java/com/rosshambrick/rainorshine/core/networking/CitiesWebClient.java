package com.rosshambrick.rainorshine.core.networking;

import com.rosshambrick.rainorshine.core.networking.entities.CitiesData;

import retrofit.http.GET;
import rx.Observable;

public interface CitiesWebClient {
    @GET("/citiesJSON?formatted=true&north=44.1&south=-9.9&east=-22.4&west=55.2&username=rainorshine&style=full&maxRows=30")
    Observable<CitiesData> getCities();
}
