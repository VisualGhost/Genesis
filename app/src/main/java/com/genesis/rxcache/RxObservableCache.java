package com.genesis.rxcache;


import android.support.v4.util.LruCache;

import io.reactivex.observables.ConnectableObservable;

public class RxObservableCache {

    private final static int MAX_SIZE = 10;

    private final LruCache<Class<?>, ConnectableObservable<?>> mLruCache;

    public RxObservableCache() {
        mLruCache = new LruCache<>(MAX_SIZE);
    }

    public void put(Class<?> keyClass, ConnectableObservable<?> observable) {
        mLruCache.put(keyClass, observable);
    }

    public ConnectableObservable<?> get(Class<?> keyClass) {
        return mLruCache.get(keyClass);
    }
}
