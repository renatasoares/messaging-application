package com.messaginapp.messaging_application.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.messaginapp.messaging_application.R;
import com.messaginapp.messaging_application.controller.ChatAdapter;
import com.messaginapp.messaging_application.model.Chat;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ListView chatListView;
    private ChatAdapter chatAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acception);


        chatListView = (ListView) findViewById(R.id.chatListView);

        List<Chat> solicitations = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, R.layout.item_chat, solicitations);
        chatListView.setAdapter(chatAdapter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("room");

        Chat chat = new Chat();
        databaseReference.push().setValue(chat);

        handleAcception();
    }

    private void handleAcception(){
        if(childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    chatAdapter.add(chat);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    chatAdapter.remove(chat);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
                
            };

            databaseReference.addChildEventListener(childEventListener);
        }
    }
}
