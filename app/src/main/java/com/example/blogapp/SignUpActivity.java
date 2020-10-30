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

public class SignUpActivity extends AppCompatActivity
{

    private static final String TAG = "defaultLogCheck" ;
    private Context context = SignUpActivity.this;
    private EditText editTextEmail,editTextPassword,editTextReconfirmPass;
    private Button buttonSignUp,buttonToLogInScreen;
    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        editTextEmail = findViewById(R.id.editTextSignUpActivityEmail);
        editTextPassword = findViewById(R.id.editTextSignUpActivityPassword);
        editTextReconfirmPass = findViewById(R.id.editTextSignUpActivityReConfirmPass);
        buttonSignUp = findViewById(R.id.buttonSignUpActivitySignUp);
        buttonToLogInScreen = findViewById(R.id.buttonSignUpActivityGoToLogInScreen);

        mFirebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading ...");
        progressDialog.setMessage("Please Wait ... ");
        progressDialog.setCanceledOnTouchOutside(false);
        buttonSignUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = editTextEmail.getText().toString().trim();
                String pass = editTextPassword.getText().toString().trim();
                String reConfirmPass = editTextReconfirmPass.getText().toString().trim();


                 if(email.isEmpty())
                {
                    Toast.makeText(context, "Please Enter Email", Toast.LENGTH_SHORT).show();
                }
                else if(pass.isEmpty())
                {
                    Toast.makeText(context, "Please Enter Password", Toast.LENGTH_SHORT).show();
                }
                else if (reConfirmPass.isEmpty())
                 {
                     Toast.makeText(context, "Please Re Enter Your Password", Toast.LENGTH_SHORT).show();
                 }
                 else if (!reConfirmPass.equals(pass))
                 {
                     Toast.makeText(context, "Password Does Not match please Retry", Toast.LENGTH_SHORT).show();
                     editTextPassword.setText(null);
                     editTextReconfirmPass.setText(null);
                 }
                else
                {
                    SignUpIntoFirebase(email,pass);
                }
            }
        }); // button Sign Up onClick Listener

        buttonToLogInScreen.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(context,LogInActivity.class));
            }
        });
    } // onCreate closed

    private void SignUpIntoFirebase(String email, String pass)
    {
        progressDialog.show();
        mFirebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    String id = mFirebaseAuth.getCurrentUser().getUid();
                    // if account Created send user to setup account details
                    Intent intent = new Intent(context,AccountSettingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } // if closed
                progressDialog.dismiss();
            }// onComplete for account Creation
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {

                progressDialog.dismiss();
                Log.d(TAG, "onFailure: "+e.getMessage());
            } // onFailure For Account creation
        }); // addOnFailure for account creation
    } // SignUpIntoFirebase method closed
} // class closed