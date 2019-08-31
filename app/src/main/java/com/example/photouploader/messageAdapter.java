package com.example.photouploader;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Map;

public class messageAdapter extends RecyclerView.Adapter<messageAdapter.recyclerViewHolder>
{
    List<messageModel> list;
    Context context;
    SharedPreferences prefs;
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    DatabaseReference reference1, reference2;
    String otherID, otherName;
    List<messageModel> messageModelList;
    TextView message;

    public messageAdapter(List<messageModel> list1, Context context) {
        this.list = list1;
        this.context = context;
    }

    @NonNull
    @Override
    public recyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(context).inflate(R.layout.messagetext, parent,false);
//
//
//        final String currentUserID = prefs.getString("userID", "N/A");
//        final String receiverID = prefs.getString("recieverID", "N/A");
//        final String currUserName = prefs.getString("name", "N/A");
//        final String receiverName = prefs.getString("recieverName", "N/A");
           Log.i("Data", "lol");
        if (list.size() != 0 ) {
            Log.i("lmao1", "1");

        }
        return new messageAdapter.recyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull recyclerViewHolder holder, int position) {
        {
            messageModel msg = list.get(position);
            if (msg != null)
            {
                String sender = msg.getSenderID();
                if (sender != null)
                {
                    Log.i("Data", null);

                }
                String receiver = msg.getRecieverID();
                Log.i("Data", receiver);
                if (sender.compareTo(receiver)==0)
                {
                    addMessageBox(msg.messsage,2);
                    Log.i("Data", "called");

                }
                else
                {
                    addMessageBox(msg.messsage,1);
                    Log.i("Data", "called2");

                }
            }




//        reference1.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
//                String message = map.get("Message").toString();
//                String userName = map.get("Sender").toString();
//
//
//
//                if(userName.equals(currUserName)){
//                    addMessageBox("You:-\n" + message, 1);
//                }
//                else{
//                    addMessageBox(recieverName + ":-\n" + message, 2);
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//
//    }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void addMessageBox(String messageStr, int type) {

        message.setText(messageStr);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if (type == 1) {
            lp2.gravity = Gravity.LEFT;
            // textView.setBackgroundResource(R.drawable.bubble_in);
        } else {
            lp2.gravity = Gravity.RIGHT;
//            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        //textView.setLayoutParams(lp2);
        message.setLayoutParams(lp2);

    }
    public class recyclerViewHolder extends RecyclerView.ViewHolder
    {
        public recyclerViewHolder(View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.messageText);
            Log.i("Data", "lmao");


        }
    }
}
