package com.syfn.grandreunion;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
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

public class JoinGroupActivity extends AppCompatActivity {

    private FloatingActionButton backButton;
    private EditText codeView1, codeView2, codeView3, codeView4, codeView5, codeView6, codeView7;
    private Button joinButton;
    private String fullCode, currentUserId, userName, userImage;
    private DatabaseReference userListRef, rootRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
        initiateVars();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserHome();
            }
        });
        addChangedListeners();
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initateFullCode();
                rootRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        joinGroup(dataSnapshot);
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

            }
        });
    }

    private void joinGroup(DataSnapshot dataSnapshot) {
        String code = dataSnapshot.child("Code").getValue().toString();
        if (fullCode.equals(code)) {
            String groupName = dataSnapshot.getKey();
            Intent joinToGroup = new Intent(JoinGroupActivity.this, LoggedInActivity.class);
            joinToGroup.putExtra("ADD_GROUP", groupName);
            startActivity(joinToGroup);
        }

    }

    private void sendUserHome () {
            Intent joinToHome = new Intent(JoinGroupActivity.this, LoggedInActivity.class);
            startActivity(joinToHome);
        }

        private void addChangedListeners () {
            codeView1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView text = (TextView) getCurrentFocus();
                    if (text.length() == 1) {
                        View next = text.focusSearch(View.FOCUS_RIGHT); // or FOCUS_FORWARD
                        if (next != null)
                            next.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            codeView2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    TextView text = (TextView) getCurrentFocus();
                    if (text.length() == 1) {
                        View next = text.focusSearch(View.FOCUS_RIGHT); // or FOCUS_FORWARD
                        if (next != null)
                            next.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            codeView3.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    TextView text = (TextView) getCurrentFocus();
                    if (text.length() == 1) {
                        View next = text.focusSearch(View.FOCUS_RIGHT); // or FOCUS_FORWARD
                        if (next != null)
                            next.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            codeView4.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    TextView text = (TextView) getCurrentFocus();
                    if (text.length() == 1) {
                        View next = text.focusSearch(View.FOCUS_RIGHT); // or FOCUS_FORWARD
                        if (next != null)
                            next.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            codeView5.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    TextView text = (TextView) getCurrentFocus();
                    if (text.length() == 1) {
                        View next = text.focusSearch(View.FOCUS_RIGHT); // or FOCUS_FORWARD
                        if (next != null)
                            next.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            codeView6.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    TextView text = (TextView) getCurrentFocus();
                    if (text.length() == 1) {
                        View next = text.focusSearch(View.FOCUS_RIGHT); // or FOCUS_FORWARD
                        if (next != null)
                            next.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

        }
        private void initiateVars () {
            mAuth = FirebaseAuth.getInstance();

            currentUserId = mAuth.getCurrentUser().getUid();
            userListRef = FirebaseDatabase.getInstance().getReference().child("Users");
            rootRef = FirebaseDatabase.getInstance().getReference().child("Groups");
            backButton = findViewById(R.id.joinBackButton);
            codeView1 = (EditText) findViewById(R.id.codeViewn1);
            codeView2 = (EditText) findViewById(R.id.codeViewn2);
            codeView3 = (EditText) findViewById(R.id.codeViewn3);
            codeView4 = (EditText) findViewById(R.id.codeViewn4);
            codeView5 = (EditText) findViewById(R.id.codeViewn5);
            codeView6 = (EditText) findViewById(R.id.codeViewn6);
            codeView7 = (EditText) findViewById(R.id.codeViewn7);
            joinButton = findViewById(R.id.joinButton);

        }
        private void initateFullCode () {
            fullCode = "";
            fullCode += codeView1.getText().toString();
            fullCode += codeView2.getText().toString();
            fullCode += codeView3.getText().toString();
            fullCode += codeView4.getText().toString();
            fullCode += codeView5.getText().toString();
            fullCode += codeView6.getText().toString();
            fullCode += codeView7.getText().toString();
        }
    }

