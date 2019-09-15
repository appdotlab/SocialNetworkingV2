package com.example.photouploader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class userAdapter extends RecyclerView.Adapter<userAdapter.RecyclerViewHolder>
{
    List<userModel> userModelList;
    Context context;





    public userAdapter(List<userModel> userModelList, Context context) {
        this.userModelList = userModelList;
        this.context = context;

    }

    @NonNull
    @Override
    public userAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_item, parent, false);

        return new userAdapter.RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull userAdapter.RecyclerViewHolder holder, int position)
    {
        final userModel list = userModelList.get(position);


        String userID = list.getUserID();
        String name = list.getName();
        String DpLink = list.getDpLink();

        Log.i("DET",name);
        Log.i("DET",DpLink);

        holder.nameText.setText(name);
        Picasso.get().load(DpLink).into(holder.Dp);

    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView nameText;
        CircleImageView Dp;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = (TextView) itemView.findViewById(R.id.name);
            Dp = (CircleImageView) itemView.findViewById(R.id.DP);
        }
    }
}
