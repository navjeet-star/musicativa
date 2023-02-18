package com.developer.musicatiiva.fragments;

import android.content.Context;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.developer.musicatiiva.R;
import com.developer.musicatiiva.activities.MainActivity;
import com.developer.musicatiiva.databinding.FragmentInstrumentsOptionsBinding;

public class InstrumentsOptionsFragments extends Fragment {

    FragmentInstrumentsOptionsBinding fragmentInstrumentsOptionsBinding;
    View view;
    Context context;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        fragmentInstrumentsOptionsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_instruments_options, container, false);
        view = fragmentInstrumentsOptionsBinding.getRoot();
        fragmentInstrumentsOptionsBinding.setData(this);

        context = getActivity();

        if (getActivity() != null) {
            ((MainActivity) context).activityMainBinding.iTollbar.tvActivities.setVisibility(View.GONE);
            ((MainActivity) context).activityMainBinding.iTollbar.tvAppName.setVisibility(View.VISIBLE);

            ((MainActivity) context).setTittle(getResources().getString(R.string.instruments));
            ((MainActivity) context).setMenuVisibilty(false);

            ((MainActivity) context).activityMainBinding.iTollbar.layoutTimer.setVisibility(View.GONE);
        }

        return view;

    }

   public void onChordLayoutClicked()
   {
       ChordFragments chordFragments=new ChordFragments();
       FragmentTransaction transaction =  ((MainActivity) context).getSupportFragmentManager().beginTransaction();
       transaction.replace(R.id.frame,chordFragments);
       transaction.addToBackStack("null");
       transaction.commit();
   }

}
