package com.messaginapp.messaging_application.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.messaginapp.messaging_application.R;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import com.messaginapp.messaging_application.controller.MessageAdapter;
import com.messaginapp.messaging_application.controller.CryptoEEHelper;
import com.messaginapp.messaging_application.model.AppMessage;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ListView messageListView;
    private MessageAdapter messageAdapter;
    private ProgressBar progressBar;
    private ImageButton photoButton;
    private EditText messageEditText;
    private Button sendButton;

    public static final int LIMIT_LENGTH = 1000;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;
    private FirebaseStorage firebaseStorage;
    private StorageReference photosStorageReference;
    private StorageReference videosStorageReference;
    private String idUser1;
    private String idUser2;
    private String idUser;

    private FirebaseAuth firebaseAuth;
    private static final int RC_SIGN_IN = 123;

    private static final int RC_PHOTO_PICKER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        idUser1 = intent.getStringExtra("idUser1");
        idUser2 = intent.getStringExtra("idUser2");


        if(idUser1 == null || idUser2 == null || idUser1.isEmpty()  || idUser2.isEmpty()){
            finish();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        databaseReference = firebaseDatabase.getReference().child("messages");
        photosStorageReference = firebaseStorage.getReference().child("messaging_application_photo");
        videosStorageReference = firebaseStorage.getReference().child("messaging_application_video");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        messageListView = (ListView) findViewById(R.id.messageListView);
        photoButton = (ImageButton) findViewById(R.id.photoPickerButton);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        sendButton = (Button) findViewById(R.id.sendButton);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getCurrentUser().reload();
        idUser = firebaseAuth.getCurrentUser().getUid().toString();

        List<AppMessage> appMessages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, R.layout.item_message, appMessages);
        messageListView.setAdapter(messageAdapter);

        progressBar.setVisibility(ProgressBar.INVISIBLE);

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/* video/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Choose"), RC_PHOTO_PICKER);
            }
        });

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    sendButton.setEnabled(true);
                } else {
                    sendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        messageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(LIMIT_LENGTH)});

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CryptoEEHelper cryptoEEHelper = new CryptoEEHelper();
                String messagePlainText = messageEditText.getText().toString();
                String option = "";
                if(firebaseAuth.getCurrentUser().getUid().toString().equals(idUser1)){
                    option = idUser2;
                }else{
                    option = idUser1;
                }
                Log.d("PQP", option + " " + idUser1 + " " + idUser2 + " " + idUser);
                String encryptedData = cryptoEEHelper.encrypt(firebaseAuth.getCurrentUser().getUid().toString(), messagePlainText, option);
                AppMessage appMessage = new AppMessage(idUser1, idUser2,encryptedData , firebaseAuth.getCurrentUser().getDisplayName(), null, null, null);
                databaseReference.push().setValue(appMessage);

                messageEditText.setText("");
            }
        });

        handleMessage();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "Logged in!", Toast.LENGTH_SHORT).show();
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Goodbye!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }else if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedMediaUri = data.getData();

            if (selectedMediaUri.toString().contains("image")) {
                StorageReference photoReference = photosStorageReference.child(selectedMediaUri.getLastPathSegment());

                photoReference.putFile(selectedMediaUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        AppMessage imageMessage = new AppMessage(idUser1, idUser2, null, firebaseAuth.getCurrentUser().getDisplayName(), downloadUrl.toString(), null, null);

                        databaseReference.push().setValue(imageMessage);
                    }
                });
            } else if (selectedMediaUri.toString().contains("video")) {
                StorageReference videoReference = videosStorageReference.child(selectedMediaUri.getLastPathSegment());

                videoReference.putFile(selectedMediaUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        AppMessage videoMessage = new AppMessage(idUser1, idUser2, null, firebaseAuth.getCurrentUser().getDisplayName(), null, downloadUrl.toString(), null);
                        databaseReference.push().setValue(videoMessage);
                    }
                });
            }
        }
    }

    private void handleMessage(){
        if(childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    CryptoEEHelper cryptoEEHelper = new CryptoEEHelper();
                    AppMessage message = dataSnapshot.getValue(AppMessage.class);

                    String decryptedMessage= cryptoEEHelper.decrypt(message.getBodyMessage().toString(),
                            firebaseAuth.getCurrentUser().getUid().toString(), getApplicationContext());

                    if(message.getIdUser1().equals(idUser) || message.getIdUser2().equals(idUser)) {
                        message.setBodyMessage(decryptedMessage);
                        messageAdapter.add(message);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
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