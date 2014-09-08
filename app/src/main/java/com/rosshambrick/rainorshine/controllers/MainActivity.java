package com.rosshambrick.rainorshine.controllers;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.rosshambrick.rainorshine.Injector;
import com.rosshambrick.rainorshine.R;
import com.rosshambrick.rainorshine.networking.NetworkActivity;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.subjects.PublishSubject;

public class MainActivity extends Activity implements Observer<NetworkActivity> {

    private AtomicInteger mNetworkCount = new AtomicInteger(0);

    @Inject PublishSubject<NetworkActivity> mNetworkActivitySubject;
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        Injector.inject(this);

        mSubscription = AndroidObservable.bindActivity(this, mNetworkActivitySubject)
                .subscribe(this);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new WeatherFragment())
                    .commit();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Fragment fragment = getFragmentManager().findFragmentById(R.id.container);
            if (fragment instanceof WeatherFragment) {
                WeatherFragment weatherFragment = (WeatherFragment) fragment;
                weatherFragment.onActivityResult(WeatherFragment.REQUEST_SEARCH, RESULT_OK, intent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscription.unsubscribe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCompleted() {}

    @Override
    public void onError(Throwable e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        if (mNetworkCount.decrementAndGet() == 0) {
            setProgressBarIndeterminateVisibility(false);
        }
    }

    @Override
    public void onNext(NetworkActivity networkEvent) {
        switch (networkEvent) {
            case STARTED:
                if (mNetworkCount.getAndIncrement() == 0) {
                    setProgressBarIndeterminateVisibility(true);
                }
                break;
            case ENDED:
                if (mNetworkCount.decrementAndGet() == 0) {
                    setProgressBarIndeterminateVisibility(false);
                }
                break;

        }
    }
}
