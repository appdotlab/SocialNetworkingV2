package com.example.photouploader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button chooseBtn, uploadBtn;
    ImageView imageView;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chooseBtn = (Button) findViewById(R.id.chooseBtn);
        uploadBtn = (Button) findViewById(R.id.uploadBtn);
        imageView = (ImageView) findViewById(R.id.imageView);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        upload();
        choose();

    }

    public void choose(){
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 71);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 71 && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

        public void upload(){
            uploadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(filePath != null)
                    {
                        final ProgressDialog progressDialog = new ProgressDialog(view.getContext());
                        progressDialog.setTitle("Uploading...");
                        progressDialog.show();

                        StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
                        ref.putFile(filePath)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                                .getTotalByteCount());
                                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                                    }
                                });
                    }
                }
            });
        }
}
