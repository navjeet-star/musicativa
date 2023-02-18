package com.developer.musicatiiva.stopwatch;


import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.developer.musicatiiva.BR;

public class TimerValue extends BaseObservable {

    private String value;

    public TimerValue(){
        value = "00:00";
    }

    @Bindable
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        notifyPropertyChanged(BR.value);
    }
}
