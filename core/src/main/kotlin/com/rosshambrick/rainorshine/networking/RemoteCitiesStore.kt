package com.rosshambrick.rainorshine.networking

import retrofit.http.GET
import rx.Observable

public trait RemoteCitiesStore {
    GET("/citiesJSON?formatted=true&north=44.1&south=-9.9&east=-22.4&west=55.2&username=rainorshine&style=full&maxRows=30")
    public fun getCities(): Observable<CitiesData>

}
