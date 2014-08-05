package com.rosshambrick.rainorshine.controllers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rosshambrick.rainorshine.R;
import com.rosshambrick.rainorshine.core.networking.model.WeatherData;
import com.rosshambrick.rainorshine.model.services.WeatherRepo;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;

public class WeatherFragment extends RainOrShineFragment
        implements AdapterView.OnItemClickListener, Observer<WeatherData> {

    private static final String TAG = WeatherFragment.class.getSimpleName();

    @Inject WeatherRepo mCitiesWeatherData;

    private ListView mListView;
    private Subscription mSubscription;
    private ArrayList<WeatherData> mCityWeathers = new ArrayList<WeatherData>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mListView = (ListView) view.findViewById(R.id.fragment_main_list);
        mListView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setProgressBarIndeterminateVisibility(true);
        mSubscription = AndroidObservable.bindFragment(this, mCitiesWeatherData.getCitiesWithWeather())
                .subscribe(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.cities);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscription.unsubscribe();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long itemId) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, WeatherDetailFragment.newInstance(itemId))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onNext(WeatherData weatherData) {
        Log.d(TAG, "onNext");
        mCityWeathers.add(weatherData);
    }

    @Override
    public void onCompleted() {
        Log.d(TAG, "onCompleted");
        mListView.setAdapter(new WeatherDataAdapter(mCityWeathers));
        getActivity().setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onError(Throwable e) {
        Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    private class WeatherDataAdapter extends ArrayAdapter<WeatherData> {
        public WeatherDataAdapter(ArrayList<WeatherData> weatherData) {
            super(getActivity(), 0, weatherData);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(TAG, "getView");

            LinearLayout cityView = (LinearLayout) convertView;
            ViewHolder viewHolder;

            if (cityView == null) {
                cityView = (LinearLayout) LayoutInflater.from(getContext())
                        .inflate(R.layout.row_city, parent, false);

                viewHolder = new ViewHolder(cityView);
                cityView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) cityView.getTag();
            }

            WeatherData cityWeather = getItem(position);

            double tempInKelvin = cityWeather.main.temp;
            long tempInFahrenheit = Math.round((tempInKelvin - 273.15) * 1.8000 + 32.00);

            viewHolder.cityNameView.setText(String.format("%s", cityWeather.name));
            viewHolder.cityTempView.setText(String.format("%s\u00B0F", tempInFahrenheit));

            return cityView;
        }

        public class ViewHolder {
            public final TextView cityNameView;
            public final TextView cityTempView;

            public ViewHolder(View view) {
                cityNameView = (TextView) view.findViewById(R.id.row_city_name);
                cityTempView = (TextView) view.findViewById(R.id.row_city_temp);
            }
        }

    }

}
