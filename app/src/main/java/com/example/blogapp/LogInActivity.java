package com.example.blogapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity
{

    private static final String TAG = "defaultLogCheck" ;
    private Context context = LogInActivity.this;
    private EditText editTextEmail , editTextPassword;
    private Button buttonLogIn,buttonToSignUpScreen;
    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        editTextEmail =findViewById(R.id.editTextLogInActivityEmail);
        editTextPassword =findViewById(R.id.editTextLogInActivityPassword);
        buttonLogIn = findViewById(R.id.buttonLogInActivityLogIn);
        buttonToSignUpScreen = findViewById(R.id.buttonLogInActivityGoToSignUpScreen);

        mFirebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading ...");
        progressDialog.setMessage("Please Wait ... ");
        progressDialog.setCanceledOnTouchOutside(false);

        // if there is current user then bypass logIn Activity
        if (mFirebaseAuth.getCurrentUser()!=null)
        {
            Intent intent = new Intent(context,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }  // if closed

        buttonLogIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String email = editTextEmail.getText().toString().trim();
                String pass = editTextPassword.getText().toString().trim();

                if(email.isEmpty())
                {
                    Toast.makeText(context, "Please Enter Email", Toast.LENGTH_SHORT).show();
                }
                else if(pass.isEmpty())
                {
                    Toast.makeText(context, "Please Enter Password", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    LogIntoFirebase(email,pass);
                } // else closed
            } // onClick closed
        }); // button LogIn closed

        buttonToSignUpScreen.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(context,SignUpActivity.class));
            }
        }); // buttonToSignUpScreen onClick Listener closed
    } // onCreate closed

    private void LogIntoFirebase(String email, String pass)
    {
        progressDialog.show();
        mFirebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                progressDialog.dismiss();
                if(task.isSuccessful())
                {
                    Intent intent = new Intent(context,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            } // onComplete for LogIn
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                progressDialog.dismiss();
                Log.d(TAG, "onFailure: "+e.getMessage());
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            } // onFailure for Login
        }); // addOnFailure for //login

    } // LogIntoFirebase
} // class closed