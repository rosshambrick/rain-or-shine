package com.rosshambrick.rainorshine.core.model.services;

import com.rosshambrick.rainorshine.core.model.entities.CityWeather;
import com.rosshambrick.rainorshine.core.networking.CitiesWebClient;
import com.rosshambrick.rainorshine.core.networking.WeatherWebClient;
import com.rosshambrick.rainorshine.core.networking.model.CitiesData;
import com.rosshambrick.rainorshine.core.networking.model.WeatherData;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;

public class WeatherRepo {

    private static final String TAG = WeatherRepo.class.getSimpleName();

    private WeatherWebClient mWeatherWebClient;
    private CitiesWebClient mCitiesWebClient;
    private Observable<List<CityWeather>> mCitiesWithWeatherCache;
    private Observable<String> mCitiesCache;

    @Inject
    public WeatherRepo(WeatherWebClient weatherWebClient, CitiesWebClient citiesWebClient) {
        mWeatherWebClient = weatherWebClient;
        mCitiesWebClient = citiesWebClient;
    }

    public Observable<CityWeather> getCityById(final long cityId) {
        return getCitiesWithWeatherCache()
                .flatMap(new Func1<List<CityWeather>, Observable<? extends CityWeather>>() {
                    @Override
                    public Observable<? extends CityWeather> call(List<CityWeather> cityWeathers) {
                        return Observable.from(cityWeathers);
                    }
                })
                .filter(new Func1<CityWeather, Boolean>() {
                    @Override
                    public Boolean call(CityWeather cityWeather) {
                        return cityWeather.getId() == cityId;
                    }
                });
    }

    public Observable<List<CityWeather>> getCitiesWithWeatherCache() {
        if (mCitiesWithWeatherCache == null) {
            mCitiesWithWeatherCache = getCitiesWithWeather().cache();
        }
        return mCitiesWithWeatherCache;
    }

    private Observable<List<CityWeather>> getCitiesWithWeather() {
        return getCitiesCache()
                .flatMap(new Func1<String, Observable<WeatherData>>() {
                    @Override
                    public Observable<WeatherData> call(String cityAndCountryCode) {
                        return mWeatherWebClient.getWeatherByQuery(cityAndCountryCode);
                    }
                })
                .filter(new Func1<WeatherData, Boolean>() {
                    @Override
                    public Boolean call(WeatherData weatherData) {
                        return weatherData.id != 0;
                    }
                })
                .map(new Func1<WeatherData, CityWeather>() {
                    @Override
                    public CityWeather call(WeatherData weatherData) {
                        return weatherData.toCityWeather();
                    }
                })
                .toSortedList(new Func2<CityWeather, CityWeather, Integer>() {
                    @Override
                    public Integer call(CityWeather cityWeather, CityWeather cityWeather2) {
                        return cityWeather.getName().compareTo(cityWeather2.getName());
                    }
                });
    }

    private Observable<String> getCitiesCache() {
        if (mCitiesCache == null) {
            mCitiesCache = getCities().cache();
        }
        return mCitiesCache;
    }

    private Observable<String> getCities() {
        return mCitiesWebClient.getCities()
                .flatMap(new Func1<CitiesData, Observable<? extends String>>() {
                    @Override
                    public Observable<? extends String> call(final CitiesData citiesData) {
                        return Observable.create(new Observable.OnSubscribe<String>() {
                            public void call(Subscriber<? super String> subscriber) {
                                for (CitiesData.City geoname : citiesData.geonames) {
                                    String cityAndCountryCode = String.format("%s,%s", geoname.name, geoname.countrycode);
                                    subscriber.onNext(cityAndCountryCode);
                                }
                                subscriber.onCompleted();
                            }
                        });
                    }
                });
    }

}
