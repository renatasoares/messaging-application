package com.messaginapp.messaging_application.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.messaginapp.messaging_application.R;
import com.messaginapp.messaging_application.controller.AcceptionAdapter;
import com.messaginapp.messaging_application.model.Acception;
import com.messaginapp.messaging_application.model.AppMessage;

import java.util.ArrayList;
import java.util.List;


public class AcceptionActivity extends AppCompatActivity {

    private ListView acceptionListView;
    private AcceptionAdapter acceptionAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String idSender;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acception);

        Intent intent = getIntent();
        idSender = intent.getStringExtra("idSender");

        acceptionListView = (ListView) findViewById(R.id.acceptionListView);

        List<Acception> solicitations = new ArrayList<>();
        acceptionAdapter = new AcceptionAdapter(this, R.layout.item_invitation, solicitations);
        acceptionListView.setAdapter(acceptionAdapter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("solicitations");

        if(idSender != null){
            Log.d("SENDER", idSender);
            Acception acception = new Acception(idSender);
            databaseReference.push().setValue(acception);
        }

        handleAcception();
    }

    private void handleAcception(){
        if(childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Acception acception = dataSnapshot.getValue(Acception.class);
                    acceptionAdapter.add(acception);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Acception acception = dataSnapshot.getValue(Acception.class);
                    acceptionAdapter.remove(acception);
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
