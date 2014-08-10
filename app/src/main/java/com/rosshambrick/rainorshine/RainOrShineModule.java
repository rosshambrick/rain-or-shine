package com.rosshambrick.rainorshine;

import com.rosshambrick.rainorshine.controllers.MainActivity;
import com.rosshambrick.rainorshine.controllers.WeatherDetailFragment;
import com.rosshambrick.rainorshine.controllers.WeatherFragment;
import com.rosshambrick.rainorshine.core.domain.services.WeatherStore;
import com.rosshambrick.rainorshine.core.networking.RemoteCitiesStore;
import com.rosshambrick.rainorshine.core.networking.RemoteWeatherStore;
import com.rosshambrick.rainorshine.core.networking.events.NetworkCallEndedEvent;
import com.rosshambrick.rainorshine.core.networking.events.NetworkCallStartedEvent;
import com.rosshambrick.rainorshine.core.networking.events.NetworkErrorOccurred;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import retrofit.ErrorHandler;
import retrofit.Profiler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

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
    public RemoteWeatherStore provideWeatherWebClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.openweathermap.org/data/2.5")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(new ErrorHandler() {
                    public Throwable handleError(RetrofitError retrofitError) {
                        EventBus.getDefault().post(new NetworkErrorOccurred(retrofitError));
                        return retrofitError;
                    }
                })
                .setProfiler(new Profiler() {
                    public Object beforeCall() {
                        EventBus.getDefault().post(new NetworkCallStartedEvent());
                        return null;
                    }

                    public void afterCall(RequestInformation requestInformation, long l, int i, Object o) {
                        EventBus.getDefault().post(new NetworkCallEndedEvent());
                    }
                })
                .build();
        return restAdapter.create(RemoteWeatherStore.class);
    }

    @Provides
    @Singleton
    public RemoteCitiesStore provideCitiesWebClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.geonames.org/")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(new ErrorHandler() {
                    public Throwable handleError(RetrofitError retrofitError) {
                        EventBus.getDefault().post(new NetworkErrorOccurred(retrofitError));
                        return retrofitError;
                    }
                })
                .setProfiler(new Profiler() {
                    public Object beforeCall() {
                        EventBus.getDefault().post(new NetworkCallStartedEvent());
                        return null;
                    }

                    public void afterCall(RequestInformation requestInformation, long l, int i, Object o) {
                        EventBus.getDefault().post(new NetworkCallEndedEvent());
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
