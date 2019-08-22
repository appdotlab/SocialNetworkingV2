package com.example.photouploader;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference postRef;
    List<postModel> postList;
    SharedPreferences prefs;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        prefs = getActivity().getSharedPreferences("Prefs", Context.MODE_PRIVATE);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        postRef = database.getReference();
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList = new ArrayList<postModel>();
                String currentUserID = prefs.getString("userID","N/A");
                if (dataSnapshot.getValue() == null) {
                    Log.e("Get Data", "Not Working");
                }
                for (DataSnapshot postSnapshot : dataSnapshot.child("Posts").getChildren()) {
                    postModel model = new postModel();
                    String desc = postSnapshot.child("desc").getValue(String.class);
                    String img = postSnapshot.child("url").getValue(String.class);
                    String userID = postSnapshot.child("userID").getValue(String.class);
                    String postID = postSnapshot.getKey();
                    ArrayList<String> likes = new ArrayList<>();

                    for (DataSnapshot likeSnapshot : postSnapshot.child("likes").getChildren()) {
                        likes.add(likeSnapshot.getKey());
                    }

                    model.setPostID(postID);
                    model.setUserID(userID);
                    model.setDesc(desc);
                    model.setImg(img);
                    model.setLikes(likes);
                    model.setPostID(postSnapshot.getKey());
                    for (DataSnapshot userSnapshot : dataSnapshot.child("Users").child(currentUserID).child("following").getChildren()){
                        if(userID.compareTo(userSnapshot.getKey()) == 0){
                            postList.add(0,model);
                        }
                    }
                }
                String userID = prefs.getString("userID","N/A");
                Log.i("Count", "Count : " + postList.size());
                FragmentManager fragmentManager = getFragmentManager();
                recyclerView.setAdapter(new postsAdapter((postList), userID, getActivity(), fragmentManager));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Get Data", "Not Working");
            }
        });
        return view;

    }
}