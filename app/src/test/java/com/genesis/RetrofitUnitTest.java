package com.genesis;


import com.genesis.di.ApiModule;
import com.genesis.networking.RestClient;
import com.genesis.networking.ServerApi;
import com.genesis.networking.model.BestSellersHistory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class RetrofitUnitTest {

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

    private ApiModule mApiModule;
    private RestClient mRestClient;

    @Before
    public void prepareServerApi() {
        mApiModule = new ApiModule();
        mRestClient = new RestClient();
    }

    @Test
    public void getBestSellersHistoryIsCorrect() throws Exception {
        MockWebServer mockWebServer = new MockWebServer();
        mRestClient.setBaseUrl(mockWebServer.url("").toString());
        mockWebServer.enqueue(new MockResponse().setBody(MOCK_SERVER_RESPONSE));
        ServerApi serverApi = mApiModule.provideRestClient(mRestClient);

        Observable<BestSellersHistory> bestSellersHistory = serverApi
                .getBestSellersHistory(BuildConfig.API_KEY);

        TestObserver<BestSellersHistory> testObserver = new TestObserver<>();
        bestSellersHistory.subscribe(testObserver);
        testObserver.assertComplete();
        testObserver.assertNoErrors();
        List<BestSellersHistory> histories = testObserver.values();
        Assert.assertNotNull(histories);
        Assert.assertEquals(1, histories.size());

        BestSellersHistory actualHistory = histories.get(0);

        Assert.assertNotNull(actualHistory);
        Assert.assertEquals(2, actualHistory.getNumResults());
        Assert.assertEquals("OK", actualHistory.getStatus());
        Assert.assertNotNull(actualHistory.getBestSellerList());
        Assert.assertEquals(2, actualHistory.getBestSellerList().size());
        Assert.assertEquals("Copyright (c) 2017 The New York Times Company.  All Rights Reserved.", actualHistory.getCopyRight());
        Assert.assertEquals("\"I GIVE YOU MY BODY ...\"", actualHistory.getBestSellerList().get(0).getTitle());
        Assert.assertEquals("Diana Gabaldon", actualHistory.getBestSellerList().get(0).getAuthor());
        Assert.assertEquals("\"MOST BLESSED OF THE PATRIARCHS\"", actualHistory.getBestSellerList().get(1).getTitle());
        Assert.assertEquals("Annette Gordon-Reed and Peter S Onuf", actualHistory.getBestSellerList().get(1).getAuthor());

        mockWebServer.shutdown();
    }

    @Test
    public void getRealBestSellersHistoryIsCorrect() throws Exception {
        mRestClient.setBaseUrl(BuildConfig.BASE_URL);
        ServerApi serverApi = mApiModule.provideRestClient(mRestClient);
        Observable<BestSellersHistory> bestSellersHistory = serverApi
                .getBestSellersHistory(BuildConfig.API_KEY);

        TestObserver<BestSellersHistory> testObserver = new TestObserver<>();
        bestSellersHistory.subscribe(testObserver);
        testObserver.assertComplete();
        testObserver.assertNoErrors();
        List<BestSellersHistory> histories = testObserver.values();
        Assert.assertNotNull(histories);
        Assert.assertEquals(1, histories.size());

        BestSellersHistory actualHistory = histories.get(0);

        Assert.assertNotNull(actualHistory);
        Assert.assertTrue("Actual status: " + actualHistory.getStatus(), actualHistory.getStatus().equals("OK"));
        Assert.assertTrue("Actual numResults: " + actualHistory.getNumResults(), actualHistory.getNumResults() > 0);
        Assert.assertNotNull(actualHistory.getBestSellerList());
        Assert.assertEquals(20, actualHistory.getBestSellerList().size());
    }

    @Test
    public void getBestSellersHistoryAnotherApiKeyIsNotCorrect() throws Exception {
        mRestClient.setBaseUrl(BuildConfig.BASE_URL);
        ServerApi serverApi = mApiModule.provideRestClient(mRestClient);

        String apiKey = "45eafe06150f4a9aa430a7b914add011";

        Observable<BestSellersHistory> bestSellersHistory = serverApi
                .getBestSellersHistory(apiKey);

        TestObserver<BestSellersHistory> testObserver = new TestObserver<>();
        bestSellersHistory.subscribe(testObserver);
        testObserver.assertNotComplete();
        testObserver.assertErrorMessage("HTTP 403 Forbidden");
        List<BestSellersHistory> histories = testObserver.values();
        Assert.assertNotNull(histories);
        Assert.assertEquals(0, histories.size());
    }

}
