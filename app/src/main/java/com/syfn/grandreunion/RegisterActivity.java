package com.syfn.grandreunion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText mFullName, mEmail, mPassword, mConfirmPassword;
    private Button Register;
    private TextView haveaccount;
    private FirebaseAuth fAuth;
    private DatabaseReference rootRef;
    private StorageReference emptyProfileImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        emptyProfileImage = FirebaseStorage.getInstance().getReference().child("Profile Images");
        mFullName = findViewById(R.id.fullname);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mConfirmPassword = findViewById(R.id.confirmpas);
        Register = findViewById(R.id.register);
        haveaccount = findViewById(R.id.signIn);
        fAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        if(fAuth.getCurrentUser() != null){
            Intent goInside = new Intent(RegisterActivity.this, LoggedInActivity.class);
            startActivity(goInside);
            finish();
        }

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = mFullName.getText().toString().trim();
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String cpassword = mConfirmPassword.getText().toString().trim();

                if(TextUtils.isEmpty(name)){
                    mFullName.setError("Name Is Empty");
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email Is Empty");
                    return;
                }
                else if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password Is Empty");
                    return;
                }
                else if(!(password.equals(cpassword))){
                    mConfirmPassword.setError("Passwords Do Not Match");
                    return;
                }
                else if(password.length() < 6){
                    mPassword.setError("Password Must Be 6 Or More Characters");
                    return;
                }
                else{
                    fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                final String currentUserId = fAuth.getCurrentUser().getUid();
                                final StorageReference filepath = emptyProfileImage.child("profile_00000_00000.png");
                                final String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String downloadUrl = uri.toString();
                                        rootRef.child("Users").child(currentUserId).setValue("");
                                        HashMap<String, String> profileMap = new HashMap<>();
                                        profileMap.put("uid", currentUserId);
                                        profileMap.put("name", name);
                                        profileMap.put("image", downloadUrl);
                                        profileMap.put("email", email);
                                        profileMap.put("device_token", deviceToken);
                                        rootRef.child("Users").child(currentUserId).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(RegisterActivity.this,"User Created",Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    String error = task.getException().toString();
                                                    Toast.makeText(RegisterActivity.this, "Error: " +  error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });
                                Toast.makeText(RegisterActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                Intent goInside = new Intent(RegisterActivity.this, LoggedInActivity.class);
                                startActivity(goInside);
                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "Account Not Created, Please Try Again", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }
            }
        });
        haveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSignIn = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(goToSignIn);
            }
        });
    }
}