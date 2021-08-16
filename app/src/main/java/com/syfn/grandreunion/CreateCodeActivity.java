package com.syfn.grandreunion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CreateCodeActivity extends AppCompatActivity {

    private EditText codeView1, codeView2, codeView3, codeView4, codeView5, codeView6, codeView7;
    private FloatingActionButton backButton;
    private TextView shareCode;
    private int[] numList;
    private String[] letterList;
    private DatabaseReference rootRef, userListRef, checkCodeRef;
    private String groupName, getOwnerId, userName, userImage, currentUserId;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_code);
        fAuth = FirebaseAuth.getInstance();
        currentUserId = fAuth.getCurrentUser().getUid();
        shareCode = findViewById(R.id.shareCode);
        userListRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        getOwnerId = getIntent().getStringExtra("EXTRA_USERID");
        codeView1 = findViewById(R.id.codeView1);
        codeView2 = findViewById(R.id.codeView2);
        codeView3 = findViewById(R.id.codeView3);
        codeView4 = findViewById(R.id.codeView4);
        codeView5 = findViewById(R.id.codeView5);
        codeView6 = findViewById(R.id.codeView6);
        codeView7 = findViewById(R.id.codeView7);
        groupName = getIntent().getStringExtra("EXTRA_GROUPNAME");
        rootRef = FirebaseDatabase.getInstance().getReference();
        checkCodeRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        getProfileMap();
        backButton = findViewById(R.id.createBackButton);
        numList = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
        letterList = new String[]{"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
        final String tCode = generateCode();
        codeView1.setText(tCode.substring(0,1));
        codeView2.setText(tCode.substring(1,2));
        codeView3.setText(tCode.substring(2,3));
        codeView4.setText(tCode.substring(3,4));
        codeView5.setText(tCode.substring(4,5));
        codeView6.setText(tCode.substring(5,6));
        codeView7.setText(tCode.substring(6));
        rootRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                HashMap<String, String> profileMap = new HashMap<>();
                profileMap.put("name", userName);
                profileMap.put("image", userImage);
                rootRef.child("Groups").child(groupName).child("Owner").setValue(currentUserId);
                rootRef.child("Groups").child(groupName).child("Userlist").child(getOwnerId).setValue(profileMap);
                rootRef.child("Groups").child(groupName).child("Code").setValue(tCode);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userToHome = new Intent(CreateCodeActivity.this, LoggedInActivity.class);
                startActivity(userToHome);
                finish();
            }
        });
        shareCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                String shareBody = "Download App: Grand Reunion" + "\nJoin Code: " + (String)tCode;
                share.putExtra(Intent.EXTRA_TEXT, shareBody);
                share.putExtra(Intent.EXTRA_TITLE, "Share With");
                startActivity(Intent.createChooser(share, Intent.EXTRA_TITLE));
            }
        });
    }


    private void getProfileMap() {
        userListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("name").getValue().toString();
                userImage = dataSnapshot.child("image").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private String generateCode(){
        String code = "";
        for(int i = 0; i < 7; i++) {
            String numOrLetter = "";
            if (Math.random() < .5)
                numOrLetter = "num";
            else
                numOrLetter = "letter";

            if (numOrLetter.equals("num")) {
                code += numList[(int)(1+(Math.random() * 9))];
            }
            if (numOrLetter.equals("letter")) {
                code += letterList[(int)(1+(Math.random() * 25))];
            }
        }
        return code;
    }
}
