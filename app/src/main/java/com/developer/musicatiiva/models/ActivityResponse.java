package com.developer.musicatiiva.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class ActivityResponse {



   @SerializedName("status_code")
   @Expose
   private String statusCode;
    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("todayPracticeDate")
    @Expose
    private String todayPracticeDate;

    @SerializedName("weeklyLastPracticeDate")
    @Expose
    private String weeklyLastPracticeDate;

    @SerializedName("monthlyLastPracticeDate")
    @Expose
    private String monthlyLastPracticeDate;

    @SerializedName("weeklyData")
    @Expose
    private List<WeeklyData> weeklyData = null;

    @SerializedName("data")
    @Expose
    private List<MonthlyData> monthlyData = null;

    @SerializedName("todayData")
    @Expose
    private List<TodayData> todayData = null;
    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<WeeklyData> getWeeklyData() {
        return weeklyData;
    }

    public void setWeeklyData(List<WeeklyData> weeklyData) {
        this.weeklyData = weeklyData;
    }

    public List<MonthlyData> getMonthlyData() {
        return monthlyData;
    }

    public void setMonthlyData(List<MonthlyData> monthlyData) {
        this.monthlyData = monthlyData;
    }

    public List<TodayData> getTodayData() {
        return todayData;
    }

    public void setTodayData(List<TodayData> todayData) {
        this.todayData = todayData;
    }

    public String getTodayPracticeDate() {
        return todayPracticeDate;
    }

    public void setTodayPracticeDate(String todayPracticeDate) {
        this.todayPracticeDate = todayPracticeDate;
    }

    public String getWeeklyLastPracticeDate() {
        return weeklyLastPracticeDate;
    }

    public void setWeeklyLastPracticeDate(String weeklyLastPracticeDate) {
        this.weeklyLastPracticeDate = weeklyLastPracticeDate;
    }

    public String getMonthlyLastPracticeDate() {
        return monthlyLastPracticeDate;
    }

    public void setMonthlyLastPracticeDate(String monthlyLastPracticeDate) {
        this.monthlyLastPracticeDate = monthlyLastPracticeDate;
    }
}
