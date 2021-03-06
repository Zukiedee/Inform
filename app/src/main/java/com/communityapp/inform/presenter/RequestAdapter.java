package com.communityapp.inform.presenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.communityapp.inform.model.Notice;
import com.example.inform.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class RequestAdapter extends FirestoreRecyclerAdapter<Notice, RequestAdapter.RequestHolder> {
    private OnItemClickListener listener;

    public RequestAdapter(@NonNull FirestoreRecyclerOptions<Notice> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RequestHolder requestHolder, int position, @NonNull Notice notice) {
        requestHolder.Title.setText(notice.getTitle());
        requestHolder.Description.setText(notice.getDescription());
        requestHolder.Date.setText(notice.getDate());
        requestHolder.Username.setText(notice.getUsername());
        requestHolder.Category.setText(notice.getCategory());
        requestHolder.Community.setText(notice.getCommunity());
        requestHolder.userEngagements.setVisibility(View.GONE);
        requestHolder.Feedback.setVisibility(View.VISIBLE);

        if (notice.getImage().equals("noImage")){
            requestHolder.imgResource.setVisibility(View.GONE);
        }
        else {
            try { Picasso.get().load(notice.getImage()).into(requestHolder.imgResource); }
            catch (Exception e){ requestHolder.imgResource.setImageResource(R.drawable.ic_broken_image); }
        }
    }

    /**
     * Deletes notice item from firestore database
     * @param position position of notice in database
     */
    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }


    @NonNull
    @Override
    public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item, parent, false);
        return new RequestHolder(v);
    }

    class RequestHolder extends RecyclerView.ViewHolder{
        //initialise views
        TextView Title, Category, Description, Date, Username, Community;
        Button Accept, Reject;
        ImageView imgResource;
        LinearLayout userEngagements, Feedback;

        private RequestHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.title);
            Category = itemView.findViewById(R.id.category);
            Description = itemView.findViewById(R.id.description);
            Date = itemView.findViewById(R.id.datePosted);
            Username = itemView.findViewById(R.id.userPosted);
            imgResource = itemView.findViewById(R.id.file);
            Community = itemView.findViewById(R.id.community);
            userEngagements = itemView.findViewById(R.id.user_engagement);
            Feedback = itemView.findViewById(R.id.feedback);

            Accept = itemView.findViewById(R.id.accept_btn);
            Reject = itemView.findViewById(R.id.reject_btn);

            Accept.setOnClickListener(view -> {
                int position = getAdapterPosition();
                //handle click if notice not deleted
                if (position!= RecyclerView.NO_POSITION && listener!=null){
                    listener.acceptBtnClick(getSnapshots().getSnapshot(position), position);
                }
            });
            Reject.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position!= RecyclerView.NO_POSITION && listener!=null) {
                    listener.rejectBtnClick(getSnapshots().getSnapshot(position), position);
                }
            });
        }
    }

    /**
     * Accept and reject button click methods
     */
    public interface OnItemClickListener {
        void acceptBtnClick (DocumentSnapshot documentSnapshot, int position);
        void rejectBtnClick (DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}