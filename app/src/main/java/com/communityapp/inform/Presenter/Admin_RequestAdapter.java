package com.communityapp.inform.Presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inform.R;

import java.util.ArrayList;
import com.communityapp.inform.Model.Notice;

public class Admin_RequestAdapter extends RecyclerView.Adapter<User_NoticeHolder> {
    private ArrayList<Notice> NoticeList;
    private Context context;

    public Admin_RequestAdapter(Context c, ArrayList<Notice> noticeList){
        this.context = c;
        this.NoticeList = noticeList;
    }

    @NonNull
    @Override
    public User_NoticeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_request_item, null);
        return new User_NoticeHolder(v); //returns view to holder class
    }

    @Override
    public void onBindViewHolder(@NonNull User_NoticeHolder holder, int position) {
        holder.Title.setText(NoticeList.get(position).getTitle());
        holder.Category.setText(NoticeList.get(position).getCategory());
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

