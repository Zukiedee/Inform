package com.example.inform;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Community posts will be displayed here
 */
public class Newsfeed extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<Integer> userReminder = new ArrayList<>();
    private Button reminder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView noticeRecyclerView = findViewById(R.id.NoticeRecyclerView);
        noticeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.Adapter noticeAdapter = new NoticeAdapter(this, getNtoiceList());
        noticeRecyclerView.setAdapter(noticeAdapter);

        //Create a notice button
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentCreateNotice = new Intent(Newsfeed.this, createNotice.class);
                startActivity(intentCreateNotice);
            }
        });

        //add reminder button
        reminder = findViewById(R.id.add_reminder);
        /*reminder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //show options to user
                ;
            }
        });*/


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private ArrayList<NoticeItem> getNtoiceList() {
        ArrayList<NoticeItem> noticeList = new ArrayList<>();

        noticeList.add(new NoticeItem("Crime Report", "Crime report", "This is a report about crime. Crime is bad. Don't steal - you will go to jail.", "Bob Stuart", "8 Aug 2019", R.drawable.crime));
        noticeList.add(new NoticeItem("Latest News", "Local news", "The news notices will contain a headline, body, author and a poster attached to the story", "The Daily Mail", "20 July 2019", R.drawable.news));
        noticeList.add(new NoticeItem("Missing Pet", "Missing pet", "The pet notices will contain a poster of the missing pet, the date last seen, contact details and anything else you want to add?", "Joe Spark", "5 May 2019", R.drawable.pets));

        return noticeList;

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.newsfeed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Directs back to Welcome screen
        if (id == R.id.logout) {
            Intent intentWelcome = new Intent(Newsfeed.this, Welcome.class);
            startActivity(intentWelcome);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            //show newsfeed
        } else if (id == R.id.nav_profile) {
            //Edit profile screen
            Intent intentProfile = new Intent(Newsfeed.this, Profile.class);
            startActivity(intentProfile);
        } else if (id == R.id.nav_inbox) {
            //View Inbox Inbox
            Intent intentInbox = new Intent(Newsfeed.this, Admin_Inbox.class);
            startActivity(intentInbox);
        } else if (id == R.id.nav_reminders) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
