package com.rosshambrick.rainorshine.core.managers;

import android.util.Log;

import com.rosshambrick.rainorshine.core.entities.WeatherReport;
import com.rosshambrick.rainorshine.webservices.geonames.GeoNamesClient;
import com.rosshambrick.rainorshine.webservices.openweathermap.OpenWeatherMapClient;
import com.rosshambrick.rainorshine.webservices.openweathermap.WeatherResponse;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.subjects.ReplaySubject;

@Singleton
public class CoordinatedWeatherManager implements WeatherManager {

    private static final String TAG = "CoordinatedWeatherManager";
    // dependencies
    private OpenWeatherMapClient openWeatherMapClient;
    private GeoNamesClient geoNamesClient;

    // caches
    private ReplaySubject<List<String>> cityNames;
//    private Observable<List<WeatherReport>> weatherReports;

    @Inject
    public CoordinatedWeatherManager(OpenWeatherMapClient openWeatherMapClient,
                                     GeoNamesClient geoNamesClient) {
        this.openWeatherMapClient = openWeatherMapClient;
        this.geoNamesClient = geoNamesClient;
    }

    @Override
    public Observable<WeatherReport> getByCityId(int cityId) {
        return openWeatherMapClient.getWeatherReportById(cityId)
                .map(WeatherResponse::toWeatherReport);
    }

    @Override
    public Observable<WeatherReport> searchWeatherForCity(String cityName) {
        return openWeatherMapClient.search(cityName)
                .filter(weatherResponse -> weatherResponse.id != 0)
                .map(WeatherResponse::toWeatherReport);
    }

    @Override
    public Observable<List<WeatherReport>> getAll() {
        return getCityNames()
                .flatMap(Observable::from)
                .flatMap(this::searchWeatherForCity)
                .toSortedList(this::sort);
    }

    private Observable<List<String>> getCityNames() {
        if (cityNames == null) {
            cityNames = ReplaySubject.create(1);

            geoNamesClient.getCities()
                    .flatMap(cities -> Observable.from(cities.geonames))
                    .map(city -> {
                        String format = String.format("%s,%s", city.name, city.countrycode);
                        return format;
                    })
                    .toList()
                    .subscribe(cityNames);
        }
        Log.d(TAG, "Returning city names");
        return cityNames.asObservable();
    }

    private int sort(WeatherReport weatherReport1, WeatherReport weatherReport2) {
        return weatherReport1.getName().compareToIgnoreCase(weatherReport2.getName());
    }

}
