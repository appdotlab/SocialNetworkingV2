package com.example.photouploader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UserProfile extends Fragment implements editProfileDialog.editProfileDialogListener {

    TextView usernameText, followersText, followingText,bioText;
    Button editProfileBtn;
    SharedPreferences prefs;
    DatabaseReference userRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        usernameText = (TextView) view.findViewById(R.id.usernameText);
        followersText = (TextView) view.findViewById(R.id.followersText);
        followingText = (TextView) view.findViewById(R.id.followingText);
        editProfileBtn = (Button) view.findViewById(R.id.editProfileBtn);
        bioText = (TextView) view.findViewById(R.id.bioText);
        prefs = view.getContext().getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        updateUI();

        editProfile();
        return view;
    }


    public void openDialog() {
        editProfileDialog editProfileDialog = new editProfileDialog();
        editProfileDialog.show(getActivity().getSupportFragmentManager(), "editProfile dialog");
    }

    @Override
    public void applyTexts(String name, String bio) {
        usernameText.setText(name);
        bioText.setText(bio);
    }
    public void editProfile()
    {
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openDialog();

            }
        });

    }
    private void updateUI(){
        String userID = prefs.getString("userID", "N/A");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = String.valueOf(dataSnapshot.child("name").getValue());
                int followersCount = 0, followingCount = 0;
                for(DataSnapshot userSnapshot : dataSnapshot.child("followers").getChildren()){
                    followersCount++;
                }
                for(DataSnapshot userSnapshot : dataSnapshot.child("following").getChildren()){
                    followingCount++;
                }
                usernameText.setText(String.valueOf(name));
                followersText.setText(String.valueOf(followersCount));
                followingText.setText(String.valueOf(followingCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
