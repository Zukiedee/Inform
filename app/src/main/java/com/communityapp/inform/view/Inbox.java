package com.communityapp.inform.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.communityapp.inform.presenter.NotificationAdapter;
import com.example.inform.R;

import com.communityapp.inform.model.Notification;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

/**
 * Inbox from admin regarding post notification statuses will be posted here
 * User receives notifications once they've made a notice request to the community Admin
 * and once feedback has been received from Admin.
 */
public class Inbox extends AppCompatActivity {
    private NotificationAdapter adapter;
    private FirebaseAuth mAuth; //Firebase authentication
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private CollectionReference msgsRef;
    private ProgressDialog progressDialog;
    private RelativeLayout relativeLayout;
    private RecyclerView msgRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        //Back button on toolbar
        Objects.requireNonNull(getSupportActionBar()).setTitle("Inbox");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        msgRecyclerView = findViewById(R.id.inbox_recyclerView);
        relativeLayout = findViewById(R.id.inbox);


        mAuth = FirebaseAuth.getInstance();
        checkUserStatus();
        String email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        msgsRef = database.collection("Users/"+email+"/Messages");
        loadMessages();
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
                Snackbar.make(relativeLayout, "Message deleted", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }).attachToRecyclerView(msgRecyclerView);


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
     * Verifies if user is signed in
     */
    private void checkUserStatus(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //If user is not logged in, redirect to sign in screen
        if (currentUser== null){
            Intent loginIntent = new Intent(Inbox.this, SignIn.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        }
    }

    /**
     * Loads all messages sent to user in their account
     */
    private void loadMessages() {
        progressDialog.setTitle("Loading messages..");
        progressDialog.show();

        String ID_KEY = "Id";

        Query query = msgsRef.orderBy(ID_KEY, Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Notification> options = new FirestoreRecyclerOptions.Builder<Notification>()
                .setQuery(query, Notification.class)
                .build();

        adapter = new NotificationAdapter(options);

        msgRecyclerView.setHasFixedSize(true);
        msgRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        msgRecyclerView.setAdapter(adapter);
        adapter.startListening();
        progressDialog.dismiss();
    }
}