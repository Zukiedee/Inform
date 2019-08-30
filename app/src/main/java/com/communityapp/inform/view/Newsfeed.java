package com.communityapp.inform.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.communityapp.inform.model.User;
import com.communityapp.inform.presenter.ReminderDialog;
import com.communityapp.inform.presenter.NoticeHolder;
import com.example.inform.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.multidex.MultiDex;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.communityapp.inform.model.Notice;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * The main screen.
 * Community posts will be displayed here.
 */
public class Newsfeed extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ReminderDialog.SingleChoiceListener {

    private RecyclerView noticeRecyclerView;
    private NoticeHolder.NoticeAdapter noticeAdapter;
    private FirebaseAuth mAuth; //Firebase authentication
    ArrayList<Notice> noticeList;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //If user is not logged in, redirect to sign in screen
        if (currentUser== null){
            Intent loginIntent = new Intent(Newsfeed.this, signIn.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);

        //initialize firebase
        mAuth = FirebaseAuth.getInstance();

        noticeRecyclerView = findViewById(R.id.NoticeRecyclerView);
        noticeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        noticeList = new ArrayList<>();

        showMenu();

        loadNotices();

        noticeAdapter.setOnItemClickListener(new NoticeHolder.NoticeAdapter.OnItemClickListener() {
            @Override
            public void onReminderClick(int position) {
                DialogFragment reminder = new ReminderDialog();
                reminder.setCancelable(false);
                reminder.show(getSupportFragmentManager(), "Set Reminder");
                //TextView r = findViewById(R.id.add_reminder);
                //r.setTextColor(getResources().getColor(R.color.colorReminder));
                //r.setText("Reminder Set");
            }
        });

        //Create a notice button
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentCreateNotice = new Intent(Newsfeed.this, createNotice.class);
                startActivity(intentCreateNotice);
            }
        });
    }

    /**
     * Handles drawer navigation menu.
     */
    public void showMenu(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

    }

    /**
     * List of notices to be posted
     * Needs to be extracted from the database
     * @return notice lists to be displayed in cardview on Newsfeed
     */
    private void loadNotices() {


        //path of all posts
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        //get all data from this ref
        /*reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //noticeList.clear();
                /*for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Notice notice = ds.getValue(Notice.class);
                    noticeList.add(notice);


                //}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //in case of error
                Toast.makeText(Newsfeed.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/

        noticeList.add(new Notice("Crime report", "8 Aug 2019", "This is a report about crime. Crime is bad. Don't steal - you will go to jail.", "1234",String.valueOf(R.drawable.crime),"Man shot twice in Area 1", "Bob Stuart"));
        noticeList.add(new Notice("Local news","20 July 2019",  "The news notices will contain a headline, body, author and a poster attached to the story" , "5678",  String.valueOf(R.drawable.news),"Interest rates are expected to increase", "The Daily Mail"));
        noticeList.add(new Notice("Missing pet",  "5 May 2019", "The pet notices will contain a poster of the missing pet, the date last seen, contact details and anything else you want to add?", "9012", String.valueOf(R.drawable.pets), "Our dog, Sally, is Missing","Joe Spark"));

        noticeAdapter = new NoticeHolder.NoticeAdapter(Newsfeed.this, noticeList);
        noticeRecyclerView.setAdapter(noticeAdapter);
        //return noticeList;
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

        /*
         * Logs user out of application
         * Directs user back to Welcome screen
         */
        if (id == R.id.logout) {
            mAuth.signOut();

            Intent intentWelcome = new Intent(Newsfeed.this, signIn.class);
            startActivity(intentWelcome);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handles navigation view item clicks.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            //Navigate to user profile screen
            Intent intentProfile = new Intent(Newsfeed.this, editProfile.class);
            startActivity(intentProfile);
        } else if (id == R.id.nav_inbox) {
            //Navigate to user inbox
            Intent intentInbox = new Intent(Newsfeed.this, Inbox.class);
            startActivity(intentInbox);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPositiveButtonClicked(String[] list, int pos) {
        //
    }

    @Override
    public void onNegativeButtonClicked() {
        //
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }
}