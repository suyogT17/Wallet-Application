package com.example.suyog.codeit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterFragment extends Fragment {

    private EditText mRegisterEmail;
    private EditText mRegisterPassword;
    private Button mRegisterbtn;
    private Button mLoginbtn;
    private EditText mRegisterName;
    private EditText mRegisterPhoneno;
    private EditText mRegisterLocation;
    FirebaseAuth mAuth;
    ProgressDialog mProgressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        mRegisterEmail=(EditText) view.findViewById(R.id.registerEmail);
        mRegisterPassword=(EditText) view.findViewById(R.id.registerPassword);
        mRegisterbtn=(Button) view.findViewById(R.id.registerbtn);
        mLoginbtn=(Button) view.findViewById(R.id.linksignin);
        mRegisterName=(EditText) view.findViewById(R.id.registername);
        mRegisterPhoneno=(EditText) view.findViewById(R.id.registerPhone);
        mRegisterLocation=(EditText) view.findViewById(R.id.registerCountry);
        mProgressDialog=new ProgressDialog(getActivity());
        mAuth=FirebaseAuth.getInstance();


        mLoginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,new LoginFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
            }
        });

        mRegisterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email=mRegisterEmail.getText().toString().trim();
                String pass=mRegisterPassword.getText().toString().trim();
                final String name=mRegisterName.getText().toString().trim();
                final String phone=mRegisterPhoneno.getText().toString().trim();
                final String country=mRegisterLocation.getText().toString().trim();


                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) ||TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(country)) {
                    Toast.makeText(getActivity() , "Fill All Fields" ,Toast.LENGTH_LONG ).show();
                    return;
                }



                mProgressDialog.setMessage("Registering User...");
                mProgressDialog.show();

                mAuth.createUserWithEmailAndPassword(email,pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                        if(!AddUser.addUser(Database.connect(),name,email,phone,country,"sbi","12345")){
                                            mProgressDialog.dismiss();
                                            Toast.makeText(getActivity(),"Registration failed",Toast.LENGTH_LONG);
                                        }


                                    Intent intent=new Intent(getActivity(),ChatActivity.class);
                                    getActivity().startActivity(intent);
                                    getActivity().finish();
                                }
                                else{
                                    mProgressDialog.dismiss();
                                    Toast.makeText(getActivity(),"Registration failed",Toast.LENGTH_LONG);

                                }
                            }
                        });
            }
        });



        return view;
    }

}
