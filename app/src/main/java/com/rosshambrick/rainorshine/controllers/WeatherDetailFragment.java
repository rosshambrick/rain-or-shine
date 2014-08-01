package com.rosshambrick.rainorshine.controllers;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rosshambrick.rainorshine.R;
import com.rosshambrick.rainorshine.model.WeatherData;
import com.rosshambrick.rainorshine.model.services.WeatherRepo;
import com.rosshambrick.rainorshine.networking.WebClient;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class WeatherDetailFragment extends RainOrShineFragment {

    private static final String CITY_ID = "city_id";

    @Inject WebClient mWebClient;
    @Inject WeatherRepo mWeatherRepo;
    @Inject Observable<WeatherData> mWeatherDataObservable;

    private TextView mCityView;
    private long mCityId;

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

        mWeatherDataObservable
                .filter(new Func1<WeatherData, Boolean>() {
                    public Boolean call(WeatherData weatherData) {
                        return weatherData.id == mCityId;
                    }
                })
                .subscribe(new Action1<WeatherData>() {
                    public void call(WeatherData weatherData) {
                        display(weatherData);
                    }
                });
    }

    private void display(WeatherData weatherData) {
        getActivity().setTitle(weatherData.name);
    }
}
