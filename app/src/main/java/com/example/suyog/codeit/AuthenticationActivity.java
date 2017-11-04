package com.example.suyog.codeit;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
