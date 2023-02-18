package com.developer.musicatiiva.adapters;

import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.developer.musicatiiva.R;
import com.developer.musicatiiva.activities.MainActivity;
import com.developer.musicatiiva.apiModels.JsonPlaceHolderApi;
import com.developer.musicatiiva.commonClasses.CommonDialogs;
import com.developer.musicatiiva.commonClasses.CommonMethods;
import com.developer.musicatiiva.databinding.ItemInstrumentsBinding;
import com.developer.musicatiiva.fragments.InstrumentsFragments;
import com.developer.musicatiiva.fragments.SubInstrumentActivitiesFragment;
import com.developer.musicatiiva.fragments.SubInstrumentFragment;
import com.developer.musicatiiva.models.DeleteSubInstrumentResponse;
import com.developer.musicatiiva.models.Instrument;
import com.developer.musicatiiva.utils.Constants;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.developer.musicatiiva.utils.Constants.INSTRUMENT_DATA_URL;

public class InstrumentAdapters extends RecyclerView.Adapter<InstrumentAdapters.MyViewHolder> {

    private Context context;
    ItemInstrumentsBinding itemInstrumentsBinding;
    private List<Instrument> listOfInstruments;
    Fragment visible;
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    public InstrumentAdapters(Context context,List<Instrument> listOfInstruments)

    {

        this.context=context;
        this.listOfInstruments=listOfInstruments;
        visible=((MainActivity)context).getSupportFragmentManager().findFragmentById(R.id.frame);
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(INSTRUMENT_DATA_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);

    }

    @NonNull
    @Override
    public InstrumentAdapters.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        itemInstrumentsBinding=ItemInstrumentsBinding.inflate(LayoutInflater.from(context),viewGroup,false);

        return new MyViewHolder(itemInstrumentsBinding);

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ItemInstrumentsBinding itemInstrumentsBinding;

