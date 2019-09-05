package com.communityapp.inform.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.communityapp.inform.presenter.NoticeAdapter;
import com.communityapp.inform.presenter.ReminderDialog;
import com.example.inform.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.SubMenu;
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
import android.widget.TextView;
import android.widget.Toast;

import com.communityapp.inform.model.Notice;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * The main newsfeed screen.
 * Community posts will be displayed here depending on the communities selected by users in their profile.
 */
public class Newsfeed extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ReminderDialog.SingleChoiceListener  {
    private FirebaseAuth mAuth; //Firebase authentication
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private CollectionReference noticesRef = database.collection("Notices");
    private NoticeAdapter adapter;
    private ProgressDialog progressDialog;
    private String username;
    private TextView nav_username;
    private String currentcommunity ="";

    //database reference keys
    private static final String COMMUNITY_KEY = "Community";
    private static final String CATEGORY_KEY = "Category";
    private static final String ID_KEY = "Id";
    private static final String USERNAME_KEY = "Username";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);

        //initialize firebase
        mAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        progressDialog = new ProgressDialog(this);


        loadNotices(currentcommunity);
        showMenu();

        //Create a notice button
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(view -> {
            Intent intentCreateNotice = new Intent(Newsfeed.this, createNotice.class);
            startActivity(intentCreateNotice);
        });
    }

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

    /**
     * Verifies that user is signed in
     */
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

    /**
     * Loads notices to be displayed filtered by community in the general newsfeed
     * @param community Community to filter notices by.
     */
    private void loadNotices(String community) {
        progressDialog.setTitle("Loading notices..");
        progressDialog.show();

        Query query = noticesRef.whereEqualTo(COMMUNITY_KEY, community).orderBy(ID_KEY, Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Notice> options = new FirestoreRecyclerOptions.Builder<Notice>()
                .setQuery(query, Notice.class)
                .build();

        adapter = new NoticeAdapter(options);
        RecyclerView noticeRecyclerView = findViewById(R.id.NoticeRecyclerView);
        noticeRecyclerView.setHasFixedSize(true);
        noticeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        noticeRecyclerView.setAdapter(adapter);
        progressDialog.dismiss();
        adapter.startListening();
    }

    /**
     * Displays notices from input category
     * @param category Category to filter notices by
     */
    private void categoryNotice(String category){
        progressDialog.setTitle("Loading "+ category+"..");
        progressDialog.show();
        Query query = noticesRef.whereEqualTo(COMMUNITY_KEY, currentcommunity).whereEqualTo(CATEGORY_KEY, category).orderBy(ID_KEY, Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Notice> options = new FirestoreRecyclerOptions.Builder<Notice>()
                .setQuery(query, Notice.class)
                .build();
        adapter = new NoticeAdapter(options);
        RecyclerView noticeRecyclerView = findViewById(R.id.NoticeRecyclerView);
        noticeRecyclerView.setHasFixedSize(true);
        noticeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        noticeRecyclerView.setAdapter(adapter);
        progressDialog.dismiss();
        adapter.startListening();
    }

    /**
     * Handles drawer navigation menu.
     */
    private void showMenu(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View view = navigationView.getHeaderView(0);
        TextView nav_email = view.findViewById(R.id.main_email);

        String email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        nav_email.setText(email);

        DocumentReference userRef = database.document("Users/"+email);
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        nav_username = findViewById(R.id.main_username);
                        username = documentSnapshot.getString(USERNAME_KEY);
                        nav_username.setText(username);

                        //retrieve communities from user's profile and display it in the navigation drawer menu
                        String communities = documentSnapshot.getString("Communities");
                        assert communities != null;
                        ArrayList<String> communityList = new ArrayList<>(Arrays.asList(communities.split(",")));

                        Menu menu = navigationView.getMenu();
                        SubMenu communitiesMenu = menu.addSubMenu("Communities");
                        for (int i=0; i< communityList.size(); i++){
                            communitiesMenu.add(communityList.get(i).trim()).setIcon(R.drawable.ic_location);
                        }
                        navigationView.invalidate();
                        currentcommunity = communityList.get(0);
                        Objects.requireNonNull(getSupportActionBar()).setTitle(currentcommunity);
                        loadNotices(currentcommunity);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Newsfeed.this, "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Newsfeed.this, Profile.class));
                });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) { drawer.closeDrawer(GravityCompat.START); }
        else { super.onBackPressed(); }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.newsfeed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        //Logs user out of application. Directs user back to SignIn screen
        if (id == R.id.logout) {
            mAuth.signOut();
            Intent intentWelcome = new Intent(Newsfeed.this, SignIn.class);
            startActivity(intentWelcome);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handles navigation view item clicks.
        int id = item.getItemId();
        String title = ""+item.getTitle();
        switch (id) {
            case R.id.nav_profile:
                //Navigate to user profile screen
                Intent intentProfile = new Intent(Newsfeed.this, Profile.class);
                startActivity(intentProfile);
                break;
            case R.id.nav_inbox:

                //Navigate to user inbox
                Intent intentInbox = new Intent(Newsfeed.this, RequestsAdmin.class);
                startActivity(intentInbox);
                break;
            case R.id.news:
                //Display Local News posts notices
                categoryNotice("Local News");
                break;
            case R.id.crime:
                //Display Crime Report notices
                categoryNotice("Crime Report");
                break;
            case R.id.pets:
                //Display Missing Pet notices
                categoryNotice("Missing Pet");
                break;
            case R.id.events:
                //Display Entertainment & Events notices
                categoryNotice("Events");
                break;
            case R.id.fundraiser:
                //Display Fundraiser notices
                categoryNotice("Fundraiser");
                break;
            case R.id.tradesmen:
                //Display Tradesmen referrals notices
                categoryNotice("Tradesmen Referrals");
                break;
            case R.id.recommendations:
                //Display Recommendations notices
                categoryNotice("Recommendations");
                break;
            default:
                NavigationView navigationView = findViewById(R.id.nav_view);
                Menu menu =  navigationView.getMenu();
                unCheckAllMenuItems(menu);
                loadNotices(title);
                currentcommunity = title;
                Objects.requireNonNull(getSupportActionBar()).setTitle(currentcommunity);
                item.setChecked(true);
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Unchecks all menu items when new menu item is clicked
     * @param menu Navigation drawer menu
     */
    private void unCheckAllMenuItems(@NonNull final Menu menu) {
        int size = menu.size();
        for (int i = 0; i < size; i++) {
            final MenuItem item = menu.getItem(i);
            // Un check sub menu items
            if(item.hasSubMenu()) { unCheckAllMenuItems(item.getSubMenu()); }
            else { item.setChecked(false); }
        }
    }

    @Override
    public void onPositiveButtonClicked(String[] list, int pos) {    }

    @Override
    public void onNegativeButtonClicked() {    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }
}