package com.example.photouploader;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    TextView name, age , followText, followingText;
    CircleImageView dp;
    userModel user;
    ImageButton followBtn, unfollowBtn;
    DatabaseReference followers, following, followers2, following2, currentUser, notiRef;
    SharedPreferences prefs;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        user = new userModel();
        user.setName(getArguments().getString("name"));
        user.setAge(getArguments().getString("age"));
        user.setUserID(getArguments().getString("userID"));
        user.setDpLink(getArguments().getString("DpLink"));
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        dp =  (CircleImageView) view.findViewById(R.id.img);
        name = (TextView) view.findViewById(R.id.name);
        age = (TextView) view.findViewById(R.id.age);
        followBtn = (ImageButton) view.findViewById(R.id.followBtn);
        unfollowBtn = (ImageButton) view.findViewById(R.id.unfollowBtn);
        followText = (TextView) view.findViewById(R.id.followText);
        followingText = (TextView) view.findViewById(R.id.followingText);

        prefs = getActivity().getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        setProfile();
        follow();
        unfollow();
        return view;
    }

    public void setProfile(){
        name.setText(user.name);
        age.setText(user.age);
        unfollowBtn.setVisibility(View.GONE);
        followingText.setVisibility(View.GONE);
        followText.setVisibility(View.VISIBLE);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        String currentUserID = prefs.getString("userID", "N/A");

        currentUser = database.getReference().child("Users").child(currentUserID).child("following");
        currentUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    if(user.getUserID().compareTo(userSnapshot.getKey()) == 0){
                        unfollowBtn.setVisibility(View.VISIBLE);
                        followBtn.setVisibility(View.GONE);
                        followingText.setVisibility(View.VISIBLE);
                        followText.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Error", "Error");
            }
        });

    }

    public void follow(){
        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAG", "Followed");
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                String currentUserID = prefs.getString("userID", "N/A");
                String currentUserName = prefs.getString("name", "N/A");
                String CurrUserDPLink = prefs.getString("DpLink","N/A") ;
                followers = database.getReference().child("Users").child(user.getUserID()).child("followers").child(currentUserID);
                followers.child("userID").setValue(currentUserID);
                followers.child("name").setValue(currentUserName);
                followers.child("DpLink").setValue(CurrUserDPLink);
                following = database.getReference().child("Users").child(currentUserID).child("following").child(user.getUserID());
                following.child("userID").setValue(user.getUserID());
                following.child("name").setValue(user.getName());
                following.child("DpLink").setValue(user.getDpLink());
                Toast.makeText(getActivity().getApplicationContext(), "Followed", Toast.LENGTH_SHORT).show();
                unfollowBtn.setVisibility(View.VISIBLE);
                followBtn.setVisibility(View.GONE);
                followingText.setVisibility(View.VISIBLE);
                followText.setVisibility(View.GONE);
                notiRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(user.getUserID()).push();
                notiRef.child("type").setValue("follow");
                notiRef.child("userID").setValue(currentUserID);
            }
        });
    }

    public void unfollow(){
        unfollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                String currentUserID = prefs.getString("userID", "N/A");
                followers2 = database.getReference().child("Users").child(user.getUserID()).child("followers").child(currentUserID);
                followers2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeValue();
                        Log.e("TAG", "Unfollowed");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                following2 = database.getReference().child("Users").child(currentUserID).child("following").child(user.getUserID());
                following2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Toast.makeText(getActivity(), "Unfollowed", Toast.LENGTH_SHORT).show();
                unfollowBtn.setVisibility(View.GONE);
                followBtn.setVisibility(View.VISIBLE);
                followingText.setVisibility(View.GONE);
                followText.setVisibility(View.VISIBLE);
            }
        });
    }

}
