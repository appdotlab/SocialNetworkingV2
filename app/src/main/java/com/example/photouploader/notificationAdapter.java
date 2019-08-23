package com.example.photouploader;

import android.content.Context;
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

import java.util.List;

public class notificationAdapter extends RecyclerView.Adapter<notificationAdapter.RecyclerViewHolder> {

    List<notificationModel> notiList;
    Context context;

    public notificationAdapter(List<notificationModel> notiList, Context context) {
        this.notiList = notiList;
        this.context = context;
    }

    @NonNull
    @Override
    public notificationAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.notification_item, parent, false);
        return new notificationAdapter.RecyclerViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final notificationAdapter.RecyclerViewHolder holder, int position) {
        notificationModel model = notiList.get(position);
        String userID = model.getUserID();
        final String type = model.getType();
        DatabaseReference userRef;
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = String.valueOf(dataSnapshot.child("name").getValue());
                if(type.compareTo("like") == 0){
                    holder.notiText.setText(username + " liked your post");
                }
                else{
                    holder.notiText.setText(username + " started following you");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Error", String.valueOf(databaseError));
            }
        });
    }


    @Override
    public int getItemCount() {
        return notiList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView notiText;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            notiText = (TextView) itemView.findViewById(R.id.notiText);
        }
    }

}
