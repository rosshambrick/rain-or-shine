package com.rosshambrick.rainorshine.controllers;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.rosshambrick.rainorshine.R;
import com.rosshambrick.rainorshine.core.entities.CityWeather;
import com.rosshambrick.rainorshine.core.services.WeatherStore;
import com.rosshambrick.rainorshine.networking.Urls;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observer;
import rx.android.observables.AndroidObservable;

public class WeatherFragment extends RainOrShineFragment
        implements AdapterView.OnItemClickListener, Observer<CityWeather> {

    private static final String TAG = WeatherFragment.class.getSimpleName();
    public static final int REQUEST_SEARCH = 0;

    @Inject WeatherStore mWeatherStore;

    @InjectView(R.id.fragment_main_list) ListView mListView;
    private WeatherDataAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, view);

        mListView.setOnItemClickListener(this);

        mAdapter = new WeatherDataAdapter(getActivity());
        mListView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSubscriptions.add(AndroidObservable
                .bindFragment(this, mWeatherStore.getCitiesWithWeather())
                .timeout(20, TimeUnit.SECONDS)
                .subscribe(this));
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.my_cities);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.fragment_weather, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SEARCH) {
            String query = data.getStringExtra(SearchManager.QUERY);
            mSubscriptions.add(AndroidObservable
                    .bindFragment(this, mWeatherStore.getCityByName(query))
                    .subscribe(mAdapter::add));
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long itemId) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, WeatherDetailFragment.newInstance(itemId))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCompleted() {
        //nothing to do
    }

    @Override
    public void onError(Throwable e) {
        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNext(CityWeather cityWeather) {
        mAdapter.add(cityWeather);
    }

    private class WeatherDataAdapter extends ArrayAdapter<CityWeather> {
        public WeatherDataAdapter(Context context) {
            super(context, 0, new ArrayList<>());
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
