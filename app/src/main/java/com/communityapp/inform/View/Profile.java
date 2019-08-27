package com.communityapp.inform.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.communityapp.inform.Presenter.Add_Communities_Dialog;
import com.example.inform.R;

import java.util.ArrayList;

/**
 * User profile interface
 * User edits name, account type and communities to follow
 */
public class Profile extends AppCompatActivity implements Add_Communities_Dialog.MultiChoiceListener {
    private TextView username;
    private Spinner user_type_spinner;                                                              //spinner with types of user
    private Button add_communities;                                                                 //button to select communities

    //list view
    public ListView selectedCommunities;
    public ArrayList<String> shownList;
    public ArrayAdapter<String> community_list_Adapter;                                             //display of user selected communities

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Sets up back button on toolbar
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setCategory();

        add_communities = (Button) findViewById(R.id.add_community_btn);
        setCommunities();


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
            Toast done = Toast.makeText(Profile.this, "Profile Updated!", Toast.LENGTH_SHORT);
            done.show();
            Intent intentMain = new Intent(Profile.this, Newsfeed.class);
            startActivity(intentMain);
        }

        return super.onOptionsItemSelected(menuItem);
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
