package com.genesis;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.genesis.di.ApiComponent;
import com.genesis.networking.ServerApi;
import com.genesis.networking.model.BestSellersHistory;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String DATA_LOADED = "dataLoaded";
    private static final String ERROR_LOADING = "errorLoading";
    private static final String INT_RESULT = "intResult";
    private static final String API_KEY = BuildConfig.API_KEY;

    private boolean mIsDataLoaded;
    private boolean mIsError;
    private int mResults;

    @Inject
    public ServerApi mServerApi;

    private TextView mBestSellersResultTextView;

    private ConnectableObservable<BestSellersHistory> mBestSellersHistoryConnectableObservable;
    private ResourceObserver<BestSellersHistory> mResourceObserver;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApiComponent apiComponent = ((CustomApplication) getApplication()).getApiComponent();
        apiComponent.inject(this);

        setContentView(R.layout.activity_main);

        mBestSellersResultTextView = (TextView) findViewById(R.id.results_id);

        if (savedInstanceState != null) {
            mIsDataLoaded = savedInstanceState.getBoolean(DATA_LOADED);
            mIsError = savedInstanceState.getBoolean(ERROR_LOADING);
            mResults = savedInstanceState.getInt(INT_RESULT);
        }

        if (mIsDataLoaded) {
            mBestSellersResultTextView.setText(String.valueOf(mResults));
        } else {
            if (getLastCustomNonConfigurationInstance() != null) {
                mBestSellersHistoryConnectableObservable = (ConnectableObservable<BestSellersHistory>) getLastCustomNonConfigurationInstance();
            } else {
                mBestSellersHistoryConnectableObservable = getBestSellersHistoryObservable();
            }

            if (mBestSellersHistoryConnectableObservable != null) {
                mResourceObserver = getResourceObserver();
                mBestSellersHistoryConnectableObservable.subscribe(mResourceObserver);
                mBestSellersHistoryConnectableObservable.connect();
            }
        }
    }

    public ConnectableObservable<BestSellersHistory> getBestSellersHistoryObservable() {
        return mServerApi.getBestSellersHistory(API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .replay();
    }

    private ResourceObserver<BestSellersHistory> getResourceObserver() {
        return new ResourceObserver<BestSellersHistory>() {
            @Override
            public void onNext(BestSellersHistory bestSellersHistory) {
                mResults = bestSellersHistory.getNumResults();
                mBestSellersResultTextView.setText(String.valueOf(mResults));
            }

            @Override
            public void onError(Throwable e) {
                mIsError = true;
            }

            @Override
            public void onComplete() {
                mIsDataLoaded = true;
            }
        };
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DATA_LOADED, mIsDataLoaded);
        outState.putBoolean(ERROR_LOADING, mIsError);
        outState.putInt(INT_RESULT, mResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mResourceObserver != null && !mResourceObserver.isDisposed()) {
            mResourceObserver.dispose();
        }
        ((CustomApplication) getApplication()).getRefWatcher().watch(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return isDataLoading() ? mBestSellersHistoryConnectableObservable : null;
    }

    private boolean isDataLoading() {
        return !mIsDataLoaded && !mIsError;
    }

}
