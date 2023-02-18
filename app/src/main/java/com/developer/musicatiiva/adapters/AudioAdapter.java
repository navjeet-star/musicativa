package com.developer.musicatiiva.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.musicatiiva.apiModels.JsonPlaceHolderApi;
import com.developer.musicatiiva.databinding.AudioListAdapterBinding;
import com.developer.musicatiiva.models.AllData;

import java.util.ArrayList;
import java.util.List;


public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.MyViewHolder> {

    private Context context;
    AudioListAdapterBinding itemActivityBinding;
    private List<AllData> activityDataList;
     private JsonPlaceHolderApi jsonPlaceHolderApi;
    ArrayList<String> FilesInFolder;
    public AudioAdapter(Context context, ArrayList<String> filesInFolder)
    {
        this.context=context;
        this.FilesInFolder=filesInFolder;

    }

    @NonNull
    @Override
    public AudioAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        itemActivityBinding=AudioListAdapterBinding.inflate(LayoutInflater.from(context),viewGroup,false);

        return new MyViewHolder(itemActivityBinding);
    }
    @Override
    public void onBindViewHolder(@NonNull AudioAdapter.MyViewHolder myViewHolder, int i) {
        String activityData=FilesInFolder.get(i);

//       myViewHolder.customItemCommentsBinding.setActivity(activityData);


    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return FilesInFolder.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        AudioListAdapterBinding customItemCommentsBinding;

        public MyViewHolder(@NonNull AudioListAdapterBinding itemActivityBinding) {
            super(itemActivityBinding.getRoot());
            this.customItemCommentsBinding=itemActivityBinding;

        }
    }










}
