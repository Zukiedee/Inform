package com.communityapp.inform.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.communityapp.inform.Presenter.User_NotificationHolder;
import com.example.inform.R;

import java.util.ArrayList;

import com.communityapp.inform.Model.Notification;

/**
 * Inbox from admin regarding post notification statuses will be posted here
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
        RecyclerView msgRecyclerView = findViewById(R.id.inbox_recyclerView);
        msgRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.Adapter msgAdapter = new User_NotificationHolder.User_NotificationAdapter(this, getMessageList());
        msgRecyclerView.setAdapter(msgAdapter);
    }

    private ArrayList<Notification> getMessageList() {
        ArrayList<Notification> messageList = new ArrayList<>();

        //Hardcoded dummy notification post inserts
        Notification n1 = new Notification("Our dog, Sally, is missing!", "1 Apr 2019");
        messageList.add(n1);
        Notification n2 = new Notification("Fundraiser in Memorial Hall", "23 Mar 2019");
        n2.approve();
        messageList.add(n2);
        Notification n3 = new Notification("Man dancing naked", "20 Mar 2019");
        n3.reject();
        messageList.add(n3);

        return messageList;
    }
}
