package com.developer.musicatiiva.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TodayData {
    @SerializedName("categoryName")
    @Expose
    private String categoryName;
    @SerializedName("instrumentName")
    @Expose
    private String instrumentName;

    public String getInstrumentName() {
        return instrumentName;
    }


    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }
    @SerializedName("activityName")
    @Expose
    private String activityName;


    @SerializedName("id")
    @Expose
    private String id;


    @SerializedName("type")
    @Expose
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("numberOfClicks")
    @Expose
    private Integer numberOfClicks;
    @SerializedName("comments")
    @Expose
    private String comments;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Integer getNumberOfClicks() {
        return numberOfClicks;
    }

    public void setNumberOfClicks(Integer numberOfClicks) {
        this.numberOfClicks = numberOfClicks;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

}
