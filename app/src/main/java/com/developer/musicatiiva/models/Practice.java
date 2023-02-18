package com.developer.musicatiiva.models;

public class Practice {
    private Integer status_code;
    private String description;
    private PracticeData data=null;

    public Integer getStatus_code() {
        return status_code;
    }

    public void setStatus_code(Integer status_code) {
        this.status_code = status_code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PracticeData getData() {
        return data;
    }

    public void setData(PracticeData data) {
        this.data = data;
    }


}
