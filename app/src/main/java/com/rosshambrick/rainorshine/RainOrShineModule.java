package com.rosshambrick.rainorshine;

import com.rosshambrick.rainorshine.controllers.MainActivity;
import com.rosshambrick.rainorshine.controllers.WeatherDetailFragment;
import com.rosshambrick.rainorshine.controllers.WeatherFragment;
import com.rosshambrick.rainorshine.model.events.NetworkCallEndedEvent;
import com.rosshambrick.rainorshine.model.events.NetworkCallStartedEvent;
import com.rosshambrick.rainorshine.model.events.NetworkErrorOccurred;
import com.rosshambrick.rainorshine.model.services.WeatherRepo;
import com.rosshambrick.rainorshine.core.networking.CitiesWebClient;
import com.rosshambrick.rainorshine.core.networking.WeatherWebClient;
import com.rosshambrick.rainorshine.core.networking.model.CitiesData;
import com.rosshambrick.rainorshine.core.networking.model.WeatherData;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import retrofit.ErrorHandler;
import retrofit.Profiler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

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
    public Observable<WeatherData> provideCitiesWeatherData(CitiesWebClient citiesWebClient, final WeatherWebClient weatherWebClient) {

        Observable<WeatherData> weatherDataObservable = citiesWebClient.getCities()
                .flatMap(new Func1<CitiesData, Observable<? extends String>>() {
                    @Override
                    public Observable<? extends String> call(final CitiesData citiesData) {
                        return Observable.create(new Observable.OnSubscribe<String>() {
                            public void call(Subscriber<? super String> subscriber) {
                                for (CitiesData.City geoname : citiesData.geonames) {
                                    subscriber.onNext(geoname.name + "," + geoname.countrycode);
                                }
                                subscriber.onCompleted();
                            }
                        });
                    }
                })
                .flatMap(new Func1<String, Observable<? extends WeatherData>>() {
                    @Override
                    public Observable<? extends WeatherData> call(String city) {
                        return weatherWebClient.getWeatherByQuery(city);
                    }
                })
                .filter(new Func1<WeatherData, Boolean>() {
                    @Override
                    public Boolean call(WeatherData weatherData) {
                        return weatherData.id != 0;
                    }
                });

        return weatherDataObservable.cache();
    }

    @Provides
    @Singleton
    public WeatherRepo provideWeatherRepo(WeatherWebClient weatherWebClient) {
        return new WeatherRepo(weatherWebClient);
    }
}
