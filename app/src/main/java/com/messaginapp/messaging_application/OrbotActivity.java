package com.messaginapp.messaging_application;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class OrbotActivity extends AppCompatActivity {

    private Button orbotButton;
    private boolean orbotInstalled;
    private ProgressBar progressBar;
    private TextView txtConfig;
    private TextView txtCategoria;
    private TextView txtIntro1;
    private TextView txtIntro2;
    private TextView txtIntro3;

    private static final String appPackageName = "org.torproject.android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orbot);

        final ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

        orbotButton = (Button) findViewById(R.id.orbotButton);

        progressBar = (ProgressBar) findViewById(R.id.orbotProgressBar);
        progressBar.setVisibility(ProgressBar.INVISIBLE);

        txtCategoria = (TextView) findViewById(R.id.txtCategoria);
        txtIntro1 = (TextView) findViewById(R.id.txtIntro1);
        txtIntro2 = (TextView) findViewById(R.id.txtIntro2);
        txtIntro3 = (TextView) findViewById(R.id.txtIntro3);

        txtConfig = (TextView) findViewById(R.id.txtConfig);
        txtConfig.setVisibility(TextView.INVISIBLE);


        OrbotHelper orbotHelper = new OrbotHelper();


        orbotInstalled = orbotHelper.isAppInstalled(OrbotActivity.this, appPackageName);


        if(orbotInstalled) {

            txtConfig.setVisibility(TextView.VISIBLE);
            txtCategoria.setVisibility(TextView.INVISIBLE);
            txtIntro1.setVisibility(TextView.INVISIBLE);
            txtIntro2.setVisibility(TextView.INVISIBLE);
            txtIntro3.setVisibility(TextView.INVISIBLE);

            orbotButton.setVisibility(Button.INVISIBLE);

            progressBar.setVisibility(ProgressBar.VISIBLE);

            final String url = "https://check.torproject.org/api/ip";

            final RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public synchronized void onResponse(JSONObject response){
                            try {
                                if(response.getBoolean("IsTor")){
                                    Intent intent = new Intent(OrbotActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Intent i = new Intent();
                                    PackageManager managerclock = getPackageManager();
                                    i = managerclock.getLaunchIntentForPackage(appPackageName);
                                    i.addCategory(Intent.CATEGORY_LAUNCHER);
                                    startActivity(i);
                                    finish();
                                }
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error.Response", error.toString());
                        }
                    }
            );
            queue.add(getRequest);
        }

        orbotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(!orbotInstalled){
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                    finish();
                }
            }
        });
    }
}
