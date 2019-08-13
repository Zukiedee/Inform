package com.communityapp.inform.Presenter;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inform.R;

public class MessageHolder extends RecyclerView.ViewHolder {
    TextView Subject, Status, Message, Date;

    public MessageHolder(View view){
            super(view);
            Subject = itemView.findViewById(R.id.subject);
            Status = itemView.findViewById(R.id.status);
            Message = itemView.findViewById(R.id.message);
            Date = itemView.findViewById(R.id.MsgDate);
        }
}
