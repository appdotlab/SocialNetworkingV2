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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.PublicKey;
import java.util.List;
import java.util.Map;

public class messageAdapter extends RecyclerView.Adapter<messageAdapter.recyclerViewHolder>
{
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    List<messageModel> modelList;
    Context context;
    SharedPreferences prefs;
    public String currID, receiverID;


    public messageAdapter(List<messageModel> modelList, Context context) {
        this.modelList = modelList;
        this.context = context;
    }

    @NonNull
    @Override
    public messageAdapter.recyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        receiverID = prefs.getString("recieverID", "N/A");
        if (i == MSG_TYPE_RIGHT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_right_layout, parent, false);
            return new messageAdapter.recyclerViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_left_layout, parent, false);
            return new messageAdapter.recyclerViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull messageAdapter.recyclerViewHolder holder, final int i)
    {
       messageModel model = modelList.get(i);
       holder.show_message.setText(model.getMessage());
       if((modelList.get(i).getSender()).compareTo(currID) == 0 )
       {
           if (i == modelList.size() - 1)
           {
               if (model.isIsseen())
               {
                   holder.seenText.setText("Seen");
                   holder.seenText.setVisibility(View.VISIBLE);
               }
               else
               {
                   holder.seenText.setText("Delivered");
               }
           }
       }



    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class recyclerViewHolder extends RecyclerView.ViewHolder
    {
        public TextView show_message,seenText;

        public recyclerViewHolder(@NonNull View itemView)
        {
            super(itemView);
            show_message = (TextView) itemView.findViewById(R.id.show_message);
            seenText =(TextView) itemView.findViewById(R.id.seen_text);



        }
    }
    @Override
    public int getItemViewType(int position)
    {
        prefs = context.getSharedPreferences("Prefs",context.MODE_PRIVATE);

        currID = prefs.getString("userID", "N/A");
        if((modelList.get(position).getSender()).compareTo(currID) == 0 )
        {
            Log.i("User sending", "Message sent by user");
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }
    }
}
/*public class messageAdapter extends RecyclerView.Adapter<messageAdapter.recyclerViewHolder>
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


        return new messageAdapter.recyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull recyclerViewHolder holder, int position) {
        {

            messageModel msg = list.get(position);
            if (msg != null)
            {
                prefs = context.getSharedPreferences("Prefs", Context.MODE_PRIVATE);
                final String currUserName = prefs.getString("name", "N/A");

                String sender = msg.getSenderName();
                String receiver = msg.getRecieverName();
                if (sender.compareTo(currUserName)==0)
                {
                    addMessageBox(msg.messsage,2);


                }
                else
                {
                    addMessageBox(msg.messsage,1);

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
//        TextView textView = new TextView(context);
//        textView.setText(messageStr);
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


        }
    }
}*/
