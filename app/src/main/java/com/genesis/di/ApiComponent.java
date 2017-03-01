package com.genesis.di;

import com.genesis.MainActivity;
import com.genesis.networking.RestClient;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApiModule.class)
public interface ApiComponent {

    RestClient getRestClient();

    void inject(MainActivity activity);

 }
