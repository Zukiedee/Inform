package com.communityapp.inform.view;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.communityapp.inform.presenter.NoticeAdapter;
import com.communityapp.inform.presenter.ReminderDialog;
import com.example.inform.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.provider.CalendarContract;
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
import androidx.fragment.app.DialogFragment;
import androidx.multidex.MultiDex;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.communityapp.inform.model.Notice;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

/**
 * The main newsfeed screen.
 * Community posts will be displayed here depending on the communities selected by users in their profile.
 */
public class Newsfeed extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ReminderDialog.SingleChoiceListener {
    private FirebaseAuth mAuth; //Firebase authentication
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private CollectionReference noticesRef = database.collection("Notices");
    private NoticeAdapter adapter;
    private RelativeLayout relativeLayout;
    private ProgressDialog progressDialog;
    private String username, user_email;
    private TextView nav_username;
    private String currentcommunity = "", remind_me;

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
        relativeLayout = findViewById(R.id.newsfeed);

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
    private void checkUserStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //If user is not logged in, redirect to sign in screen
        if (currentUser == null) {
            Intent loginIntent = new Intent(Newsfeed.this, SignIn.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        } else {
            user_email = mAuth.getCurrentUser().getEmail();
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

        ButtonClicks(noticeRecyclerView);
    }

    /**
     * Displays notices from input category
     * @param category Category to filter notices by
     */
    private void categoryNotice(String category) {
        progressDialog.setTitle("Loading " + category + "..");
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

        ButtonClicks(noticeRecyclerView);
    }

    /**
     * Handles drawer navigation menu.
     */
    private void showMenu() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View view = navigationView.getHeaderView(0);
        TextView nav_email = view.findViewById(R.id.main_email);

        nav_email.setText(user_email);

        DocumentReference userRef = database.document("Users/" + user_email);
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        nav_username = findViewById(R.id.main_username);
                        username = documentSnapshot.getString(USERNAME_KEY);
                        nav_username.setText(username);

                        //retrieve communities from user's profile and display it in the navigation drawer menu
                        String communities = documentSnapshot.getString("Communities");
                        assert communities != null;
                        ArrayList<String> communityList = new ArrayList<>(Arrays.asList(communities.split(",")));

                        Menu menu = navigationView.getMenu();
                        SubMenu communitiesMenu = menu.addSubMenu("Communities");
                        for (int i = 0; i < communityList.size(); i++) {
                            communitiesMenu.add(communityList.get(i).trim()).setIcon(R.drawable.ic_location);
                        }
                        navigationView.invalidate();
                        currentcommunity = communityList.get(0);
                        Objects.requireNonNull(getSupportActionBar()).setTitle(currentcommunity);
                        loadNotices(currentcommunity);
                    }
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(relativeLayout, "Error occurred: " + e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        /*
         Log out alert dialog displayed to user to prompt if they are sure they want to log out.
         Logs user out of application. Directs user back to SignIn screen
         */
        if (id == R.id.logout) {
            new AlertDialog.Builder(Newsfeed.this)
                    .setTitle("Log out")
                    .setMessage("Are you sure want to log out?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        mAuth.signOut();
                        Intent intentWelcome = new Intent(Newsfeed.this, SignIn.class);
                        startActivity(intentWelcome);
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {
                        //do nothing
                    })
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handles navigation view item clicks.
        int id = item.getItemId();
        String title = "" + item.getTitle();
        switch (id) {
            case R.id.nav_profile:
                //Navigate to user profile screen
                Intent intentProfile = new Intent(Newsfeed.this, Profile.class);
                startActivity(intentProfile);
                break;
            case R.id.nav_inbox:
                //navigate to requests inbox if user is admin

                //Navigate to user inbox
                Intent intentInbox = new Intent(Newsfeed.this, RequestsAdmin.class);
                startActivity(intentInbox);
                break;
             /*
                Displays community posts filtered by selected category
             */
            case R.id.news:
                categoryNotice("Local News");
                break;
            case R.id.crime:
                categoryNotice("Crime Report");
                break;
            case R.id.pets:
                categoryNotice("Missing Pet");
                break;
            case R.id.events:
                categoryNotice("Events");
                break;
            case R.id.fundraiser:
                categoryNotice("Fundraiser");
                break;
            case R.id.tradesmen:
                categoryNotice("Tradesmen Referrals");
                break;
            case R.id.recommendations:
                categoryNotice("Recommendations");
                break;
            default:
                NavigationView navigationView = findViewById(R.id.nav_view);
                Menu menu = navigationView.getMenu();
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
            if (item.hasSubMenu()) {
                unCheckAllMenuItems(item.getSubMenu());
            } else {
                item.setChecked(false);
            }
        }
    }

    @Override
    public void onPositiveReminderButtonClicked(String[] list, int pos) {
        //positive button clicked on reminder dialog
        remind_me = list[pos];
        Toast.makeText(this, "Reminder set for: "+list[pos], Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNegativeReminderButtonClicked() {
        //negative button clicked on reminder dialog
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    private void ButtonClicks(RecyclerView noticeRecyclerView) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
                Snackbar.make(relativeLayout, "Notice deleted", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }).attachToRecyclerView(noticeRecyclerView);

        adapter.setOnItemClickListener(new NoticeAdapter.OnItemClickListener() {
            @Override
            public void addReminderBtnClick(DocumentSnapshot documentSnapshot, int position) {
                DialogFragment reminder = new ReminderDialog();
                reminder.setCancelable(false);
                reminder.show(getSupportFragmentManager(), "Set Reminder");

                if (remind_me.equals("30 minutes")){
                    insertEvent(documentSnapshot.getString("Title"), documentSnapshot.getString("Description"), 30*60*60);
                }

            }

            @Override
            public void likeBtnClick(DocumentSnapshot documentSnapshot, int position) {
                //To do
                Toast.makeText(Newsfeed.this, "Liked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void dislikeBtnClick(DocumentSnapshot documentSnapshot, int position) {
                //To do
                Toast.makeText(Newsfeed.this, "Disliked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void commentBtnClick(DocumentSnapshot documentSnapshot, int position) {
                //To do
                Toast.makeText(Newsfeed.this, "Commented", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertEvent(String title, String description, int endTime) {
        ContentResolver contentResolver = this.getContentResolver();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CalendarContract.Events.TITLE, title);
        contentValues.put(CalendarContract.Events.DESCRIPTION, description);
        contentValues.put(CalendarContract.Events.DTSTART, Calendar.getInstance().getTimeInMillis());
        contentValues.put(CalendarContract.Events.DTEND, Calendar.getInstance().getTimeInMillis() + endTime*1000);
        contentValues.put(CalendarContract.Events.CALENDAR_ID, 1);
        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues);

        Toast.makeText(this, "Reminder set", Toast.LENGTH_SHORT).show();
    }
}