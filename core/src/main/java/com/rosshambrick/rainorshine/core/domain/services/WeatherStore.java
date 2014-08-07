package com.rosshambrick.rainorshine.core.domain.services;

import com.rosshambrick.rainorshine.core.domain.entities.CityWeather;
import com.rosshambrick.rainorshine.core.networking.CitiesWebClient;
import com.rosshambrick.rainorshine.core.networking.WeatherWebClient;
import com.rosshambrick.rainorshine.core.networking.entities.CitiesData;
import com.rosshambrick.rainorshine.core.networking.entities.WeatherResponseDto;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;

public class WeatherStore {

    private static final String TAG = WeatherStore.class.getSimpleName();

    private WeatherWebClient mWeatherWebClient;
    private CitiesWebClient mCitiesWebClient;
    private Observable<List<CityWeather>> mCitiesWithWeatherCache;
    private Observable<String> mCitiesCache;

    @Inject
    public WeatherStore(WeatherWebClient weatherWebClient, CitiesWebClient citiesWebClient) {
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

    public Observable<CityWeather> getCityByName(String name) {
        return getCityWeatherObservable(mWeatherWebClient.getWeatherByQuery(name));
    }

    private Observable<List<CityWeather>> getCitiesWithWeather() {
        Observable<WeatherResponseDto> weatherResponseDtoObservable = getCitiesCache()
                .flatMap(new Func1<String, Observable<WeatherResponseDto>>() {
                    @Override
                    public Observable<WeatherResponseDto> call(String cityAndCountryCode) {
                        return mWeatherWebClient.getWeatherByQuery(cityAndCountryCode);
                    }
                });

        Observable<CityWeather> cityWeatherObservable = getCityWeatherObservable(weatherResponseDtoObservable);

        return cityWeatherObservable
                .toSortedList(new Func2<CityWeather, CityWeather, Integer>() {
                    @Override
                    public Integer call(CityWeather cityWeather, CityWeather cityWeather2) {
                        return cityWeather.getName().compareTo(cityWeather2.getName());
                    }
                });
    }

    private Observable<CityWeather> getCityWeatherObservable(Observable<WeatherResponseDto> weatherResponseDtoObservable) {
        return weatherResponseDtoObservable
                .filter(new Func1<WeatherResponseDto, Boolean>() {
                    @Override
                    public Boolean call(WeatherResponseDto weatherResponseDto) {
                        return weatherResponseDto.id != 0;
                    }
                })
                .map(new Func1<WeatherResponseDto, CityWeather>() {
                    @Override
                    public CityWeather call(WeatherResponseDto weatherResponseDto) {
                        return weatherResponseDto.toCityWeather();
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
