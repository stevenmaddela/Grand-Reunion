package com.syfn.grandreunion;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private ArrayList<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private String currentUserId;

    public MessageAdapter(ArrayList<Messages> userMessagesList){
        this.userMessagesList = userMessagesList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView senderMessageText, senderMessageTime, senderMessageDate, receiverMessageText, receiverMessageName, receiverMessageTime, receiverMessageDate;
        public CircleImageView receiverProfileImage;
        public MessageViewHolder(@NonNull View itemView) {

            super(itemView);
            senderMessageText = itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = itemView.findViewById(R.id.message_profile_image);
            senderMessageDate = itemView.findViewById(R.id.sender_message_date);
            receiverMessageDate = itemView.findViewById(R.id.receiver_message_date);
            receiverMessageTime = itemView.findViewById(R.id.receiver_message_time);
            receiverMessageName = itemView.findViewById(R.id.receiver_message_name);

        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messages_layout, parent, false);
        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        currentUserId = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);
        String fromId = messages.getSenderId();

        String fromUserName = messages.getName();
        String fromMessage = messages.getMessage();
        String fromTime = messages.getTime();
        String fromDate = messages.getDate();
        String fromType = messages.getType();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromId);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("image")){
                    String receiverImage = dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(receiverImage).into(holder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(fromType.equals("text")){
            if(fromId.equals(currentUserId)){
                holder.receiverMessageText.setVisibility(View.INVISIBLE);
                holder.receiverMessageName.setVisibility(View.INVISIBLE);
                holder.receiverMessageDate.setVisibility(View.INVISIBLE);
                holder.receiverMessageTime.setVisibility(View.INVISIBLE);
                holder.receiverProfileImage.setVisibility(View.INVISIBLE);

                holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.senderMessageDate.setVisibility(View.VISIBLE);
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageText.setTextColor(Color.BLACK);
                holder.senderMessageText.setText(fromMessage);
                holder.senderMessageDate.setText(fromDate + "\n\t\t" + fromTime);

            }
            else{

                holder.senderMessageText.setVisibility(View.INVISIBLE);
                holder.senderMessageDate.setVisibility(View.INVISIBLE);
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverMessageName.setVisibility(View.VISIBLE);
                holder.receiverMessageTime.setVisibility(View.VISIBLE);
                holder.receiverMessageDate.setVisibility(View.VISIBLE);

                holder.receiverMessageText.setBackgroundResource(R.drawable.reciever_messages_layout);
                holder.receiverMessageName.setTextColor(Color.BLACK);
                holder.receiverMessageText.setTextColor(Color.BLACK);
                holder.receiverMessageText.setText(fromMessage);
                holder.receiverMessageDate.setText(fromDate);
                holder.receiverMessageName.setText(fromUserName);
                holder.receiverMessageTime.setText(fromTime);
            }
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }


}
