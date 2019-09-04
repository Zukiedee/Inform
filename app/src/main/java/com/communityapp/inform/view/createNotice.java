package com.communityapp.inform.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inform.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User creates a notice by selecting a notice category, relevant notices are then shown and user submits notice.
 */
public class createNotice extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 100;
    private Uri image_uri = null;

    private EditText Title, Body;
    private ImageView imageView;
    private Spinner communities_categories;
    private TextView upload, community_label, disclaimer;
    private Button submit;
    private String category, name, email, communities;
    private ProgressDialog progressDialog;
    private ArrayAdapter<String> dataAdapter;   //display of user selected communities

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore database;

    private static final String TITLE_KEY = "Title";
    private static final String CATEGORY_KEY = "Category";
    private static final String DESCRIPTION_KEY = "Description";
    private static final String DATE_KEY = "Date";
    private static final String ID_KEY = "Id";
    private static final String IMAGE_KEY = "Image";
    private static final String COMMUNITY_KEY = "Community";
    private static final String USERNAME_KEY = "Username";
    private static final String UID_KEY = "User ID";

    private ArrayList<String> admin_requests = new ArrayList<String>(Arrays.asList("Events", "Tradesman referrals", "Fundraiser"));

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notice);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Notice");
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        checkUserStatus();

        progressDialog = new ProgressDialog(this);

        loadUserInfo();

        selectNoticeCategory();

        disclaimer = findViewById(R.id.disclaimer);
        Title = findViewById(R.id.notice_headline);
        Body = findViewById(R.id.notice_body);
        imageView = findViewById(R.id.upload_image);
        upload = findViewById(R.id.add_image);
        submit = findViewById(R.id.submit);
        community_label = findViewById(R.id.community_label);
        communities_categories = findViewById(R.id.community);

        Title.addTextChangedListener(createNotice);
        Body.addTextChangedListener(createNotice);

        final String[] community_categories = getResources().getStringArray(R.array.communities_options);
        dataAdapter = new ArrayAdapter<>(createNotice.this, android.R.layout.simple_spinner_item, community_categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        communities_categories.setAdapter(dataAdapter);
        communities_categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //on selecting a user_type_spinner
                communities = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                communities = adapterView.getItemAtPosition(0).toString();
            }
        });

        //uploading an image from user gallery
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromGallery();
            }
        });
        submitNotice();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    /**
     * Verifies if user is signed in
     */
    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){ email = user.getEmail(); }
        else { startActivity(new Intent(this, Profile.class)); }
    }

    /**
     * Retrieves user's username to display on notice.
     */
    private void loadUserInfo() {
        DocumentReference userRef = database.document("Users/"+email);
        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            name = documentSnapshot.getString(USERNAME_KEY);
                        }else {
                            Toast.makeText(createNotice.this, "Complete User Profile", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(createNotice.this, "Error: "+ e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * User selects category of notice and relevant fields of that notice
     */
    private void selectNoticeCategory(){
        Spinner spinner = findViewById(R.id.noticeCategory);
        final String[] categories = getResources().getStringArray(R.array.notice_categories);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(com.communityapp.inform.view.createNotice.this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
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
     * Removes all fields from the user interface
     */
    private void nothingSelected(){
        Toast.makeText(com.communityapp.inform.view.createNotice.this, "Please select a category", Toast.LENGTH_LONG).show();
        disclaimer.setVisibility(View.GONE);
        Title.setVisibility(View.GONE);
        Body.setVisibility(View.GONE);
        Body.setVisibility(View.GONE);
        upload.setVisibility(View.GONE);
        submit.setVisibility(View.GONE);
        community_label.setVisibility(View.GONE);
        communities_categories.setVisibility(View.GONE);
    }

    /**
     * Makes the title, body fields, upload a file and submit button visible to user.
     */
    private void setBaseCategoryVisible(){
        disclaimer.setVisibility(View.VISIBLE);
        Title.setVisibility(View.VISIBLE);
        Body.setVisibility(View.VISIBLE);
        Body.setVisibility(View.VISIBLE);
        upload.setVisibility(View.VISIBLE);
        submit.setVisibility(View.VISIBLE);
        community_label.setVisibility(View.VISIBLE);
        communities_categories.setVisibility(View.VISIBLE);
    }

    private void setDisclaimer(Boolean request){
        if (request){
            disclaimer.setText("This notice will be sent to Admin for approval before it is posted.");
            disclaimer.setTextColor(getResources().getColor(R.color.color_reject));
        }
        else {
            disclaimer.setText("This notice will be posted to the community's newsfeed.");
            disclaimer.setTextColor(getResources().getColor(R.color.color_accept));
        }
    }
    /**
     * Displays relevant local news categories to user
     */
    private void showNews(){
        category = "Local News";
        setBaseCategoryVisible();
        setDisclaimer(false);
        Title.setHint("Headline");
        Body.setHint("Description");
    }

    /**
     * Displays relevant Crime Report categories to user
     */
    private void showCrime(){
        category = "Crime Report";
        setBaseCategoryVisible();
        setDisclaimer(false);
        Title.setHint("Report Title");
        Body.setHint("Description");
    }

    /**
     * Displays relevant Event and Entertainment categories to user
     */
    private void showEvents(){
        category = "Events/Entertainment";
        setBaseCategoryVisible();
        setDisclaimer(true);
        Title.setHint("Event Name");
        Body.setHint("Description");
    }

    /**
     * Displays relevant Fundraiser categories to user
     */
    private void showFundraiser(){
        category = "Fundraiser";
        setBaseCategoryVisible();
        setDisclaimer(true);
        Title.setHint("Fundraiser Name");
        Body.setHint("Description");
    }

    /**
     * Displays relevant Missing pet categories to user
     */
    private void showPet(){
        category = "Missing Pet";
        setBaseCategoryVisible();
        setDisclaimer(false);
        Title.setHint("Title");
        Body.setHint("Description of pet, date last seen");
    }

    /**
     * Displays relevant tradesmen referral categories to user
     */
    private void showTradesmen(){
        category = "Tradesmen Referrals";
        setBaseCategoryVisible();
        setDisclaimer(true);
    }

    /**
     * Displays relevant recommendation categories to user
     */
    private void showRecommendations(){
        category = "Recommendations";
        setBaseCategoryVisible();
        setDisclaimer(false);
        Title.setHint("Recommendation title");
        Body.setHint("Description");
    }

    /**
     * Ensures all fields are filled in
     */
    private TextWatcher createNotice = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String TitleInput = Title.getText().toString().trim();
            String BodyInput = Body.getText().toString().trim();
            submit.setEnabled(!TitleInput.isEmpty() && !BodyInput.isEmpty());
        }
        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private void pickFromGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            image_uri = data.getData();
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageURI(image_uri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Uploads post related data to the Firebase database and storage
     * @param title Post Title
     * @param body Post Description
     * @param uri Post Image uri
     */
    private void uploadData(String title, String body, String uri) {
        progressDialog.setMessage("Publishing post...");
        progressDialog.show();

        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Posts/" + "post_" +timeStamp;

        //formatting date of notice
        Date currentDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d ''yy");
        String DatetoString = format.format(currentDate);

        //post with image
        if (!uri.equals("noImage")){
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(Uri.parse(uri))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //image is uploaded to firebase storage, now get its url
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                            while (!uriTask.isSuccessful());

                            String downloadURI = uriTask.getResult().toString();

                            String collectionPath = "";
                            if (admin_requests.contains(category)) { collectionPath = "Requests"; }
                            else { collectionPath = "Notices"; }

                            if (uriTask.isSuccessful()){
                                // url is received upload post to firebase
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put(CATEGORY_KEY, category);
                                hashMap.put(DATE_KEY, DatetoString);
                                hashMap.put(DESCRIPTION_KEY, body);
                                hashMap.put(ID_KEY, timeStamp);
                                hashMap.put(IMAGE_KEY, downloadURI);
                                hashMap.put(TITLE_KEY, title);
                                hashMap.put(USERNAME_KEY, name);
                                hashMap.put(COMMUNITY_KEY, communities);
                                hashMap.put(UID_KEY, email);

                                database.collection(collectionPath).document(timeStamp).set(hashMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressDialog.dismiss();
                                                Toast.makeText(createNotice.this, "Post published", Toast.LENGTH_LONG).show();
                                                Intent done = new Intent(createNotice.this, Newsfeed.class);
                                                startActivity(done);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(createNotice.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed uploading image
                            progressDialog.dismiss();
                            Toast.makeText(createNotice.this, "Error occured" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(CATEGORY_KEY, category);
            hashMap.put(DATE_KEY, DatetoString);
            hashMap.put(DESCRIPTION_KEY, body);
            hashMap.put(ID_KEY, timeStamp);
            hashMap.put(IMAGE_KEY, "noImage");
            hashMap.put(TITLE_KEY, title);
            hashMap.put(USERNAME_KEY, name);
            hashMap.put(COMMUNITY_KEY, communities);
            hashMap.put(UID_KEY, email);

            String collectionPath = "";
            if (admin_requests.contains(category)) { collectionPath = "Requests"; }
            else { collectionPath = "Notices"; }

            //put data in this database
            database.collection(collectionPath).document(timeStamp).set(hashMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent done = new Intent(createNotice.this, Newsfeed.class);
                            startActivity(done);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(createNotice.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    /**
     * User clicks submit button and the notice is published to the databsase
     */
    private void submitNotice(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = Title.getText().toString().trim();
                String body = Body.getText().toString().trim();
                //post without image
                if (image_uri == null){ uploadData(title, body, "noImage"); }
                //post with image
                else { uploadData(title, body, String.valueOf(image_uri)); }
            }
        });
    }
}