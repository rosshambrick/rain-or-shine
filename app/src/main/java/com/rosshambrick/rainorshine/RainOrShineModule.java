package com.rosshambrick.rainorshine;

import com.rosshambrick.rainorshine.controllers.MainActivity;
import com.rosshambrick.rainorshine.controllers.WeatherDetailFragment;
import com.rosshambrick.rainorshine.controllers.WeatherFragment;
import com.rosshambrick.rainorshine.core.services.WeatherStore;
import com.rosshambrick.rainorshine.networking.NetworkActivity;
import com.rosshambrick.rainorshine.networking.RemoteCitiesStore;
import com.rosshambrick.rainorshine.networking.RemoteWeatherStore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.ErrorHandler;
import retrofit.Profiler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import rx.subjects.PublishSubject;

@Module(
        injects = {
                MainActivity.class,
                WeatherFragment.class,
                WeatherDetailFragment.class
        }
)
public class RainOrShineModule {

    @Provides
    @Singleton
    public PublishSubject<NetworkActivity> provideNetworkObservable() {
        return PublishSubject.create();
    }

    @Provides
    @Singleton
    public RemoteWeatherStore provideWeatherWebClient(final PublishSubject<NetworkActivity> networkSubject) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.openweathermap.org/data/2.5")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(new ErrorHandler() {
                    public Throwable handleError(RetrofitError retrofitError) {
                        networkSubject.onError(retrofitError);
                        return retrofitError;
                    }
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
                .build();
        return restAdapter.create(RemoteWeatherStore.class);
    }

    @Provides
    @Singleton
    public RemoteCitiesStore provideCitiesWebClient(final PublishSubject<NetworkActivity> networkSubject) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.geonames.org/")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(new ErrorHandler() {
                    public Throwable handleError(RetrofitError retrofitError) {
                        networkSubject.onError(retrofitError);
                        return retrofitError;
                    }
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
                .build();
        return restAdapter.create(RemoteCitiesStore.class);
    }

    @Provides
    @Singleton
    public WeatherStore provideWeatherStore(RemoteWeatherStore remoteWeatherStore, RemoteCitiesStore remoteCitiesStore) {
        return new WeatherStore(remoteWeatherStore, remoteCitiesStore);
    }
}
