package com.communityapp.inform.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inform.R;

import java.util.ArrayList;
import java.util.List;

/**
 * User creates a notice by selecting a notice category, relevant notices are then shown and user submits notice.
 */
public class Create_Notice extends AppCompatActivity {

    private Spinner spinner;
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText Title;
    private EditText Body;
    private ImageView imageView;
    private TextView upload;
    private Button submit;
    public String category;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notice);

        //Back button on toolbar to navigate to newsfeed
        getSupportActionBar().setTitle("Create Notice");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selectNoticeCategory();

        Title = findViewById(R.id.notice_headline);
        Body = findViewById(R.id.notice_body);
        imageView = findViewById(R.id.upload_image);
        upload = findViewById(R.id.add_image);
        submit = findViewById(R.id.submit);

        Title.addTextChangedListener(createNotice);
        Body.addTextChangedListener(createNotice);

        submitNotice();
    }

    /**
     * User selects category of notice and relevant fields of that notice
     */
    private void selectNoticeCategory(){
        //Notice category selections
        spinner = findViewById(R.id.noticeCategory);

        final String[] categories = getResources().getStringArray(R.array.notice_categories);

        //Style and populate the category spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Create_Notice.this, android.R.layout.simple_spinner_item, categories);

        //Dropdown layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Attaching adapter to spinner
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //on selecting a spinner
                String item = adapterView.getItemAtPosition(i).toString();

                //show relevant text fields to user
                switch(item){
                    case "Local News":
                        showNews();
                        break;
                    case "Crime Report":
                        showCrime();
                        break;
                    case "Events/Entertainment":
                        showEvents();
                        break;
                    case "Fundraiser":
                        showFundraiser();
                        break;
                    case "Missing Pet":
                        showPet();
                        break;
                    case "Tradesmen Referrals":
                        showTradesmen();
                        break;
                    case "Recommendations":
                        showRecommendations();
                        break;
                    default:
                        nothingSelected();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                nothingSelected();
            }
        });
    }

    /**
     * Displays relevant local news categories to user
     */
    private void nothingSelected(){
        Toast.makeText(Create_Notice.this, "Please select a category", Toast.LENGTH_LONG).show();
        Title.setVisibility(View.GONE);
        Body.setVisibility(View.GONE);
        Body.setVisibility(View.GONE);
        upload.setVisibility(View.GONE);
        submit.setVisibility(View.GONE);
    }

    /**
     * Makes the title, body fields, upload a file and submit button visible to user.
     */
    private void setBaseCategoryVisible(){
        Title.setVisibility(View.VISIBLE);
        Body.setVisibility(View.VISIBLE);
        Body.setVisibility(View.VISIBLE);
        upload.setVisibility(View.VISIBLE);
        submit.setVisibility(View.VISIBLE);
    }

    /**
     * Displays relevant local news categories to user
     */
    private void showNews(){
        category = "Local News";
        setBaseCategoryVisible();
        Title.setHint("Headline");
        Body.setHint("Description");
    }

    /**
     * Displays relevant crime categories to user
     */
    private void showCrime(){
        category = "Crime Report";
        setBaseCategoryVisible();
    }

    /**
     * Displays relevant Event and Entertainment categories to user
     */
    private void showEvents(){
        category = "Events/Entertainment";
        setBaseCategoryVisible();
    }

    /**
     * Displays relevant fundraiser categories to user
     */
    private void showFundraiser(){
        category = "Fundraiser";
        setBaseCategoryVisible();
    }

    /**
     * Displays relevant missing pet categories to user
     */
    private void showPet(){
        category = "Missing Pet";
        setBaseCategoryVisible();
    }

    /**
     * Displays relevant tradesmen referral categories to user
     */
    private void showTradesmen(){
        category = "Tradesmen Referrals";
        setBaseCategoryVisible();
    }

    /**
     * Displays relevant recommendation categories to user
     */
    private void showRecommendations(){
        category = "Recommendations";
        setBaseCategoryVisible();
    }

    /**
     * User clicks submit button and the notice is submitted
     */
    private void submitNotice(){
        //submit notice button

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Notice submitted!", Toast.LENGTH_LONG).show();
                Intent intentMain = new Intent(Create_Notice.this, Newsfeed.class);
                startActivity(intentMain);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    private TextWatcher createNotice = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String TitleInput = Title.getText().toString().trim();
            String BodyInput = Body.getText().toString().trim();

            submit.setEnabled(!TitleInput.isEmpty() && !BodyInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
