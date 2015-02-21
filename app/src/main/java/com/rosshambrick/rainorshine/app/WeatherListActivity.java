package com.rosshambrick.rainorshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.widget.Toast;

import com.rosshambrick.rainorshine.Injector;
import com.rosshambrick.rainorshine.webservices.NetworkActivity;
import com.rosshambrick.standardlib.SingleFragmentActivity;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.subjects.PublishSubject;

public class WeatherListActivity extends SingleFragmentActivity implements Observer<NetworkActivity> {

    private AtomicInteger networkCount = new AtomicInteger(0);

    @Inject PublishSubject<NetworkActivity> networkActivity;
    private Subscription subscription;
    private WeatherListFragment fragment;

    @Override
    protected Fragment getFragment() {
        fragment = new WeatherListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        Injector.inject(this);

        subscription = AppObservable.bindActivity(this, networkActivity)
                .subscribe(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            fragment.onActivityResult(WeatherListFragment.REQUEST_SEARCH, RESULT_OK, intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }

    @Override
    public void onCompleted() {}

    @Override
    public void onError(Throwable e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        if (networkCount.decrementAndGet() == 0) {
            setSupportProgressBarIndeterminateVisibility(false);
        }
    }

    @Override
    public void onNext(NetworkActivity networkEvent) {
        switch (networkEvent) {
            case STARTED:
                if (networkCount.getAndIncrement() == 0) {
                    setSupportProgressBarIndeterminateVisibility(true);
                }
                break;
            case ENDED:
                if (networkCount.decrementAndGet() == 0) {
                    setSupportProgressBarIndeterminateVisibility(false);
                }
                break;

        }
    }
}
