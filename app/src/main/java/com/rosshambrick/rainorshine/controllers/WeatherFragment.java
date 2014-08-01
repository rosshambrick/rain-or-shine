package com.rosshambrick.rainorshine.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rosshambrick.rainorshine.R;
import com.rosshambrick.rainorshine.model.WeatherData;
import com.rosshambrick.rainorshine.networking.WebClient;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class WeatherFragment extends RainOrShineFragment {

    private static final String TAG = WeatherFragment.class.getSimpleName();

    @Inject Observable<WeatherData> mCitiesWeatherData;

    private LinearLayout mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mRootView = (LinearLayout) view.findViewById(R.id.fragment_main_list);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCitiesWeatherData
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<WeatherData>() {
                    public void call(WeatherData weatherData) {
                        display(weatherData);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.cities);
    }

    private void display(final WeatherData weatherData) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        ViewGroup cityRow = (ViewGroup) layoutInflater.inflate(R.layout.row_city, mRootView, false);
        TextView cityNameView = (TextView) cityRow.findViewById(R.id.row_city_name);
        TextView cityTempView = (TextView) cityRow.findViewById(R.id.row_city_temp);

        //TODO: move this to the model and test
        double tempInKelvin = weatherData.main.temp;
        long tempInFahrenheit = Math.round((tempInKelvin - 273.15) * 1.8000 + 32.00);

        cityNameView.setText(String.format("%s", weatherData.name));
        cityTempView.setText(String.format("%s\u00B0F", tempInFahrenheit));

        cityRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, WeatherDetailFragment.newInstance(weatherData.id))
                        .addToBackStack(null)
                        .commit();
            }
        });

        mRootView.addView(cityRow);
    }
}
