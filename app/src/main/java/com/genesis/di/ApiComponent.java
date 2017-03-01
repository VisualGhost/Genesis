package com.genesis.di;

import com.genesis.networking.RestClientProvider;
import com.genesis.loading.DataLoadingController;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApiModule.class)
public interface ApiComponent {

    RestClientProvider getRestClient();

    void inject(DataLoadingController dataLoadingController);

}
