package com.developer.musicatiiva.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.musicatiiva.R;
import com.developer.musicatiiva.activities.MainActivity;
import com.developer.musicatiiva.adapters.ActivityAdapter;
import com.developer.musicatiiva.apiModels.JsonPlaceHolderApi;
import com.developer.musicatiiva.commonClasses.CommonDialogs;
import com.developer.musicatiiva.commonClasses.CommonMethods;
import com.developer.musicatiiva.databinding.FragmentSubInstrumentActivitiesBinding;
import com.developer.musicatiiva.models.AllData;
import com.developer.musicatiiva.models.SubInstrActivitiesResponse;
import com.developer.musicatiiva.utils.Constants;
import com.developer.musicatiiva.utils.MySharedPreferences;
import com.developer.musicatiiva.utils.SwipeToDeleteCallback;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.developer.musicatiiva.utils.Constants.CLOCK_URL;


public class SubInstrumentActivitiesFragment extends Fragment {

    FragmentSubInstrumentActivitiesBinding fragmentSubInstrumentActivitiesBinding;
    Context context;

    String instrumentName;
    ActivityAdapter activityAdapter;
    View rootView;
    SwipeToDeleteCallback swipeToDeleteCallback;



    int sub_id,user_id;
    Bundle arguments;
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    public SubInstrumentActivitiesFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(rootView==null)
        {
            fragmentSubInstrumentActivitiesBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_sub_instrument_activities, container, false);
            rootView=fragmentSubInstrumentActivitiesBinding.getRoot();
            fragmentSubInstrumentActivitiesBinding.setActivity(this);
            context=getActivity();
            arguments=getArguments();
            instrumentName=arguments.getString(Constants.INSTRUMENT_NAME);
            sub_id =arguments.getInt(Constants.SUB_CAT);

            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl(CLOCK_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
            swipeToDeleteCallback = new SwipeToDeleteCallback(context) {
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                    //Toast.makeText(context, "on Swiped "+instrument.getText(), Toast.LENGTH_SHORT).show();
                    activityAdapter.deleteActivity(viewHolder.getAdapterPosition());


                }
            };
            setActivityAdapter();


        }

        return rootView;
    }
    private void setUpToolBar(){
        if (getActivity() != null) {

            ((MainActivity) context).setTittle(instrumentName);
            ((MainActivity) context).activityMainBinding.iTollbar.tvAppName.setVisibility(View.GONE);

            ((MainActivity) context).activityMainBinding.iTollbar.tvActivities.setVisibility(View.VISIBLE);
            ((MainActivity) context).setMenuVisibilty(false);
            ((MainActivity) context).activityMainBinding.iTollbar.layoutTimer.setVisibility(View.GONE);


        }
    }
    private void setActivityAdapter(){
         activityAdapter =new ActivityAdapter(context,new ArrayList<AllData>());
        fragmentSubInstrumentActivitiesBinding.rvActivities.setLayoutManager(new LinearLayoutManager(context));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(fragmentSubInstrumentActivitiesBinding.rvActivities);
        fragmentSubInstrumentActivitiesBinding.rvActivities.setAdapter(activityAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpToolBar();
        loadActivities();
    }

     public void loadActivities(){
        new CommonMethods.InternetCheck(context, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {
                if (internet) {
                    final int user_id= MySharedPreferences.getInstance().getUserID(context,Constants.USERID);

                    CommonDialogs.getInstance().showProgressDialog(context);
                    JsonObject jsonObject=new JsonObject();
                     jsonObject.addProperty("s_id",sub_id);
                    jsonObject.addProperty(Constants.UID,user_id);
                     Call<SubInstrActivitiesResponse> call=jsonPlaceHolderApi.getSubInstrumentActivityData(jsonObject);
                    call.enqueue(new Callback<SubInstrActivitiesResponse>() {
                        @Override
                        public void onResponse(Call<SubInstrActivitiesResponse> call, Response<SubInstrActivitiesResponse> response) {
                            if(response.isSuccessful())
                            {
                                SubInstrActivitiesResponse subInstrActivitiesResponse=response.body();
                                String desc=subInstrActivitiesResponse.getDescription();

                                //String last_practiced_date=practiceData.getLast_practice_date();
                                String status_code=subInstrActivitiesResponse.getStatusCode();
                                List<AllData> activities=subInstrActivitiesResponse.getData();


                                if(status_code.equals("1"))
                                {

                                    if(activities!=null)
                                    {
                                        for(AllData allData:activities)
                                        {
                                            Log.d("data", "onResponse: activity "+allData.toString());
                                        }
                                        if(!activities.isEmpty())
                                        {
                                            showDataLayout();
                                            activityAdapter.update(activities);

                                        }

                                        else
                                            showNoDataLayout();
                                    }
                                    else
                                        showNoDataLayout();


                                }
                                else
                                {
                                    Log.d("mohit", "onResponse: Status code : "+status_code+"\n"+"Description : "+desc);
                                    Toast.makeText(context, desc, Toast.LENGTH_SHORT).show();
                                }



                            }
                            else
                            {
                                Log.d("mohit", "onResponse: "+response.message());
                                Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show();

                            }
                            CommonDialogs.getInstance().dismissProgressDialog();




                        }

                        @Override
                        public void onFailure(Call<SubInstrActivitiesResponse> call, Throwable t) {
                            Log.d("mohit","Error code: "+t.getMessage());
                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                            CommonDialogs.getInstance().dismissProgressDialog();


                        }
                    });
                }
            }
        });
    }
    private void showDataLayout(){
        fragmentSubInstrumentActivitiesBinding.textViewNoDataFound.setVisibility(View.GONE);
        fragmentSubInstrumentActivitiesBinding.rvActivities.setVisibility(View.VISIBLE);

    }
    private void showNoDataLayout(){
        fragmentSubInstrumentActivitiesBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
        fragmentSubInstrumentActivitiesBinding.rvActivities.setVisibility(View.GONE);

    }

    public void createActivity(){
            openActivityTitleDialog();
        }
        private void openActivityTitleDialog(){

            final Dialog dialogAddActivityTitle = new Dialog(context);
                dialogAddActivityTitle.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogAddActivityTitle.setContentView(R.layout.dialog_activity_title);
                dialogAddActivityTitle.setCanceledOnTouchOutside(true);
                dialogAddActivityTitle.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                dialogAddActivityTitle.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                final EditText mEdActivityTitle = dialogAddActivityTitle.findViewById(R.id.editTextTitle);
            mEdActivityTitle.requestFocus();
                dialogAddActivityTitle.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                TextView mTvCancel = dialogAddActivityTitle.findViewById(R.id.textViewCancel);

                TextView mTvSave = dialogAddActivityTitle.findViewById(R.id.textViewSave);

                mTvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogAddActivityTitle.dismiss();
                    }
                });
                mTvSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title=mEdActivityTitle.getText().toString().trim();
                                if(title.isEmpty())
                                {
                                    mEdActivityTitle.setError("Title cannot be empty!");
                                    mEdActivityTitle.requestFocus();
                                    return;
                                }
                                dialogAddActivityTitle.dismiss();
                                navigate(title);
                    }
                });

                dialogAddActivityTitle.show();
            }
            private void navigate(String title){
                ChordFragments chordFragments=new ChordFragments();
                arguments.putInt("KeyboardTime",0);
                arguments.putString(Constants.ACTIVITY_TITLE,title);
                chordFragments.setArguments(arguments);
                FragmentManager fragmentManager = ((MainActivity )context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame, chordFragments);
                fragmentTransaction.addToBackStack("null");
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();
            }


        
}