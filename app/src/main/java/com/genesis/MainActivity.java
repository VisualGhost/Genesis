package com.genesis;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.genesis.networking.ServerApi;
import com.genesis.networking.model.BestSellersHistory;
import com.genesis.rxcache.RxObservableCache;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String DATA_LOADED_KEY = "dataLoadedKey";
    private static final String ERROR_DATA_LOADING = "errorDataLoading";

    private boolean mIsDataLoaded;
    private boolean mIsError;

    @Inject
    public ServerApi mServerApi;

    @Inject
    public RxObservableCache mRxObservableCache;


    private ResourceObserver<BestSellersHistory> resourceObserver;

    private ConnectableObservable<Integer> integerObservable;
    private ResourceObserver<Integer> mResourceObserver;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CustomApplication.sApiComponent.inject(this);

        Log.d("Test", "===> " + getLastCustomNonConfigurationInstance());

        setContentView(R.layout.activity_main);
        ConnectableObservable cachedObservable = mRxObservableCache.get(MainActivity.class);

        Log.d("Test", "CACHE: " + cachedObservable);

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

        if (cachedObservable == null) {
            createObservable();
        } else {
            integerObservable = cachedObservable;
        }

        integerObservable.subscribe(mResourceObserver);
        integerObservable.connect();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mResourceObserver != null && !mResourceObserver.isDisposed()) {
            if (!mIsDataLoaded && !mIsError) {
                mRxObservableCache.put(MainActivity.class, integerObservable);
            }
            mResourceObserver.dispose();
        }
        //resourceObserver.dispose();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return integerObservable;
    }


}