        public MyViewHolder(@NonNull ItemInstrumentsBinding itemView) {
            super(itemView.getRoot());
            this.itemInstrumentsBinding=itemView;

        }
    }

    private static final String TAG = "mohit";

    @Override
    public void onBindViewHolder(@NonNull InstrumentAdapters.MyViewHolder myViewHolder,  int i) {


        itemInstrumentsBinding.setItem(this);
        final Instrument instrument=listOfInstruments.get(i);
        Log.d(TAG, "onBindViewHolder: instrument id "+instrument.getId());
        Log.d(TAG, "onBindViewHolder: instrument name "+instrument.getText());
        itemInstrumentsBinding.instrumentName.setText(instrument.getText());
        Log.d(TAG, "onBindViewHolder: image url "+instrument.getImage());
        Glide.with(context)
                .load(instrument.getImage())
                .into(itemInstrumentsBinding.instrumentImage);
        itemInstrumentsBinding.relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(visible instanceof InstrumentsFragments)
                {
                    SubInstrumentFragment subInstrumentFragment=new SubInstrumentFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.ID, instrument.getId());
                    subInstrumentFragment.setArguments(bundle);
                    FragmentManager fragmentManager = ((MainActivity )context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame, subInstrumentFragment);
                    fragmentTransaction.addToBackStack("null");
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.commit();
                }
                else if(visible instanceof SubInstrumentFragment)
                {
                    SubInstrumentActivitiesFragment subInstrumentActivitiesFragment=new SubInstrumentActivitiesFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.INSTRUMENT_NAME, instrument.getText());
                    bundle.putString(Constants.INSTRUMENT_IMAGE_URL, instrument.getImage());
                    bundle.putInt(Constants.SUB_CAT,instrument.getSub_id());
                    //bundle.putInt(Constants.PRACTICED,practiceData.getPracticed());
                    //bundle.putString(Constants.PRACTICE_TODAY,practiceData.getPractice_today());
                    //bundle.putString(Constants.LAST_PRACTICED_DATE,practiceData.getLast_practice_date());
                    // bundle.putInt(Constants.USER_ID,user_id);
                    subInstrumentActivitiesFragment.setArguments(bundle);
                    FragmentManager fragmentManager = ((MainActivity )context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame, subInstrumentActivitiesFragment);
                    fragmentTransaction.addToBackStack("null");
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.commit();
//                    new CommonMethods.InternetCheck(context, new CommonMethods.Consumer() {
//                        @Override
//                        public void accept(Boolean internet) {
//                            if (internet) {
//                                CommonDialogs.getInstance().showProgressDialog(context);
//                                JsonObject jsonObject=new JsonObject();
//                                final int user_id= MySharedPreferences.getInstance().getUserID(context,Constants.USERID);
//                                String currentDate=CommonMethods.getInstance().getCurrentDate();
//                                Log.d(TAG, "accept: user id "+user_id);
//                                jsonObject.addProperty(Constants.USER_ID,user_id);
//                                Log.d(TAG, "accept: instrument syb id "+instrument.getSub_id());
//                                jsonObject.addProperty(Constants.SUB_CAT,instrument.getSub_id());
//                                jsonObject.addProperty(Constants.DATE,currentDate);
//                                Call<Practice> call=jsonPlaceHolderApi.getPracticeeData(jsonObject);
//                                call.enqueue(new Callback<Practice>() {
//                                    @Override
//                                    public void onResponse(Call<Practice> call, Response<Practice> response) {
//                                        if(response.isSuccessful())
//                                        {
//                                            Practice practice=response.body();
//                                            PracticeData practiceData=practice.getData();
//                                            String desc=practice.getDescription();
//
//                                            //String last_practiced_date=practiceData.getLast_practice_date();
//                                            int status_code=practice.getStatus_code();
//
//
//
//
//
//                                            Log.d(TAG, "onResponse: status code "+status_code);
//                                            Log.d(TAG, "onResponse: description "+desc);
//                                          //  Log.d(TAG, "onResponse: last_practice data "+last_practiced_date);
//                                            Log.d(TAG, "onResponse: practice data list "+practiceData);
//
//
//                                            if(status_code==1)
//                                            {
//
//                                               if(practiceData!=null)
//                                               {
//                                                   Log.d(TAG, "onResponse: last practiced date "+practiceData.getLast_practice_date());
//
//                                               }
//
//
//                                            }
//                                            else
//                                            {
//                                                Log.d("mohit", "onResponse: Status code : "+status_code+"\n"+"Description : "+desc);
//                                                Toast.makeText(context, desc, Toast.LENGTH_SHORT).show();
//                                            }
//
//
//
//                                        }
//                                        else
//                                        {
//                                            Log.d("mohit", "onResponse: "+response.message());
//                                            Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show();
//
//                                        }
//                                        CommonDialogs.getInstance().dismissProgressDialog();
//
//
//
//
//                                    }
//
//                                    @Override
//                                    public void onFailure(Call<Practice> call, Throwable t) {
//                                        Log.d("mohit","Error code: "+t.getMessage());
//                                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
//                                        CommonDialogs.getInstance().dismissProgressDialog();
//
//
//                                    }
//                                });
//                            }
//                        }
//                    });

                }

            }
        });

    }


    public void deleteSubInstrument(final int position)
    {
        final Instrument selected_instrument=listOfInstruments.get(position);
        new CommonMethods.InternetCheck(context, new CommonMethods.Consumer() {
            @Override
            public void accept(final Boolean internet) {
                if (internet) {
                    CommonDialogs.getInstance().showProgressDialog(context);
                    JsonObject jsonObject=new JsonObject();
//                    int user_id= MySharedPreferences.getInstance().getUserID(context,Constants.USERID);
//                    jsonObject.addProperty(Constants.UID,user_id);
                    jsonObject.addProperty(Constants.SUB_INSTRUMENT_ID,listOfInstruments.get(position).getSub_id());



                    Call<DeleteSubInstrumentResponse> call=jsonPlaceHolderApi.deleteSubInstrument(jsonObject);
                    call.enqueue(new Callback<DeleteSubInstrumentResponse>() {
                        @Override
                        public void onResponse(Call<DeleteSubInstrumentResponse> call, Response<DeleteSubInstrumentResponse> response) {
                            if(response.isSuccessful())
                            {
                                DeleteSubInstrumentResponse deleteSubInstrumentResponse=response.body();

                                String desc=deleteSubInstrumentResponse.getDescription();

                                //String last_practiced_date=practiceData.getLast_practice_date();
                                int status_code=deleteSubInstrumentResponse.getStatus_code();





                                Log.d(TAG, "onResponse: status code "+status_code);
                                Log.d(TAG, "onResponse: description "+desc);
                                //  Log.d(TAG, "onResponse: last_practice data "+last_practiced_date);



                                if(status_code==1)
                                {
                                    listOfInstruments.remove(position);
                                    notifyItemRemoved(position);

                                    Toast.makeText(context, desc, Toast.LENGTH_SHORT).show();


                                }
                                else
                                {
                                    Log.d("mohit", "onResponse: Status code : "+status_code+"\n"+"Description : "+desc);
                                    Toast.makeText(context, desc, Toast.LENGTH_SHORT).show();

                                   restoreItem(position,selected_instrument);

                                }



                            }
                            else
                            {
                                Log.d("mohit", "onResponse: "+response.message());
                                Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show();
                                restoreItem(position,selected_instrument);

                            }
                            CommonDialogs.getInstance().dismissProgressDialog();




                        }

                        @Override
                        public void onFailure(Call<DeleteSubInstrumentResponse> call, Throwable t) {

                            Log.d("mohit","Error code: "+t.getMessage());

                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                            CommonDialogs.getInstance().dismissProgressDialog();
                            restoreItem(position,selected_instrument);
                        }
                    });
                }
            }
        });
    }

private void restoreItem(int position,Instrument selected_instrument)
{
   // listOfInstruments.add(position, selected_instrument);
    FragmentManager fragmentManager = ((MainActivity )context).getSupportFragmentManager();
    SubInstrumentFragment subInstrumentFragment= (SubInstrumentFragment) fragmentManager.findFragmentById(R.id.frame);
    subInstrumentFragment.getListOfInstruments();
   //notifyDataSetChanged();

}


    @Override
    public int getItemCount() {
        return listOfInstruments.size();
    }
}
