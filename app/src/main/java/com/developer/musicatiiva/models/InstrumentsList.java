package com.developer.musicatiiva.models;

import java.util.List;

public class InstrumentsList {
    private Integer status_code;
    private String description;
    private List<Instrument> data=null;

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

    public List<Instrument> getData() {
        return data;
    }

    public void setData(List<Instrument> data) {
        this.data = data;
    }
}
