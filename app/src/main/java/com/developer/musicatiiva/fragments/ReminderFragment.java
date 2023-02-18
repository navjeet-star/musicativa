package com.developer.musicatiiva.fragments;

import static com.developer.musicatiiva.utils.Constants.INSTRUMENT_DATA_URL;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.developer.musicatiiva.R;
import com.developer.musicatiiva.activities.MainActivity;
import com.developer.musicatiiva.adapters.ReminderAdpter;
import com.developer.musicatiiva.apiModels.JsonPlaceHolderApi;
import com.developer.musicatiiva.commonClasses.CommonDialogs;
import com.developer.musicatiiva.commonClasses.CommonMethods;
import com.developer.musicatiiva.databinding.FragmentReminderBinding;
import com.developer.musicatiiva.models.GetReminderList;
import com.developer.musicatiiva.models.Reminder;
import com.developer.musicatiiva.utils.Constants;
import com.developer.musicatiiva.utils.MySharedPreferences;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReminderFragment extends Fragment {

    FragmentReminderBinding fragmentReminderBinding;
    View view;
    Context context;
    ReminderAdpter reminderAdapter;
    private JsonPlaceHolderApi jsonPlaceHolderApi;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //return super.onCreateView(inflater, container, savedInstanceState);

        fragmentReminderBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reminder, container, false);
        view = fragmentReminderBinding.getRoot();
        fragmentReminderBinding.setData(this);

        context = getActivity();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(INSTRUMENT_DATA_URL).addConverterFactory(GsonConverterFactory.create()).build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        if (getActivity() != null) {

            ((MainActivity) context).activityMainBinding.iTollbar.tvActivities.setVisibility(View.GONE);
            ((MainActivity) context).activityMainBinding.iTollbar.tvAppName.setVisibility(View.VISIBLE);
            ((MainActivity) context).setTittle(getResources().getString(R.string.reminder));
            ((MainActivity) context).setMenuVisibilty(true);
            ((MainActivity) context).activityMainBinding.iTollbar.layoutTimer.setVisibility(View.GONE);
            ((MainActivity) context).activityMainBinding.iTollbar.tvAppName.setVisibility(View.GONE);

        }

        initialization();
        return view;

    }

    private void initialization() {

       getReminder();

    }

    @Override
    public void onResume() {
        super.onResume();

        getReminder();

    }

    private void getReminder() {

        fragmentReminderBinding.textViewNoDataFound.setVisibility(View.GONE);

        new CommonMethods.InternetCheck(context, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {

                if (internet) {

                    JsonObject jsonObject = new JsonObject();
                    int user_id = MySharedPreferences.getInstance().getUserID(context, Constants.USERID);
                    jsonObject.addProperty(Constants.USER_ID, user_id);
                    CommonDialogs.getInstance().showProgressDialog(context);
                    Call<GetReminderList> call = jsonPlaceHolderApi.getReminder(jsonObject);

                    call.enqueue(new Callback<GetReminderList>() {
                        @Override
                        public void onResponse(Call<GetReminderList> call, Response<GetReminderList> response) {

                            if (response.isSuccessful()) {

                                GetReminderList getReminder = response.body();
                                String description = getReminder.getDescription();
                                int status_code = getReminder.getStatus_code();
                                List<Reminder> reminderList = getReminder.getData();

                                if (status_code == 1) {

                                    if (reminderList != null) {

                                        reminderAdapter = new ReminderAdpter(context, reminderList);
                                        fragmentReminderBinding.reminderRecyclerview.setLayoutManager(new LinearLayoutManager(context));
                                        fragmentReminderBinding.reminderRecyclerview.setAdapter(reminderAdapter);

                                        Collections.reverse(reminderList);

                                    } else {

                                        fragmentReminderBinding.textViewNoDataFound.setVisibility(View.VISIBLE);

                                    }

                                    // finish();

                                } else {

                                    Log.d("mohit", "onResponse: Status code : " + status_code + "\n" + "Description : " + description);

                                    Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
                                    fragmentReminderBinding.textViewNoDataFound.setVisibility(View.VISIBLE);

                                }

                            } else {

                                Log.d("mohit", "onResponse: " + response.message());

                                Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show();
                                fragmentReminderBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
                                fragmentReminderBinding.textViewNoDataFound.setText(response.message());

                            }

                            CommonDialogs.getInstance().dismissProgressDialog();


                         }

                        @Override
                        public void onFailure(Call<GetReminderList> call, Throwable t) {

                            Log.d("mohit", "Error code: " + t.getMessage());

                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();

                            CommonDialogs.getInstance().dismissProgressDialog();

                            fragmentReminderBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
                            fragmentReminderBinding.textViewNoDataFound.setText(t.getMessage());

                        }
                    });
                }
            }
        });
    }
}

