package com.developer.musicatiiva.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateSubscribed {
    @SerializedName("description")
    @Expose
    String description;
    @SerializedName("status_code")
    @Expose
    String status_code;
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }


}
