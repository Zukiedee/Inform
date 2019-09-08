package com.communityapp.inform.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.communityapp.inform.model.Comments;
import com.communityapp.inform.presenter.CommentsAdapter;
import com.example.inform.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

/**
 * Displays all comments made on notice, and allows user to add a comment
 */
public class addComment extends AppCompatActivity {
    private CommentsAdapter adapter;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private CollectionReference commentsRef;
    private ProgressDialog progressDialog;
    private RecyclerView commentsRecyclerView;
    private EditText comment;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        //Back button on toolbar
        Objects.requireNonNull(getSupportActionBar()).setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String id;
        Bundle extras = getIntent().getExtras();
        if (extras == null){ id = null; }
        else { id = extras.getString("Id"); }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = Objects.requireNonNull(auth.getCurrentUser()).getEmail();

        DocumentReference userRef = database.document("Users/" + email);
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        username = documentSnapshot.getString("Username");

                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(addComment.this, Newsfeed.class));
                });


        comment = findViewById(R.id.the_comment);
        ImageButton sendCommentBtn = findViewById(R.id.add_comment);

        progressDialog = new ProgressDialog(this);
        commentsRecyclerView = findViewById(R.id.comments_recyclerView);

        assert id != null;
        commentsRef = database.collection("Notices").document(id).collection("Comments");
        loadComments();
        adapter.startListening();

        sendCommentBtn.setOnClickListener(view -> {
            String the_comment = comment.getText().toString();

            String timeStamp = String.valueOf(System.currentTimeMillis());
            //formatting date of comment
            Date currentDate = new Date();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("d MMM yyyy, HH:mm");
            String date = format.format(currentDate);

            HashMap<String, String> comments = new HashMap<>();
            comments.put("Username", username);
            comments.put("Date", date);
            comments.put("Id", timeStamp);
            comments.put("Comment", the_comment);

            commentsRef.document(timeStamp).set(comments)
                    .addOnSuccessListener(aVoid -> Toast.makeText(addComment.this, "Comment added!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(addComment.this, "Error adding comment", Toast.LENGTH_LONG).show());
        });
    }

    /**
     * Loads all comments made on notice
     */
    private void loadComments() {
        progressDialog.setTitle("Loading comments..");
        progressDialog.show();

        String ID_KEY = "Id";
        Query query = commentsRef.orderBy(ID_KEY, Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Comments> options = new FirestoreRecyclerOptions.Builder<Comments>()
                .setQuery(query, Comments.class)
                .build();
        adapter = new CommentsAdapter(options);

        commentsRecyclerView.setHasFixedSize(true);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(adapter);
        adapter.startListening();
        progressDialog.dismiss();
    }
}