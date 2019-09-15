package com.example.photouploader;

import android.content.Context;
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

public class searchAdapter extends RecyclerView.Adapter<searchAdapter.RecyclerViewHolder> {
    List<userModel> userList;
    Context context;
    FragmentManager fragmentManager;

    public searchAdapter(List<userModel> userList, Context context, FragmentManager fragmentManager) {
        this.userList = userList;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public searchAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_item, parent, false);

        return new searchAdapter.RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        final userModel user = userList.get(position);
        Log.i("User Count : " , String.valueOf(userList.size()));
        holder.name.setText(user.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ProfileFragment();
                Bundle data = new Bundle();
                data.putString("name",user.getName());
                data.putString("age",user.getAge());
                data.putString("userID",user.getUserID());
                data.putString("DpLink",user.getDpLink());
                fragment.setArguments(data);
                fragmentManager.beginTransaction()
                        .replace(R.id.fragments_container, fragment)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        TextView name;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);

        }
    }
}
