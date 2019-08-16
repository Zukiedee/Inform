package com.communityapp.inform.Presenter;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.inform.R;

public class User_NoticeHolder extends RecyclerView.ViewHolder {
    public TextView Title, Category, Description, Date, Username;
    public ImageView imgResource;
    public Button reminder;

    public User_NoticeHolder(View view, final User_NoticeAdapter.OnItemClickListener listener){
        super(view);
        Title = itemView.findViewById(R.id.title);
        Category = itemView.findViewById(R.id.category);
        Description = itemView.findViewById(R.id.description);
        Date = itemView.findViewById(R.id.datePosted);
        Username = itemView.findViewById(R.id.userPosted);
        imgResource = itemView.findViewById(R.id.file);
        reminder = itemView.findViewById(R.id.add_reminder);

        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener!=null){
                    int position = getAdapterPosition();
                    if (position!= RecyclerView.NO_POSITION){
                        listener.onReminderClick(position);
                    }
                }
            }
        });
    }
}
