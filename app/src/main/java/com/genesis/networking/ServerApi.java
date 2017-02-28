package com.genesis.networking;


import com.genesis.networking.model.BestSellersHistory;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServerApi {

    @GET("lists/best-sellers/history.json")
    Observable<BestSellersHistory> getBestSellersHistory(
            @Query("api-key") String apiKey);

}
