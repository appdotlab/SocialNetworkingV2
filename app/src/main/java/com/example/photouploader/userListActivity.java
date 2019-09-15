package com.example.photouploader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class userListActivity extends AppCompatActivity {
    boolean lol;
    RecyclerView users;
    SharedPreferences prefs;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference,reference1;
    List<userModel> userModelList;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Intent intent = getIntent();
        lol = intent.getBooleanExtra("bool",false);

        users =(RecyclerView) findViewById(R.id.users);
        users.setLayoutManager(new LinearLayoutManager(this));


        displayUsers();

    }
    void displayUsers()
    {
        prefs = getSharedPreferences("Prefs",MODE_PRIVATE);
        final String UID = prefs.getString("userID","N/A");
        reference = firebaseDatabase.getInstance().getReference("Users").child(UID).child("following") ;
        reference1 = firebaseDatabase.getInstance().getReference("Users").child(UID).child("followers") ;

        userModelList = new ArrayList<userModel>();

        if (lol==true)
        {
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {


                    for (DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        userModel user = new userModel();
                        String followingID = snapshot.child("userID").getValue(String.class);
                        user.setUserID(followingID);
                        Log.i("DUDE",followingID);
                        String followingName = snapshot.child("name").getValue(String.class);
                        user.setName((followingName));
                        Log.i("Following",followingName);

                        String DpLink = snapshot.child("DpLink").getValue(String.class);
                        user.setDpLink(DpLink);
                        userModelList.add(user);
//
                    }
                    users.setAdapter(new userAdapter(userModelList,getApplicationContext()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else
        {
            reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        userModel user = new userModel();

                        String followerID = snapshot.getKey();
                        user.setUserID(followerID);
                        Log.i("DUDE",followerID);
                        String followerName = snapshot.child("name").getValue(String.class);
                        user.setName((followerName));
                        String DpLink = snapshot.child("DpLink").getValue(String.class);
                        user.setDpLink(DpLink);
                        userModelList.add(user);
                    }
                    users.setAdapter(new userAdapter(userModelList,getApplicationContext()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }
}
