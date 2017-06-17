package com.example.cameraapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import static android.R.attr.data;

public class MainActivity extends AppCompatActivity {

    private Button mUploadbtn;
    private ImageView mImageView;
    private static final int CAMERA_REQUEST_CODE = 100;
    private StorageReference mStorage;
    private ProgressDialog mprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStorage = FirebaseStorage.getInstance().getReference();
        mUploadbtn = (Button)findViewById(R.id.upload);
        mImageView = (ImageView)findViewById(R.id.imageView);
        mprogress = new ProgressDialog(this);
        mUploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAMERA_REQUEST_CODE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode , resultCode , data);
//        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
//            mprogress.setMessage(" Uploading Imagre....");
//            mprogress.show();
//            Uri uri = data.getData();
//            StorageReference filepath = mStorage.child("Photos").child(uri.getLastPathSegment());
//            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    mprogress.dismiss();
//                    Uri downloadUri = taskSnapshot.getDownloadUrl();
//                    Picasso.with(MainActivity.this).load(downloadUri).fit().centerCrop().into(mImageView);
//                    Toast.makeText(MainActivity.this,"Upload Finished....",Toast.LENGTH_LONG).show();
//
//                }
//            });
//        }
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            //set the progress dialog
//            progressDialog.setMessage("Uploding image...");
//            progressDialog.show();

            //get the camera image
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] databaos = baos.toByteArray();

            //set the image into imageview
            mImageView.setImageBitmap(bitmap);
            //String img = "fire"

            //Firebase storage folder where you want to put the images
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            //name of the image file (add time to have different files to avoid rewrite on the same file)
            StorageReference imagesRef = storageRef.child("child" + new Date().getTime());
            //send this name to database
            //upload image
            UploadTask uploadTask = imagesRef.putBytes(databaos);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(MainActivity.this, "Sending failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.e("tag0",taskSnapshot.getDownloadUrl().toString());
//                    progressDialog.dismiss();
                }
            } );}


    }
}
