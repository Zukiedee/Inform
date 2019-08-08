package com.example.inform;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class NoticeHolder extends RecyclerView.ViewHolder {
    TextView Title, Description, Date, Username;

    public NoticeHolder(View view){
        super(view);
        Title = itemView.findViewById(R.id.title);
        Description = itemView.findViewById(R.id.description);
        Date = itemView.findViewById(R.id.datePosted);
        Username = itemView.findViewById(R.id.userPosted);
    }
}
