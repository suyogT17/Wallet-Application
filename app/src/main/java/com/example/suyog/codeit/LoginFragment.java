package com.example.suyog.codeit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LoginFragment extends Fragment  {


    private View emailview;
    private View passview;
    private EditText mLoginEmail;
    private EditText mLoginPassword;
    private Button mLoginbtn;
    private Button mRegister;
    //private SignInButton  mSignInButton;
    //public static GoogleApiClient mGoogleApiClient;
    private static final int REQ_CODE=9001;
    public static String name;
    public static String email;
    FirebaseAuth mAuth;
    ProgressDialog mProgressDialog;
    private static LoginFragment loginFragment;
private Button test;
    public static LoginFragment getInstance(){
        if(loginFragment != null){
            return loginFragment;
        }
        return new LoginFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_login, container, false);

        mLoginEmail=(EditText) view.findViewById(R.id.loginEmail);
        mLoginPassword=(EditText) view.findViewById(R.id.loginPassword);
        mLoginbtn=(Button) view.findViewById(R.id.loginbtn);
        mRegister=(Button) view.findViewById(R.id.linksignup);
        mAuth = FirebaseAuth.getInstance();
        mProgressDialog= new ProgressDialog(getActivity());
        emailview=(View) view.findViewById(R.id.emailview);
        passview=(View) view.findViewById(R.id.passview);




        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,new RegisterFragment(),"register")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack("register")
                        .commit();
            }
        });

        mLoginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=mLoginEmail.getText().toString().trim();
                String pass=mLoginPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getActivity(),"Enter Valid Email",Toast.LENGTH_LONG).show();
                    return;
                }


                if(TextUtils.isEmpty(pass)){
                    Toast.makeText(getActivity(),"Enter Valid Password",Toast.LENGTH_LONG).show();
                    return;
                }

                mProgressDialog.setMessage("Signing In");
                mProgressDialog.show();

                mAuth.signInWithEmailAndPassword(email,pass)
                        .addOnCompleteListener(getActivity(),new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    Intent intent=new Intent(getActivity(), ChatActivity.class);
                                    getActivity().startActivity(intent);
                                    getActivity().finish();

                                }
                                else{
                                    mProgressDialog.dismiss();
                                    Toast.makeText(getActivity(),"Login Failed",Toast.LENGTH_LONG).show();

                                }
                            }
                        });
            }
        });


        return view;
    }




}
