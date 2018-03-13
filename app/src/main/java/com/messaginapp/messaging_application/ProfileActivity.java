package com.messaginapp.messaging_application;

import android.content.DialogInterface;
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
import java.util.UUID;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ImageView imageView;
    private EditText editText;
    private TextView textView;
    private Button button;
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

        textView = (TextView) findViewById(R.id.timerQrCode);
        editText = (EditText) findViewById(R.id.editUsernameField);
        button = (Button) findViewById(R.id.editUsername);

        editText.setText(getUsername(),TextView.BufferType.EDITABLE);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setUsername(editText.getText().toString());
                editText.setText(getUsername(),TextView.BufferType.EDITABLE);
                Toast.makeText(ProfileActivity.this, "Saved", Toast.LENGTH_SHORT).show();
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

        String code = UUID.randomUUID().toString();

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
