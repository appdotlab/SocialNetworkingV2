package com.example.photouploader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class profileInfoActivity extends AppCompatActivity {
    String Dname,Bio;
    ImageButton backButton;
    CircleImageView DP;
    Button setData, setPhotoButton;
    MaterialEditText setDName,setBio;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference, ref2;
    DatabaseReference photoRef;
    SharedPreferences prefs;
    Boolean bool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

        DP = (CircleImageView) findViewById(R.id.setDP);
        setPhotoButton = (Button) findViewById(R.id.setPhotoButton);
        setData = (Button) findViewById(R.id.setData);
        backButton = (ImageButton) findViewById(R.id.backButton);


        setDName = (MaterialEditText) findViewById(R.id.setDName);
        setBio = (MaterialEditText) findViewById(R.id.setBio);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        ref2 = storage.getReference();
        prefs = getApplicationContext().getSharedPreferences("Prefs", Context.MODE_PRIVATE);

        String img = prefs.getString("img","N/A");
        Picasso.get().load(img).into(DP);
        bool=prefs.getBoolean("firstSignIn",false);
        prefs.edit()
                .putBoolean("firstSignIn",false)
                .apply();
        final String id = prefs.getString("userID", "N/A");
        setPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 71);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        upload();

    }
    public void upload(){
        final String id = prefs.getString("userID", "N/A");
        final boolean firstTime = prefs.getBoolean("firstSignIn",false);
        setData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dname = setDName.getText().toString();
                if (Dname.isEmpty()&& firstTime==true)
                {
                    Toast.makeText(getApplicationContext(), "Display Name Cannot be Empty!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Bio = setBio.getText().toString();
                    if (filePath != null)
                    {
                        final ProgressDialog progressDialog = new ProgressDialog(view.getContext());
                        progressDialog.setTitle("Uploading...");
                        progressDialog.show();
                        final StorageReference ref = storageReference.child("profilePics/" + id + "/" + UUID.randomUUID().toString());
                        ref.putFile(filePath)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                                        uploadSuccess(ref);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                                .getTotalByteCount());
                                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                                    }
                                });
                                photoRef = FirebaseDatabase.getInstance().getReference("Users").child(id);
                                photoRef.child("Display Name").setValue(Dname);
                                photoRef.child("Bio").setValue(Bio);
                    }
                    else if ((setBio.getText().toString()!=null)||(setDName.getText().toString()!=null))
                    {
                        photoRef = FirebaseDatabase.getInstance().getReference("Users").child(id);
                        photoRef.child("Display Name").setValue(Dname);
                        photoRef.child("Bio").setValue(Bio);
                        Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();

                    }
                }

//                final String id = prefs.getString("userID", "N/A");
//                imgPath = ref2.child("images/"+id+"/"+ UUID.randomUUID().toString()).getDownloadUrl().getResult();
//                String img = String.valueOf(imgPath);
//                Log.i("img", img);
            }
        });
    }

    private void uploadSuccess(StorageReference ref){
        final String userID = prefs.getString("userID", "N/A");

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                photoRef = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                String img = String.valueOf(uri);
                Log.i("Img",img);
                photoRef.child("DpLink").setValue(img);
                prefs.edit()
                        .putString("DpLink",img)
                        .apply();
                if (bool == true)
                {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
//                Picasso.get().load(img).into(DP);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });



    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 71 && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                DP.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }
}
