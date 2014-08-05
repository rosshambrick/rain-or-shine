package com.rosshambrick.rainorshine;

import com.rosshambrick.rainorshine.controllers.MainActivity;
import com.rosshambrick.rainorshine.controllers.WeatherDetailFragment;
import com.rosshambrick.rainorshine.controllers.WeatherFragment;
import com.rosshambrick.rainorshine.core.networking.CitiesWebClient;
import com.rosshambrick.rainorshine.core.networking.WeatherWebClient;
import com.rosshambrick.rainorshine.model.events.NetworkCallEndedEvent;
import com.rosshambrick.rainorshine.model.events.NetworkCallStartedEvent;
import com.rosshambrick.rainorshine.model.events.NetworkErrorOccurred;
import com.rosshambrick.rainorshine.model.services.WeatherRepo;

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
    public WeatherWebClient provideWeatherWebClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setServer("http://api.openweathermap.org/data/2.5")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError retrofitError) {
                        EventBus.getDefault().post(new NetworkErrorOccurred(retrofitError));
                        return retrofitError;
                    }
                })
//                .setProfiler(new Profiler() {
//                    @Override
//                    public Object beforeCall() {
//                        EventBus.getDefault().post(new NetworkCallStartedEvent());
//                        return null;
//                    }
//
//                    @Override
//                    public void afterCall(RequestInformation requestInformation, long l, int i, Object o) {
//                        EventBus.getDefault().post(new NetworkCallEndedEvent());
//                    }
//                })
                .build();
        return restAdapter.create(WeatherWebClient.class);
    }

    @Provides
    public CitiesWebClient provideCitiesWebClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setServer("http://api.geonames.org/")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError retrofitError) {
                        EventBus.getDefault().post(new NetworkErrorOccurred(retrofitError));
                        return retrofitError;
                    }
                })
                .setProfiler(new Profiler() {
                    @Override
                    public Object beforeCall() {
                        EventBus.getDefault().post(new NetworkCallStartedEvent());
                        return null;
                    }

                    @Override
                    public void afterCall(RequestInformation requestInformation, long l, int i, Object o) {
                        EventBus.getDefault().post(new NetworkCallEndedEvent());
                    }
                })
                .build();
        return restAdapter.create(CitiesWebClient.class);
    }

    @Provides
    @Singleton
    public WeatherRepo provideWeatherRepo(WeatherWebClient weatherWebClient, CitiesWebClient citiesWebClient) {
        return new WeatherRepo(weatherWebClient, citiesWebClient);
    }
}
