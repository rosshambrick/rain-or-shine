package com.rosshambrick.rainorshine.app;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.rosshambrick.rainorshine.R;
import com.rosshambrick.rainorshine.core.entities.WeatherReport;
import com.rosshambrick.rainorshine.core.managers.WeatherManager;
import com.rosshambrick.rainorshine.networking.openweathermap.OpenWeatherMapClient;
import com.rosshambrick.standardlib.SingleFragmentActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import rx.Observer;

public class WeatherListFragment extends RainOrShineFragment
        implements AdapterView.OnItemClickListener, Observer<List<WeatherReport>> {
    private static final String TAG = "WeatherFragment";

    public static final int REQUEST_SEARCH = 0;

    @Inject WeatherManager weatherManager;

    @InjectView(R.id.fragment_main_list) ListView listView;

    private WeatherDataAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new WeatherDataAdapter(getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        bind(weatherManager.getAll()).subscribe(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.my_cities);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_weather, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) return;

        switch (requestCode) {
            case REQUEST_SEARCH:
                String query = data.getStringExtra(SearchManager.QUERY);
                bind(weatherManager.searchWeatherForCity(query)).subscribe(adapter::add);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long itemId) {
        Bundle fragmentArgs = new Bundle();
        fragmentArgs.putInt(WeatherDetailFragment.ARGS_WEATHER_ID, (int) itemId);
        startActivity(SingleFragmentActivity.newIntent(getActivity(), WeatherDetailFragment.class, fragmentArgs));
//        getFragmentManager().beginTransaction()
//                .replace(R.id.container, WeatherDetailFragment.newInstance((int) itemId))
//                .addToBackStack(null)
//                .commit();
    }

    @Override
    public void onNext(List<WeatherReport> weatherReport) {
        adapter.addAll(weatherReport);
    }

    private class WeatherDataAdapter extends ArrayAdapter<WeatherReport> {
        public WeatherDataAdapter(Context context) {
            super(context, 0, new ArrayList<>());
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
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

            WeatherReport weatherReport = getItem(position);

            viewHolder.cityNameView.setText(String.format("%s", weatherReport.getName()));
            viewHolder.cityTempView.setText(weatherReport.getFormattedCurrentTempInFahrenheit());
            Picasso.with(getActivity())
                    .load(String.format(OpenWeatherMapClient.WEATHER_ICON_FORMAT, weatherReport.getWeatherImageUrl()))
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
