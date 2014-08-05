package com.rosshambrick.rainorshine.core.model.entities;

public class CityWeather {
    private long mId;
    private String mName;
    private double mTemperatureInKelvin;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setTemperatureInKelvin(double temperatureInKelvin) {
        mTemperatureInKelvin = temperatureInKelvin;
    }

    public double getTemperatureInKelvin() {
        return mTemperatureInKelvin;
    }

    public double getTemperatrureInFahrenheit() {
        return Math.round((mTemperatureInKelvin - 273.15) * 1.8000 + 32.00);
    }
}
