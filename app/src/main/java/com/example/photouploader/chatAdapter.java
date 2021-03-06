package com.example.photouploader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatAdapter extends RecyclerView.Adapter<chatAdapter.RecyclerViewHolder>
{
    List<userModel> userList;
    Context context;
    String theLastMessage;
    SharedPreferences prefs;
    public String currID;
    public chatAdapter(List<userModel> userList, Context context) {
        this.userList = userList;
        this.context = context;

    }

    @NonNull
    @Override
    public chatAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.chatlistlayout, parent, false);
        prefs = context.getSharedPreferences("Prefs",context.MODE_PRIVATE);
        currID = prefs.getString("userID", "N/A");
        return new chatAdapter.RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull chatAdapter.RecyclerViewHolder holder, int position) {
        final userModel user = userList.get(position);
        holder.name.setText(user.getName());
        final String id = user.getUserID();
        final String name = user.getName();
        final String DP = user.getDpLink();
        lastMessage(user.getUserID(), holder.lastmsg);

        Picasso.get().load(DP).into(holder.dp);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(view.getContext(), messageActivity.class);

                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                i.putExtra("otherName", user.getName());
                i.putExtra("otherID", user.getUserID());
                i.putExtra("otherDP",user.getDpLink());
                i.putExtra("lastmessage",theLastMessage);
                context.startActivity(i);

            }
        });
    }
    public void lastMessage(final String userID, final TextView textView)
    {
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    messageModel messagemodel = snapshot.getValue(messageModel.class);
                    if(messagemodel.getReceiver().equals(currID) && messagemodel.getSender().equals(userID) ||  messagemodel.getReceiver().equals(userID) && messagemodel.getSender().equals(currID))
                    {
                        theLastMessage = messagemodel.getMessage();


                    }
                }

                switch (theLastMessage)
                {
                    case "default":
                        textView.setText("No Message");
                        break;

                    default:
                        textView.setText(theLastMessage);
                        break;
                }
                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }



    public class RecyclerViewHolder extends RecyclerView.ViewHolder
    {   CircleImageView dp;
        TextView name, lastmsg;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.userText);
            dp = (CircleImageView) itemView.findViewById(R.id.dp);
            lastmsg = (TextView) itemView.findViewById(R.id.lastMessage);

        }

    }
}
