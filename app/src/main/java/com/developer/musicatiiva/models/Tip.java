package com.developer.musicatiiva.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tip implements Runnable {
    @SerializedName("tip")
    @Expose
    private String tip;
    @SerializedName("count")
    @Expose
    private int count;

    public String getTip() {

        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public void run() {

    }
}
