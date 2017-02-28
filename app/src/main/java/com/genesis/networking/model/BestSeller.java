package com.genesis.networking.model;


import com.google.gson.annotations.SerializedName;

public class BestSeller {

    @SerializedName("title")
    private String mTitle;

    @SerializedName("author")
    private String mAuthor;

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }
}
