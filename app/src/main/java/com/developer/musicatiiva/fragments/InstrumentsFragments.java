package com.developer.musicatiiva.fragments;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.developer.musicatiiva.R;
import com.developer.musicatiiva.activities.MainActivity;
import com.developer.musicatiiva.adapters.InstrumentAdapters;
import com.developer.musicatiiva.apiModels.JsonPlaceHolderApi;
import com.developer.musicatiiva.commonClasses.CommonDialogs;
import com.developer.musicatiiva.commonClasses.CommonMethods;
import com.developer.musicatiiva.databinding.FragmentInstrumentsBinding;
import com.developer.musicatiiva.models.Instrument;
import com.developer.musicatiiva.models.InstrumentsList;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.developer.musicatiiva.utils.Constants.INSTRUMENT_DATA_URL;
import static com.developer.musicatiiva.utils.Constants.NEW_PASSWORD;

public class InstrumentsFragments extends Fragment {

    FragmentInstrumentsBinding fragmentsInstrumenstsBinding;
    View view;
    Context context;
    InstrumentAdapters instrumentAdapters;
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        fragmentsInstrumenstsBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_instruments, container, false);
        view=fragmentsInstrumenstsBinding.getRoot();
        fragmentsInstrumenstsBinding.setData(this);

        context=getActivity();
        Retrofit retrofit=new Retrofit.Builder().baseUrl(INSTRUMENT_DATA_URL).addConverterFactory(GsonConverterFactory.create()).build();
        jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);

        if (getActivity() != null) {

            ((MainActivity) context).activityMainBinding.iTollbar.tvActivities.setVisibility(View.GONE);
            ((MainActivity) context).activityMainBinding.iTollbar.tvAppName.setVisibility(View.VISIBLE);
            ((MainActivity) context).setTittle(getResources().getString(R.string.instruments));
            ((MainActivity) context).setMenuVisibilty(true);
            ((MainActivity) context).activityMainBinding.iTollbar.layoutTimer.setVisibility(View.GONE);

        }

        adapterData();
        return view;
    }

    private void adapterData() {

        getListOfInstruments();

    }


    private void getListOfInstruments()

    {

        fragmentsInstrumenstsBinding.textViewNoDataFound.setVisibility(View.GONE);
        new CommonMethods.InternetCheck(context, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {
                if (internet) {
                    CommonDialogs.getInstance().showProgressDialog(context);
                    Call<InstrumentsList> call=jsonPlaceHolderApi.getInstrumentData();
                    call.enqueue(new Callback<InstrumentsList>() {
                        @Override
                        public void onResponse(Call<InstrumentsList> call, Response<InstrumentsList> response) {
                            if(response.isSuccessful())
                            {
                                InstrumentsList instrumentsList=response.body();

                                String description=instrumentsList.getDescription();
                                int status_code=instrumentsList.getStatus_code();
                                List<Instrument> instrumentList1=instrumentsList.getData();


                                if(status_code==1)
                                {
                                    if(instrumentList1!=null)

                                    {
                                        instrumentAdapters=new InstrumentAdapters(context,instrumentList1);
                                        fragmentsInstrumenstsBinding.instrumentsRecyclerview.setLayoutManager(new LinearLayoutManager(context));
                                        fragmentsInstrumenstsBinding.instrumentsRecyclerview.setAdapter(instrumentAdapters);
                                    }
                                    else
                                    {
                                        fragmentsInstrumenstsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
                                    }
                                    // finish();
                                }
                                else
                                {
                                    Log.d("mohit", "onResponse: Status code : "+status_code+"\n"+"Description : "+description);
                                    Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
                                    fragmentsInstrumenstsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
                                }
                            }
                            else
                            {
                                Log.d("mohit", "onResponse: "+response.message());
                                Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show();
                                fragmentsInstrumenstsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
                                fragmentsInstrumenstsBinding.textViewNoDataFound.setText(response.message());
                            }
                            CommonDialogs.getInstance().dismissProgressDialog();

                        }
                        @Override
                        public void onFailure(Call<InstrumentsList> call, Throwable t) {

                            Log.d("mohit","Error code: "+t.getMessage());
                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                            CommonDialogs.getInstance().dismissProgressDialog();
                            fragmentsInstrumenstsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
                            fragmentsInstrumenstsBinding.textViewNoDataFound.setText(t.getMessage());
                        }
                    });
                }
            }
        });
    }

    public void newRoutines(){


    }

    public String getImagePath (int resourceId) {

        return Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + resourceId).toString();

    }


}
