package com.developer.musicatiiva.models;

public class PracticeData {
    private Integer practiced;
    private String last_practice_date;
    private String practice_today;
    private Integer practice_id;

    public Integer getPracticed() {
        return practiced;
    }

    public void setPracticed(Integer practiced) {
        this.practiced = practiced;
    }

    public String getLast_practice_date() {
        return last_practice_date;
    }

    public void setLast_practice_date(String last_practice_date) {
        this.last_practice_date = last_practice_date;
    }

    public String getPractice_today() {
        return practice_today;
    }

    public void setPractice_today(String practice_today) {
        this.practice_today = practice_today;
    }

    public Integer getPractice_id() {
        return practice_id;
    }

    public void setPractice_id(Integer practice_id) {
        this.practice_id = practice_id;
    }
}
