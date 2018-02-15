package com.example.suyog.codeit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

public class AuthenticationActivity extends AppCompatActivity {



    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);



        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();


        mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            Intent intent=new Intent(AuthenticationActivity.this,ChatActivity.class);
            startActivity(intent);
            finish();
        }
        getSupportFragmentManager().beginTransaction().add(R.id.container,new LoginFragment()).commit();


    }


}
