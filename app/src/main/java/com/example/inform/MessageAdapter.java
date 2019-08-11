package com.example.inform;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//myHolder
public class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {
    private ArrayList<MessageItem> MessageList;
    private Context context;

    public MessageAdapter (Context c, ArrayList<MessageItem> messageList){
        this.context = c;
        this.MessageList = messageList;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, null);
        return new MessageHolder(v); //returns view to holder class
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        holder.Subject.setText(MessageList.get(position).getTitle());
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
