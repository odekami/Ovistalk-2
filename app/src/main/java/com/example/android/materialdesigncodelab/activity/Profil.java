package com.example.android.materialdesigncodelab.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.materialdesigncodelab.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class Profil extends AppCompatActivity {

    private StorageReference mStorageRef;
    private ImageView btnChoose, btnUpload;
    private ImageView imageView;
    private TextView txt_Name, txt_Email, txt_Status, txt_Exit;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;
    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase database;
    String profil_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //   toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        String name = getIntent().getStringExtra("EXTRA_NAME");
        String id = getIntent().getStringExtra("EXTRA_ID");
        String token = getIntent().getStringExtra("EXTRA_TOKEN");
        String pin_channel_group = getIntent().getStringExtra("EXTRA_PIN");
        String type = getIntent().getStringExtra("EXTRA_TYPE");

        //Initialize Views
        //btnChoose = (ImageView) findViewById(R.id.img_add_profil);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.img_add_profil);
        imageView = (ImageView) findViewById(R.id.img_profil);
        Bitmap bitmap = ((BitmapDrawable) MainActivity.img_profil.getDrawable()).getBitmap();
        imageView.setImageBitmap(bitmap);

        /*
        // text view
        txt_Name = (TextView) findViewById(R.id.txt_Name);
        txt_Email = (TextView) findViewById(R.id.txt_Email);
        txt_Status = (TextView) findViewById(R.id.txt_Status);
        txt_Exit = (TextView) findViewById(R.id.txt_Exit);


        // set text
        txt_Name.setText(name);

        txt_Email.setText(name);
        txt_Status.setText(pin_channel_group);
        txt_Exit.setText("Exit");
        */

        // database

        database = FirebaseDatabase.getInstance();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }

        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void chooseImage() {

        // storage = FirebaseStorage.getInstance();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            FirebaseUser currentFirebaseUser1 = FirebaseAuth.getInstance().getCurrentUser();
            storageReference = FirebaseStorage.getInstance().getReference();
            profil_image = "profil_image_" + currentFirebaseUser1.getUid();
            StorageReference ref = storageReference.child("images/" + profil_image);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                            // save to database

                            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference myRef1 = database.getReference("users").child("user_" + currentFirebaseUser.getUid());
                            myRef1.setValue(new User_data(currentFirebaseUser.getEmail(), currentFirebaseUser.getUid(), currentFirebaseUser.getUid(), currentFirebaseUser.getUid(), profil_image));

                            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            MainActivity.img_profil.setImageBitmap(bitmap);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }
}
