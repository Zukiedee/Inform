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
    private User_NoticeAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        //void onReminderClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(User_NoticeAdapter.OnItemClickListener listener){
        mListener = listener;

    }

    public Admin_RequestAdapter(Context c, ArrayList<Notice> noticeList){
        this.context = c;
        this.NoticeList = noticeList;
    }

    @NonNull
    @Override
    public User_NoticeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_request_item, null);
        return new User_NoticeHolder(v, mListener); //returns view to holder class
    }

    @Override
    public void onBindViewHolder(@NonNull User_NoticeHolder holder, int position) {
        Notice currentItem = NoticeList.get(position);

        holder.Title.setText(currentItem.getTitle());
        holder.Category.setText(currentItem.getCategory());
        holder.Description.setText(currentItem.getDescription());
        holder.Username.setText(currentItem.getUsername());
        holder.Date.setText(currentItem.getDate());
        holder.imgResource.setImageResource(currentItem.getImgResource());
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

