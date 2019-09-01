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

class commentsAdapter extends RecyclerView.Adapter<commentsAdapter.RecyclerViewHolder> {

    ArrayList<commentModel> commentsList;
    DatabaseReference userRef;

    public commentsAdapter(ArrayList<commentModel> commentsList) {
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.comment_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position) {
        commentModel comment = commentsList.get(position);
        holder.commentText.setText(comment.getComment());
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getUserID()).child("name");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.commentUserName.setText(String.valueOf(dataSnapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Get Data", String.valueOf(databaseError));
            }
        });
        holder.commentUserName.setText(comment.getUserID());
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView commentUserName, commentText;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            commentUserName = (TextView) itemView.findViewById(R.id.commentUserName);
            commentText = (TextView) itemView.findViewById(R.id.commentText);

        }
    }
}
