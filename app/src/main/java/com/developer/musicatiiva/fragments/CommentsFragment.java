package com.developer.musicatiiva.fragments;


import android.content.Context;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.musicatiiva.R;
import com.developer.musicatiiva.activities.MainActivity;
import com.developer.musicatiiva.adapters.CommentAdapter;
import com.developer.musicatiiva.apiModels.JsonPlaceHolderApi;
import com.developer.musicatiiva.commonClasses.CommonDialogs;
import com.developer.musicatiiva.commonClasses.CommonMethods;
import com.developer.musicatiiva.databinding.FragmentCommentsBinding;
import com.developer.musicatiiva.models.ActivityResponse;
import com.developer.musicatiiva.models.AllData;
import com.developer.musicatiiva.models.MonthlyData;
import com.developer.musicatiiva.models.TodayData;
import com.developer.musicatiiva.models.WeeklyData;
import com.developer.musicatiiva.utils.Constants;
import com.developer.musicatiiva.utils.CustomLinearLayoutManager;
import com.developer.musicatiiva.utils.MySharedPreferences;
import com.developer.musicatiiva.utils.SwipeToDeleteCallback;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.developer.musicatiiva.utils.Constants.CLOCK_URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends Fragment {
    FragmentCommentsBinding fragmentCommentsBinding;
    View view;
    Context context;
    public String dataType;
    public List<AllData> activityDataList=new ArrayList<>();
    List<AllData> filteredList=new ArrayList<>();
    CommentAdapter commentAdapter;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private static final String TAG = "mohit";
    List<String> stringListYear;
    SwipeToDeleteCallback swipeToDeleteCallback;

    public CommentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentCommentsBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_comments, container, false);
        view=fragmentCommentsBinding.getRoot();

        context=getActivity();
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(CLOCK_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        swipeToDeleteCallback = new SwipeToDeleteCallback(context) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                Log.d(TAG, "onSwiped: p "+i);
                Log.d(TAG, "onSwiped: v "+viewHolder.getAdapterPosition());
                //Toast.makeText(context, "on Swiped "+instrument.getText(), Toast.LENGTH_SHORT).show();
                commentAdapter.deleteActivity(viewHolder.getAdapterPosition());


            }
        };

        ((MainActivity) context).activityMainBinding.iTollbar.layoutTimer.setVisibility(View.GONE);
        ((MainActivity) context).activityMainBinding.iTollbar.tvActivities.setVisibility(View.GONE);
        ((MainActivity) context).activityMainBinding.iTollbar.tvAppName.setVisibility(View.VISIBLE);

        dataType=getArguments().getString("dataType");
        setCommentAdapter();
        loadData(false);
        setSpinner();

        return view;
    }
    public void setCommentAdapter(){
        commentAdapter=new CommentAdapter(context,activityDataList);
        fragmentCommentsBinding.recyclerViewComments.setLayoutManager(new CustomLinearLayoutManager(context));
        fragmentCommentsBinding.recyclerViewComments.setHasFixedSize(true);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(fragmentCommentsBinding.recyclerViewComments);
        fragmentCommentsBinding.recyclerViewComments.setAdapter(commentAdapter);

    }
    public void loadData(boolean reload){
        if(dataType.equals(Constants.TODAY))
        {
            getTodayActivityData(reload);
        }
        else if(dataType.equals(Constants.WEEKLY))
        {
            getActivityWeeklyData(reload);
        }
        else if(dataType.equals(Constants.MONTHLY))
        {

            getActivityMonthlyData(reload);
        }
        else if(dataType.equals(Constants.YEARLY))
        {
            if(!reload)
            {

                fragmentCommentsBinding.spinnerSelectDate.setVisibility(View.VISIBLE);
                setDataInSpinner();

                String currentYear=String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                getActivityYearlyData(currentYear,reload);
            }
        }
    }

    private void setSpinner() {
        fragmentCommentsBinding.spinnerSelectDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(dataType.equals(Constants.MONTHLY))
                {
                    filterListForSelectedMonth(i);
                }
                else if(dataType.equals(Constants.YEARLY))
                {
                    filterListForSelectedYear(i);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void filterListForSelectedYear(int selectedYear) {
        if(filteredList!=null)
            filteredList.clear();

                if(selectedYear!=0)
                {
                    if(stringListYear!=null)
                    {
                        if(!stringListYear.isEmpty())
                        {
                            String year=stringListYear.get(selectedYear);
                            getActivityYearlyData(year,false);

                        }
                    }



        }
    }

    private void filterListForSelectedMonth(int selectedMonth) {
        if(filteredList!=null)
        {
            if(!filteredList.isEmpty())
                filteredList.clear();

        }
        if(activityDataList!=null)
        {
            if(!activityDataList.isEmpty())
            {
               if(selectedMonth!=0)
               {
                   for(AllData activityData:activityDataList){
                       String date=activityData.getDate();
                       int month=Integer.parseInt(date.split("-")[1]);
                       Log.d(TAG, "filterListForSelectedMonth: month "+month);
                       if(month==selectedMonth)
                       {
                           Log.d(TAG, "filterListForSelectedMonth: added "+activityData.getDate());
                           filteredList.add(activityData);
                       }

                   }
                   if(filteredList!=null)
                   {
                       if(!filteredList.isEmpty())
                       {
                           fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.GONE);
                           fragmentCommentsBinding.containerActivity.setVisibility(View.VISIBLE);

                           commentAdapter.updateList(filteredList);
                       }
                       else
                       {
                           fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
                           fragmentCommentsBinding.containerActivity.setVisibility(View.GONE);
                       }
                   }
                   else
                   {

                       fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
                       fragmentCommentsBinding.containerActivity.setVisibility(View.GONE);

                   }
               }

            }
        }
    }


    private void getActivityWeeklyData(final boolean reload) {
        if(reload)
        {
            if(activityDataList!=null)
            {
                if(!activityDataList.isEmpty())
                    activityDataList.clear();
            }
        }
        if(!reload)
            fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.GONE);
        new CommonMethods.InternetCheck(context, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {
                if (internet) {
                    if(!reload)
                        CommonDialogs.getInstance().showProgressDialog(context);
                    JsonObject jsonObject=new JsonObject();
                    int user_id= MySharedPreferences.getInstance().getUserID(context, Constants.USERID);
                    Log.d(TAG, "accept: user id "+user_id);
                    jsonObject.addProperty(Constants.UID,user_id);
                    Call<ActivityResponse> call=jsonPlaceHolderApi.getActivityWeeklyData(jsonObject);
                    call.enqueue(new Callback<ActivityResponse>() {
                        @Override
                        public void onResponse(Call<ActivityResponse> call, Response<ActivityResponse> response) {
                            if(response.isSuccessful())
                            {
                                ActivityResponse activityResponse=response.body();

                                String description=activityResponse.getDescription();

                                int status_code=Integer.parseInt(activityResponse.getStatusCode());
                                List<WeeklyData> weeklyData=activityResponse.getWeeklyData();
                                String date=activityResponse.getWeeklyLastPracticeDate();


                                Log.d(TAG, "onResponse: status code "+status_code);
                                Log.d(TAG, "onResponse: description "+description);
                                Log.d(TAG, "onResponse: weekly data "+weeklyData);


                                if(status_code==1)
                                {
                                    if(weeklyData!=null && !weeklyData.isEmpty())
                                    {
                                        List<AllData> mainList=new ArrayList<>();

                                        for(WeeklyData data:weeklyData)
                                        {
                                            AllData activityData=new AllData();
                                            activityData.setDate(date);
                                            activityData.setCategoryName(data.getCategoryName());
                                            activityData.setNumberOfClicks(data.getNumberOfClicks());
                                            activityData.setDuration(data.getDuration());
                                            activityData.setComments(data.getComments());
                                            activityData.setInstrumentName(data.getInstrumentName());
                                            activityData.setTitle(data.getActivityName());
                                            activityData.setId(data.getId());
                                            activityData.setType(data.getType());
                                            activityDataList.add(activityData);
                                            mainList.add(activityData);

                                        }

                                        if(!reload) {

                                            fragmentCommentsBinding.containerActivity.setVisibility(View.VISIBLE);
                                            commentAdapter.updateList(mainList);

                                        }
                                    }

                                    else

                                    {

                                     if(!reload)

                                     fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);

                                    }

                                    // finish();
                                }

                                else

                                {
                                    Log.d("mohit", "onResponse: Status code : "+status_code+"\n"+"Description : "+description);

                                    if(!reload) {

                                        Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
                                        fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);

                                    }

                                }
                            }
                            else
                            {
                                Log.d("mohit", "onResponse: "+response.message());
                                if(!reload) {
                                    Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show();
                                    fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);

                                    fragmentCommentsBinding.textViewNoDataFound.setText(response.message());
                                }

                            }

                            CommonDialogs.getInstance().dismissProgressDialog();

                        }

                        @Override
                        public void onFailure(Call<ActivityResponse> call, Throwable t) {
                            Log.d("mohit","Error code: "+t.getMessage());
                            if(!reload) {
                                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                                CommonDialogs.getInstance().dismissProgressDialog();
                                fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);

                                fragmentCommentsBinding.textViewNoDataFound.setText(t.getMessage());
                            }


                        }
                    });
                }
            }
        });
    }
    private void getActivityYearlyData(final String year, final boolean reload) {
        Log.d(TAG, "getActivityYearlyData: "+reload);
        if(reload)
        {
            if(activityDataList!=null)
            {
                if(!activityDataList.isEmpty())
                    activityDataList.clear();
            }
        }
        if(!reload)
        {
            fragmentCommentsBinding.containerActivity.setVisibility(View.GONE);
            fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.GONE);

        }
                new CommonMethods.InternetCheck(context, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {
                if (internet) {
                    if(!reload) {
                        CommonDialogs.getInstance().showProgressDialog(context);
                    }
                    JsonObject jsonObject=new JsonObject();
                    int user_id= MySharedPreferences.getInstance().getUserID(context, Constants.USERID);
                    Log.d(TAG, "accept: user id "+user_id);
                    jsonObject.addProperty(Constants.UID,user_id);
                    jsonObject.addProperty(Constants.YEAR,year);
                    Call<ActivityResponse> call=jsonPlaceHolderApi.getActivityYearlyData(jsonObject);
                    call.enqueue(new Callback<ActivityResponse>() {
                        @Override
                        public void onResponse(Call<ActivityResponse> call, Response<ActivityResponse> response) {
                            if(response.isSuccessful())
                            {
                                ActivityResponse activityResponse=response.body();

                                String description=activityResponse.getDescription();
                                String date=activityResponse.getMonthlyLastPracticeDate();
                                int status_code=Integer.parseInt(activityResponse.getStatusCode());
                                List<MonthlyData> monthlyData=activityResponse.getMonthlyData();


                                Log.d(TAG, "onResponse: status code "+status_code);
                                Log.d(TAG, "onResponse: description "+description);
                                Log.d(TAG, "onResponse: monthly data "+monthlyData);


                                if(status_code==1)
                                {
                                    if(monthlyData!=null && !monthlyData.isEmpty())
                                    {
                                        List<AllData> mainList=new ArrayList<>();

                                        for(MonthlyData data:monthlyData)
                                        {
                                            AllData activityData=new AllData();
                                            activityData.setDate(data.getDate());
                                            activityData.setCategoryName(data.getCategoryName());
                                            activityData.setNumberOfClicks(data.getNumberOfClicks());
                                            activityData.setDuration(data.getDuration());
                                            activityData.setComments(data.getComments());
                                            activityData.setTitle(data.getActivityName());
                                            activityData.setId(data.getId());
                                            activityData.setType(data.getType());
                                            activityData.setInstrumentName(data.getInstrumentName());
                                            activityDataList.add(activityData);
                                            mainList.add(activityData);

                                        }
                                        if(!reload) {
                                            fragmentCommentsBinding.containerActivity.setVisibility(View.VISIBLE);
                                            commentAdapter.updateList(mainList);
                                        }
                                    }
                                    else
                                    {
                                        if(!reload)
                                            fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
                                    }
                                    // finish();
                                }
                                else
                                {
                                    Log.d("mohit", "onResponse: Status code : "+status_code+"\n"+"Description : "+description);
                                    if(!reload) {
                                        Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
                                        fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
                                    }

                                }



                            }
                            else
                            {
                                Log.d("mohit", "onResponse: "+response.message());
                                if(!reload) {
                                    Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show();
                                    fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
                                    fragmentCommentsBinding.textViewNoDataFound.setText(response.message());
                                }

                            }
                            CommonDialogs.getInstance().dismissProgressDialog();




                        }

                        @Override
                        public void onFailure(Call<ActivityResponse> call, Throwable t) {
                            Log.d("mohit","Error code: "+t.getMessage());
                            if(!reload) {
                                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                                CommonDialogs.getInstance().dismissProgressDialog();

                                fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
                                fragmentCommentsBinding.textViewNoDataFound.setText(t.getMessage());
                            }


                        }
                    });
                }
            }
        });
    }
    private void getActivityMonthlyData(final boolean reload) {
//        if(reload)
//        {
//            if(activityDataList!=null)
//            {
//                if(!activityDataList.isEmpty())
//                    activityDataList.clear();
//            }
//        }
        if(!reload)
            fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.GONE);
        new CommonMethods.InternetCheck(context, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {
                if (internet) {
                    if(!reload)
                        CommonDialogs.getInstance().showProgressDialog(context);
                    JsonObject jsonObject=new JsonObject();
                    int user_id= MySharedPreferences.getInstance().getUserID(context, Constants.USERID);
                    Log.d(TAG, "accept: user id "+user_id);
                    jsonObject.addProperty(Constants.UID,user_id);
                    Call<ActivityResponse> call=jsonPlaceHolderApi.getActivityMonthlyData(jsonObject);
                    call.enqueue(new Callback<ActivityResponse>() {
                        @Override
                        public void onResponse(Call<ActivityResponse> call, Response<ActivityResponse> response) {
                            if(response.isSuccessful())
                            {
                                ActivityResponse activityResponse=response.body();

                                String description=activityResponse.getDescription();
                                String date=activityResponse.getMonthlyLastPracticeDate();
                                int status_code=Integer.parseInt(activityResponse.getStatusCode());
                                List<MonthlyData> monthlyData=activityResponse.getMonthlyData();


                                Log.d(TAG, "onResponse: status code "+status_code);
                                Log.d(TAG, "onResponse: description "+description);
                                Log.d(TAG, "onResponse: monthly data "+monthlyData);


                                if(status_code==1)
                                {
                                    if(monthlyData!=null && !monthlyData.isEmpty())
                                    {
                                        List<AllData> mainList=new ArrayList<>();
                                        for(MonthlyData data:monthlyData)
                                        {
                                            AllData activityData=new AllData();
                                            activityData.setDate(data.getDate());
                                            activityData.setCategoryName(data.getCategoryName());
                                            activityData.setNumberOfClicks(data.getNumberOfClicks());
                                            activityData.setDuration(data.getDuration());
                                            activityData.setComments(data.getComments());
                                            activityData.setTitle(data.getActivityName());
                                            activityData.setInstrumentName(data.getInstrumentName());
                                            activityData.setId(data.getId());
                                            activityData.setType(data.getType());
                                            mainList.add(activityData);
                                            activityDataList.add(activityData);

                                        }
                                        if(!reload) {
                                            fragmentCommentsBinding.spinnerSelectDate.setVisibility(View.VISIBLE);
                                            fragmentCommentsBinding.containerActivity.setVisibility(View.VISIBLE);
                                            commentAdapter.updateList(mainList);
                                        }
                                    }
                                    else
                                    {
                                        if(!reload)
                                            fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
                                    }
                                    // finish();
                                }
                                else
                                {
                                    Log.d("mohit", "onResponse: Status code : "+status_code+"\n"+"Description : "+description);
                                    if(!reload) {
                                        Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
                                        fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
                                    }

                                }



                            }
                            else
                            {
                                Log.d("mohit", "onResponse: "+response.message());
                                if(!reload) {
                                    Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show();
                                    fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);

                                    fragmentCommentsBinding.textViewNoDataFound.setText(response.message());
                                }

                            }
                            CommonDialogs.getInstance().dismissProgressDialog();




                        }

                        @Override
                        public void onFailure(Call<ActivityResponse> call, Throwable t) {
                            Log.d("mohit","Error code: "+t.getMessage());
                            if(!reload) {
                                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                                CommonDialogs.getInstance().dismissProgressDialog();
                                fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);

                                fragmentCommentsBinding.textViewNoDataFound.setText(t.getMessage());
                            }


                        }
                    });
                }
            }
        });
    }

    private void getTodayActivityData(final boolean reload)
    {
        if(reload)
        {
            if(activityDataList!=null)
            {
                if(!activityDataList.isEmpty())
                    activityDataList.clear();
            }
        }
        if(!reload)
            fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.GONE);
        new CommonMethods.InternetCheck(context, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {
                if (internet) {
                    if(!reload)
                    CommonDialogs.getInstance().showProgressDialog(context);
                    JsonObject jsonObject=new JsonObject();
                    int user_id= MySharedPreferences.getInstance().getUserID(context, Constants.USERID);
                    Log.d(TAG, "accept: user id "+user_id);
                    jsonObject.addProperty(Constants.UID,user_id);
                    Call<ActivityResponse> call=jsonPlaceHolderApi.getActivityTodayData(jsonObject);
                    call.enqueue(new Callback<ActivityResponse>() {
                        @Override
                        public void onResponse(Call<ActivityResponse> call, Response<ActivityResponse> response) {
                            if(response.isSuccessful())
                            {
                                ActivityResponse activityResponse=response.body();

                                String description=activityResponse.getDescription();
                                int status_code=Integer.parseInt(activityResponse.getStatusCode());
                                String date=activityResponse.getTodayPracticeDate();
                                List<TodayData> todayData=activityResponse.getTodayData();


                                Log.d(TAG, "onResponse: status code "+status_code);
                                Log.d(TAG, "onResponse: description "+description);
                                Log.d(TAG, "onResponse: today data "+todayData);


                                if(status_code==1)
                                {
                                    if(todayData!=null && !todayData.isEmpty())
                                    {
                                        List<AllData> mainList=new ArrayList<>();

                                        for(TodayData data:todayData)
                                         {
                                             AllData activityData=new AllData();
                                             activityData.setDate(date);
                                             activityData.setCategoryName(data.getCategoryName());
                                             activityData.setNumberOfClicks(data.getNumberOfClicks());
                                             activityData.setDuration(data.getDuration());
                                             activityData.setComments(data.getComments());
                                             activityData.setTitle(data.getActivityName());
                                             activityData.setId(data.getId());
                                             activityData.setType(data.getType());

                                             activityData.setInstrumentName(data.getInstrumentName());
                                             mainList.add(activityData);
                                             activityDataList.add(activityData);

                                         }
                                         if(!reload)
                                         {

                                             fragmentCommentsBinding.containerActivity.setVisibility(View.VISIBLE);
                                             commentAdapter.updateList(mainList);
                                         }


                                    }
                                    else
                                    {
                                        if(!reload)
                                        fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
                                    }
                                    // finish();
                                }
                                else
                                {
                                    Log.d("mohit", "onResponse: Status code : "+status_code+"\n"+"Description : "+description);
                                    if(!reload)
                                    {

                                        Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
                                        fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
                                    }

                                }



                            }
                            else
                            {
                                if(!reload)
                                {
                                    Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show();
                                    fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);

                                    fragmentCommentsBinding.textViewNoDataFound.setText(response.message());
                                }
                                Log.d("mohit", "onResponse: "+response.message());


                            }
                            CommonDialogs.getInstance().dismissProgressDialog();




                        }

                        @Override
                        public void onFailure(Call<ActivityResponse> call, Throwable t) {
                            Log.d("mohit","Error code: "+t.getMessage());
                            if(!reload)
                            {

                                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                                CommonDialogs.getInstance().dismissProgressDialog();
                                fragmentCommentsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);

                                fragmentCommentsBinding.textViewNoDataFound.setText(t.getMessage());
                            }


                        }
                    });
                }
            }
        });
    }
   private void setDataInSpinner(){
        int currentYear=Calendar.getInstance().get(Calendar.YEAR);
        int year=currentYear;
        stringListYear =  new ArrayList<String>();
        stringListYear.add("Select a year");
        while(year!=2018)
        {
            stringListYear.add(String.valueOf(year));
            year--;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_dropdown_item, stringListYear);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentCommentsBinding.spinnerSelectDate.setAdapter(adapter);

    }


}
