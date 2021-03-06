package com.example.photouploader;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class postsAdapter extends RecyclerView.Adapter<postsAdapter.RecyclerViewHolder> {

    List<postModel> list;
    Context context;
    String currentUserID;
    SharedPreferences prefs;
    DatabaseReference postRef,notiRef, userRef;
    FragmentManager fragmentManager;
    Boolean isCurrentUserProfile;

    public postsAdapter(List<postModel> modelList, String currentUserID, Context context, FragmentManager fragmentManager, Boolean isCurrentUserProfile)
    {
        this.list = modelList;
        this.context = context;
        this.currentUserID = currentUserID;
        this.fragmentManager = fragmentManager;
        this.isCurrentUserProfile = isCurrentUserProfile;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.post_item, parent, false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position) {
        final postModel myList = list.get(position);
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        prefs = context.getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        final String currentUserID = prefs.getString("userID", "NULL");
        final String currentUserName = prefs.getString("name", "NULL");
        if(myList.getDesc() != null){
            holder.postDescView.setText(myList.getDesc());
        }
        else{
            holder.postDescView.setVisibility(View.GONE);
        }
        Picasso.get().load(myList.getImg()).into(holder.postImgView);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(myList.getUserID()).child("name");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = String.valueOf(dataSnapshot.getValue());
                holder.postAuthorNameText.setText("@" + name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Get Data", String.valueOf(databaseError));
            }
        });

        if(isCurrentUserProfile){
            holder.postLikeBtn.setVisibility(View.GONE);
            holder.postUnlikeBtn.setVisibility(View.GONE);
            holder.postLikesText.setVisibility(View.GONE);
            holder.commentsButton.setVisibility(View.GONE);
            holder.postDescView.setVisibility(View.GONE);
            holder.postAuthorNameText.setVisibility(View.GONE);
            holder.rl2.setVisibility(View.GONE);
            postIntent(myList.getPostID(), holder);
        }
        else{
            postRef.child(myList.getPostID()).child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int likes = 0;
                    String likeText;
                    for(DataSnapshot likeSnapshot : dataSnapshot.getChildren()){
                        if(currentUserID.compareTo(likeSnapshot.getKey()) == 0){
                            holder.postUnlikeBtn.setVisibility(View.VISIBLE);
                            holder.postLikeBtn.setVisibility(View.GONE);
                        }
                        likes++;
                    }
                    likeText = likes == 1 ? "1 like" : likes + " likes";
                    holder.postLikesText.setText(likeText);
                    if(likes > 0){
                        holder.postLikesText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Fragment fragment = new LikesFragment();
                                Bundle data = new Bundle();
                                data.putStringArrayList("likes", myList.getLikes());
                                data.putString("postID", myList.getPostID());
                                fragment.setArguments(data);
                                fragmentManager.beginTransaction()
                                        .replace(R.id.fragments_container, fragment)
                                        .addToBackStack("tag")
                                        .commit();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Error", String.valueOf(databaseError));
                }
            });
            holder.postUnlikeBtn.setVisibility(View.GONE);
            holder.postLikeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    postRef.child(myList.getPostID()).child("likes").child(currentUserID).setValue(currentUserName);
                    holder.postLikeBtn.setVisibility(View.GONE);
                    holder.postUnlikeBtn.setVisibility(View.VISIBLE);
                    updateLike(holder, myList.getPostID());

                    notiRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(myList.getUserID());
                    notiRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Boolean exists = false;
                            for(DataSnapshot notiSnapshot : dataSnapshot.getChildren()){
                                String userID = String.valueOf(notiSnapshot.child("userID").getValue());
                                String postID = String.valueOf(notiSnapshot.child("postID").getValue());
                                if(userID.compareTo(currentUserID) == 0 && postID.compareTo(myList.getPostID()) == 0){
                                    Log.i("MSG","Notification Cancelled");
                                    exists = true;
                                }
                            }
                            if(exists == false) {
                                Log.i("MSG","Notified");
                                notiRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(myList.getUserID()).push();
                                notiRef.child("type").setValue("like");
                                notiRef.child("userID").setValue(currentUserID);
                                notiRef.child("postID").setValue(myList.getPostID());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("Error", String.valueOf(databaseError));
                        }
                    });

                }
            });
            holder.postUnlikeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    postRef.child(myList.getPostID()).child("likes").child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataSnapshot.getRef().removeValue();
                            holder.postUnlikeBtn.setVisibility(View.GONE);
                            holder.postLikeBtn.setVisibility(View.VISIBLE);
                            updateLike(holder, myList.getPostID());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("Error", String.valueOf(databaseError));
                        }
                    });
                }
            });

            holder.commentsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new CommentsFragment();
                    Bundle data = new Bundle();
                    data.putString("postID",myList.getPostID());
                    fragment.setArguments(data);
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragments_container, fragment)
                            .commit();
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        int arr = 0;

        try{
            if(list.size()==0){

                arr = 0;

            }
            else{

                arr=list.size();
            }



        }catch (Exception e)
         {

        }

        return arr;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{

        TextView postDescView, postLikesText, postAuthorNameText;
        ImageView postImgView;
        RelativeLayout rl,rl2;
        ImageButton postLikeBtn, postUnlikeBtn, commentsButton;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            rl = (RelativeLayout) itemView.findViewById(R.id.rl);
            rl2 = (RelativeLayout) itemView.findViewById(R.id.rl2);
            postDescView = (TextView) itemView.findViewById(R.id.postDescText);
            postAuthorNameText = (TextView) itemView.findViewById(R.id.postAuthorNameText);
            commentsButton = (ImageButton) itemView.findViewById(R.id.commentsButton);
            postImgView = (ImageView) itemView.findViewById(R.id.postImg);
            postLikeBtn = (ImageButton) itemView.findViewById(R.id.likeBtn);
            postUnlikeBtn = (ImageButton) itemView.findViewById(R.id.unlikeBtn);
            postLikesText = (TextView) itemView.findViewById(R.id.likesText);
        }
    }

    public void updateLike(final RecyclerViewHolder holder, String postID){
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        postRef.child(postID).child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int likes = 0;
                for(DataSnapshot likeSnapshot : dataSnapshot.getChildren()){
                    likes++;
                }
                String likeText = likes == 1 ? "1 like" : likes + " likes";
                holder.postLikesText.setText(likeText);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void postIntent(final String postID, RecyclerViewHolder holder){
        holder.postImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new HomeFragment(postID);
                /*
                Bundle data = new Bundle();
                data.putString("postID",myList.getPostID());
                fragment.setArguments(data);
                */
                fragmentManager.beginTransaction()
                        .replace(R.id.fragments_container, fragment)
                        .commit();
            }
        });
    }

}
