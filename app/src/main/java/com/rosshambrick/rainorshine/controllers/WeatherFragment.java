package com.rosshambrick.rainorshine.controllers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rosshambrick.rainorshine.R;
import com.rosshambrick.rainorshine.core.domain.entities.CityWeather;
import com.rosshambrick.rainorshine.core.domain.services.WeatherStore;
import com.rosshambrick.rainorshine.networking.Urls;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.android.observables.AndroidObservable;

public class WeatherFragment extends RainOrShineFragment
        implements AdapterView.OnItemClickListener, Observer<List<CityWeather>> {

    private static final String TAG = WeatherFragment.class.getSimpleName();

    @Inject WeatherStore mWeatherStore;

    private ListView mListView;

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

        mSubscriptions.add(AndroidObservable
                .bindFragment(this, mWeatherStore.getCitiesWithWeatherCache())
                .subscribe(this));
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.cities);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long itemId) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, WeatherDetailFragment.newInstance(itemId))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onNext(List<CityWeather> weatherData) {
        Log.d(TAG, "onNext");
        mListView.setAdapter(new WeatherDataAdapter(weatherData));
    }

    @Override
    public void onCompleted() {
        // do nothing
    }

    @Override
    public void onError(Throwable e) {
        Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    private class WeatherDataAdapter extends ArrayAdapter<CityWeather> {
        public WeatherDataAdapter(List<CityWeather> weatherData) {
            super(getActivity(), 0, weatherData);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
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

            CityWeather cityWeather = getItem(position);

            viewHolder.cityNameView.setText(String.format("%s", cityWeather.getName()));
            viewHolder.cityTempView.setText(cityWeather.getFormattedCurrentTempInFahrenheit());
            Picasso.with(getActivity())
                    .load(String.format(Urls.WEATHER_ICON_FORMAT, cityWeather.getWeatherImageUrl()))
                    .into(viewHolder.cityWeatherImageView);


            return cityView;
        }

        public class ViewHolder {
            public final TextView cityNameView;
            public final TextView cityTempView;
            public final ImageView cityWeatherImageView;

            public ViewHolder(View view) {
                cityNameView = (TextView) view.findViewById(R.id.row_city_name);
                cityTempView = (TextView) view.findViewById(R.id.row_city_temp);
                cityWeatherImageView = (ImageView) view.findViewById(R.id.row_city_weather_image);
            }
        }

    }

}
