package com.communityapp.inform.Presenter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.inform.R;

public class NoticeHolder extends RecyclerView.ViewHolder {
    TextView Title, Category, Description, Date, Username;
    ImageView imgResource;

    public NoticeHolder(View view){
        super(view);
        Title = itemView.findViewById(R.id.title);
        Category = itemView.findViewById(R.id.category);
        Description = itemView.findViewById(R.id.description);
        Date = itemView.findViewById(R.id.datePosted);
        Username = itemView.findViewById(R.id.userPosted);
        imgResource = itemView.findViewById(R.id.file);
    }
}
