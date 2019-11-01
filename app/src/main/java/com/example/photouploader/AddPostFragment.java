package com.example.photouploader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
//import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

public class AddPostFragment extends Fragment {

    Bitmap croppedImageBitmap;

    ImageButton cancelButton, uploadBtn,chooseButton;
    ImageView imageView;
    CropImageView cropImageView;
    private Uri filePath, croppedImagePath;
    FirebaseStorage storage;
    StorageReference storageReference, ref2;
    DatabaseReference postRef;
    SharedPreferences prefs;
    Context context;
    TextView text;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        context = getContext();

        cancelButton = (ImageButton) view.findViewById(R.id.cancel_button);
        uploadBtn = (ImageButton) view.findViewById(R.id.upload_button);
        chooseButton = (ImageButton) view.findViewById(R.id.choose_button);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        cropImageView = (CropImageView) view.findViewById(R.id.cropImageView);
        text = (TextView) view.findViewById(R.id.textView2);

        imageView.setVisibility(view.GONE);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        ref2 = storage.getReference();
        prefs = view.getContext().getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        final String id = prefs.getString("userID", "N/A");

        choose();
        upload();
//        crop();
        cancel();
        return view;
    }
    public  void  cancel()
    {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setVisibility(View.GONE);
                text.setVisibility(View.VISIBLE);
                chooseButton.setVisibility(View.VISIBLE);


            }
        });
    }
    public void choose(){

        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                crop();
//
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 71);


            }
        });

    }
    public void upload(){
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(croppedImagePath != null)
                {
                    compressImage();

                    final ProgressDialog progressDialog = new ProgressDialog(view.getContext());
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();
                    final String id = prefs.getString("userID", "N/A");
                    final StorageReference ref = storageReference.child("images/"+id+"/"+ UUID.randomUUID().toString());
                    ref.putFile(croppedImagePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity().getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();

                                    uploadSuccess(ref);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity().getApplicationContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                            .getTotalByteCount());
                                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                                    imageView.setVisibility(View.GONE);
                                    text.setVisibility(View.VISIBLE);
                                    chooseButton.setVisibility(View.VISIBLE);

                                }
                            });

                }
                else
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Please select an Image to Upload", Toast.LENGTH_LONG).show();
                }


//                final String id = prefs.getString("userID", "N/A");
//                imgPath = ref2.child("images/"+id+"/"+ UUID.randomUUID().toString()).getDownloadUrl().getResult();
//                String img = String.valueOf(imgPath);
//                Log.i("img", img);
            }
        });
    }

    private void compressImage() {
        File imageFile = new File(croppedImagePath.getPath());
        try {
            File compressedImageFile = new Compressor(context).compressToFile(imageFile);
            Uri compressedImageURI = Uri.fromFile(compressedImageFile);
            croppedImagePath = compressedImageURI;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 71 && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                cropImageView.setImageBitmap(bitmap);
                croppedImageBitmap = cropImageView.getCroppedImage();
                croppedImagePath = getImageUri(context, croppedImageBitmap);
                cropImageView.setImageBitmap(croppedImageBitmap);


            }

            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                croppedImagePath = resultUri;
                text.setVisibility(View.GONE);
                chooseButton.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                Picasso.get().load(croppedImagePath).into(imageView);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadSuccess(StorageReference ref){
       final String userID = prefs.getString("userID", "N/A");

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'

                postRef = FirebaseDatabase.getInstance().getReference().child("Posts").push();
                postRef.child("userID").setValue(userID);
                String img = String.valueOf(uri);
                Log.i("Img",img);
                postRef.child("url").setValue(img);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    private void crop(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setFixAspectRatio(true)
                .setMaxCropResultSize(1080,1080)
                .setAspectRatio(1,1)
                .start(getContext(), this);
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        //ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}
