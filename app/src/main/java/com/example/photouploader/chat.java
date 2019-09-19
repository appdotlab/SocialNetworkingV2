package com.example.photouploader;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class chat extends AppCompatActivity {

    public Context context;
    RecyclerView chatlist;
    DatabaseReference userRef;
    List<userModel> userList;
    SharedPreferences prefs;
    ImageButton back2home;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        chatlist = (RecyclerView) findViewById(R.id.chatView);
        chatlist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        back2home = (ImageButton) findViewById(R.id.backButton);

        prefs = getSharedPreferences("Prefs", Context.MODE_PRIVATE);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        back2home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
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
                    String userID = userSnapshot.getKey();
                    String DpLink = userSnapshot.child("DpLink").getValue(String.class);
                    user.setName(name);
                    user.setUserID(userID);
                    user.setDpLink(DpLink);
                    String currentUserID = prefs.getString("userID","N/A");
                    Log.i("User ID : " , currentUserID);
                    Log.i("user : " , userID);
                    if(userID.compareTo(currentUserID) != 0){
                        userList.add(user);

                    }
                }
                chatlist.setAdapter(new chatAdapter(userList,getApplicationContext()));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("Error","Error Retrieving Users");
            }
        });
    }
}

/*
public static Context context;
    RecyclerView chatlist;
    DatabaseReference userRef;
    List<userModel> userList;
    SharedPreferences prefs;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        context = this;
        chatlist = (RecyclerView) findViewById(R.id.chatView);
        chatlist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        prefs = getSharedPreferences("Prefs", Context.MODE_PRIVATE);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");


    }

    @Override
    protected void onStart() {
        super.onStart();
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
                        Log.i("lol", "entered task in chat");

                    }
                }
                chatlist.setAdapter(new chatAdapter(userList,getApplicationContext() ));

                Log.i("lol", "entered after setting adapter");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("Error","Error Retrieving Users");
            }
        });
    }
 */