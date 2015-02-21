package com.rosshambrick.rainorshine.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rosshambrick.rainorshine.R;
import com.rosshambrick.rainorshine.core.entities.WeatherReport;
import com.rosshambrick.rainorshine.core.managers.WeatherManager;
import com.rosshambrick.rainorshine.networking.openweathermap.OpenWeatherMapClient;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.InjectView;
import rx.Observer;

public class WeatherDetailFragment extends RainOrShineFragment implements Observer<WeatherReport> {

    public static final String ARGS_WEATHER_ID = "ARGS_WEATHER_ID";

    @Inject WeatherManager weatherManager;

    @InjectView(R.id.weather_detail_current_temperature) TextView mCurrentTemperature;
    @InjectView(R.id.weather_detail_high_temperature) TextView mHighTemperature;
    @InjectView(R.id.weather_detail_low_temperature) TextView mLowTemperature;
    @InjectView(R.id.weather_detail_weather_image) ImageView mWeatherImage;
    @InjectView(R.id.weather_detail_conditions) TextView mWeatherConditions;

    private int cityId;

    public static Fragment newInstance(int id) {
        Fragment fragment = new WeatherDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_WEATHER_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cityId = getArguments().getInt(ARGS_WEATHER_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind(weatherManager.getByCityId(cityId)).subscribe(this);
    }

    @Override
    public void onNext(WeatherReport weatherReport) {
        display(weatherReport);
    }

    private void display(WeatherReport weatherData) {
        getActivity().setTitle(weatherData.getName());
        mCurrentTemperature.setText(weatherData.getFormattedCurrentTempInFahrenheit());
        mHighTemperature.setText(weatherData.getFormattedHighTempInFahrenheit());
        mLowTemperature.setText(weatherData.getFormattedLowTempInFahrenheit());
        mWeatherConditions.setText(weatherData.getWeatherConditions());
        Picasso.with(getActivity())
                .load(String.format(OpenWeatherMapClient.WEATHER_ICON_FORMAT, weatherData.getWeatherImageUrl()))
                .into(mWeatherImage);
    }
}
