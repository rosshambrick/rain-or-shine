package com.rosshambrick.rainorshine.core.services;

import com.rosshambrick.rainorshine.core.entities.CityWeather;
import com.rosshambrick.rainorshine.networking.CitiesData;
import com.rosshambrick.rainorshine.networking.RemoteCitiesStore;
import com.rosshambrick.rainorshine.networking.RemoteWeatherStore;
import com.rosshambrick.rainorshine.networking.WeatherResponseDto;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;

@Singleton
public class WeatherStore {

    private static final String TAG = WeatherStore.class.getSimpleName();

    private RemoteWeatherStore mRemoteWeatherStore;
    private RemoteCitiesStore mRemoteCityStore;
    private Observable<List<CityWeather>> mCityWeathersCache;
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

    public Observable<CityWeather> getCityByName(String name) {
        return mRemoteWeatherStore.getWeatherByQuery(name)
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

    private Observable<List<CityWeather>> getCitiesWithWeather() {
        return mCitiesCache
                .flatMap(new Func1<String, Observable<WeatherResponseDto>>() {
                    @Override
                    public Observable<WeatherResponseDto> call(String cityAndCountryCode) {
                        return mRemoteWeatherStore.getWeatherByQuery(cityAndCountryCode);
                    }
                })
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
                })
                .toSortedList(new Func2<CityWeather, CityWeather, Integer>() {
                    @Override
                    public Integer call(CityWeather cityWeather, CityWeather cityWeather2) {
                        return cityWeather.getName().compareTo(cityWeather2.getName());
                    }
                });
    }

    private Observable<String> getCities() {
        return mRemoteCityStore.getCities()
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

    public Observable<List<CityWeather>> getTop(int count) {
        return mCityWeathersCache.take(count);
    }
}
