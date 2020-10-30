package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.blogapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

public class AccountSettingActivity extends AppCompatActivity
{
    private static final String TAG = "defaultLogCheck" ;
    private static final int PICK_IMAGE = 111;
    private Context context = AccountSettingActivity.this;
    private Toolbar toolbar;
    private ImageView imageViewProfile;
    private EditText editTextName;
    private Button buttonSaveSettings;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private ProgressDialog progressDialog;
    private StorageReference storageReference,path;
    private Uri mImageUri;
    private boolean isImageChanged = false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        toolbar = findViewById(R.id.toolbarAccountSettingActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageViewProfile = findViewById(R.id.imageViewAccountSettingActivityProfile);
        editTextName = findViewById(R.id.editTextAccountSettingActivityName);
        buttonSaveSettings = findViewById(R.id.buttonAccountSettingSaveSetting);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore= FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        path = storageReference.child(Utils.Profile).child(mFirebaseAuth.getCurrentUser().getUid()).child("image.jpg");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading ...");
        progressDialog.setMessage("Please Wait ... ");
        progressDialog.setCanceledOnTouchOutside(false);

        imageViewProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                } // if closed
                else
                {
                    ActivityCompat.requestPermissions(AccountSettingActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                } // else closed
            } // on Click closed
        }); // onClick listener on imageView closed




        progressDialog.show();
        buttonSaveSettings.setEnabled(false);


        mFirebaseFirestore.collection(Utils.USERS).document(mFirebaseAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>()
                {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e)
                    {
                        if(documentSnapshot.exists())
                        {
                            editTextName.setText(documentSnapshot.get(Utils.name).toString());
                            Picasso.get().load(documentSnapshot.get(Utils.imageUrl).toString()).placeholder(R.drawable.profile_image).into(imageViewProfile);
                            mImageUri = Uri.parse(documentSnapshot.getString(Utils.imageUrl));
                        } // if closed
                        else
                        {
                            imageViewProfile.setImageResource(R.drawable.profile_image);
                            editTextName.setHint(R.string.enter_name);
                        } // else closed
                        buttonSaveSettings.setEnabled(true);
                        progressDialog.dismiss();
                    }
                });

//        mFirebaseFirestore.collection(Utils.USERS).document(mFirebaseAuth.getCurrentUser().getUid())
//                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
//        {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task)
//            {
//                if(task.getResult().exists())
//                {
//                    editTextName.setText(task.getResult().get(Utils.name).toString());
//                    Picasso.get().load(task.getResult().get(Utils.imageUrl).toString()).placeholder(R.drawable.profile_image).into(imageViewProfile);
//                    mImageUri = Uri.parse(task.getResult().getString(Utils.imageUrl));
//                }
//                else
//                {
//                    imageViewProfile.setImageResource(R.drawable.profile_image);
//                    editTextName.setHint(R.string.enter_name);
//                }
//                buttonSaveSettings.setEnabled(true);
//                progressDialog.dismiss();
//
//            } // onComplete closed
//        }); // getData From Firebase Complete Listener closed


        buttonSaveSettings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String name = editTextName.getText().toString().trim();
                if(isImageChanged)
                {

                    if (mImageUri == null)
                    {
                        Toast.makeText(context, "Please Select an image", Toast.LENGTH_SHORT).show();
                    } else if (name.isEmpty())
                    {
                        Toast.makeText(context, "Please  Enter Name", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        saveSetting(name);
                    }
                } // if closed
                else
                {

                    if (name.isEmpty())
                    {
                        Toast.makeText(context, "Please  Enter Name", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        progressDialog.show();
                        User user = new User();
                        if(mImageUri == null)
                        {
                            user.setImageUrl("default");
                        } // if closed
                        else
                        {
                            user.setImageUrl(mImageUri.toString());
                        }
                        user.setName(name);
                        user.setUserId(mFirebaseAuth.getCurrentUser().getUid());
                        mFirebaseFirestore.collection(Utils.USERS).document(mFirebaseAuth.getCurrentUser().getUid())
                                .set(user).addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(AccountSettingActivity.this, "Changes Saved Successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context,MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }  // if closed
                                else
                                {
                                    Toast.makeText(context, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                } // else closed
                                progressDialog.dismiss();
                            } // onComplete closed
                        });

                    } // else closed

                } // else closed
            } // onClick closed
        }); // buttonSaveSettingOnClick Listener closed


    } // onCreate closed
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                } // if closed
                else
                {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(context, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                } // else closed
                return;
            } // case 1 closed
        } // switch closed
    } // onRequestPermissionsResult closed

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE)
        {
            if(resultCode == RESULT_OK)
            {
                CropImage.activity(data.getData())
                        .setAspectRatio(1,1)
                        .start(this);

            } // if closed
            else if (resultCode == RESULT_CANCELED)
            {
                Toast.makeText(context, "No Image Picked", Toast.LENGTH_SHORT).show();
            } // else if closed

        } //if for request code closed

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                isImageChanged = true;
                Uri resultUri = result.getUri();
                mImageUri = resultUri;
                imageViewProfile.setImageURI(mImageUri);
            } // if closed
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            } // else if cosed
        } // if closed


    } // onActivityResult closed

    private void saveSetting(final String name)
    {
            progressDialog.show();
            path.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                {
                    if(task.isSuccessful())
                    {
                        path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                        {
                            @Override
                            public void onSuccess(Uri uri)
                            {
                                User user = new User();
                                user.setUserId(mFirebaseAuth.getCurrentUser().getUid());
                                user.setName(name);
                                user.setImageUrl(uri.toString());

                                mFirebaseFirestore.collection(Utils.USERS)
                                        .document(mFirebaseAuth.getCurrentUser().getUid()).set(user)
                                        .addOnCompleteListener(new OnCompleteListener<Void>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if(task.isSuccessful())
                                                {

                                                    progressDialog.dismiss();
                                                    Toast.makeText(AccountSettingActivity.this, "Changes Saved Successfully", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(context,MainActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                } // if closed
                                            }// onComplete closed
                                        }).addOnFailureListener(new OnFailureListener()
                                {
                                    @Override
                                    public void onFailure(@NonNull Exception e)
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } // onSuccess closed
                        }); // download Url OnSuccess closed
                    } // if closed

                } // onComplete closed
            }); // put image Complete Listener
    } // SaveSetting closed



    @Override
    public void onBackPressed()
    {
        finish();
    }



} // class closed