package com.rosshambrick.rainorshine.networking;

import retrofit.http.GET;
import rx.Observable;

public interface RemoteCitiesStore {
    @GET("/citiesJSON?formatted=true&north=44.1&south=-9.9&east=-22.4&west=55.2&username=rainorshine&style=full&maxRows=30")
    Observable<CitiesData> getCities();

}
