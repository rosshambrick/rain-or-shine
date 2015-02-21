package com.rosshambrick.rainorshine.webservices.openweathermap;

import com.rosshambrick.rainorshine.webservices.NetworkActivity;

import retrofit.Profiler;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;
import rx.subjects.PublishSubject;

public interface OpenWeatherMapClient {
    public static final String WEATHER_ICON_FORMAT = "http://openweathermap.org/img/w/%s.png";

    public class Factory {
        public static OpenWeatherMapClient create(PublishSubject<NetworkActivity> networkSubject) {
            return new RestAdapter.Builder()
                    .setEndpoint("http://api.openweathermap.org/data/2.5")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setRequestInterceptor(request -> {
                        request.addQueryParam("APPID", "d7a15a2cfa97fe82b402d8da342eec37");
                    })
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
    Observable<WeatherResponse> search(@Query("q") String place);

    @GET("/weather")
    Observable<WeatherResponse> getWeatherReportById(@Query("id") long id);
}
