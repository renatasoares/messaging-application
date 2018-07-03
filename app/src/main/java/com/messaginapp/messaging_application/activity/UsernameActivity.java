package com.messaginapp.messaging_application.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.messaginapp.messaging_application.R;

import java.util.UUID;

import static android.provider.CalendarContract.CalendarCache.URI;

public class UsernameActivity extends AppCompatActivity {

    private EditText editText;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        firebaseAuth = FirebaseAuth.getInstance();

        editText = (EditText) findViewById(R.id.editFirebaseUser);

        editText.setText(firebaseAuth.getCurrentUser().getDisplayName(), TextView.BufferType.EDITABLE);


        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(editText.getText().toString())
                            .build();

                    user.updateProfile(profileUpdates);
                    Toast.makeText(UsernameActivity.this, "Changed to " + editText.getText(), Toast.LENGTH_SHORT).show();

                    Intent intentBack = new Intent(UsernameActivity.this, ChatActivity.class);
                    startActivity(intentBack);

                    return true;

                }
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intentBack = new Intent(UsernameActivity.this, ChatActivity.class);
        startActivity(intentBack);
    }
}
