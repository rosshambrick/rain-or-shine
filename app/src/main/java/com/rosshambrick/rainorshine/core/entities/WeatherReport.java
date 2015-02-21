package com.rosshambrick.rainorshine.core.entities;

public class WeatherReport {
    private long id;
    private String name;
    private double currentTempInKelvin;
    private double highTempInKelvin;
    private double lowTempInKelvin;
    private String weatherImageUrl;
    private String weatherConditions;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCurrentTempInKelvin(double currentTempInKelvin) {
        this.currentTempInKelvin = currentTempInKelvin;
    }

    public void setHighTempInKelvin(double highTempInKelvin) {
        this.highTempInKelvin = highTempInKelvin;
    }

    public void setLowTempInKelvin(double lowTempInKelvin) {
        this.lowTempInKelvin = lowTempInKelvin;
    }

    public double getCurrentTempInFahrenheit() {
        return toFahrenheit(currentTempInKelvin);
    }

    public double getHighTempInFahrenheit() {
        return toFahrenheit(highTempInKelvin);
    }

    private double getLowTempInFahrenheit() {
        return toFahrenheit(lowTempInKelvin);
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
        return weatherImageUrl;
    }

    public void setWeatherImageName(String weatherImageUrl) {
        this.weatherImageUrl = weatherImageUrl;
    }

    public void setWeatherConditions(String weatherConditions) {
        this.weatherConditions = weatherConditions;
    }

    public String getWeatherConditions() {
        return weatherConditions;
    }
}
