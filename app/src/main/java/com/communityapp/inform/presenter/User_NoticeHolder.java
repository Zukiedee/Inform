package com.communityapp.inform.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.communityapp.inform.model.Notice;
import com.example.inform.R;

import java.util.ArrayList;

public class User_NoticeHolder extends RecyclerView.ViewHolder {
    TextView Title;
    TextView Category;
    TextView Description;
    TextView Date;
    TextView Username;
    ImageView imgResource;

    User_NoticeHolder(View view, final User_NoticeAdapter.OnItemClickListener listener){
        super(view);
        Title = itemView.findViewById(R.id.title);
        Category = itemView.findViewById(R.id.category);
        Description = itemView.findViewById(R.id.description);
        Date = itemView.findViewById(R.id.datePosted);
        Username = itemView.findViewById(R.id.userPosted);
        imgResource = itemView.findViewById(R.id.file);
        TextView reminder = itemView.findViewById(R.id.add_reminder);

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

    public static class User_NoticeAdapter extends RecyclerView.Adapter<User_NoticeHolder> {
        private ArrayList<Notice> NoticeList;
        private Context context;
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onReminderClick(int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener){
            mListener = listener;

        }

        public User_NoticeAdapter(Context context, ArrayList<Notice> noticeList){
            this.NoticeList = noticeList;
            this.context = context;
        }

        @NonNull
        @Override
        public User_NoticeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item, null);
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
}
