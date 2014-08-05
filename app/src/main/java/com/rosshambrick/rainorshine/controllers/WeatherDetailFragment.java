package com.rosshambrick.rainorshine.controllers;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rosshambrick.rainorshine.R;
import com.rosshambrick.rainorshine.core.model.entities.CityWeather;
import com.rosshambrick.rainorshine.core.model.services.WeatherRepo;
import com.rosshambrick.rainorshine.core.networking.model.WeatherData;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

public class WeatherDetailFragment extends RainOrShineFragment {

    private static final String CITY_ID = "city_id";

    @Inject WeatherRepo mWeatherRepo;

    private TextView mCityView;
    private long mCityId;
    private Subscription mSubscription;

    public static Fragment newInstance(long id) {
        Fragment fragment = new WeatherDetailFragment();
        Bundle args = new Bundle();
        args.putLong(CITY_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCityId = getArguments().getLong(CITY_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_detail, container, false);
        mCityView = (TextView) view.findViewById(R.id.fragment_weather_detail_city);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSubscription = AndroidObservable.bindFragment(this, mWeatherRepo.getCityById(mCityId))
                .subscribe(new Action1<CityWeather>() {
                    public void call(CityWeather weatherData) {
                        display(weatherData);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscription.unsubscribe();
    }

    private void display(CityWeather weatherData) {
        getActivity().setTitle(weatherData.getName());
    }
}
