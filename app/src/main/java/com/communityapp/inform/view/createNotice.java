package com.communityapp.inform.view;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.communityapp.inform.model.Notification;
import com.example.inform.R;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tapadoo.alerter.Alerter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

/**
 * User creates a notice by selecting a notice category, relevant notices are then shown and user submits notice.
 * Certain notice categories require admin approval.
 */
public class createNotice extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener{
    private EditText Title, Body;
    private ImageView imageView;
    private Spinner communities_categories;
    private TextView upload, community_label;
    private Button submit;
    private String category, name, email, communities, PostDate, actualDate;
    private ProgressDialog progressDialog;
    private TextView DateText;
    private LinearLayout date_linearLayout;
    private ConstraintLayout constraintLayout;

    private static final int PICK_IMAGE_REQUEST = 100;
    private Uri image_uri = null;

    private String collectionPath;
    private boolean postDisclaimer;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore database;
    private CollectionReference notifications;

    private static final String TITLE_KEY = "Title";
    private static final String CATEGORY_KEY = "Category";
    private static final String DESCRIPTION_KEY = "Description";
    private static final String DATE_KEY = "Date";
    private static final String ID_KEY = "Id";
    private static final String IMAGE_KEY = "Image";
    private static final String COMMUNITY_KEY = "Community";
    private static final String USERNAME_KEY = "Username";
    private static final String UID_KEY = "User ID";

