package com.developer.musicatiiva.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AddDeviceResponse {



   @SerializedName("status_code")
   @Expose
   private String statusCode;
    @SerializedName("description")
    @Expose
    private String description;

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
}
