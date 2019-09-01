package com.communityapp.inform.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.communityapp.inform.presenter.NoticeAdapter;
import com.communityapp.inform.presenter.ReminderDialog;
import com.example.inform.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import androidx.multidex.MultiDex;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.View.OnClickListener;

import com.communityapp.inform.model.Notice;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * The main screen.
 * Community posts will be displayed here.
 */
public class Newsfeed extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ReminderDialog.SingleChoiceListener {
    private RecyclerView noticeRecyclerView;
    private FirebaseAuth mAuth; //Firebase authentication
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference noticesRef = db.collection("Notices");
    private CollectionReference userRef = db.collection("Users");
    private NoticeAdapter adapter;
    private ProgressDialog progressDialog;

    private static final String CATEGORY_KEY = "Category";
    private static final String ID_KEY = "Id";

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void checkUserStatus(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //If user is not logged in, redirect to sign in screen
        if (currentUser== null){
            Intent loginIntent = new Intent(Newsfeed.this, SignIn.class);
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
        checkUserStatus();

        progressDialog = new ProgressDialog(this);

        loadNotices();

        showMenu();

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
     * Loads notices to be displayed in the general newsfeed
     */
    private void loadNotices() {
        progressDialog.setTitle("Loading data..");
        progressDialog.show();
        Query query = noticesRef.orderBy(ID_KEY, Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Notice> options = new FirestoreRecyclerOptions.Builder<Notice>()
                .setQuery(query, Notice.class)
                .build();

        adapter = new NoticeAdapter(options);
        progressDialog.dismiss();

        noticeRecyclerView = findViewById(R.id.NoticeRecyclerView);
        noticeRecyclerView.setHasFixedSize(true);
        noticeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        noticeRecyclerView.setAdapter(adapter);
    }

    public void categoryNotice(String category){
        progressDialog.setTitle("Loading "+ category+"..");
        progressDialog.show();
        Query query = noticesRef.whereEqualTo(CATEGORY_KEY, category);
        FirestoreRecyclerOptions<Notice> options = new FirestoreRecyclerOptions.Builder<Notice>()
                .setQuery(query, Notice.class)
                .build();

        adapter = new NoticeAdapter(options);
        progressDialog.dismiss();
        noticeRecyclerView.setAdapter(adapter);
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

    private void searchPosts(String searchQuery){

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

            Intent intentWelcome = new Intent(Newsfeed.this, SignIn.class);
            startActivity(intentWelcome);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handles navigation view item clicks.
        int id = item.getItemId();
        switch (id){
            case R.id.nav_home:
                //Navigate to user profile screen
                loadNotices();
                break;
            case R.id.nav_profile:
                //Navigate to user profile screen
                Intent intentProfile = new Intent(Newsfeed.this, Profile.class);
                startActivity(intentProfile);
                break;
            case R.id.nav_inbox:
                //Navigate to user inbox
                Intent intentInbox = new Intent(Newsfeed.this, Inbox.class);
                startActivity(intentInbox);
                break;
            case R.id.filter_news:
                //Display Local News posts notices
                categoryNotice("Local News");
                break;
            case R.id.filter_crime:
                //Display Crime Report notices
                categoryNotice("Crime Report");
                break;
            case R.id.filter_pet:
                //Display Missing Pet notices
                categoryNotice("Missing Pet");
                break;
            case R.id.filter_events:
                //Display Entertainment & Events notices
                categoryNotice("Events");
                break;
            case R.id.filter_fundraiser:
                //Display Fundraiser notices
                categoryNotice("Fundraiser");
                break;
            case R.id.filter_tradesmen:
                //Display Tradesmen refferals notices
                categoryNotice("Tradesmen Refferals");
                break;
            case R.id.filter_recommendations:
                //Display Recommendations notices
                categoryNotice("Recommendations");
                break;
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