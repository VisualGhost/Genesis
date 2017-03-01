package com.genesis.di;

import com.genesis.networking.RestClient;
import com.genesis.view.DataLoadingController;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApiModule.class)
public interface ApiComponent {

    RestClient getRestClient();

    void inject(DataLoadingController dataLoadingController);

}
