package com.example.photouploader;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

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

public class CommentsFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference postRef;
    ArrayList<commentModel> commentsList;
    EditText commentEditText;
    Button postBtn;
    SharedPreferences prefs;
    String currentUserID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        postBtn = (Button) view.findViewById(R.id.postBtn);
        commentEditText = (EditText) view.findViewById(R.id.commentEditText);
        prefs = getActivity().getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        currentUserID = prefs.getString("userID", "nil");

        final String postID = getArguments().getString("postID");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postID).child("comments");
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("comment", "Comments Exist for Post : " + postID);
                commentsList = new ArrayList<>();
                for(DataSnapshot commentSnapshot : dataSnapshot.getChildren()){
                    commentModel comment = new commentModel();
                    comment.setUserID(String.valueOf(commentSnapshot.child("userID").getValue()));
                    comment.setComment(String.valueOf(commentSnapshot.child("comment").getValue()));
                    Log.i("comment",String.valueOf(commentSnapshot.child("comment").getValue()));
                    commentsList.add(comment);
                }
                recyclerView.setAdapter(new commentsAdapter(commentsList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Get Data", String.valueOf(databaseError));
            }
        });
        postComment();

        return view;
    }

    private void postComment(){
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = commentEditText.getText().toString();
                postRef = postRef.push();
                if(comment != null){
                    postRef.child("comment").setValue(comment);
                    postRef.child("userID").setValue(currentUserID);
                }
            }
        });
    }
}
