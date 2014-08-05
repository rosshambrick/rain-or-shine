package com.rosshambrick.rainorshine.model.services;

import android.util.Log;

import com.rosshambrick.rainorshine.core.networking.CitiesWebClient;
import com.rosshambrick.rainorshine.core.networking.WeatherWebClient;
import com.rosshambrick.rainorshine.core.networking.model.CitiesData;
import com.rosshambrick.rainorshine.core.networking.model.WeatherData;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WeatherRepo {

    private static final String TAG = WeatherRepo.class.getSimpleName();

    private WeatherWebClient mWeatherWebClient;
    private CitiesWebClient mCitiesWebClient;
    private Observable<WeatherData> mCitiesWithWeatherCache;

    @Inject
    public WeatherRepo(WeatherWebClient weatherWebClient, CitiesWebClient citiesWebClient) {
        mWeatherWebClient = weatherWebClient;
        mCitiesWebClient = citiesWebClient;
    }

    public Observable<WeatherData> getCityById(final long cityId) {
        return mCitiesWithWeatherCache
                .subscribeOn(Schedulers.computation())
                .filter(new Func1<WeatherData, Boolean>() {
                    public Boolean call(WeatherData weatherData) {
                        return weatherData.id == cityId;
                    }
                });
    }

    public Observable<WeatherData> getCitiesWithWeather() {
        if (mCitiesWithWeatherCache == null) {
            Observable<WeatherData> citiesWithWeather = mCitiesWebClient.getCities()
                    .subscribeOn(Schedulers.io())
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
                    })
                    .map(new Func1<String, WeatherData>() {
                        @Override
                        public WeatherData call(String cityAndCountryCode) {
                            Log.d(TAG, "calling: getWeatherByQuery()");
                            return mWeatherWebClient.getWeatherByQuery(cityAndCountryCode);
                        }
                    })
                    .filter(new Func1<WeatherData, Boolean>() {
                        @Override
                        public Boolean call(WeatherData weatherData) {
                            return weatherData.id != 0;
                        }
                    });

            mCitiesWithWeatherCache = citiesWithWeather.cache();
        }

        return mCitiesWithWeatherCache;
    }

}
