package com.communityapp.inform.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inform.R;

import java.util.ArrayList;
import com.communityapp.inform.model.Notice;

public class Admin_RequestToUploadAdapter extends RecyclerView.Adapter<NoticeHolder> {
    private ArrayList<Notice> NoticeList;
    private Context context;
    private NoticeHolder.NoticeAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        //void onReminderClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(NoticeHolder.NoticeAdapter.OnItemClickListener listener){
        mListener = listener;

    }

    public Admin_RequestToUploadAdapter(Context c, ArrayList<Notice> noticeList){
        this.context = c;
        this.NoticeList = noticeList;
    }

    @NonNull
    @Override
    public NoticeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item, null);
        return new NoticeHolder(v, mListener); //returns view to holder class
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeHolder holder, int position) {
        Notice currentItem = NoticeList.get(position);

        holder.Title.setText(currentItem.getTitle());
        holder.Category.setText(currentItem.getCategory());
        holder.Description.setText(currentItem.getDescription());
        holder.Username.setText(currentItem.getUsername());
        holder.Date.setText(currentItem.getDate());
        //holder.imgResource.setImageResource(currentItem.getImgResource());
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

