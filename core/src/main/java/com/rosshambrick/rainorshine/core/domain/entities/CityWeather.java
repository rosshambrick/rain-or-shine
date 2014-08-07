package com.rosshambrick.rainorshine.core.domain.entities;

public class CityWeather {
    private long mId;
    private String mName;
    private double mCurrentTempInKelvin;
    private double mHighTempInKelvin;
    private double mLowTempInKelvin;
    private String mWeatherImageUrl;
    private String mWeatherConditions;

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

    public void setCurrentTempInKelvin(double currentTempInKelvin) {
        mCurrentTempInKelvin = currentTempInKelvin;
    }

    public void setHighTempInKelvin(double highTempInKelvin) {
        mHighTempInKelvin = highTempInKelvin;
    }

    public void setLowTempInKelvin(double lowTempInKelvin) {
        mLowTempInKelvin = lowTempInKelvin;
    }

    public double getCurrentTempInFahrenheit() {
        return toFahrenheit(mCurrentTempInKelvin);
    }

    public double getHighTempInFahrenheit() {
        return toFahrenheit(mHighTempInKelvin);
    }

    private double getLowTempInFahrenheit() {
        return toFahrenheit(mLowTempInKelvin);
    }

    public String getFormattedCurrentTempInFahrenheit() {
        return toFormattedTempInFahrenheit(getCurrentTempInFahrenheit());
    }

    public String getFormattedHighTempInFahrenheit() {
        return toFormattedTempInFahrenheit(getHighTempInFahrenheit());
    }

    public String getFormattedLowTempInFahrenheit() {
        return toFormattedTempInFahrenheit(getLowTempInFahrenheit());
    }

    private double toFahrenheit(double kelvin) {
        return Math.round((kelvin - 273.15) * 1.8000 + 32.00);
    }

    private String toFormattedTempInFahrenheit(double fahrenheit) {
        return String.format("%.0f\u00B0F", fahrenheit);
    }

    public String getWeatherImageUrl() {
        return mWeatherImageUrl;
    }

    public void setWeatherImageName(String weatherImageUrl) {
        mWeatherImageUrl = weatherImageUrl;
    }

    public void setWeatherConditions(String weatherConditions) {
        mWeatherConditions = weatherConditions;
    }

    public String getWeatherConditions() {
        return mWeatherConditions;
    }
}
