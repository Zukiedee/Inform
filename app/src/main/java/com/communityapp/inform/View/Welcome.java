package com.communityapp.inform.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.communityapp.inform.Model.User;
import com.example.inform.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Default screen for first timw users of the application or logged out users.
 * Redirects users to Google sign in
 */
public class Welcome extends AppCompatActivity {

    final static int GOOGLE_SIGN_IN = 234;
    FirebaseAuth auth;
    private Button sign_in;
    ProgressBar progressBar;
    GoogleSignInClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        sign_in = findViewById(R.id.sign_in);
        progressBar = findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(this, googleSignInOptions);

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInGoogle();

            }
        });
    }

    void SignInGoogle(){
        progressBar.setVisibility(View.VISIBLE);
        Intent signIntent = client.getSignInIntent();
        startActivityForResult(signIntent, GOOGLE_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn
                    .getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account !=null) {
                    firebaseAuthWithGoogle(account);


                }
            } catch (ApiException e){
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("TAG", "firebaseAutWithGoogle: " + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        progressBar.setVisibility(View.VISIBLE);
                        Log.d("TAG", "signin success");
                        FirebaseUser user = auth.getCurrentUser();
                        Intent intentProfile = new Intent(Welcome.this, Profile.class);
                        startActivity(intentProfile);
                        //updateUI(user);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.w("TAG", "signin failure", task.getException());
                        Toast.makeText(this, "Sign-In Failed!", Toast.LENGTH_SHORT).show();
                        ////updateUI(null);
                    }

                });
    }

    private void updateUI(FirebaseUser user) {
        if (user !=  null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            //String photo = String.valueOf(user.getPhotoUrl());

            User user1 = new User(name, email);

            //
            Toast.makeText(this, name + " logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
