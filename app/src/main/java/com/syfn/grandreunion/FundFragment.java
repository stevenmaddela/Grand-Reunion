package com.syfn.grandreunion;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FundFragment extends Fragment {
    private String currentGroupName, currentUserName, currentUserId;
    private FirebaseAuth fAuth;
    private DatabaseReference usersRef;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> funders = new ArrayList<>();
    private DatabaseReference groupRef;
    private Button donateButton;
    private TextView gNameReserve, gAmt;
    private int totalReserve;

    public FundFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fund, container, false);
        listView = view.findViewById(R.id.donatedUsersListView);
        gAmt = view.findViewById(R.id.fundDollarAmount);
        gNameReserve = view.findViewById(R.id.fundGroupName);
        donateButton = view.findViewById(R.id.donateButton);
        fAuth = FirebaseAuth.getInstance();
        currentUserId = fAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_expandable_list_item_1, funders);
        listView.setAdapter(adapter);
        currentGroupName = getActivity().getIntent().getStringExtra("groupName");
        gNameReserve.setText(currentGroupName + " 's Reserve");
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUserName = dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        retrieveFunders();
        donateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                Context context = getContext();
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText amt = new EditText(context);
                amt.setInputType(InputType.TYPE_CLASS_NUMBER);
                amt.setHint("Amount");
                layout.addView(amt); // Notice this is an add method

                final EditText forr = new EditText(context);
                forr.setHint("For");
                layout.addView(forr); // Another add method
                dialog.setMessage("Pledge to This Reunion")
                        .setPositiveButton("Confirm Donation", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String value = amt.getText().toString();
                                final String forrr = forr.getText().toString();
                                checkForExistingFunder(value, forrr);
                                adapter.notifyDataSetChanged();
                                retrieveFunders();
                                Intent restart = new Intent(getActivity(), GroupChatActivity.class);
                                restart.putExtra("groupName", currentGroupName);
                                startActivity(restart);
                            }
                        }).setNegativeButton("Cancel", null);
                dialog.setView(layout);
                dialog.create().show();


            }
        });
        return view;
    }

    private void checkForExistingFunder(final String amt , final String forr) {
        groupRef.child("Donations").child(currentUserName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int addedAmt = Integer.parseInt(dataSnapshot.child("Amount").getValue().toString()) + Integer.parseInt(amt);
                    groupRef.child("Donations").child(currentUserName).child("Amount").setValue(""+addedAmt);
                    groupRef.child("Donations").child(currentUserName).child("For").setValue(forr);

                }
                else {
                    groupRef.child("Donations").child(currentUserName).child("Amount").setValue(amt);
                    groupRef.child("Donations").child(currentUserName).child("For").setValue(forr);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void retrieveFunders() {
        funders.clear();
        groupRef.child("Donations").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String funder = dataSnapshot.getKey();
                String amt = dataSnapshot.child("Amount").getValue().toString();
                String forr = dataSnapshot.child("For").getValue().toString();

                adapter.add(funder + " has pledged $" + amt + " for " + forr);
                adapter.notifyDataSetChanged();
                changeTotalReserve(Integer.parseInt(amt));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String funder = dataSnapshot.getKey();
                String amt = dataSnapshot.child("Amount").getValue().toString();
                String forr = dataSnapshot.child("For").getValue().toString();

                adapter.add(funder + " has donated $" + amt + " for " + forr);
                adapter.notifyDataSetChanged();
                changeTotalReserve(Integer.parseInt(amt));
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

    private void changeTotalReserve(int amt) {
        totalReserve+=amt;
        gAmt.setText("$"+totalReserve);
    }


}
