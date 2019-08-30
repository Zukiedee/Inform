package com.communityapp.inform.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.communityapp.inform.model.Notice;
import com.example.inform.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NoticeHolder extends RecyclerView.ViewHolder {
    TextView Title;
    TextView Category;
    TextView Description;
    TextView Date;
    TextView Username;
    ImageView imgResource;

    TextView deleteBtn, likeBtn, commentBtn;

    public NoticeHolder(View view, final NoticeAdapter.OnItemClickListener listener){
        super(view);

        //initialise views
        Title = itemView.findViewById(R.id.title);
        Category = itemView.findViewById(R.id.category);
        Description = itemView.findViewById(R.id.description);
        Date = itemView.findViewById(R.id.datePosted);
        Username = itemView.findViewById(R.id.userPosted);
        imgResource = itemView.findViewById(R.id.file);

        deleteBtn = itemView.findViewById(R.id.removeNotice);
        likeBtn = itemView.findViewById(R.id.like);
        commentBtn = itemView.findViewById(R.id.comment);

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

    public static class NoticeAdapter extends RecyclerView.Adapter<NoticeHolder> {
        private ArrayList<Notice> NoticeList;
        private Context context;
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onReminderClick(int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener){
            mListener = listener;

        }

        public NoticeAdapter(Context context, ArrayList<Notice> noticeList){
            this.NoticeList = noticeList;
            this.context = context;
        }

        @NonNull
        @Override
        public NoticeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //inflate notice item
            View v = LayoutInflater.from(context).inflate(R.layout.notice_item, parent, false);
            return new NoticeHolder(v, mListener); //returns view to holder class
        }

        @Override
        public void onBindViewHolder(@NonNull NoticeHolder holder, int position) {

            String title = NoticeList.get(position).getTitle();
            String cat = NoticeList.get(position).getCategory();
            String descrip = NoticeList.get(position).getDescription();
            String date = NoticeList.get(position).getDate();
            String id = NoticeList.get(position).getId();
            String uname = NoticeList.get(position).getUsername();

            //getData
            Notice currentItem = NoticeList.get(position);

            holder.Title.setText(title);
            holder.Category.setText(cat);
            holder.Description.setText(descrip);
            holder.Username.setText(uname);
            holder.Date.setText(date);
            holder.imgResource.setVisibility(View.GONE);
            //holder.imgResource.setImageResource(currentItem.getImgResource());

            /*String img = currentItem.getImgResource();
            try {
                Picasso.get().load(img).into(holder.imgResource);
            } catch (Exception e){

            }*/

            //handle button clicks
            holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();
                }
            });
            holder.commentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Commented", Toast.LENGTH_SHORT).show();
                }
            });
            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
                }
            });


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
