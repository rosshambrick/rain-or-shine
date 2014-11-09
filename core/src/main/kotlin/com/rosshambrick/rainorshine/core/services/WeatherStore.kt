package com.rosshambrick.rainorshine.core.services

import com.rosshambrick.rainorshine.core.entities.CityWeather
import com.rosshambrick.rainorshine.networking.RemoteCitiesStore
import com.rosshambrick.rainorshine.networking.RemoteWeatherStore

import rx.Observable
import rx.Subscriber
import rx.lang.kotlin.asObservable

public class WeatherStore(private val mRemoteWeatherStore: RemoteWeatherStore, private val mRemoteCityStore: RemoteCitiesStore) {

    class object {
        private val TAG = javaClass<WeatherStore>().getSimpleName()
    }

    private val mCityWeathersCache: Observable<CityWeather>?
    private val mCitiesCache: Observable<String>?

    {
        mCitiesCache = getCities()!!.cache()
        mCityWeathersCache = getCitiesWithWeather()!!.cache()
    }

    public fun getCityById(cityId: Long): Observable<CityWeather>? {
        return mCityWeathersCache!!.filter { cityWeather -> cityWeather?.getId() == cityId }
    }

    public fun getTop(count: Int): Observable<CityWeather>? {
        return mCityWeathersCache!!.take(count)
    }

    public fun getCityByName(name: String): Observable<CityWeather>? {
        return mRemoteWeatherStore.getWeatherByQuery(name)
                ?.filter { responseDto -> responseDto?.id != 0L }
                ?.map { weatherResponseDto -> weatherResponseDto?.toCityWeather() }
    }

    public fun getCityWeathersCache(): Observable<CityWeather>? {
        return mCityWeathersCache
    }

    private fun getCitiesWithWeather(): Observable<CityWeather>? {
        return mCitiesCache
                ?.flatMap { cityAndCountryCode -> mRemoteWeatherStore.getWeatherByQuery(cityAndCountryCode) }
                ?.filter { weatherResponseDto -> weatherResponseDto?.id != 0L }
                ?.map { weatherResponseDto -> weatherResponseDto?.toCityWeather() }
    }

    private fun getCities(): Observable<String>? {
        return mRemoteCityStore.getCities()
                ?.flatMap { citiesData ->
                    {(subscriber: Subscriber<in String>) ->
                        citiesData?.geonames?.forEach { geoname ->
                            val cityAndCountryCode = java.lang.String.format("%s,%s", geoname.name, geoname.countrycode)
                            subscriber.onNext(cityAndCountryCode)
                        }
                        subscriber.onCompleted()
                    }.asObservable()
                }
    }
}
