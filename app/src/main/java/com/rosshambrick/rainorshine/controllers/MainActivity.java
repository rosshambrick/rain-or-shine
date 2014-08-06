package com.rosshambrick.rainorshine.controllers;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.rosshambrick.rainorshine.R;
import com.rosshambrick.rainorshine.core.model.events.NetworkCallEndedEvent;
import com.rosshambrick.rainorshine.core.model.events.NetworkCallStartedEvent;
import com.rosshambrick.rainorshine.core.model.events.NetworkErrorOccurred;

import java.util.concurrent.atomic.AtomicInteger;

import de.greenrobot.event.EventBus;

public class MainActivity extends Activity {

    private AtomicInteger mNetworkCount = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new WeatherFragment())
                    .commit();
        }

        EventBus.getDefault().register(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onEventMainThread(NetworkCallStartedEvent event) {
        if (mNetworkCount.getAndIncrement() == 0) {
            setProgressBarIndeterminateVisibility(true);
        }
    }

    public void onEventMainThread(NetworkCallEndedEvent event) {
        if (mNetworkCount.decrementAndGet() == 0) {
            setProgressBarIndeterminateVisibility(false);
        }
    }

    public void onEventMainThread(NetworkErrorOccurred event) {
        Toast.makeText(this, "Error: " + event.getRetrofitError(), Toast.LENGTH_LONG).show();
    }

}
