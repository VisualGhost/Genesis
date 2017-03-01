package com.genesis.di;

import com.genesis.networking.RestClientProvider;
import com.genesis.networking.ServerApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class ApiModule {

    @Provides
    @Singleton
    public ServerApi provideServerApi(RestClientProvider restClientProvider) {
        return restClientProvider.getRetrofit().create(ServerApi.class);
    }
}
