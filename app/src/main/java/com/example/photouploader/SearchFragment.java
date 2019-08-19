package com.example.photouploader;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class SearchFragment extends Fragment {
    RecyclerView recyclerView;
    DatabaseReference userRef;
    List<userModel> userList;
    SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        prefs = getActivity().getSharedPreferences("Prefs", Context.MODE_PRIVATE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userRef = database.getReference().child("Users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList = new ArrayList<userModel>();
                if (dataSnapshot.getValue() == null) {
                    Log.e("Get Data", "Not Working");
                }
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    userModel user = new userModel();
                    String name = userSnapshot.child("name").getValue(String.class);
                    String age = String.valueOf(userSnapshot.child("age").getValue(int.class));
                    String userID = userSnapshot.getKey();
                    user.setName(name);
                    user.setAge(age);
                    user.setUserID(userID);
                    String currentUserID = prefs.getString("userID","N/A");
                    Log.i("User ID : " , currentUserID);
                    Log.i("user : " , userID);
                    if(userID.compareTo(currentUserID) != 0){
                        userList.add(user);
                    }
                }
                FragmentManager fragmentManager = getFragmentManager();
                recyclerView.setAdapter(new searchAdapter(userList, getActivity(), fragmentManager));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("Error","Error Retrieving Users");
            }
        });

        return view;
    }
}
