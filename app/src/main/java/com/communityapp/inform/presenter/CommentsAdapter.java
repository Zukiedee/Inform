package com.communityapp.inform.presenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.communityapp.inform.model.Comments;
import com.example.inform.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class CommentsAdapter extends FirestoreRecyclerAdapter<Comments, CommentsAdapter.CommentsHolder> {

    public CommentsAdapter(@NonNull FirestoreRecyclerOptions<Comments> options) { super(options); }

    @NonNull
    @Override
    public CommentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentsHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentsHolder commentsHolder, int i, @NonNull Comments comments) {
        commentsHolder.Username.setText(comments.getUsername());
        commentsHolder.Comment.setText(comments.getComment());
        commentsHolder.Date.setText(comments.getDate());
    }

    class CommentsHolder extends RecyclerView.ViewHolder{
        TextView Username, Comment, Date;
        EditText comment_text;

        CommentsHolder(@NonNull View itemView) {
            super(itemView);
            Username = itemView.findViewById(R.id.comment_username);
            Comment = itemView.findViewById(R.id.comment_msg);
            Date = itemView.findViewById(R.id.comment_date);

            comment_text = itemView.findViewById(R.id.comment_text);

        }
    }
}
