package com.communityapp.inform.View;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inform.R;

import java.util.ArrayList;

import com.communityapp.inform.Model.Notice;
import com.communityapp.inform.Presenter.Admin_RequestAdapter;

/**
 * Inbox from admin regarding post status will be posted here
 */
public class Admin_Inbox extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        //Back button on toolbar
        getSupportActionBar().setTitle("Inbox");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Notification layouts
        RecyclerView requestRecyclerView = findViewById(R.id.inbox_recyclerView);
        requestRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.Adapter noticeAdapter = new Admin_RequestAdapter(this, getNoticeList());
        requestRecyclerView.setAdapter(noticeAdapter);


    }

    private ArrayList<Notice> getNoticeList() {
        ArrayList<Notice> noticeList = new ArrayList<>();

        noticeList.add(new Notice("Latest News", "Local news", "The news notices will contain a headline, body, author and a poster attached to the story", "The Daily Mail", "20 July 2019", R.drawable.news));
        noticeList.add(new Notice("Missing Pet", "Missing pet", "The pet notices will contain a poster of the missing pet, the date last seen, contact details and anything else you want to add?", "Joe Spark", "5 May 2019", R.drawable.pets));

        return noticeList;

    }
}
