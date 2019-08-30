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
import java.util.Arrays;
import java.util.HashMap;

/**
 * User profile interface
 * User edits name, account type and communities to follow
 */
public class editProfile extends AppCompatActivity implements Add_Communities_Dialog.MultiChoiceListener {
    private EditText username;
    private TextView email;
    private Spinner user_type_spinner;                                                              //spinner with types of user
    private Button add_communities;                                                                 //button to select communities

    ArrayAdapter<String> dataAdapter;

    //list view
    public ListView selectedCommunities;
    public ArrayList<String> shownList;
    public ArrayAdapter<String> community_list_Adapter;                                             //display of user selected communities

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private static final int STORAGE_REQUEST_CODE = 200;
    String currentUserID, user_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setTitle("Edit editProfile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        add_communities = findViewById(R.id.add_community_btn);
        username = findViewById(R.id.username_hint);
        email = findViewById(R.id.email_hint);

        currentUserID = mAuth.getCurrentUser().getUid();

        setDefaultInfo();

        setType();

        setCommunities();
    }

    /**
     * Sets the default username
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
                    String type = ""+ ds.child("type").getValue();
                    String communities = ""+ ds.child("communities").getValue();

                    username.setText(uname);
                    email.setText(uemail);

                    int type_position = dataAdapter.getPosition(type);
                    if (type_position == -1)
                        user_type_spinner.setSelection(0);
                    else
                        user_type_spinner.setSelection(type_position);

                    if (!communities.isEmpty()){
                        ArrayList<String> communityList = new ArrayList<String>(Arrays.asList(communities.split(",")));
                        shownList = communityList;
                        selectedCommunities.setVisibility(View.VISIBLE);
                        community_list_Adapter = new ArrayAdapter<String>(editProfile.this, android.R.layout.simple_list_item_1, shownList);
                        selectedCommunities.setAdapter(community_list_Adapter);
                    }

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
        // automatically handle clicks on the Home/Up button.
        int id = menuItem.getItemId();

        //Saving profile information
        if (id == R.id.submit_profile) {
            try {
                saveUserInfo();
            }catch (Error e){
                Toast.makeText(editProfile.this, "Error Occured updating profile!", Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /**
     * Saves user information from input fields
     */
    private void saveUserInfo() {
        String uname = username.getText().toString();
        String type = user_type;
        String uemail = user.getEmail();
        ArrayList<String> communities = shownList;
        String uid = user.getUid();

        //missing fields
        if (TextUtils.isEmpty(uname)){
            Toast.makeText(this, "Please fill in username", Toast.LENGTH_SHORT).show();
        }
        if (communities.isEmpty()){
            Toast.makeText(this, "Please select at least one community to follow", Toast.LENGTH_SHORT).show();
        }
        else {
            //save info
            HashMap<Object, String> userMap = new HashMap<>();
            userMap.put("username", uname);
            userMap.put("email", uemail);
            userMap.put("type", type);
            String comm = communities.toString();
            userMap.put("communities", comm.substring(1,(comm.length()-1)));

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            //path to store use data
            DatabaseReference reference = database.getReference("Users");
            //put data within hashmap in database
            reference.child(uid).setValue(userMap);

            Toast.makeText(editProfile.this, "editProfile Updated!", Toast.LENGTH_SHORT).show();

            Intent intentMain = new Intent(editProfile.this, Newsfeed.class);
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

    public void setType() {
        ///User type category selection using spinner
        user_type_spinner = findViewById(R.id.userTypeCategory);
        final String[] categories = getResources().getStringArray(R.array.user_type);

        //Style and populate the category user_type_spinner
        dataAdapter = new ArrayAdapter<>(editProfile.this, android.R.layout.simple_spinner_item, categories);

        //Dropdown layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Attaching community_list_Adapter to user_type_spinner
        user_type_spinner.setAdapter(dataAdapter);

        //Items selected from categories
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

    public void setCommunities(){
        //shows selected communities to user
        selectedCommunities = (ListView) findViewById(R.id.selectedCommunities);
        shownList = new ArrayList<>();
        community_list_Adapter = new ArrayAdapter<String>(editProfile.this, android.R.layout.simple_list_item_1, shownList);
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
