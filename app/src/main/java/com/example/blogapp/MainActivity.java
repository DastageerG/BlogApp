package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity
{


    private static final String TAG = "defaultLogCheck" ;
    private Context context = MainActivity.this;
    private Toolbar toolbar;
    private FloatingActionButton buttonAddPost;
    private RecyclerView recyclerView;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbarMainActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Photo Blog");
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        buttonAddPost = findViewById(R.id.floatingActionButtonAddPost);
        recyclerView = findViewById(R.id.recycleViewMainActivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);

        buttonAddPost.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context,AddPostActivity.class);
                startActivity(intent);
            }
        });


    } // onCreate closed
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main_activity,menu);
        return super.onCreateOptionsMenu(menu);
    } // OnCreateOptionsMenu closed

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menuMainActivityLogOutFirebase:
                if(mFirebaseAuth.getCurrentUser()!=null)
                {
                    mFirebaseAuth.signOut();
                    Intent intent = new Intent(context,LogInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } // if closed
                break;
            case R.id.menuMainActivityAccountSetting:
                Intent intent = new Intent(context,AccountSettingActivity.class);
                startActivity(intent);
                break;
        } // switch closed
        return super.onOptionsItemSelected(item);
    } // onOptionsItemsSelected closed

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if(user == null)
        {
            Intent intent = new Intent(context, LogInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else
        {
            firebaseFirestore.collection(Utils.USERS).document(mFirebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                {

                    if(!task.getResult().exists())
                    {
                        Intent intent = new Intent(context, AccountSettingActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }// onComplete closed
            }); // addOnCompleteListener closed
        }
    }
} // MainActivity Class closed