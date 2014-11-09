package com.rosshambrick.rainorshine.networking;

import retrofit.Profiler;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;
import rx.subjects.PublishSubject;

public interface OpenWeatherMapClient {
    public class Factory {
        public static OpenWeatherMapClient create(PublishSubject<NetworkActivity> networkSubject) {
            return new RestAdapter.Builder()
                    .setEndpoint("http://api.openweathermap.org/data/2.5")
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
                    .create(OpenWeatherMapClient.class);
        }
    }

    @GET("/weather")
    Observable<WeatherResponseDto> getWeatherByQuery(@Query("q") String place);

    @GET("/weather")
    Observable<WeatherResponseDto> getWeatherById(@Query("id") long id);
}
