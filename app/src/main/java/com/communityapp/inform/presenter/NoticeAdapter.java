package com.communityapp.inform.presenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.communityapp.inform.model.Notice;
import com.example.inform.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class NoticeAdapter extends FirestoreRecyclerAdapter<Notice, NoticeAdapter.NoticeHolder> {

    public NoticeAdapter(@NonNull FirestoreRecyclerOptions<Notice> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NoticeHolder noticeHolder, int i, @NonNull Notice notice) {
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

        noticeHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();
            }
        });
        noticeHolder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Commented", Toast.LENGTH_SHORT).show();
            }
        });
        noticeHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
                deleteItem(i);
            }
        });


        noticeHolder.Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Post to notices

                //remove request
                deleteItem(i);
            }
        });

        noticeHolder.Reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete request
                deleteItem(i);
            }
        });
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public void setReminder(){

    }

    @NonNull
    @Override
    public NoticeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item, parent, false);
        return new NoticeHolder(v);
    }

    class NoticeHolder extends RecyclerView.ViewHolder{
        //initialise views
        TextView Title, Category, Description, Date, Username, Community, deleteBtn, likeBtn, commentBtn;
        Button Accept, Reject;
        ImageView imgResource;

        public NoticeHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.title);
            Category = itemView.findViewById(R.id.category);
            Description = itemView.findViewById(R.id.description);
            Date = itemView.findViewById(R.id.datePosted);
            Username = itemView.findViewById(R.id.userPosted);
            imgResource = itemView.findViewById(R.id.file);
            Community = itemView.findViewById(R.id.community);

            Accept = itemView.findViewById(R.id.accept_btn);
            Reject = itemView.findViewById(R.id.reject_btn);

            deleteBtn = itemView.findViewById(R.id.removeNotice);
            likeBtn = itemView.findViewById(R.id.like);
            commentBtn = itemView.findViewById(R.id.comment);
            TextView reminder = itemView.findViewById(R.id.add_reminder);
        }
    }
}
