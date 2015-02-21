package com.rosshambrick.rainorshine.webservices.geonames;

import com.rosshambrick.rainorshine.webservices.NetworkActivity;

import retrofit.Profiler;
import retrofit.RestAdapter;
import retrofit.http.GET;
import rx.Observable;
import rx.subjects.PublishSubject;

public interface GeoNamesClient {
    public class Factory {
        public static GeoNamesClient create(PublishSubject<NetworkActivity> networkSubject) {
            return new RestAdapter.Builder()
                    .setEndpoint("http://api.geonames.org/")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setErrorHandler(retrofitError -> {
                        networkSubject.onError(retrofitError);
                        return retrofitError;
                    })
                    .setProfiler(new Profiler() {
                        public Object beforeCall() {
                            networkSubject.onNext(NetworkActivity.STARTED);
                            return null;
                        }

                        public void afterCall(RequestInformation requestInformation, long l, int i, Object o) {
                            networkSubject.onNext(NetworkActivity.ENDED);
                        }
                    })
                    .build()
                    .create(GeoNamesClient.class);
        }
    }

    @GET("/citiesJSON?formatted=true&north=44.1&south=-9.9&east=-22.4&west=55.2&username=rainorshine&style=full&maxRows=30")
    Observable<GeoNamesCities> getCities();
}
