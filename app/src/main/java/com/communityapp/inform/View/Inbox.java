package com.communityapp.inform.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.inform.R;

import java.util.ArrayList;

import com.communityapp.inform.Model.Notification;
import com.communityapp.inform.Presenter.User_NotificationAdapter;

/**
 * Inbox from admin regarding post status will be posted here
 */
public class Inbox extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        //Back button on toolbar
        getSupportActionBar().setTitle("Inbox");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Notification layouts
        RecyclerView msgRecyclerView = findViewById(R.id.recyclerView);
        msgRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.Adapter msgAdapter = new User_NotificationAdapter(this, getMessageList());
        msgRecyclerView.setAdapter(msgAdapter);
    }

    private ArrayList<Notification> getMessageList() {
        ArrayList<Notification> messageList = new ArrayList<>();

        //Hardcoded dummy notification post inserts
        messageList.add(new Notification("Our dog, Sally, is missing!", "Pending","Your request to post a notice has been received. We are currently reviewing your notice and will let you know of the outcome as soon as possible.", "1 Apr 2019"));
        messageList.add(new Notification("Fundraiser in Memorial Hall", "Accepted","Your post has been approved! Your notice now appears in the newsfeed.", "23 Mar 2019"));
        messageList.add(new Notification("Fundraiser in Memorial Hall", "Pending", "Your request to post a notice has been received. We are currently reviewing your notice and will let you know of the outcome as soon as possible.", "23 Apr 2019"));
        messageList.add(new Notification("Man dancing naked", "Rejected", "Unfortunately your post has been rejected.", "20 Mar 2019"));

        return messageList;

    }
}
