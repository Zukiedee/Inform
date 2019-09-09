package com.communityapp.inform.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.inform.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

/**
 * Default screen for first time users of the application or logged out users.
 * Redirects users to Google sign in
 */
public class SignIn extends AppCompatActivity {
    private final static int GOOGLE_SIGN_IN = 234;
    private GoogleSignInClient client;
    private FirebaseAuth mAuth;
    private ProgressDialog signInProgressBar;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInProgressBar = new ProgressDialog(this);
        constraintLayout = findViewById(R.id.constraint_layout);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString((R.string.default_web_client_id)))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        client = GoogleSignIn.getClient(this, googleSignInOptions);
        mAuth = FirebaseAuth.getInstance();

        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(view -> SignInGoogle());
    }

    /**
     * Prompts the user to select a Google account to sign in with.
     * Dispays google accounts
     */
    private void SignInGoogle(){
        signInProgressBar.setMessage("Signing in...");
        signInProgressBar.show();
        Intent signIntent = client.getSignInIntent();
        startActivityForResult(signIntent, GOOGLE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(Objects.requireNonNull(account));

            } catch (ApiException e){
                // Google Sign In failed, update UI appropriately
                signInProgressBar.dismiss();
                Snackbar.make(constraintLayout, "Error signing up. Check your internet connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
    }

    /**
     * Handles firebase authentification with Google sign in
     * @param account User's google account
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        // Sign in success
                        Toast.makeText(SignIn.this,  "Successfully signed in!", Toast.LENGTH_SHORT).show();

                        Intent intentProfile = new Intent(SignIn.this, Profile.class);
                        startActivity(intentProfile);

                    } else {
                        // If sign in fails, display a message to the user.
                        Snackbar.make(constraintLayout, "Authentication Failed!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!= null){
            startActivity(new Intent(SignIn.this, Newsfeed.class));
            finish();
        }
    }
}