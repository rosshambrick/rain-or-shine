package com.rosshambrick.rainorshine.core.services;

import com.rosshambrick.rainorshine.core.entities.CityWeather;
import com.rosshambrick.rainorshine.networking.CitiesData;
import com.rosshambrick.rainorshine.networking.RemoteCitiesStore;
import com.rosshambrick.rainorshine.networking.RemoteWeatherStore;
import com.rosshambrick.rainorshine.networking.WeatherResponseDto;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;

@Singleton
public class WeatherStore {

    private static final String TAG = WeatherStore.class.getSimpleName();

    private RemoteWeatherStore mRemoteWeatherStore;
    private RemoteCitiesStore mRemoteCityStore;
    private Observable<CityWeather> mCityWeathersCache;
    private Observable<String> mCitiesCache;

    @Inject
    public WeatherStore(RemoteWeatherStore remoteWeatherStore, RemoteCitiesStore remoteCityStore) {
        mRemoteWeatherStore = remoteWeatherStore;
        mRemoteCityStore = remoteCityStore;
        mCitiesCache = getCities().cache();
        mCityWeathersCache = getCitiesWithWeather().cache();
    }

    public Observable<CityWeather> getCityById(final long cityId) {
        return mCityWeathersCache
                .flatMap(Observable::from)
                .filter(cityWeather -> cityWeather.getId() == cityId);
    }

    public Observable<CityWeather> getCityByName(String name) {
        return mRemoteWeatherStore.getWeatherByQuery(name)
                .filter(weatherResponseDto -> weatherResponseDto.id != 0)
                .map(WeatherResponseDto::toCityWeather);
    }

    public Observable<CityWeather> getCitiesWithWeather() {
        return mCitiesCache
                .flatMap(mRemoteWeatherStore::getWeatherByQuery)
                .filter(weatherResponseDto -> weatherResponseDto.id != 0)
                .map(WeatherResponseDto::toCityWeather);
    }

    private Observable<String> getCities() {
        return mRemoteCityStore.getCities()
                .flatMap(citiesData ->
                        Observable.create((Subscriber<? super String> subscriber) -> {
                            for (CitiesData.City geoname : citiesData.geonames) {
                                String cityAndCountryCode = String.format("%s,%s", geoname.name, geoname.countrycode);
                                subscriber.onNext(cityAndCountryCode);
                            }
                            subscriber.onCompleted();
                        }));
    }

    public Observable<CityWeather> getTop(int count) {
        return mCityWeathersCache.take(count);
    }
}
