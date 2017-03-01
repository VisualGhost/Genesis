package com.genesis;


import android.app.Application;

import com.genesis.di.ApiComponent;
import com.genesis.di.DaggerApiComponent;
import com.genesis.networking.RestClientProvider;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class CustomApplication extends Application {

    private ApiComponent mApiComponent;
    private RefWatcher mRefWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        mApiComponent = DaggerApiComponent.builder().build();
        setRestClientBaseUrl(mApiComponent.getRestClient());
        mRefWatcher = LeakCanary.install(this);
    }

    private void setRestClientBaseUrl(RestClientProvider restClientProvider) {
        restClientProvider.setBaseUrl(BuildConfig.BASE_URL);
    }

    public ApiComponent getApiComponent() {
        return mApiComponent;
    }

    public RefWatcher getRefWatcher() {
        return mRefWatcher;
    }
}
