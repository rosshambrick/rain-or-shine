package com.rosshambrick.rainorshine.core.managers;

import com.rosshambrick.rainorshine.core.entities.WeatherReport;
import com.rosshambrick.rainorshine.networking.geonames.GeoNamesCities;
import com.rosshambrick.rainorshine.networking.geonames.GeoNamesClient;
import com.rosshambrick.rainorshine.networking.openweathermap.OpenWeatherMapClient;
import com.rosshambrick.rainorshine.networking.openweathermap.WeatherResponseDto;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;

@Singleton
public class CoordinatedWeatherManager implements WeatherManager {

    // dependencies
    private OpenWeatherMapClient openWeatherMapClient;
    private GeoNamesClient geoNamesClient;

    // caches
    private Observable<WeatherReport> weatherReportsCache;
    private Observable<String> cityNamesCache;

    @Inject
    public CoordinatedWeatherManager(OpenWeatherMapClient openWeatherMapClient, GeoNamesClient remoteCityStore) {
        this.openWeatherMapClient = openWeatherMapClient;
        geoNamesClient = remoteCityStore;
        cityNamesCache = getCityNameSearchTerms().cache();
        weatherReportsCache = getAll().cache();
    }

    @Override
    public Observable<WeatherReport> getByCityId(final long cityId) {
        return weatherReportsCache
                .filter(cityWeather -> cityWeather.getId() == cityId);
    }

    @Override
    public Observable<WeatherReport> getByCityName(String name) {
        return openWeatherMapClient.getWeatherByQuery(name)
                .filter(weatherResponseDto -> weatherResponseDto.id != 0)
                .map(WeatherResponseDto::toCityWeather);
    }

    @Override
    public Observable<WeatherReport> getAll() {
        return cityNamesCache
                .flatMap(openWeatherMapClient::getWeatherByQuery)
                .filter(weatherResponseDto -> weatherResponseDto.id != 0)
                .map(WeatherResponseDto::toCityWeather);
    }

    private Observable<String> getCityNameSearchTerms() {
        return geoNamesClient.getCities()
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
        return weatherReportsCache.take(count);
    }
}
