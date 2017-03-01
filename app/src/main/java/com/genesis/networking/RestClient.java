package com.genesis.networking;

import android.util.Log;

import com.genesis.BuildConfig;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// https://github.com/square/okhttp/tree/master/mockwebserver
// response.throttleBody(1024, 1, TimeUnit.SECONDS);

@Singleton
public class RestClient {

    private static final String MOCK_SERVER_RESPONSE = "{\n" +
            "  \"status\": \"OK\",\n" +
            "  \"copyright\": \"Copyright (c) 2017 The New York Times Company.  All Rights Reserved.\",\n" +
            "  \"num_results\": 2,\n" +
            "  \"results\": [\n" +
            "    {\n" +
            "      \"title\": \"\\\"I GIVE YOU MY BODY ...\\\"\",\n" +
            "      \"author\": \"Diana Gabaldon\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"title\": \"\\\"MOST BLESSED OF THE PATRIARCHS\\\"\",\n" +
            "      \"author\": \"Annette Gordon-Reed and Peter S Onuf\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    private MockWebServer mockWebServer;


    private static final String TAG = RestClient.class.getSimpleName();

    private volatile String mBaseUrl;

    @Inject
    public RestClient() {

        Log.d(TAG, "::::: RestClient");

        mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse().setBody(MOCK_SERVER_RESPONSE).throttleBody(70, 1, TimeUnit.SECONDS));
//        mockWebServer.setDispatcher(new Dispatcher() {
//            @Override
//            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
//                SystemClock.sleep(5000);
//                return new MockResponse().setBody(MOCK_SERVER_RESPONSE);
//            }
//        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBaseUrl = mockWebServer.url("").toString();
            }
        }).start();
    }

    public void setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    public Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getOkHttpClient())
                .build();
    }

    private OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.addNetworkInterceptor(getHttpLoggingInterceptor());
        }
        return okHttpClientBuilder.build();
    }

    private HttpLoggingInterceptor getHttpLoggingInterceptor() {
        return new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(TAG, message);
            }
        }).setLevel(HttpLoggingInterceptor.Level.BODY);
    }
}
