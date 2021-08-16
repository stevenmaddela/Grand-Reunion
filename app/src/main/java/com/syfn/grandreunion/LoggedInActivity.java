package com.syfn.grandreunion;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;

import com.google.android.material.internal.NavigationMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import io.github.yavski.fabspeeddial.FabSpeedDial;

public class LoggedInActivity extends AppCompatActivity{

    private ActionMode mActionMode;
    private FirebaseAuth mAuth;
    private FabSpeedDial fabSpeedDial;
    private DatabaseReference rootRef, userRef;
    private ListView listView;
    private ArrayList<String> groups = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private String currentUserId, joinedGroupRefresh, longHeldGroupName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loggedin);
        joinedGroupRefresh = getIntent().getStringExtra("ADD_GROUP");
        mAuth = FirebaseAuth.getInstance();
        fabSpeedDial = findViewById(R.id.create_or_join);
        listView = findViewById(R.id.listView);
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        adapter = new ArrayAdapter<>(this, R.layout.rows, groups);
        listView.setAdapter(adapter);
        if(joinedGroupRefresh!=null) {
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String userName = dataSnapshot.child(currentUserId).child("name").getValue().toString();
                    String userimage = dataSnapshot.child(currentUserId).child("image").getValue().toString();
                    userJoinAndRefresh(userName, userimage);
                    retrieveAndDisplayGroups();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        retrieveAndDisplayGroups();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longHeldGroupName = parent.getItemAtPosition(position).toString();
                mActionMode = startSupportActionMode(mActionMoreCallback);
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String currentGroupName = adapterView.getItemAtPosition(position).toString();
                Intent groupChatIntent = new Intent(LoggedInActivity.this, GroupChatActivity.class);
                groupChatIntent.putExtra("groupName",currentGroupName);
                startActivity(groupChatIntent);
            }
        });
        checkForBackButtonPressed();

    }



    private ActionMode.Callback mActionMoreCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle(longHeldGroupName);
            mode.getMenuInflater().inflate(R.menu.long_hold_main, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.longHoldTrash:
                    trashClicked();
                    return true;
                case R.id.longHoldShare:
                   shareClicked();
                   return true;
                default:
                    return LoggedInActivity.super.onOptionsItemSelected(item);
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    private void shareClicked() {
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String code = dataSnapshot.child(longHeldGroupName).child("Code").getValue().toString();
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                String shareBody = "Download App: Grand Reunion" + "\nJoin Code: " + (String)code;
                share.putExtra(Intent.EXTRA_TEXT, shareBody);
                share.putExtra(Intent.EXTRA_TITLE, "Share With");
                startActivity(Intent.createChooser(share, Intent.EXTRA_TITLE));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        sendUserToHere();
    }

    private void trashClicked() {
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String owner = "";
                if(dataSnapshot.child(longHeldGroupName).child("Owner").getValue()!=null)
                    owner = dataSnapshot.child(longHeldGroupName).child("Owner").getValue().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(LoggedInActivity.this);
                if(currentUserId.equals(owner)) {
                    builder.setMessage("Are You Sure You Want To Delete This Reunion?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    groups.remove(longHeldGroupName);
                                    adapter.notifyDataSetChanged();
                                    rootRef.child(longHeldGroupName).removeValue();
                                    sendUserToHere();
                                }
                            }).setNegativeButton("Cancel", null);
                    AlertDialog dialog  = builder.create();
                    if(!owner.equals(currentUserId)){
                        dialog.cancel();
                    }
                    else
                        dialog.show();
                }
                else{
                    builder.setMessage("Are You Sure You Want To Leave This Reunion?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    groups.remove(longHeldGroupName);
                                    adapter.notifyDataSetChanged();
                                    rootRef.child(longHeldGroupName).child("Userlist").child(currentUserId).removeValue();
                                    sendUserToHere();
                                }
                            }).setNegativeButton("Cancel", null);
                    AlertDialog dialog  = builder.create();
                    if(!owner.equals(currentUserId)){
                        dialog.cancel();
                    }
                    else
                        dialog.show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void checkForBackButtonPressed() {
        fabSpeedDial.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                if(menuItem.getTitle().equals("Join Group")){
                    Intent menuToJoin = new Intent(LoggedInActivity.this, JoinGroupActivity.class);
                    startActivity(menuToJoin);
                }
                else if(menuItem.getTitle().equals("Create Group")){
                    Intent menuToCreate = new Intent(LoggedInActivity.this, CreateGroupActivity.class);
                    startActivity(menuToCreate);
                }
                return false;
            }

            @Override
            public void onMenuClosed() {

            }
        });
    }

    private void retrieveAndDisplayGroups() {
        rootRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String groupName = dataSnapshot.getKey();
                if(dataSnapshot.child("Userlist").hasChild(currentUserId)) {
                    groups.add(groupName);
                    adapter.notifyDataSetChanged();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String groupName = dataSnapshot.getKey();
                groups.remove(groupName);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void userJoinAndRefresh(String uName, String uImage){
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("name", uName);
            profileMap.put("image", uImage);
            rootRef.child(joinedGroupRefresh).child("Userlist").child(currentUserId).setValue(profileMap);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.main_settings_option){
            sendUserToSettings();
        }
        return true;
    }
    private void sendUserToHere(){
        Intent loggedinToHere = new Intent(LoggedInActivity.this, LoggedInActivity.class);
        startActivity(loggedinToHere);
    }
    private void sendUserToSettings(){
        Intent loggedinToSettings = new Intent(LoggedInActivity.this, SettingsActivity.class);
        startActivity(loggedinToSettings);
    }


}
