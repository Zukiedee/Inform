package com.example.inform;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inform.R;

public class MessageHolder extends RecyclerView.ViewHolder {
    TextView Subject, Message, Date;

    public MessageHolder(View view){
            super(view);
            Subject = itemView.findViewById(R.id.subject);
            Message = itemView.findViewById(R.id.message);
            Date = itemView.findViewById(R.id.MsgDate);
        }
}
