package com.communityapp.inform.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.communityapp.inform.model.Notice;
import com.communityapp.inform.presenter.NoticeAdapter;
import com.example.inform.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class RequestsAdmin extends AppCompatActivity {

    private FirebaseAuth mAuth; //Firebase authentication
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private CollectionReference requests = database.collection("Requests");
    private NoticeAdapter adapter;
    private ProgressDialog progressDialog;
    //private static final String ID_KEY = "Id";
    private static final String COMMUNITY_KEY = "Community";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        //Back button on toolbar
        Objects.requireNonNull(getSupportActionBar()).setTitle("Requests");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        progressDialog =  new ProgressDialog(this);

        checkUserStatus();

        visbility();

        String community ="UCT";
        loadRequests(community);

        /*Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RequestsAdmin.this, "Accept button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        Reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RequestsAdmin.this, "Accept button clicked", Toast.LENGTH_SHORT).show();
            }
        });*/
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
     * Makes accept and reject button visible to admin & user engagements i.e. like, comment, delete and set reminder invisible
     */
    private void visbility(){
        Button accept = findViewById(R.id.accept_btn);
        Button reject = findViewById(R.id.reject_btn);
        accept.setVisibility(View.VISIBLE);
        reject.setVisibility(View.VISIBLE);

        LinearLayout user_engagements = findViewById(R.id.user_engagement);
        user_engagements.setVisibility(View.GONE);
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
        Query query = requests.whereEqualTo(COMMUNITY_KEY, community);

        FirestoreRecyclerOptions<Notice> options = new FirestoreRecyclerOptions.Builder<Notice>()
                .setQuery(query, Notice.class)
                .build();

        adapter = new NoticeAdapter(options);
        progressDialog.dismiss();
        RecyclerView noticeRecyclerView = findViewById(R.id.inbox_recyclerView);
        noticeRecyclerView.setHasFixedSize(true);
        noticeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        noticeRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    /*private void Accept(int position){

    }

    private void Reject(int position){

    }*/
}
