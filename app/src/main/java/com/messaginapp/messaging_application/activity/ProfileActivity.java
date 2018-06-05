package com.messaginapp.messaging_application.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.google.firebase.database.ValueEventListener;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.messaginapp.messaging_application.R;
import com.messaginapp.messaging_application.model.TokenDO;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ImageView imageView;
    DynamoDBMapper dynamoDBMapper;
    private EditText editText;
    private TextView textView;
    private Button buttonNameChat;
    private Button buttonNewChat;
    private String username = "ANONYMOUS";


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        AWSMobileClient.getInstance().initialize(this).execute();

        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        textView = (TextView) findViewById(R.id.timerQrCode);
        editText = (EditText) findViewById(R.id.editUsernameField);
        buttonNameChat = (Button) findViewById(R.id.editUsername);
        buttonNewChat = (Button) findViewById(R.id.newChatButton);


        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.getCurrentUser().reload();

        editText.setText(firebaseAuth.getCurrentUser().getDisplayName(),TextView.BufferType.EDITABLE);

        buttonNameChat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setUsername(editText.getText().toString());
                editText.setText(getUsername(),TextView.BufferType.EDITABLE);
                Toast.makeText(ProfileActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });

        buttonNewChat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentQrCode = new Intent(ProfileActivity.this, QrCodeReaderActivity.class);
                intentQrCode.putExtra("idSender", editText.getText().toString());
                startActivity(intentQrCode);
                finish();
            }
        });



        CountDownTimer cdt = new CountDownTimer(60000,1000) {
            public void onTick(long millisUntilFinished) {
                textView.setText("Remaining : " + millisUntilFinished/1000 + " secs");
            }
            public void onFinish() {
                textView.setText("00:00");
                generateQrCode();
                start();
            }
        };

        generateQrCode();
        cdt.start();

        firebaseAuth = FirebaseAuth.getInstance();


    }

    public void generateQrCode(){
        imageView = (ImageView) findViewById(R.id.qrCode);

        String idUser = firebaseAuth.getCurrentUser().getUid();
        String code = UUID.randomUUID().toString();

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC-3"));
        cal.add(Calendar.MINUTE, 1);
        long ttl = cal.getTimeInMillis() / 1000L;

        final TokenDO token = new TokenDO(idUser, ttl, code);

        new Thread(new Runnable() {
            @Override
            public void run() {
                dynamoDBMapper.save(token);
            }
        }).start();


        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(code, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            imageView.setImageBitmap(bmp);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
