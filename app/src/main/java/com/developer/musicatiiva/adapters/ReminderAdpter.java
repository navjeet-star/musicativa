package com.developer.musicatiiva.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.developer.musicatiiva.R;
import com.developer.musicatiiva.activities.SetReminderActivity;
import com.developer.musicatiiva.models.Reminder;

import java.util.List;

public class ReminderAdpter extends RecyclerView.Adapter<ReminderAdpter.ViewHolder>{


    private Context context;
    private List<Reminder> reminderList;


    public ReminderAdpter(Context context, List<Reminder> reminderList) {

        this.context = context;
        this.reminderList=reminderList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.layout_reminder, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.txtTesting.setText(reminderList.get(position).getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(context,SetReminderActivity.class);
                intent.putExtra("id",reminderList.get(position).getId());
                intent.putExtra("title",reminderList.get(position).getTitle());
                intent.putExtra("reminder_at",reminderList.get(position).getReminder_at());
                intent.putExtra("frequency",reminderList.get(position).getFrequency());
                intent.putExtra("timezone",reminderList.get(position).getTimezone());
                intent.putExtra("type","adpter");
                context.startActivity(intent);

            }
        });
    }
    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtTesting;
        public ViewHolder(View itemView) {
            super(itemView);
            txtTesting=itemView.findViewById(R.id.txtTesting);
        }
    }
}