package com.example.photouploader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    TextView name, followText, followingButtonText;
    Button editProfileBtn, logoutBtn;
    TextView usernameText, followersText, followingText,bioText;

    CircleImageView dp;
    userModel user;
    ImageButton followBtn, unfollowBtn;
    DatabaseReference followers, following, followers2, following2, currentUser, notiRef;
    SharedPreferences prefs;
    List<postModel> postModelLists;
    DatabaseReference userRef, postRef;
    RecyclerView postView;
    LinearLayout followingLayout,followersLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        user = new userModel();
        user.setName(getArguments().getString("name"));
        user.setAge(getArguments().getString("age"));
        user.setUserID(getArguments().getString("userID"));
        user.setDpLink(getArguments().getString("DpLink"));
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        dp =  (CircleImageView) view.findViewById(R.id.dp);
        name = (TextView) view.findViewById(R.id.usernameText);
//        age = (TextView) view.findViewById(R.id.age);
        followBtn = (ImageButton) view.findViewById(R.id.followBtn);
        unfollowBtn = (ImageButton) view.findViewById(R.id.unfollowBtn);
        followText = (TextView) view.findViewById(R.id.followText);
        followingButtonText = (TextView) view.findViewById(R.id.followingButtonText);



        followersText = (TextView) view.findViewById(R.id.followersText);
        followingText = (TextView) view.findViewById(R.id.followingText);
        editProfileBtn = (Button) view.findViewById(R.id.editProfileBtn);
        logoutBtn = (Button) view.findViewById(R.id.logout);
        followersLayout = (LinearLayout) view.findViewById(R.id.followersLayout);
        followingLayout = (LinearLayout) view.findViewById(R.id.followingLayout);

        postView = (RecyclerView) view.findViewById(R.id.postView);
        prefs = view.getContext().getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        String img = prefs.getString("img","N/A");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        postView.hasNestedScrollingParent();
        postView.setLayoutManager(gridLayoutManager);
        Log.i("Entered", "before");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                postModelLists = new ArrayList<postModel>();
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
                    if (userID.compareTo(user.getUserID())==0)
                    {
                        postModelLists.add(0,model);
                    }
                }
                String userID = prefs.getString("userID","N/A");
                Log.i("Count", "Count : " + postModelLists.size());
                FragmentManager FM = getFragmentManager();
                postView.setAdapter(new postsAdapter((postModelLists), userID, getActivity(), FM, true));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        followersLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listClick(false);
//            }
//        });

//        followingLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listClick(true);
//            }
//        });
        updateUI();


        prefs = getActivity().getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        setProfile();
        follow();
        unfollow();
        return view;
    }

    public void setProfile(){
        name.setText(user.name);
//        age.setText(user.age);
        Picasso.get().load(user.getDpLink()).into(dp);
        unfollowBtn.setVisibility(View.GONE);
        followingButtonText.setVisibility(View.GONE);
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
                        followingButtonText.setVisibility(View.VISIBLE);
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
    private void updateUI(){
        String userID = prefs.getString("userID", "N/A");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUserID());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = String.valueOf(dataSnapshot.child("name").getValue());
                String img = String.valueOf(dataSnapshot.child("DpLink").getValue());
                String bio = String.valueOf(dataSnapshot.child("Bio").getValue());
                int followersCount = 0, followingCount = 0;
                for(DataSnapshot userSnapshot : dataSnapshot.child("followers").getChildren()){
                    followersCount++;
                }
                for(DataSnapshot userSnapshot : dataSnapshot.child("following").getChildren()){
                    followingCount++;
                }

                followersText.setText(String.valueOf(followersCount));
                followingText.setText(String.valueOf(followingCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                followingButtonText.setVisibility(View.VISIBLE);
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
                followingButtonText.setVisibility(View.GONE);
                followText.setVisibility(View.VISIBLE);
            }
        });
    }
    void listClick(boolean bool)
    {
        if (bool == true)
        {
            Intent i = new Intent(getActivity().getApplicationContext(), userListActivity.class);
            i.putExtra("bool",true);
            Log.i("BOOL",String.valueOf(bool));
            startActivity(i);
        }
        else
        {
            Intent i = new Intent(getActivity().getApplicationContext(), userListActivity.class);
            i.putExtra("bool",false);
            Log.i("BOOL",String.valueOf(bool));
            startActivity(i);
        }
    }


}
