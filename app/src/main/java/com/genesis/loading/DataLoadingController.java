package com.genesis.loading;


import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.genesis.BuildConfig;
import com.genesis.CustomApplication;
import com.genesis.di.ApiComponent;
import com.genesis.networking.ServerApi;
import com.genesis.networking.model.BestSellersHistory;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;

public class DataLoadingController {

    private static final String LOADING = "loading";
    private static final String DATA_LOADED = "dataLoaded";
    private static final String RESULT = "Result";
    private static final String API_KEY = BuildConfig.API_KEY;

    private boolean mIsLoading;
    private boolean mIsDataLoaded;
    private int mResult;

    @Inject
    public ServerApi mServerApi;

    private ConnectableObservable<BestSellersHistory> mConnectableObservable;
    private ResourceObserver<BestSellersHistory> mResourceObserver;

    private TextView mResultTextView;

    public DataLoadingController(Context context, Bundle state, TextView textView) {
        ApiComponent apiComponent = ((CustomApplication) context.getApplicationContext())
                .getApiComponent();
        apiComponent.inject(this);
        restoreLoadingState(state);
        mResultTextView = textView;
    }

    public void saveLoadingState(Bundle state) {
        state.putBoolean(LOADING, mIsLoading);
        state.putBoolean(DATA_LOADED, mIsDataLoaded);
        state.putInt(RESULT, mResult);
    }

    private void restoreLoadingState(Bundle state) {
        if (state != null) {
            mIsLoading = state.getBoolean(LOADING);
            mIsDataLoaded = state.getBoolean(DATA_LOADED);
            mResult = state.getInt(RESULT);
        }
    }

    public boolean isDataLoaded() {
        return mIsDataLoaded;
    }

    @SuppressWarnings("unchecked")
    public void subscribeOn(Object observable) {
        mConnectableObservable = (ConnectableObservable<BestSellersHistory>) observable;
        if (mConnectableObservable == null) {
            mConnectableObservable = getBestSellersHistoryObservable();
        }
        subscribe();
    }

    private void subscribe() {
        mResourceObserver = getResourceObserver();
        mConnectableObservable.subscribe(mResourceObserver);
        mConnectableObservable.connect();
    }

    private ConnectableObservable<BestSellersHistory> getBestSellersHistoryObservable() {
        mIsLoading = true;
        return mServerApi.getBestSellersHistory(API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .replay();
    }

    private ResourceObserver<BestSellersHistory> getResourceObserver() {
        return new ResourceObserver<BestSellersHistory>() {
            @Override
            public void onNext(BestSellersHistory bestSellersHistory) {
                mResult = bestSellersHistory.getNumResults();
                mResultTextView.setText(getResult());
            }

            @Override
            public void onError(Throwable e) {
                mIsLoading = false;
            }

            @Override
            public void onComplete() {
                mIsLoading = false;
                mIsDataLoaded = true;
            }
        };
    }

    public String getResult() {
        return String.valueOf(mResult);
    }

    public void clear() {
        if (mResourceObserver != null && !mResourceObserver.isDisposed()) {
            mResourceObserver.dispose();
        }
    }

    public ConnectableObservable<BestSellersHistory> getConnectableObservable() {
        return mIsLoading ? mConnectableObservable : null;
    }
}
