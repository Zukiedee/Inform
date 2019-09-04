package com.communityapp.inform.presenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.communityapp.inform.model.Notification;
import com.example.inform.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class NotificationAdapter extends FirestoreRecyclerAdapter<Notification, NotificationAdapter.NotificationHolder> {

    public NotificationAdapter(@NonNull FirestoreRecyclerOptions<Notification> options) { super(options); }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new NotificationHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull NotificationHolder notificationHolder, int i, @NonNull Notification notification) {
        notificationHolder.Subject.setText(notification.getTitle());
        notificationHolder.Status.setText(notification.getStatus());
        notificationHolder.Message.setText(notification.getMessage());
        notificationHolder.Date.setText(notification.getDate());
    }

    class NotificationHolder extends RecyclerView.ViewHolder{
        TextView Subject, Status, Message, Date;

        public NotificationHolder(@NonNull View itemView) {
            super(itemView);
            Subject = itemView.findViewById(R.id.subject);
            Status = itemView.findViewById(R.id.status);
            Message = itemView.findViewById(R.id.message);
            Date = itemView.findViewById(R.id.MsgDate);
        }
    }
}
