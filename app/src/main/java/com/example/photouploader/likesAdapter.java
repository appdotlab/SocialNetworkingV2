package com.example.photouploader;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class likesAdapter extends RecyclerView.Adapter<likesAdapter.RecyclerViewHolder> {
    ArrayList<String> likes;
    String postID;

    public likesAdapter(ArrayList<String> likes, String postID) {
        this.likes = likes;
        this.postID = postID;
    }

    @NonNull
    @Override
    public likesAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.likes_item, parent, false);
        return new likesAdapter.RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final likesAdapter.RecyclerViewHolder holder, int position) {
        Log.i("likes", String.valueOf(likes.size()));
        String userID = likes.get(position);
        DatabaseReference ref;
        ref = FirebaseDatabase.getInstance().getReference().child("Posts").child(postID).child("likes").child(userID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = (String) dataSnapshot.getValue();
                holder.name.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return likes.size();
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.likerName);
        }
    }
}
