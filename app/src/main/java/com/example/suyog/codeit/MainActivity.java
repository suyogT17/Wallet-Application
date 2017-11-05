package com.example.suyog.codeit;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.SyncStateContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;


import java.sql.Connection;


public class MainActivity extends AppCompatActivity {

    public static int SPLASH_TIME=4000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();

        Connection con = Database.connect();
        Toast.makeText(getApplicationContext(),con.toString(),Toast.LENGTH_LONG);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginIntent = new Intent(MainActivity.this, AuthenticationActivity.class);
                startActivity(loginIntent);
                finish();
            }
        },SPLASH_TIME);
    }


}



