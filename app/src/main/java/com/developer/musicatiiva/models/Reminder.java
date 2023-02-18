package com.developer.musicatiiva.models;

public class Reminder {

    private String id;
    private String title;
    private String reminder_at;
    private String frequency;
    private String timezone;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReminder_at() {
        return reminder_at;
    }

    public void setReminder_at(String reminder_at) {
        this.reminder_at = reminder_at;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
