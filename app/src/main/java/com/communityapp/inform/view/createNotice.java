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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User creates a notice by selecting a notice category, relevant notices are then shown and user submits notice.
 */
public class createNotice extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 100;
    private Uri image_uri = null;

    private EditText Title;
    private EditText Body;
    private ImageView imageView;
    private TextView upload;
    private Button submit;
    private String category;
    private String name, email;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDB;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notice);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Notice");
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);

        //retrieve username to display on post once published
        userDB = FirebaseDatabase.getInstance().getReference("User");
        Query query = userDB.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        name = "" + ds.child("username").getValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //user selects notice category to reveal relevant fields
        selectNoticeCategory();

        Title = findViewById(R.id.notice_headline);
        Body = findViewById(R.id.notice_body);
        imageView = findViewById(R.id.upload_image);
        upload = findViewById(R.id.add_image);
        submit = findViewById(R.id.submit);

        Title.addTextChangedListener(createNotice);
        Body.addTextChangedListener(createNotice);

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
        if (user != null){
            email = user.getEmail();
            name = user.getDisplayName();
        }
        else {
            startActivity(new Intent(this, Newsfeed.class));
        }
    }

    /**
     * User selects category of notice and relevant fields of that notice
     */
    private void selectNoticeCategory(){
        //Notice category selections
        Spinner spinner = findViewById(R.id.noticeCategory);
        final String[] categories = getResources().getStringArray(R.array.notice_categories);
        //Style and populate the category spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(com.communityapp.inform.view.createNotice.this, android.R.layout.simple_spinner_item, categories);
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
     * Removes all fields from the user interface
     */
    private void nothingSelected(){
        Toast.makeText(com.communityapp.inform.view.createNotice.this, "Please select a category", Toast.LENGTH_LONG).show();
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
     * Displays relevant Crime Report categories to user
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
     * Displays relevant Fundraiser categories to user
     */
    private void showFundraiser(){
        category = "Fundraiser";
        setBaseCategoryVisible();
    }

    /**
     * Displays relevant Missing pet categories to user
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
     * User clicks submit button and the notice is published to the databsase
     */
    private void submitNotice(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = Title.getText().toString().trim();
                String body = Body.getText().toString().trim();

                if (image_uri == null){
                    //post without image
                    uploadData(title, body, "noImage");

                } else {
                    //post with image
                    uploadData(title, body, String.valueOf(image_uri));
                }

            }
        });
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

        if (!uri.equals("noImage")){
            //post with image
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(Uri.parse(uri))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //image is uploaded to firebase storage, now get its url
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                            while (!uriTask.isSuccessful());

                            String downloadURI = uriTask.getResult().toString();

                            if (uriTask.isSuccessful()){
                                // url is received upload post to firebase
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("Category", category);
                                hashMap.put("Date", DatetoString);
                                hashMap.put("Description", body);
                                hashMap.put("Id", timeStamp);
                                hashMap.put("Image", downloadURI);
                                hashMap.put("Title", title);
                                hashMap.put("Username", name);

                                //path to store post data
                               // DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                                //put data in this ref

                                db.collection("Documents").document(timeStamp).set(hashMap)
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
            hashMap.put("Category", category);
            hashMap.put("Date", DatetoString);
            hashMap.put("Description", body);
            hashMap.put("Id", timeStamp);
            hashMap.put("Image", "noImage");
            hashMap.put("Title", title);
            hashMap.put("Username", name);

            //put data in this database
            db.collection("Documents").document(timeStamp).set(hashMap)
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
}
