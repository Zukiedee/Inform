package com.communityapp.inform.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.communityapp.inform.model.Notice;
import com.communityapp.inform.model.Notification;
import com.communityapp.inform.presenter.RequestAdapter;
import com.example.inform.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Objects;

public class RequestsAdmin extends AppCompatActivity {
    private FirebaseAuth mAuth; //Firebase authentication
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private CollectionReference requests = database.collection("Requests");
    private RequestAdapter adapter;
    private ProgressDialog progressDialog;
    private RelativeLayout relativeLayout;

    private static final String TITLE_KEY = "Title";
    private static final String CATEGORY_KEY = "Category";
    private static final String DESCRIPTION_KEY = "Description";
    private static final String DATE_KEY = "Date";
    private static final String ID_KEY = "Id";
    private static final String IMAGE_KEY = "Image";
    private static final String COMMUNITY_KEY = "Community";
    private static final String USERNAME_KEY = "Username";
    private static final String UID_KEY = "User ID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Requests");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        progressDialog =  new ProgressDialog(this);
        relativeLayout = findViewById(R.id.inbox);

        checkUserStatus();

        String community ="UCT";
        loadRequests(community);

        adapter.setOnItemClickListener(new RequestAdapter.OnItemClickListener() {
            @Override
            public void acceptBtnClick(DocumentSnapshot documentSnapshot, int position) {
                String category = documentSnapshot.getString(CATEGORY_KEY);
                String DatetoString = documentSnapshot.getString(DATE_KEY);
                String body = documentSnapshot.getString(DESCRIPTION_KEY);
                String timeStamp = documentSnapshot.getString(ID_KEY);
                String image = documentSnapshot.getString(IMAGE_KEY);
                String title = documentSnapshot.getString(TITLE_KEY);
                String name = documentSnapshot.getString(USERNAME_KEY);
                String communities = documentSnapshot.getString(COMMUNITY_KEY);
                String email = documentSnapshot.getString(UID_KEY);
                CollectionReference notifications = database.collection("Users/"+email+"/Messages");

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(CATEGORY_KEY, category);
                hashMap.put(DATE_KEY, DatetoString);
                hashMap.put(DESCRIPTION_KEY, body);
                hashMap.put(ID_KEY, timeStamp);
                hashMap.put(IMAGE_KEY, image);
                hashMap.put(TITLE_KEY, title);
                hashMap.put(USERNAME_KEY, name);
                hashMap.put(COMMUNITY_KEY, communities);
                hashMap.put(UID_KEY, email);

                CollectionReference notices = database.collection("Notices");
                notices.document(Objects.requireNonNull(timeStamp)).set(hashMap)
                        .addOnSuccessListener(aVoid -> {
                            HashMap<String, String> msg = feedback(title, DatetoString, timeStamp);
                            notifications.document(timeStamp).set(msg);
                            Toast.makeText(RequestsAdmin.this, "Notice posted to newsfeed.\nFeedback sent to user", Toast.LENGTH_LONG).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(RequestsAdmin.this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void rejectBtnClick(DocumentSnapshot documentSnapshot, int position) {
                String DatetoString = documentSnapshot.getString(DATE_KEY);
                String timeStamp = documentSnapshot.getString(ID_KEY);
                String title = documentSnapshot.getString(TITLE_KEY);
                String email = documentSnapshot.getString(UID_KEY);

                CollectionReference notifications = database.collection("Users/"+email+"/Messages");
                HashMap<String, String> msg = feedback(title, DatetoString, timeStamp);
                notifications.document(Objects.requireNonNull(timeStamp)).set(msg);
                Toast.makeText(RequestsAdmin.this, "Notice Rejected.\nFeedback sent to user", Toast.LENGTH_LONG).show();
            }
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
        checkUserStatus();
        adapter.stopListening();
    }

    /**
     * Verifies that user is signed in
     */
    private void checkUserStatus(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //If user is not logged in, redirect to sign in screen
        if (currentUser== null){
            Intent loginIntent = new Intent(RequestsAdmin.this, SignIn.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        }
    }

    /**
     * Loads notices to be displayed in the general newsfeed
     */
    private void loadRequests(String community) {
        progressDialog.setTitle("Loading requests...");
        progressDialog.show();
        Query query = requests.whereEqualTo(COMMUNITY_KEY, community).orderBy(ID_KEY, Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Notice> options = new FirestoreRecyclerOptions.Builder<Notice>()
                .setQuery(query, Notice.class)
                .build();

        adapter = new RequestAdapter(options);
        progressDialog.dismiss();
        RecyclerView requestsRecyclerView = findViewById(R.id.inbox_recyclerView);
        requestsRecyclerView.setHasFixedSize(true);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestsRecyclerView.setAdapter(adapter);

        adapter.startListening();
        /*
         * Deletes notice from database by using swipe to delete function and removes it from RecyclerView
         */
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
        }).attachToRecyclerView(requestsRecyclerView);
    }

    private HashMap<String, String> feedback(String title, String DatetoString, String timeStamp){
        HashMap<String, String> msg = new HashMap<>();
        Notification notification = new Notification(title, DatetoString);
        notification.approve();
        msg.put("Title", title);
        msg.put("Date", DatetoString);
        msg.put("Status", notification.getStatus());
        msg.put("Message", notification.getMessage());
        msg.put("Id", timeStamp);
        return msg;
    }
}
