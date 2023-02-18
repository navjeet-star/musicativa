package com.developer.musicatiiva.fragments;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.developer.musicatiiva.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TempoFragment extends Fragment {


    public TempoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tempo, container, false);
    }

}
