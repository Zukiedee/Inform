package com.communityapp.inform.presenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.communityapp.inform.model.Notice;
import com.example.inform.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class NoticeAdapter extends FirestoreRecyclerAdapter<Notice, NoticeAdapter.NoticeHolder> {
    private NoticeAdapter.OnItemClickListener listener;

    public NoticeAdapter(@NonNull FirestoreRecyclerOptions<Notice> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NoticeHolder noticeHolder, int position, @NonNull Notice notice) {
        noticeHolder.Title.setText(notice.getTitle());
        noticeHolder.Description.setText(notice.getDescription());
        noticeHolder.Date.setText(notice.getDate());
        noticeHolder.Username.setText(notice.getUsername());
        noticeHolder.Category.setText(notice.getCategory());
        noticeHolder.Community.setText(notice.getCommunity());

        if (notice.getImage().equals("noImage")){
            noticeHolder.imgResource.setVisibility(View.GONE);
        }
        else {
            try {
                Picasso.get().load(notice.getImage()).into(noticeHolder.imgResource);
            } catch (Exception e){
                noticeHolder.imgResource.setImageResource(R.drawable.ic_broken_image);
            }
        }
    }

    /**
     * Deletes notice document item from FireStore database
     * @param position position of notice in database
     */
    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    @NonNull
    @Override
    public NoticeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item, parent, false);
        return new NoticeHolder(v);
    }

    class NoticeHolder extends RecyclerView.ViewHolder{
        //initialise views
        TextView Title, Category, Description, Date, Username, Community, reminder, likeBtn, dislikeBtn, commentBtn;
        ImageView imgResource;

        private NoticeHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.title);
            Category = itemView.findViewById(R.id.category);
            Description = itemView.findViewById(R.id.description);
            Date = itemView.findViewById(R.id.datePosted);
            Username = itemView.findViewById(R.id.userPosted);
            imgResource = itemView.findViewById(R.id.file);
            Community = itemView.findViewById(R.id.community);

            likeBtn = itemView.findViewById(R.id.Like);
            dislikeBtn = itemView.findViewById(R.id.Dislike);
            commentBtn = itemView.findViewById(R.id.comment);
            reminder = itemView.findViewById(R.id.add_reminder);

            reminder.setOnClickListener(view -> {
                int position = getAdapterPosition();
                //handle click if notice not deleted
                if (position!= RecyclerView.NO_POSITION && listener!=null){
                    listener.addReminderBtnClick(getSnapshots().getSnapshot(position), position);
                }
            });

            likeBtn.setOnClickListener(view -> {
                int position = getAdapterPosition();
                //handle click if notice not deleted
                if (position!= RecyclerView.NO_POSITION && listener!=null){
                    listener.likeBtnClick(getSnapshots().getSnapshot(position), position);
                }
            });
            dislikeBtn.setOnClickListener(view -> {
                int position = getAdapterPosition();
                //handle click if notice not deleted
                if (position!= RecyclerView.NO_POSITION && listener!=null){
                    listener.dislikeBtnClick(getSnapshots().getSnapshot(position), position);
                }
            });

            commentBtn.setOnClickListener(view -> {
                int position = getAdapterPosition();
                //handle click if notice not deleted
                if (position!= RecyclerView.NO_POSITION && listener!=null){
                    listener.commentBtnClick(getSnapshots().getSnapshot(position), position);
                }
            });
        }
    }

    /**
     * Accept and reject button click methods
     */
    public interface OnItemClickListener {
        void addReminderBtnClick (DocumentSnapshot documentSnapshot, int position);
        void likeBtnClick (DocumentSnapshot documentSnapshot, int position);
        void dislikeBtnClick (DocumentSnapshot documentSnapshot, int position);
        void commentBtnClick (DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}