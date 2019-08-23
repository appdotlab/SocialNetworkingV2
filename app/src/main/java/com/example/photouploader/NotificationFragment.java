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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    RecyclerView recyclerView;
    SharedPreferences prefs;
    DatabaseReference notiRef;
    String currentUserID;
    List<notificationModel> notiList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        prefs = getActivity().getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        currentUserID = prefs.getString("userID", "N/A");
        notiRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(currentUserID);
        notiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notiList = new ArrayList<notificationModel>();
                for(DataSnapshot notiSnapshot : dataSnapshot.getChildren()){
                    notificationModel model = new notificationModel();
                    model.setPostID(notiSnapshot.getKey());
                    model.setType(String.valueOf(notiSnapshot.child("type").getValue()));
                    model.setUserID(String.valueOf(notiSnapshot.child("userID").getValue()));
                    notiList.add(0, model);
                }
                recyclerView.setAdapter(new notificationAdapter(notiList, getActivity()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Error", String.valueOf(databaseError));
            }
        });
        return view;
    }
}
