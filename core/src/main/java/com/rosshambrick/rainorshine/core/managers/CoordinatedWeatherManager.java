package com.rosshambrick.rainorshine.core.managers;

import com.rosshambrick.rainorshine.core.entities.WeatherReport;
import com.rosshambrick.rainorshine.networking.GeoNamesCities;
import com.rosshambrick.rainorshine.networking.GeoNamesClient;
import com.rosshambrick.rainorshine.networking.OpenWeatherMapClient;
import com.rosshambrick.rainorshine.networking.WeatherResponseDto;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;

@Singleton
public class CoordinatedWeatherManager implements WeatherManager {

    private static final String TAG = CoordinatedWeatherManager.class.getSimpleName();

    private OpenWeatherMapClient mOpenWeatherMapClient;
    private GeoNamesClient mRemoteCityStore;
    private Observable<WeatherReport> mCityWeathersCache;
    private Observable<String> mCitiesCache;

    @Inject
    public CoordinatedWeatherManager(OpenWeatherMapClient openWeatherMapClient, GeoNamesClient remoteCityStore) {
        mOpenWeatherMapClient = openWeatherMapClient;
        mRemoteCityStore = remoteCityStore;
        mCitiesCache = getCities().cache();
        mCityWeathersCache = getCitiesWithWeather().cache();
    }

    @Override
    public Observable<WeatherReport> getCityById(final long cityId) {
        return mCityWeathersCache
                .flatMap(Observable::from)
                .filter(cityWeather -> cityWeather.getId() == cityId);
    }

    @Override
    public Observable<WeatherReport> getCityByName(String name) {
        return mOpenWeatherMapClient.getWeatherByQuery(name)
                .filter(weatherResponseDto -> weatherResponseDto.id != 0)
                .map(WeatherResponseDto::toCityWeather);
    }

    @Override
    public Observable<WeatherReport> getCitiesWithWeather() {
        return mCitiesCache
                .flatMap(mOpenWeatherMapClient::getWeatherByQuery)
                .filter(weatherResponseDto -> weatherResponseDto.id != 0)
                .map(WeatherResponseDto::toCityWeather);
    }

    private Observable<String> getCities() {
        return mRemoteCityStore.getCities()
                .flatMap(citiesData ->
                        Observable.create((Subscriber<? super String> subscriber) -> {
                            for (GeoNamesCities.City geoname : citiesData.geonames) {
                                String cityAndCountryCode = String.format("%s,%s", geoname.name, geoname.countrycode);
                                subscriber.onNext(cityAndCountryCode);
                            }
                            subscriber.onCompleted();
                        }));
    }

    @Override
    public Observable<WeatherReport> getTop(int count) {
        return mCityWeathersCache.take(count);
    }
}
