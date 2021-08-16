package com.syfn.grandreunion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class MembersFragment extends Fragment {
    private RecyclerView displayUsersRecyclerList;
    private FirebaseAuth fAuth;
    private DatabaseReference membersRef, userRef;
    private String currentGroupName, currentUserId, userImage;

    public MembersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_members, container, false);
        fAuth = FirebaseAuth.getInstance();
        displayUsersRecyclerList = view.findViewById(R.id.users_list_recycler);
        currentGroupName = getActivity().getIntent().getStringExtra("groupName");
        currentUserId = fAuth.getCurrentUser().getUid();
        membersRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName).child("Userlist");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        displayUsersRecyclerList.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userImage = dataSnapshot.child("image").getValue().toString();
                membersRef.child(currentUserId).child("image").setValue(userImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(membersRef, Contacts.class)
                        .build();
        FirebaseRecyclerAdapter<Contacts, MembersFragment.MemberListViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, MembersFragment.MemberListViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull MembersFragment.MemberListViewHolder holder, int position, @NonNull Contacts model) {
                        holder.username.setText(model.getName());
                        Picasso.get().load(model.getImage()).into(holder.profileImage);
                    }

                    @NonNull
                    @Override
                    public MembersFragment.MemberListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        MembersFragment.MemberListViewHolder viewHolder = new MembersFragment.MemberListViewHolder(view);
                        return viewHolder;
                    }
                };
        displayUsersRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MemberListViewHolder extends RecyclerView.ViewHolder{
        TextView username;
        CircleImageView profileImage;

        public MemberListViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_profile_name);
            profileImage = itemView.findViewById(R.id.users_profile_image);

        }

    }
}
