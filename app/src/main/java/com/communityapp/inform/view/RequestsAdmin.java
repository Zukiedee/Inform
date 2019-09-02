package com.communityapp.inform.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

public class RequestsAdmin  extends AppCompatActivity {

    private RecyclerView noticeRecyclerView;
    private FirebaseAuth mAuth; //Firebase authentication
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference requests = db.collection("Requests");
    private NoticeAdapter adapter;
    private ProgressDialog progressDialog;
    private static final String ID_KEY = "Id";
    private Button Accept, Reject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        //Back button on toolbar
        getSupportActionBar().setTitle("Inbox");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();

        checkUserStatus();

        visbility();
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

    private void visbility(){
        Accept = findViewById(R.id.accept_btn);
        Reject = findViewById(R.id.reject_btn);

        Accept.setVisibility(View.VISIBLE);
        Reject.setVisibility(View.VISIBLE);

    }

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
    private void loadRequests() {
        progressDialog.setTitle("Loading requests...");
        progressDialog.show();
        Query query = requests.orderBy(ID_KEY);

        //Query query = noticesRef.orderBy("Id");
        FirestoreRecyclerOptions<Notice> options = new FirestoreRecyclerOptions.Builder<Notice>()
                .setQuery(query, Notice.class)
                .build();

        adapter = new NoticeAdapter(options);
        progressDialog.dismiss();
        noticeRecyclerView = findViewById(R.id.NoticeRecyclerView);
        noticeRecyclerView.setHasFixedSize(true);
        noticeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        noticeRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
