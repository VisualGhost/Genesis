package com.genesis;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.genesis.networking.ServerApi;
import com.genesis.networking.model.BestSellersHistory;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String DATA_LOADED = "dataLoaded";
    private static final String ERROR_LOADING = "errorLoading";

    private boolean mIsDataLoaded;
    private boolean mIsError;

    @Inject
    public ServerApi mServerApi;


    private ResourceObserver<BestSellersHistory> resourceObserver;

    private ConnectableObservable<Integer> integerObservable;
    private ResourceObserver<Integer> mResourceObserver;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CustomApplication.sApiComponent.inject(this);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mIsDataLoaded = savedInstanceState.getBoolean(DATA_LOADED);
            mIsError = savedInstanceState.getBoolean(ERROR_LOADING);
        }

        ConnectableObservable<Integer> cachedObservable = (ConnectableObservable<Integer>) getLastCustomNonConfigurationInstance();

        mResourceObserver = new ResourceObserver<Integer>() {
            @Override
            public void onNext(Integer integer) {
                Log.d("Test", "i: " + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.d("Test", e.toString());
                mIsError = true;
            }

            @Override
            public void onComplete() {
                Log.d("Test", "onComplete");
                mIsDataLoaded = true;
            }
        };

        if (cachedObservable == null && (!mIsDataLoaded && !mIsError)) {
            Log.d("Test", "create NEW observable");
            createObservable();
        } else {
            Log.d("Test", "Use cached");
            integerObservable = cachedObservable;
        }

        if (integerObservable != null) {
            integerObservable.subscribe(mResourceObserver);
            integerObservable.connect();
        }
    }

    private void createObservable() {

        integerObservable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 20; i++) {
                    Log.d("Test", ">>>> " + i + ", " + Thread.currentThread().getName());
                    SystemClock.sleep(1000);
                    e.onNext(i);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .replay();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DATA_LOADED, mIsDataLoaded);
        outState.putBoolean(ERROR_LOADING, mIsError);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mResourceObserver != null && !mResourceObserver.isDisposed()) {
            mResourceObserver.dispose();
        }
        //resourceObserver.dispose();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return (!mIsDataLoaded && !mIsError) ? integerObservable : null;
    }


}
