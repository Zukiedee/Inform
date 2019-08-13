package com.example.inform;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<NoticeHolder> {
    private ArrayList<NoticeItem> NoticeList;
    private Context context;

    public RequestAdapter(Context c, ArrayList<NoticeItem> noticeList){
        this.context = c;
        this.NoticeList = noticeList;
    }

    @NonNull
    @Override
    public NoticeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_request_item, null);
        return new NoticeHolder(v); //returns view to holder class
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeHolder holder, int position) {
        holder.Title.setText(NoticeList.get(position).getTitle());
        holder.Category.setText(NoticeList.get(position).getTitle());
        holder.Description.setText(NoticeList.get(position).getDescription());
        holder.Username.setText(NoticeList.get(position).getUsername());
        holder.Date.setText(NoticeList.get(position).getDate());
        holder.imgResource.setImageResource(NoticeList.get(position).getImgResource());
    }

    /**
     * The number of messages to appear in Inbox.
     * @return Amount of messages.
     */
    @Override
    public int getItemCount() {
        return NoticeList.size();
    }
}

