package com.rosshambrick.rainorshine.core.managers;

import android.support.v4.util.SparseArrayCompat;

import com.rosshambrick.rainorshine.core.entities.WeatherReport;
import com.rosshambrick.rainorshine.webservices.geonames.GeoNamesClient;
import com.rosshambrick.rainorshine.webservices.openweathermap.OpenWeatherMapClient;
import com.rosshambrick.rainorshine.webservices.openweathermap.WeatherResponse;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.subjects.BehaviorSubject;

@Singleton
public class CoordinatedWeatherManager implements WeatherManager {

    // dependencies
    private OpenWeatherMapClient openWeatherMapClient;
    private GeoNamesClient geoNamesClient;

    // caches
    private BehaviorSubject<List<WeatherReport>> weatherReports;
    private BehaviorSubject<List<String>> cityNames;
    private SparseArrayCompat<BehaviorSubject<WeatherReport>> weatherReport = new SparseArrayCompat<>();

    @Inject
    public CoordinatedWeatherManager(OpenWeatherMapClient openWeatherMapClient,
                                     GeoNamesClient geoNamesClient) {
        this.openWeatherMapClient = openWeatherMapClient;
        this.geoNamesClient = geoNamesClient;
    }

    @Override
    public Observable<WeatherReport> getByCityId(final int cityId) {
        if (weatherReport.get(cityId) == null) {
            weatherReport.put(cityId, BehaviorSubject.create());

            openWeatherMapClient.getWeatherById(cityId)
                    .map(WeatherResponse::toWeatherReport)
                    .subscribe(
                            weatherReport.get(cityId)::onNext,
                            weatherReport.get(cityId)::onError
                    );
        }
        return weatherReport.get(cityId).asObservable();
    }

    @Override
    public Observable<WeatherReport> searchWeatherForCity(String name) {
        return openWeatherMapClient.getWeatherByQuery(name)
                .filter(weatherResponseDto -> weatherResponseDto.id != 0)
                .map(WeatherResponse::toWeatherReport);
    }

    @Override
    public Observable<List<WeatherReport>> getAll() {
        if (weatherReports == null) {
            weatherReports = BehaviorSubject.create();

            getCitySearchTerms()
                    .flatMap(Observable::from)
                    .flatMap(this::searchWeatherForCity)
                    .toSortedList(this::sort)
                    .subscribe(
                            weatherReports::onNext,
                            weatherReports::onError
                    );
        }

        return weatherReports.asObservable();
    }

    private int sort(WeatherReport weatherReport1, WeatherReport weatherReport2) {
        return weatherReport1.getName().compareToIgnoreCase(weatherReport2.getName());
    }

    private Observable<List<String>> getCitySearchTerms() {
        if (cityNames == null) {
            cityNames = BehaviorSubject.create();

            geoNamesClient.getCities()
                    .flatMap(cities -> Observable.from(cities.geonames))
                    .map(city -> {
                        String format = String.format("%s,%s", city.name, city.countrycode);
                        return format;
                    })
                    .toList()
                    .subscribe(
                            cityNames
                    );
        }
        return cityNames.asObservable();
    }

}
