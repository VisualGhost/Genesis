package com.genesis.di;

import com.genesis.networking.RestClient;
import com.genesis.networking.ServerApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class ApiModule {

    @Provides
    @Singleton
    public ServerApi provideRestClient(RestClient restClient) {
        return restClient
                .getRetrofit()
                .create(ServerApi.class);
    }
}
