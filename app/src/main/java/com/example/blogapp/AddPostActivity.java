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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.blogapp.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Date;

public class AddPostActivity extends AppCompatActivity
{
    private static final String TAG = "defaultLogCheck" ;
    private static final int PICK_IMAGE = 122;
    private Context context = AddPostActivity.this;
    private Toolbar toolbar;
    private ImageView imageViewPostImage;
    private EditText editTextPostDescription;
    private Button buttonPost;
    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private StorageReference path;
    private Uri mImageUri;
    private boolean isImageChanged = false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        toolbar = findViewById(R.id.toolbarAddPostActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Post ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageViewPostImage = findViewById(R.id.imageView);
        editTextPostDescription = findViewById(R.id.editTextAddPostActivityPostDescription);
        buttonPost = findViewById(R.id.buttonAddPostActivityPost);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading ...");
        progressDialog.setMessage("Please Wait ... ");
        progressDialog.setCanceledOnTouchOutside(false);


        imageViewPostImage.setOnClickListener(new View.OnClickListener()
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
                    ActivityCompat.requestPermissions(AddPostActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            2);
                } // else closed
            } // onClick closed
        }); // imageView set onclick listener closed


        buttonPost.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String postDescription = editTextPostDescription.getText().toString().trim();
                if(isImageChanged)
                {
                    if (mImageUri == null)
                    {
                        Toast.makeText(context, "Please Select an image", Toast.LENGTH_SHORT).show();
                    } else if (postDescription.isEmpty())
                    {
                        Toast.makeText(context, "Please  Enter Description", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        postBlog(postDescription);
                    }
                } // if closed

//                else
//                {
//
//                    if (name.isEmpty())
//                    {
//                        Toast.makeText(context, "Please  Enter Name", Toast.LENGTH_SHORT).show();
//                    } else
//                    {
//                        progressDialog.show();
//                        User user = new User();
//                        if (mImageUri == null)
//                        {
//                            user.setImageUrl("default");
//                        } // if closed
//                        else
//                        {
//                            user.setImageUrl(mImageUri.toString());
//                        }
//                        user.setName(name);
//                        user.setUserId(mFirebaseAuth.getCurrentUser().getUid());
//                    }
//                }
            } // onClick
        }); // buttonPost setOnClickListener closed


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
                        .start(AddPostActivity.this);

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
                Uri resultUri = result.getUri();
                mImageUri = resultUri;
                isImageChanged = true;
                imageViewPostImage.setImageURI(mImageUri);
            } // if closed
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            } // else if cosed
        } // if closed
    } // onActivityResult closed


    private void postBlog(final String postDescription)
    {
        progressDialog.show();
        path = storageReference.child("Posts").child(mFirebaseAuth.getCurrentUser()
                .getUid()).child("post_image"+ Timestamp.now().getSeconds()+".jpg");
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
                            if(uri != null)
                            {
                                String imageUrl = uri.toString();

                                Post post = new Post();
                                post.setCurrentUserId(mFirebaseAuth.getCurrentUser().getUid());
                                post.setPostDescription(postDescription);
                                post.setPostImageUrl(imageUrl);
                                post.setTimestamp(new Timestamp(new Date()));

                                firebaseFirestore.collection(Utils.POSTS).add(post).addOnCompleteListener(new OnCompleteListener<DocumentReference>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task)
                                    {
                                        progressDialog.dismiss();
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(AddPostActivity.this, "Post Added", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(context, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } //

                                    } // on Comlete closed
                                });

                            } // if closed
                        } // onSuccess closed
                    });
                } // if closed
                else
                {
                    Log.d(TAG, "onComplete: ImageUpload "+task.getException().getMessage());
                } // else closed

            } // onComplete closed
        }); // addOnCompleteListener closed


    }

} // Main Class closed

