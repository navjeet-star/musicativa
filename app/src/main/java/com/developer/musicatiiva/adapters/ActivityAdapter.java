package com.developer.musicatiiva.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.musicatiiva.R;
import com.developer.musicatiiva.activities.MainActivity;
import com.developer.musicatiiva.apiModels.JsonPlaceHolderApi;
import com.developer.musicatiiva.commonClasses.CommonDialogs;
import com.developer.musicatiiva.commonClasses.CommonMethods;
import com.developer.musicatiiva.databinding.ItemActivityBinding;
import com.developer.musicatiiva.fragments.SubInstrumentActivitiesFragment;
import com.developer.musicatiiva.models.AllData;
import com.developer.musicatiiva.models.DeleteSubInstrumentResponse;
import com.developer.musicatiiva.utils.Constants;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.developer.musicatiiva.utils.Constants.CLOCK_URL;


public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.MyViewHolder> {

    private Context context;
    ItemActivityBinding itemActivityBinding;
    private List<AllData> activityDataList;
     private JsonPlaceHolderApi jsonPlaceHolderApi;

    public ActivityAdapter(Context context,List<AllData> activityDataList)
    {
        this.context=context;
        this.activityDataList=activityDataList;
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(CLOCK_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);

    }

    @NonNull
    @Override
    public ActivityAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        itemActivityBinding=ItemActivityBinding.inflate(LayoutInflater.from(context),viewGroup,false);

        return new MyViewHolder(itemActivityBinding);
    }
    @Override
    public void onBindViewHolder(@NonNull ActivityAdapter.MyViewHolder myViewHolder, int i) {
        AllData activityData=activityDataList.get(i);

//        String comment=activityData.getComments();
//        if(comment!=null)
//        {
//            if(!comment.isEmpty())
//            {
//                if(comment.contains(","))
//                {
//                    String[] comments=comment.split(",");
//                    StringBuilder newComment=new StringBuilder();
//                    for( i=0;i<comments.length;i++)
//                    {
//                        if(i!=comments.length-1)
//                        newComment.append(comments[i]+"\n");
//                        else
//                            newComment.append(comments[i]);
//
//                    }
//                    activityData.setComments(newComment.toString());
//                }
//            }
//        }
       myViewHolder.customItemCommentsBinding.setActivity(activityData);
       if(activityData.getTitle()!=null)
       myViewHolder.customItemCommentsBinding.tvTitle.setText(activityData.getTitle());
       else
           myViewHolder.customItemCommentsBinding.tvTitle.setText("");

    }

//    @Override
//    public int getItemViewType(int position) {
//        return position;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void deleteActivity(final int position)
    {
     AllData data=activityDataList.get(position);
     final String id=data.getId();
     final String type=data.getType();
        new CommonMethods.InternetCheck(context, new CommonMethods.Consumer() {
            @Override
            public void accept(final Boolean internet) {
                if (internet) {
                    CommonDialogs.getInstance().showProgressDialog(context);
                    JsonObject jsonObject=new JsonObject();
                    jsonObject.addProperty(Constants.ID,id);
                    jsonObject.addProperty(Constants.TYPE,type);



                    Call<DeleteSubInstrumentResponse> call=jsonPlaceHolderApi.deleteActivity(jsonObject);
                    call.enqueue(new Callback<DeleteSubInstrumentResponse>() {
                        @Override
                        public void onResponse(Call<DeleteSubInstrumentResponse> call, Response<DeleteSubInstrumentResponse> response) {
                            if(response.isSuccessful())
                            {
                                DeleteSubInstrumentResponse deleteSubInstrumentResponse=response.body();

                                String desc=deleteSubInstrumentResponse.getDescription();

                                //String last_practiced_date=practiceData.getLast_practice_date();
                                int status_code=deleteSubInstrumentResponse.getStatus_code();


                                if(status_code==1)
                                {
                                    activityDataList.remove(position);
                                    notifyItemRemoved(position);

                                    Toast.makeText(context, desc, Toast.LENGTH_SHORT).show();


                                }
                                else
                                {
                                    Log.d("mohit", "onResponse: Status code : "+status_code+"\n"+"Description : "+desc);
                                    Toast.makeText(context, desc, Toast.LENGTH_SHORT).show();

                                    restoreItem();

                                }



                            }
                            else
                            {
                                Log.d("mohit", "onResponse: "+response.message());
                                Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show();
                                restoreItem();

                            }
                            CommonDialogs.getInstance().dismissProgressDialog();




                        }

                        @Override
                        public void onFailure(Call<DeleteSubInstrumentResponse> call, Throwable t) {
                            Log.d("mohit","Error code: "+t.getMessage());
                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                            CommonDialogs.getInstance().dismissProgressDialog();
                            restoreItem();


                        }
                    });
                }
            }
        });
    }

    public void update(List<AllData> activityDataList){
        this.activityDataList=activityDataList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return activityDataList.size();
    }
    private void restoreItem()
    {
        // listOfInstruments.add(position, selected_instrument);
        FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager();
        SubInstrumentActivitiesFragment subInstrumentFragment= (SubInstrumentActivitiesFragment) fragmentManager.findFragmentById(R.id.frame);
        subInstrumentFragment.loadActivities();
        //notifyDataSetChanged();

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ItemActivityBinding customItemCommentsBinding;

        public MyViewHolder(@NonNull ItemActivityBinding itemActivityBinding) {
            super(itemActivityBinding.getRoot());
            this.customItemCommentsBinding=itemActivityBinding;

        }
    }










}
