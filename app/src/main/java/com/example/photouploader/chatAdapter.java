package com.example.photouploader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class chatAdapter extends RecyclerView.Adapter<chatAdapter.RecyclerViewHolder> {

    List<userModel> userList;
    Context context;
    final int FLAG_ACTIVITY_NEW_TASK = 0;

    public chatAdapter(List<userModel> userList, Context context) {
        this.userList = userList;
        this.context = context;
        Log.i("lol", "entered cons");

    }

    @NonNull
    @Override
    public chatAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.chatlistlayout, parent, false);
        Log.i("lol", "entered oncreate");

        return new chatAdapter.RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull chatAdapter.RecyclerViewHolder holder, int position) {
        final userModel user = userList.get(position);
        Log.i("User Count : " , String.valueOf(userList.size()));
        Log.i("lol", "entered");
        holder.name.setText(user.getName());
        final String id = user.getUserID();
        final String name = user.getName();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                i = new Intent(view.getContext(), messageActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("otherName", name);
                i.putExtra("otherID", id);

                context.startActivity(i);



            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder
    {   TextView name;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.userText);

        }

    }
}
