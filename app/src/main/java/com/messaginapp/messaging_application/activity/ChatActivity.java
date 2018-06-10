package com.messaginapp.messaging_application.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.messaginapp.messaging_application.R;
import com.messaginapp.messaging_application.controller.ChatAdapter;
import com.messaginapp.messaging_application.controller.CryptoEEHelper;
import com.messaginapp.messaging_application.model.Chat;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ListView chatListView;
    private ChatAdapter chatAdapter;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ChildEventListener childEventListener;
    private String idReceiver;
    private String idUser;
    private static final int REQUEST_CAMERA = 5;
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (!haveCameraPermission()) {
            requestCameraPermission();
        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Intent intent = getIntent();
        idReceiver = intent.getStringExtra("idReceiver");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getCurrentUser().reload();

        chatListView = (ListView) findViewById(R.id.chatListView);

        List<Chat> chats = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, R.layout.item_chat, chats);
        chatListView.setAdapter(chatAdapter);

        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Chat chat = (Chat) parent.getItemAtPosition(position);
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                intent.putExtra("idUser1", chat.getIdentifierUser1());
                intent.putExtra("idUser2", chat.getIdentifierUser2());
                startActivity(intent);
                finish();
            }
        });

        idUser = firebaseAuth.getCurrentUser().getUid().toString();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("room");

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Toast.makeText(ChatActivity.this, "Welcome, " + firebaseUser.getDisplayName() + "!", Toast.LENGTH_SHORT).show();
                    try {
                        onSignedIn(firebaseUser.getDisplayName());
                    } catch (CryptoException e) {
                        e.printStackTrace();
                    }
                } else {
                    onSignedOut();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                                    .setTosUrl("https://superapp.example.com/terms-of-service.html")
                                    .setPrivacyPolicyUrl("https://superapp.example.com/privacy-policy.html")
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        if(idReceiver != null) {
            Chat chat = new Chat(idUser,idReceiver);
            databaseReference.push().setValue(chat);
        }

        handleChat();
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(ChatActivity.this, new String[]{android.Manifest.permission.CAMERA},
                REQUEST_CAMERA);
    }


    private boolean haveCameraPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private void handleChat(){
        if(childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getIdentifierUser1().equals(idUser) || chat.getIdentifierUser2().equals(idUser)) {
                        chatAdapter.add(chat);
                    }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_menu:
                Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.acception_menu:
                Intent acceptionIntent = new Intent(ChatActivity.this, AcceptionActivity.class);
                startActivity(acceptionIntent);
                finish();
                return true;
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        removeListener();
        chatAdapter.clear();
    }

    private void removeListener(){
        if(childEventListener != null) {
            databaseReference.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }
    private void onSignedIn(String providedName) throws CryptoException {
        CryptoEEHelper cryptoEEHelper = new CryptoEEHelper();
        cryptoEEHelper.createCard(firebaseAuth.getCurrentUser().getUid().toString(), getApplicationContext());
        handleChat();
    }

    private void onSignedOut(){
        chatAdapter.clear();
        removeListener();
    }
}
