package com.example.inform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

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

        RecyclerView.Adapter msgAdapter = new MessageAdapter(this, getMessageList());
        msgRecyclerView.setAdapter(msgAdapter);
    }

    private ArrayList<MessageItem> getMessageList() {
        ArrayList<MessageItem> messageList = new ArrayList<>();

        //Hardcoded dummy notification post inserts
        messageList.add(new MessageItem("Post 1", "Message 1", "1 Jan 2019"));
        messageList.add(new MessageItem("Post 2", "Message 2", "1 Feb 2019"));
        messageList.add(new MessageItem("Post 3", "Message 3", "1 Mar 2019"));
        messageList.add(new MessageItem("Post 4", "Message 4", "1 Apr 2019"));
        messageList.add(new MessageItem("Post 5", "Message 5", "1 May 2019"));

        return messageList;

    }
}
