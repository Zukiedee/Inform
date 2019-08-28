package com.communityapp.inform.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.communityapp.inform.presenter.Add_Communities_Dialog;
import com.example.inform.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * User profile interface
 * User edits name, account type and communities to follow
 */
public class Profile extends AppCompatActivity implements Add_Communities_Dialog.MultiChoiceListener {
    private EditText username;
    private TextView email;
    private Spinner user_type_spinner;                                                              //spinner with types of user
    private Button add_communities;                                                                 //button to select communities

    //list view
    public ListView selectedCommunities;
    public ArrayList<String> shownList;
    public ArrayAdapter<String> community_list_Adapter;                                             //display of user selected communities

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    String currentUserID, user_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Sets up back button on toolbar
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //init firebase
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        //init views
        add_communities = findViewById(R.id.add_community_btn);
        username = findViewById(R.id.username_hint);
        email = findViewById(R.id.email_hint);

        currentUserID = mAuth.getCurrentUser().getUid();

        //set deafault user parameters
        setDefaultInfo();

        setCategory();

        setCommunities();
    }

    /**
     * Sets the default username from gmail account i.e. username and email address
     */
    private void setDefaultInfo() {
        //Returns user's username and displays it in editable username textbar

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //checks until required data get
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    //get data
                    String uname = ""+ ds.child("username").getValue();
                    String uemail = ""+ ds.child("email").getValue();
                    //String type = ""+ ds.child("type").getValue();
                    //Object communities = ds.child("communities").getValue();

                    username.setText(uname);
                    email.setText(uemail);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //done button
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = menuItem.getItemId();

        //Saving profile information
        if (id == R.id.submit_profile) {
            try {
                saveUserInfo();


            }catch (Error e){
                Toast.makeText(Profile.this, "Error Occured updating profile!", Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(menuItem);
    }

    /**
     * Saves user information from input fields
     */
    private void saveUserInfo() {
        String uname = username.getText().toString();
        String type = user_type_spinner.getSelectedItem().toString().trim();
        String uemail = user.getEmail();
        ArrayList<String> communities = shownList;
        String uid = user.getUid();

        final String[] categories = getResources().getStringArray(R.array.user_type);

        if (TextUtils.isEmpty(uname)){
            //notify user that username is not entered
            Toast.makeText(this, "Please fill in username", Toast.LENGTH_SHORT).show();
        }
        if (type.equalsIgnoreCase(categories[0].trim())){
            //notify user that user type is not entered
            Toast.makeText(this, "Please select in user type", Toast.LENGTH_SHORT).show();
        }
        if (communities.isEmpty()){
            //notify user that communities are not entered
            Toast.makeText(this, "Please select at least one community to follow", Toast.LENGTH_SHORT).show();
        }
        else {
            //save information to firebase
            HashMap<Object, Object> userMap = new HashMap<>();
            userMap.put("username", uname);
            userMap.put("email", uemail);
            userMap.put("type", type);
            userMap.put("communities", communities);
            //databaseReference.updateChildren(userMap).add

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            //path to store use data
            DatabaseReference reference = database.getReference("Users");
            //put data within hashmap in database
            reference.child(uid).setValue(userMap);

            Toast.makeText(Profile.this, "Profile Updated!", Toast.LENGTH_SHORT).show();

            Intent intentMain = new Intent(Profile.this, Newsfeed.class);
            startActivity(intentMain);
        }
    }

    @Override
    public void onPositiveButtonClicked(String[] list, ArrayList<String> selectedList) {
        shownList.clear();
        String item = "";
        for(int j = 0; j <selectedList.size(); j++){
            item = selectedList.get(j);
            if(!shownList.contains(item)){
                shownList.add(item);
            }
        }
        selectedCommunities.setVisibility(View.VISIBLE);
        community_list_Adapter.notifyDataSetChanged();
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {
        shownList.clear();
        community_list_Adapter.notifyDataSetChanged();
    }

    public void setCategory() {
        ///User type category selection using spinner
        user_type_spinner = findViewById(R.id.userTypeCategory);
        final String[] categories = getResources().getStringArray(R.array.user_type);

        //Style and populate the category user_type_spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Profile.this, android.R.layout.simple_spinner_item, categories);

        //Dropdown layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Attaching community_list_Adapter to user_type_spinner
        user_type_spinner.setAdapter(dataAdapter);

        //Items selected from categories
        user_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).toString().equals(categories[0])){
                    //do nothing
                    user_type = "";
                }
                else {
                    //on selecting a user_type_spinner
                    user_type = adapterView.getItemAtPosition(i).toString();
                    //Store user type in database
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                user_type = "";
            }
        });
    }

    public void setCommunities(){
        //shows selected communities to user
        selectedCommunities = (ListView) findViewById(R.id.selectedCommunities);
        shownList = new ArrayList<>();
        community_list_Adapter = new ArrayAdapter<String>(Profile.this, android.R.layout.simple_list_item_1, shownList);
        selectedCommunities.setAdapter(community_list_Adapter);

        add_communities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment communities_dialog = new Add_Communities_Dialog();
                communities_dialog.setCancelable(false);
                communities_dialog.show(getSupportFragmentManager(), "Select communities");
            }
        });
    }
}
