package com.example.inform;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * User profile interface
 * User edits name, account type and communities to follow
 */
public class Profile extends AppCompatActivity {
    private Spinner user_type_spinner;                                                              //spinner with types of user
    private Button add_communities;                                                                 //button to select communities
    private String[] listCommunities = {"Area 1", "Area 2", "Area 3", "Area 4", "Area 5"};          //list of communities users can choose from
    private boolean[] checkedCommunities;
    private ArrayList<Integer> userCommunities = new ArrayList<>();                                 //user selected communities in checkboxes

    //list view
    private ListView selectedCommunities;
    private ArrayList<String> shownList;
    private ArrayAdapter<String> community_list_Adapter;                                            //display of user selected communities

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Sets up back button on toolbar
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         * User type category selection using spinner
         */
        //Notice category selections
        user_type_spinner = findViewById(R.id.userTypeCategory);
        List<String> categories = new ArrayList<>();
        categories.add(0, "Select type of user");
        categories.add("General User");
        categories.add("Organization");
        categories.add("Admin");

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
                if (adapterView.getItemAtPosition(i).equals("Select type of user")){
                    //do nothing
                }
                else {
                    //on selecting a user_type_spinner
                    String item = adapterView.getItemAtPosition(i).toString();

                    //Store user type in database
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /**
         * User community selections
         */
        add_communities = (Button) findViewById(R.id.add_community_btn);
        checkedCommunities = new boolean[listCommunities.length];

        //shows selected communities to user
        selectedCommunities = (ListView) findViewById(R.id.selectedCommunities);
        shownList = new ArrayList<>();
        community_list_Adapter = new ArrayAdapter<String>(Profile.this, android.R.layout.simple_list_item_1, shownList);
        selectedCommunities.setAdapter(community_list_Adapter);

        add_communities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogue = new AlertDialog.Builder(Profile.this);
                dialogue.setTitle("Select communities");
                dialogue.setMultiChoiceItems(listCommunities, checkedCommunities, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if(isChecked){
                            if (!userCommunities.contains(position)){
                                userCommunities.add(position);
                            }
                            else{
                                userCommunities.remove(position);
                            }
                        }
                    }
                });

                dialogue.setCancelable(false);
                dialogue.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        String item = "";
                        for(int j = 0; j <userCommunities.size(); j++){
                            item = listCommunities[userCommunities.get(j)];
                                if(!shownList.contains(item)){
                                    shownList.add(item);
                                    community_list_Adapter.notifyDataSetChanged();
                                }
                        }
                    }
                });

                dialogue.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                dialogue.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos) {
                        for (int i = 0; i<checkedCommunities.length; i++){
                            checkedCommunities[i] = false;
                            userCommunities.clear();
                            shownList.clear();
                            community_list_Adapter.notifyDataSetChanged();
                        }
                    }
                });
                AlertDialog mDialog = dialogue.create();
                mDialog.show();
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

        //Submits profile
        if (id == R.id.submit_profile) {
            Intent intentMain = new Intent(Profile.this, Newsfeed.class);
            startActivity(intentMain);
        }

        return super.onOptionsItemSelected(menuItem);
    }
}
