package com.genesis;


import android.app.Application;

import com.genesis.di.ApiComponent;
import com.genesis.di.DaggerApiComponent;

public class CustomApplication extends Application{

    public final static ApiComponent sApiComponent = DaggerApiComponent.builder().build();

}
