package com.genesis.di;

import com.genesis.BuildConfig;
import com.genesis.networking.ServerApi;
import com.genesis.rxcache.RxObservableCache;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


@Module
class ApiModule {

    @Provides
    @Singleton
    ServerApi provideRestClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
                .create(ServerApi.class);
    }

    @Provides
    @Singleton
    RxObservableCache provideRxCache() {
        return new RxObservableCache();
    }
}
