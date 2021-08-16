package com.syfn.grandreunion;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateGroupActivity extends AppCompatActivity {

    private Button createGroup;
    private EditText groupName;
    private DatabaseReference rootRef;
    private FirebaseAuth fAuth;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        createGroup = findViewById(R.id.createNewReunion);
        groupName = findViewById(R.id.groupNameEdit);
        rootRef = FirebaseDatabase.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        currentUserId = fAuth.getCurrentUser().getUid();
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String enteredGroupName = groupName.getText().toString();
                if(TextUtils.isEmpty(enteredGroupName)){
                    groupName.setError("Group Name Required");
                    return;
                }
                else{
                    Intent createToCode = new Intent(CreateGroupActivity.this, CreateCodeActivity.class);
                    createToCode.putExtra("EXTRA_GROUPNAME",enteredGroupName);
                    createToCode.putExtra("EXTRA_USERID",currentUserId);
                    startActivity(createToCode);
                }
            }
        });
    }

}
