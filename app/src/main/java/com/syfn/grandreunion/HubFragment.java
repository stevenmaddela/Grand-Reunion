package com.syfn.grandreunion;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class HubFragment extends Fragment {
    private ImageButton sendMessageButton;
    private EditText userMessageInput;
    private TextView fundEditText, membersEditText;
    private String currentGroupName, currentUserId, currentUserName, currentUserImage, currentDate, currentTime;
    private FirebaseAuth mAuth;
    private DatabaseReference messagesRef, groupNameRef, groupMessageKeyRef;
    private final ArrayList<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;



    public HubFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hub, container, false);
        mAuth = FirebaseAuth.getInstance();
        fundEditText = view.findViewById(R.id.fundMenuOption);
        membersEditText = view.findViewById(R.id.membersMenuOption);
        currentUserId = mAuth.getCurrentUser().getUid();
        currentGroupName = getActivity().getIntent().getStringExtra("groupName");
        groupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);
        messagesRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName).child("Messages");
        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = view.findViewById(R.id.private_list_of_users);
        linearLayoutManager = new LinearLayoutManager(getContext());
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);
        sendMessageButton = view.findViewById(R.id.send_message_button);
        userMessageInput = view.findViewById(R.id.input_group_message);

        getUserInfo();
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMessageToDatabase();
                userMessageInput.setText("");
            }
        });
        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();
                userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
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
        return view;
    }


    private void getUserInfo() {
        groupNameRef.child("Userlist").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUserImage = dataSnapshot.child(currentUserId).child("image").getValue().toString();
                currentUserName = dataSnapshot.child(currentUserId).child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void saveMessageToDatabase() {
        String message = userMessageInput.getText().toString();
        String messageKey = groupNameRef.push().getKey();
        if(!(TextUtils.isEmpty(message))){
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            groupNameRef.child("Messages").updateChildren(groupMessageKey);

            groupMessageKeyRef = groupNameRef.child("Messages").child(messageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time",currentTime);
            messageInfoMap.put("senderId", currentUserId);
            messageInfoMap.put("type", "text");

            groupMessageKeyRef.updateChildren(messageInfoMap);
        }
        else {
            userMessageInput.setError("Enter a message");
        }
    }
}
