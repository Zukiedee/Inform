package com.communityapp.inform.Presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.communityapp.inform.Model.Notification;
import com.example.inform.R;

import java.util.ArrayList;

//myHolder
public class User_NotificationAdapter extends RecyclerView.Adapter<User_NotificationHolder> {
    private ArrayList<Notification> MessageList;
    private Context context;

    public User_NotificationAdapter(Context c, ArrayList<Notification> messageList){
        this.context = c;
        this.MessageList = messageList;
    }

    @NonNull
    @Override
    public User_NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, null);
        return new User_NotificationHolder(v); //returns view to holder class
    }

    @Override
    public void onBindViewHolder(@NonNull User_NotificationHolder holder, int position) {
        holder.Subject.setText(MessageList.get(position).getTitle());
        holder.Status.setText(MessageList.get(position).getStatus());
        holder.Message.setText(MessageList.get(position).getMessage());
        holder.Date.setText(MessageList.get(position).getDate());
    }

    /**
     * The number of messages to appear in Inbox.
     * @return Amount of messages.
     */
    @Override
    public int getItemCount() {
        return MessageList.size();
    }
}

