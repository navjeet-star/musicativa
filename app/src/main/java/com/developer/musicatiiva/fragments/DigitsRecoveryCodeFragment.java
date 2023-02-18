package com.developer.musicatiiva.fragments;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.developer.musicatiiva.R;

public class DigitsRecoveryCodeFragment extends Fragment {

    View view;
    Context context;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        view =LayoutInflater.from(getContext()).inflate(R.layout.fragments_digits_recovery_code,container,false);
        return  view;
    }
}
