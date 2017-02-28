package com.genesis.networking.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BestSellersHistory {

    @SerializedName("status")
    private String mStatus;

    @SerializedName("copyright")
    private String mCopyRight;

    @SerializedName("num_results")
    private int mNumResults;

    @SerializedName("results")
    private List<BestSeller> mBestSellerList;

    public String getStatus() {
        return mStatus;
    }

    public String getCopyRight() {
        return mCopyRight;
    }

    public int getNumResults() {
        return mNumResults;
    }

    public List<BestSeller> getBestSellerList() {
        return mBestSellerList;
    }
}
