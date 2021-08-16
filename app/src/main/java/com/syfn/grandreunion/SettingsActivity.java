package com.syfn.grandreunion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private DatabaseReference RootRef;
    private String currentUserId;
    private EditText name, email, logOut;
    private CircleImageView userProfileImage;
    private static final int gallerypic = 1;
    private StorageReference UserProfileImageRef;
    private ProgressDialog loadingBar;
    private FloatingActionButton backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        RootRef = FirebaseDatabase.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        currentUserId = fAuth.getCurrentUser().getUid();
        name = findViewById(R.id.displayName);
        email = findViewById(R.id.displayEmail);
        logOut = findViewById(R.id.logOut);
        userProfileImage = findViewById(R.id.profile_image);
        loadingBar = new ProgressDialog(this);
        backButton = findViewById(R.id.settingsBackButton);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        retrieveUserInfo();
        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, gallerypic);

            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToHome();
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                sendUserToLogin();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == gallerypic && resultCode==RESULT_OK && data!=null){
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                loadingBar.setTitle("Setting Profile Image");
                loadingBar.setMessage("One Moment, Your Profile Image is Being Updated");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                final Uri resultUri = result.getUri();
                final StorageReference filePath = UserProfileImageRef.child(currentUserId + ".jpg");
                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();
                                RootRef.child("Users").child(currentUserId).child("image").setValue(downloadUrl)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SettingsActivity.this, "Profile Image Updated", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                } else {
                                                    String message = task.getException().getMessage();
                                                    Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                        }
                                        });
                            }
                        });
                    }
                });
            }
        }
    }

    private void retrieveUserInfo(){
        RootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("image")){
                    String getUserName = dataSnapshot.child("name").getValue().toString();
                    String getUserEmail = dataSnapshot.child("email").getValue().toString();
                    String getUserImage = dataSnapshot.child("image").getValue().toString();
                    name.setText(getUserName);
                    email.setText(getUserEmail);
                    Picasso.get().load(getUserImage).into(userProfileImage);
                }
                else if ((dataSnapshot.exists()) && dataSnapshot.hasChild("name")){
                    String getUserName = dataSnapshot.child("name").getValue().toString();
                    String getUserEmail = dataSnapshot.child("email").getValue().toString();
                    name.setText(getUserName);
                    email.setText(getUserEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendUserToLogin(){
        Intent loggedinToLogin = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(loggedinToLogin);
    }
    private void sendUserToHome(){
        Intent settingsToHome = new Intent(SettingsActivity.this, LoggedInActivity.class);
        startActivity(settingsToHome);
    }
}