    private ArrayList<String> admin_requests = new ArrayList<>(Arrays.asList("Events/Entertainment", "Tradesmen Referrals", "Fundraiser"));

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notice);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle("Create Notice");
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        checkUserStatus();

        progressDialog = new ProgressDialog(this);

        loadUserInfo();
        notifications = database.collection("Users/"+email+"/Messages");

        selectNoticeCategory();

        Title = findViewById(R.id.notice_headline);
        Body = findViewById(R.id.notice_body);
        imageView = findViewById(R.id.upload_image);
        upload = findViewById(R.id.add_image);
        submit = findViewById(R.id.submit);
        community_label = findViewById(R.id.community_label);
        communities_categories = findViewById(R.id.community);
        date_linearLayout = findViewById(R.id.eventDate);
        DateText = findViewById(R.id.PostDate);
        findViewById(R.id.show_date_dialog).setOnClickListener(view -> showDatePickerDialog());
        constraintLayout = findViewById(R.id.create_constraint_layout);

        Title.addTextChangedListener(createNotice);
        Body.addTextChangedListener(createNotice);

        final String[] community_categories = getResources().getStringArray(R.array.communities_options);
        //display of user selected communities
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(createNotice.this, android.R.layout.simple_spinner_item, community_categories);
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
        upload.setOnClickListener(view -> pickFromGallery());
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
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){ name = documentSnapshot.getString(USERNAME_KEY); }
                    else {
                        startActivity(new Intent(createNotice.this, Profile.class));
                        Toast.makeText(createNotice.this, "Please Complete User Profile", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(createNotice.this, "Error: "+ e.getMessage(),Toast.LENGTH_SHORT).show());
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
        Title.setVisibility(View.GONE);
        Body.setVisibility(View.GONE);
        Body.setVisibility(View.GONE);
        upload.setVisibility(View.GONE);
        submit.setVisibility(View.GONE);
        community_label.setVisibility(View.GONE);
        communities_categories.setVisibility(View.GONE);
        date_linearLayout.setVisibility(View.GONE);
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
        community_label.setVisibility(View.VISIBLE);
        communities_categories.setVisibility(View.VISIBLE);
        date_linearLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Sets disclaimer message for users depending on the notice category selected.
     * If notice needs admin approval, sets red disclaimer message to alert user that it will not be posted in the communities newsfeed.
     * If notice does not need admin approval, sets green disclaimer message let the user know it will posted straight to the newsfeed.
     * @param request True if notice needs approval. False if notice does not require approval.
     */
    private void setDisclaimer(boolean request){
        if (request){
            Alerter.create(this)
                    .setText(R.string.disclaimermsg)
                    .setDuration(3500)
                    .enableSwipeToDismiss()
                    .setIcon(R.drawable.ic_crime)
                    .setBackgroundColorRes(R.color.colorReminder)
                    .enableProgress(true)
                    .setProgressColorRes(R.color.colorPrimary)
                    .show();
        }
        postDisclaimer = request;
    }
    /**
     * Displays relevant local news categories to user
     */
    private void showNews(){
        category = "Local News";
        setBaseCategoryVisible();
        setDisclaimer(admin_requests.contains(category));
        Title.setHint("Headline");
        Body.setHint("Description");
        DateText.setText(R.string.news_date);
        PostDate = "News date: ";
    }

    /**
     * Displays relevant Crime Report categories to user
     */
    private void showCrime(){
        category = "Crime Report";
        setBaseCategoryVisible();
        setDisclaimer(admin_requests.contains(category));
        Title.setHint("Report Title");
        Body.setHint("Description");
        DateText.setText(R.string.crime_date);
        PostDate = "Crime date: ";
    }

    /**
     * Displays relevant Event and Entertainment categories to user
     */
    private void showEvents(){
        category = "Events/Entertainment";
        setBaseCategoryVisible();
        setDisclaimer(admin_requests.contains(category));
        Title.setHint("Event Name");
        Body.setHint("Description");
        DateText.setText(R.string.event_date);
        PostDate = "Event date: ";
    }

    /**
     * Displays relevant Fundraiser categories to user
     */
    private void showFundraiser(){
        category = "Fundraiser";
        setBaseCategoryVisible();
        setDisclaimer(admin_requests.contains(category));
        Title.setHint("Fundraiser Name");
        Body.setHint("Description");
        DateText.setText(R.string.fundraiser_date);
        PostDate = "Fundraiser date: ";
    }

    /**
     * Displays relevant Missing pet categories to user
     */
    private void showPet(){
        category = "Missing Pet";
        setBaseCategoryVisible();
        setDisclaimer(admin_requests.contains(category));
        Title.setHint("Title");
        Body.setHint("Description of pet");
        DateText.setText(R.string.pet_date);
        PostDate = "Last seen: ";
    }

    /**
     * Displays relevant tradesmen referral categories to user
     */
    private void showTradesmen(){
        category = "Tradesmen Referrals";
        setBaseCategoryVisible();
        setDisclaimer(admin_requests.contains(category));
        date_linearLayout.setVisibility(View.GONE);
        PostDate = null;
    }

    /**
     * Displays relevant recommendation categories to user
     */
    private void showRecommendations(){
        category = "Recommendations";
        setBaseCategoryVisible();
        setDisclaimer(admin_requests.contains(category));
        Title.setHint("Recommendation title");
        Body.setHint("Description");
        date_linearLayout.setVisibility(View.GONE);
        PostDate = null;
    }

    /**
     * Ensures all essential fields are filled in i.e. Title and Description
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

    /**
     * Selects image from users phone gallery to be attached to their notice
     */
    private void pickFromGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            assert data != null;
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
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("d MMM yyyy, HH:mm");
        String DatetoString = format.format(currentDate);

        //post with image
        if (!uri.equals("noImage")){
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(Uri.parse(uri))
                    .addOnSuccessListener(taskSnapshot -> {
                        //image is uploaded to firebase storage, now get its url
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                        while (!uriTask.isSuccessful());

                        String downloadURI = Objects.requireNonNull(uriTask.getResult()).toString();

                        setCollectionPath(postDisclaimer);

                        if (uriTask.isSuccessful()){
                            // url is received upload post to firebase
                            HashMap<String, String> hashMap = notice_hashMap(category, DatetoString, body, timeStamp, downloadURI, title, name, communities, email);

                            database.collection(collectionPath).document(timeStamp).set(hashMap)
                                    .addOnCompleteListener(task -> {
                                        if (postDisclaimer){
                                            HashMap<String, String> msg = user_notification(title, timeStamp, DatetoString);
                                            notifications.document(timeStamp).set(msg);
                                            Toast.makeText(createNotice.this, "Request to post notice sent to admin", Toast.LENGTH_LONG).show();
                                        }
                                        else { Toast.makeText(createNotice.this, "Post published to newsfeed!", Toast.LENGTH_LONG).show(); }
                                        progressDialog.dismiss();
                                        Intent done = new Intent(createNotice.this, Newsfeed.class);
                                        startActivity(done);
                                    })
                                    .addOnFailureListener(e -> {
                                        progressDialog.dismiss();
                                        Snackbar.make(constraintLayout, "Error occurred! "+e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        //failed uploading image
                        progressDialog.dismiss();
                        Snackbar.make(constraintLayout, "Failed to upload image. Please try again\n" + e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    });
        }
        else {
            HashMap<String, String> hashMap = notice_hashMap(category, DatetoString, body, timeStamp, "noImage", title, name, communities, email);

            setCollectionPath(postDisclaimer);

            //put data in this database
            database.collection(collectionPath).document(timeStamp).set(hashMap)
                    .addOnCompleteListener(task -> {
                        if (postDisclaimer){
                            HashMap<String, String> msg = user_notification(title, timeStamp, DatetoString);
                            notifications.document(timeStamp).set(msg);
                            Toast.makeText(createNotice.this, "Request to post notice sent to admin", Toast.LENGTH_LONG).show();
                        }
                        else { Toast.makeText(createNotice.this, "Post published", Toast.LENGTH_LONG).show(); }
                        progressDialog.dismiss();
                        Intent done = new Intent(createNotice.this, Newsfeed.class);
                        startActivity(done);
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Snackbar.make(constraintLayout, "Error occurred! "+e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    });
        }
    }

    /**
     * User clicks submit button and the notice is published to the databsase
     */
    private void submitNotice(){
        submit.setOnClickListener(view -> {
            String title = Title.getText().toString().trim();
            String body = Body.getText().toString().trim();
            if (actualDate!=null && !actualDate.isEmpty()){ body += "\n\n" + actualDate; }
            //post without image
            if (image_uri == null){ uploadData(title, body, "noImage"); }
            //post with image
            else { uploadData(title, body, String.valueOf(image_uri)); }
        });
    }

    /**
     * Returns hashMap of notice fields to be inserted into the database
     * @param cat Category
     * @param date Date posted
     * @param description Description in body of notice
     * @param ID ID of notice i.e. timestamp
     * @param image Image attachment
     * @param title Title of notice
     * @param name Username of author of post
     * @param community Community notice is posted to
     * @param email Email address of author of post
     * @return hashMap containing notice fields
     */
    private HashMap<String, String> notice_hashMap(String cat, String date, String description, String ID, String image, String title, String name, String community, String email){
        HashMap<String, String> notice_hashMap = new HashMap<>();
        notice_hashMap.put(CATEGORY_KEY, cat);
        notice_hashMap.put(DATE_KEY, date);
        notice_hashMap.put(DESCRIPTION_KEY, description);
        notice_hashMap.put(ID_KEY, ID);
        notice_hashMap.put(IMAGE_KEY, image);
        notice_hashMap.put(TITLE_KEY, title);
        notice_hashMap.put(USERNAME_KEY, name);
        notice_hashMap.put(COMMUNITY_KEY, community);
        notice_hashMap.put(UID_KEY, email);

        return notice_hashMap;
    }

    /**
     * Returns hashMap containing prompt notification sent to user when requesting a notice to be posted
     * @param title Title of notice being requested
     * @param ID ID of notification message i.e. timestamp
     * @param date Date notification sent
     * @return hashMap containing notification details
     */
    private HashMap<String, String> user_notification(String title, String ID, String date){
        HashMap<String, String> feedback = new HashMap<>();
        Notification notification = new Notification(title, date);
        feedback.put("Title", title);
        feedback.put("Date", date);
        feedback.put("Status", notification.getStatus());
        feedback.put("Message", notification.getMessage());
        feedback.put("Id", ID);

        return feedback;
    }

    /**
     * Notice needs admin approval, it is posted to the Requests collection database,
     * otherwise it is posted directly to the relevant community newsfeed
     * @param request True if notice needs approval, False otherwise.
     */
    private void setCollectionPath(boolean request){
        if (request) { collectionPath = "Requests"; }
        else { collectionPath = "Notices"; }
    }

    /**
     * Displays dialog where user is able to select a date
     */
    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        actualDate = PostDate +""+ dayOfMonth+"/"+(month+1)+"/"+year;
        DateText.setText(actualDate);
    }
}