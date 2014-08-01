package com.rosshambrick.rainorshine.core.networking.model;

public class CitiesData {
    public City[] geonames;

    public static class City {
        public String countrycode;
        public String name;
    }
}
