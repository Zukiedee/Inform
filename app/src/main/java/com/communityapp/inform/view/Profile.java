package com.communityapp.inform.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.communityapp.inform.presenter.Add_Communities_Dialog;
import com.example.inform.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * User profile user interface
 * User edits name, account type and communities to follow
 */
public class Profile extends AppCompatActivity implements Add_Communities_Dialog.MultiChoiceListener {
    private EditText username;
    private Spinner user_type_spinner;                                                              //spinner with types of user
    private Button add_communities;                                                                 //button to select communities
    private ScrollView relativeLayout;
    private ProgressDialog progressDialog;
    private ArrayAdapter<String> dataAdapter;

    //list view
    public ListView selectedCommunities;
    public ArrayList<String> shownList;
    public ArrayAdapter<String> community_list_Adapter;                                             //display of user selected communities

    //firebase
    private FirebaseUser user;
    private FirebaseFirestore database;

    //User database fields
    private static final String USERNAME_KEY = "Username";
    private static final String EMAIL_KEY = "Email";
    private static final String TYPE_KEY = "Type";
    private static final String COMMUNITIES_KEY = "Communities";

    String user_email, user_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Profile");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        database = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        add_communities = findViewById(R.id.add_community_btn);
        username = findViewById(R.id.username_hint);
        TextView email = findViewById(R.id.email_hint);
        relativeLayout = findViewById(R.id.profile);

        if (mAuth.getCurrentUser() == null){
            startActivity(new Intent(Profile.this, SignIn.class));
        }
        user_email = mAuth.getCurrentUser().getEmail();
        email.setText(user_email);

        if (user!=null) { DisplayInfo(); }
        setType();
        setCommunities();
    }

    /**
     * Display the user details - allows user to update profile
     */
    private void DisplayInfo() {
        progressDialog.setTitle("Loading profile..");
        progressDialog.show();
        DocumentReference userRef = database.collection("Users").document(Objects.requireNonNull(user.getEmail()));
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()){
                        String uname = documentSnapshot.getString(USERNAME_KEY);
                        String type = documentSnapshot.getString(TYPE_KEY);
                        String communities = Objects.requireNonNull(documentSnapshot.getString(COMMUNITIES_KEY)).trim();

                        username.setText(uname);

                        int type_position = dataAdapter.getPosition(type);
                        if (type_position == -1) user_type_spinner.setSelection(0);
                        else user_type_spinner.setSelection(type_position);

                        shownList = new ArrayList<>(Arrays.asList(communities.split(",")));
                        selectedCommunities.setVisibility(View.VISIBLE);
                        community_list_Adapter = new ArrayAdapter<>(Profile.this, android.R.layout.simple_list_item_1, shownList);
                        selectedCommunities.setAdapter(community_list_Adapter);

                        progressDialog.dismiss();
                    }
                    else {
                        if (user.getDisplayName()!=null){
                            username.setText(user.getDisplayName());
                        }
                        progressDialog.dismiss();
                        Toast.makeText(Profile.this, "Welcome! Please complete your profile", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Snackbar.make(relativeLayout, "Error occurred: "+e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //done button
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button.
        int id = menuItem.getItemId();

        //Saving profile information
        if (id == R.id.submit_profile) {
            try {
                saveUserInfo();
            }catch (Error e){
                Snackbar.make(relativeLayout, "Error occurred: "+e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /**
     * Saves user information from profile to be saved in the firestore database
     */
    private void saveUserInfo() {
        String uname = username.getText().toString();
        String type = user_type;
        ArrayList<String> communities = shownList;

        //Checks for missing fields
        if (TextUtils.isEmpty(uname)){
            Toast.makeText(this, "Please fill in username", Toast.LENGTH_SHORT).show();
        }
        if (communities.isEmpty()){
            Toast.makeText(this, "Please select at least one community to follow", Toast.LENGTH_SHORT).show();
        }

        else {
            //save info
            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put(USERNAME_KEY, uname);
            userMap.put(EMAIL_KEY, user_email);
            userMap.put(TYPE_KEY, type);
            String comm = communities.toString();
            userMap.put(COMMUNITIES_KEY, comm.substring(1,(comm.length()-1)).trim());

            database.document("Users/" + user_email).set(userMap)
                    .addOnSuccessListener(aVoid -> {
                        //user profile successfully updated to database
                        Toast.makeText(Profile.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                        Intent intentMain = new Intent(Profile.this, Newsfeed.class);
                        intentMain.putExtra("Communities", communities);
                        startActivity(intentMain);
                    })
                    .addOnFailureListener(e -> Snackbar.make(relativeLayout, "Failed to update profile: "+e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show());
        }
    }

    /**
     * Select type of user from drop down categories containing Organization
     */
    public void setType() {
        user_type_spinner = findViewById(R.id.userTypeCategory);
        final String[] categories = getResources().getStringArray(R.array.user_type);
        dataAdapter = new ArrayAdapter<>(Profile.this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        user_type_spinner.setAdapter(dataAdapter);
        user_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //on selecting a user_type_spinner
                user_type = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                user_type = adapterView.getItemAtPosition(0).toString();
            }
        });
    }

    /**
     * Set up communities dialog
     */
    public void setCommunities(){
        selectedCommunities = findViewById(R.id.selectedCommunities);
        shownList = new ArrayList<>();
        community_list_Adapter = new ArrayAdapter<>(Profile.this, android.R.layout.simple_list_item_1, shownList);
        selectedCommunities.setAdapter(community_list_Adapter);
        add_communities.setOnClickListener(view -> {
            DialogFragment communities_dialog = new Add_Communities_Dialog();
            communities_dialog.setCancelable(false);
            communities_dialog.show(getSupportFragmentManager(), "Select communities");
        });
    }

    @Override
    public void onPositiveButtonClicked(String[] list, ArrayList<String> selectedList) {
        shownList.clear();
        String item;

        for(int j = 0; j <selectedList.size(); j++){
            item = selectedList.get(j);
            if(!shownList.contains(item)){ shownList.add(item.trim()); }
        }
        selectedCommunities.setVisibility(View.VISIBLE);
        community_list_Adapter.notifyDataSetChanged();
    }

    @Override
    public void onNegativeButtonClicked() {    }

    @Override
    public void onNeutralButtonClicked() {
        shownList.clear();
        community_list_Adapter.notifyDataSetChanged();
    }
}