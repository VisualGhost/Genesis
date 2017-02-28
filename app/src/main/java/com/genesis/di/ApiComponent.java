package com.genesis.di;

import com.genesis.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApiModule.class)
public interface ApiComponent {

    void inject(MainActivity activity);

 }
