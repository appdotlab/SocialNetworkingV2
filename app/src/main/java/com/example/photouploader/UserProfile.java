package com.example.photouploader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class UserProfile extends Fragment implements editProfileDialog.editProfileDialogListener {

    TextView usernameText, followersText, followingText,bioText;
    Button editProfileBtn, logoutBtn;
    SharedPreferences prefs;
    List<postModel> postModelLists;
    DatabaseReference userRef, postRef;
    RecyclerView postView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        usernameText = (TextView) view.findViewById(R.id.usernameText);
        followersText = (TextView) view.findViewById(R.id.followersText);
        followingText = (TextView) view.findViewById(R.id.followingText);
        editProfileBtn = (Button) view.findViewById(R.id.editProfileBtn);
        logoutBtn = (Button) view.findViewById(R.id.logoutBtn);
        bioText = (TextView) view.findViewById(R.id.bioText);
        postView = (RecyclerView) view.findViewById(R.id.postView);

        prefs = view.getContext().getSharedPreferences("Prefs", Context.MODE_PRIVATE);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        postView.setLayoutManager(gridLayoutManager);
        Log.i("Entered", "before");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                postModelLists = new ArrayList<postModel>();
                String currentUserID = prefs.getString("userID","N/A");
                if (dataSnapshot.getValue() == null) {
                    Log.i("Get Data", "Not Working");
                }
                for (DataSnapshot postSnapshot1 : dataSnapshot.getChildren()) {
                    postModel model = new postModel();
                          String desc = postSnapshot1.child("desc").getValue(String.class);
                    String img = postSnapshot1.child("url").getValue(String.class);
                    String userID = postSnapshot1.child("userID").getValue(String.class);
                    String postID = postSnapshot1.getKey();
                    ArrayList<String> likes = new ArrayList<>();

                    for (DataSnapshot likeSnapshot : postSnapshot1.child("likes").getChildren()) {
                        likes.add(likeSnapshot.getKey());
                    }

                    model.setPostID(postID);
                    model.setUserID(userID);
                    model.setDesc(desc);
                    model.setImg(img);
                    model.setLikes(likes);
                    model.setPostID(postSnapshot1.getKey());
                    if (userID.compareTo(currentUserID)==0)
                    {
                        postModelLists.add(0,model);
                    }
                }
                String userID = prefs.getString("userID","N/A");
                Log.i("Count", "Count : " + postModelLists.size());
                FragmentManager fm = getFragmentManager();
                postView.setAdapter(new postsAdapter((postModelLists), userID, getActivity(), fm));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateUI();
        logout();
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

    public void logout(){
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefs.edit()
                        .remove("userID")
                        .remove("name")
                        .apply();
                startActivity(new Intent(getContext(), loginActivity.class));
            }
        });
    }

}
