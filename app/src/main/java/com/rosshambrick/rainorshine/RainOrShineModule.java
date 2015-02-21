package com.rosshambrick.rainorshine;

import com.rosshambrick.rainorshine.app.WeatherListActivity;
import com.rosshambrick.rainorshine.app.WeatherDetailFragment;
import com.rosshambrick.rainorshine.app.WeatherListFragment;
import com.rosshambrick.rainorshine.core.managers.CoordinatedWeatherManager;
import com.rosshambrick.rainorshine.core.managers.WeatherManager;
import com.rosshambrick.rainorshine.networking.NetworkActivity;
import com.rosshambrick.rainorshine.networking.geonames.GeoNamesClient;
import com.rosshambrick.rainorshine.networking.openweathermap.OpenWeatherMapClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.subjects.PublishSubject;

@Module(
        library = true,
        injects = {
                WeatherListActivity.class,
                WeatherListFragment.class,
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
    public OpenWeatherMapClient provideOpenWeatherMapClient(final PublishSubject<NetworkActivity> networkSubject) {
        return OpenWeatherMapClient.Factory.create(networkSubject);
    }

    @Provides
    @Singleton
    public GeoNamesClient provideGeoNamesClient(final PublishSubject<NetworkActivity> networkSubject) {
        return GeoNamesClient.Factory.create(networkSubject);
    }

    @Provides
    @Singleton
    public WeatherManager provideWeatherManager(OpenWeatherMapClient openWeatherMapClient, GeoNamesClient geoNamesClient) {
        return new CoordinatedWeatherManager(openWeatherMapClient, geoNamesClient);
    }
}
