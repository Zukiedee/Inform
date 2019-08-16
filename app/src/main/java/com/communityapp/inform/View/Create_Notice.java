package com.communityapp.inform.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.inform.R;

import java.util.ArrayList;
import java.util.List;

public class Create_Notice extends AppCompatActivity {

    private Spinner spinner;
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText Title;
    private EditText Body;
    private ImageView imageView;
    private ImageButton upload;
    private Button submit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notice);

        //Back button on toolbar to navigate to newsfeed
        getSupportActionBar().setTitle("Create Notice");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Notice category selections
        spinner = findViewById(R.id.noticeCategory);
        List<String> categories = new ArrayList<>();
        categories.add(0, "Select a Category");
        categories.add("Local News");
        categories.add("Crime Report");
        categories.add("Events & Entertainment");
        categories.add("Fundraiser");
        categories.add("Missing Pet");
        categories.add("Tradesmen referrals");
        categories.add("Recommendations");

        //Style and populate the category spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Create_Notice.this, android.R.layout.simple_spinner_item, categories
        );

        //Dropdown layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Attaching adapter to spinner
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).equals("Select Category")){
                    //do nothing
                }
                else {
                    //on selecting a spinner
                    String item = adapterView.getItemAtPosition(i).toString();

                    //show relevant text fields to user

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Title = findViewById(R.id.notice_headline);
        Body = findViewById(R.id.notice_body);
        imageView = findViewById(R.id.upload_image);
        upload = findViewById(R.id.add_image);

        //submit notice button
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Notice submitted!", Toast.LENGTH_LONG);
                Intent intentMain = new Intent(Create_Notice.this, Newsfeed.class);
                startActivity(intentMain);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);


    }
}
