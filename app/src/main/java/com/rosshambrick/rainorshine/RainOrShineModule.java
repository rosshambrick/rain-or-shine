package com.rosshambrick.rainorshine;

import com.rosshambrick.rainorshine.controllers.MainActivity;
import com.rosshambrick.rainorshine.controllers.WeatherDetailFragment;
import com.rosshambrick.rainorshine.controllers.WeatherFragment;
import com.rosshambrick.rainorshine.model.WeatherData;
import com.rosshambrick.rainorshine.model.events.NetworkCallEndedEvent;
import com.rosshambrick.rainorshine.model.events.NetworkCallStartedEvent;
import com.rosshambrick.rainorshine.model.events.NetworkErrorOccurred;
import com.rosshambrick.rainorshine.model.services.WeatherRepo;
import com.rosshambrick.rainorshine.networking.WebClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import retrofit.ErrorHandler;
import retrofit.Profiler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import rx.Observable;
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
    @Singleton
    public RestAdapter provideRestAdapter() {
        return new RestAdapter.Builder()
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
    }

    @Provides
    public WebClient provideWebClient(RestAdapter restAdapter) {
        return restAdapter.create(WebClient.class);
    }

    @Provides
    @Singleton
    public Observable<WeatherData> provideCitiesWeatherData(final WebClient webClient) {
        String[] cities = new String[]{
                "Atlanta,GA,USA",
                "New York,NY,USA",
                "Los Angeles,CA,USA",
                "Chicago,IL,USA",
                "New Orleans,LA,USA"
        };

        Observable<String> citiesObservable = Observable.from(cities);

        Observable<WeatherData> weatherDataObservable = citiesObservable.flatMap(
                new Func1<String, Observable<WeatherData>>() {
                    public Observable<WeatherData> call(String city) {
                        return webClient.getWeatherByQuery(city);
                    }
                }
        );

        return weatherDataObservable.cache();
    }

    @Provides
    @Singleton
    public WeatherRepo provideWeatherRepo(WebClient webClient) {
        return new WeatherRepo(webClient);
    }
}
