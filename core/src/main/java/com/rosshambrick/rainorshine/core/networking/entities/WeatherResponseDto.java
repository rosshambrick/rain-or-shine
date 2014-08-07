package com.rosshambrick.rainorshine.core.networking.entities;

import com.rosshambrick.rainorshine.core.domain.entities.CityWeather;

@SuppressWarnings("UnusedDeclaration")
public class WeatherResponseDto {

    public Coordinates coord;
    public Local sys;
    public Weather[] weather;
    public String base;
    public Main main;
    public Wind wind;
    public Rain rain;
    public Cloud clouds;
    public long id;
    public long dt;
    public String name;
    public int cod;

    public static class Coordinates {
        public double lat;
        public double lon;
    }

    public static class Local {
        public String country;
        public long sunrise;
        public long sunset;
    }

    public static class Weather {
        public int id;
        public String main;
        public String description;
        public String icon;
    }

    public static class Main {
        public double temp;
        public double pressure;
        public double humidity;
        public double temp_min;
        public double temp_max;
        public double sea_level;
        public double grnd_level;
    }

    public static class Wind {
        public double speed;
        public double deg;
    }

    public static class Rain {
        public int threehourforecast;
    }

    public static class Cloud {
        public int all;
    }

    @Override
    public String toString() {
        return name;
    }

    public CityWeather toCityWeather() {
        CityWeather cityWeather = new CityWeather();
        cityWeather.setId(id);
        cityWeather.setName(name);
        cityWeather.setCurrentTempInKelvin(main.temp);
        cityWeather.setHighTempInKelvin(main.temp_max);
        cityWeather.setLowTempInKelvin(main.temp_min);
        if (weather != null && weather[0] != null) {
            cityWeather.setWeatherConditions(weather[0].description);
            cityWeather.setWeatherImageName(weather[0].icon);
        }
        return cityWeather;
    }
}