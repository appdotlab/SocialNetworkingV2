package com.example.photouploader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.FirebaseError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class messageActivity extends AppCompatActivity {
    SharedPreferences prefs;
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    DatabaseReference reference1, reference2;
    String otherID,otherName;
    List<messageModel> messageModelList;
    RecyclerView messageView;
//    DatabaseReference userRef, ref2;

    //    messageActivity()
//    {}

//    messageActivity(String otherID, String otherName)
//    {
//        this.otherID = otherID;
//        this.otherName = otherName;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        prefs = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        Intent intent = getIntent();
        final String currentUserID = prefs.getString("userID","N/A");
        final String currUserName = prefs.getString("name", "N/A");

        final String receiverID = intent.getStringExtra("otherID");

        messageAdapter recyclerAdapter = new messageAdapter(messageModelList, getApplicationContext());
        messageView = (RecyclerView) findViewById(R.id.messageView);
        messageView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //messageView.setAdapter(recyclerAdapter);
        messageView.setHasFixedSize(true);
        prefs.edit()
                .putString("recieverID",receiverID)
                .apply();

        final String recieverName = intent.getStringExtra("otherName");
        prefs.edit()
                .putString("recieverName",recieverName)
                .apply();

        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.editMessage);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference1 = database.getReference("Messages").child(currentUserID+receiverID);
//        Intent intent =new Intent() ;


        Log.i("Other ID", receiverID);

      sendButton.setOnClickListener(new View.OnClickListener()
      {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();
                if(!messageText.equals(""))
                {
//                    messageModel messageModel = new messageModel();
//
//                    messageModel.setSenderID(currentUserID);
//                    messageModel.setSenderName(currUserName);
//                    messageModel.setMesssage(messageText);
//                    messageModel.setRecieverName(recieverName);
//                    messageModel.setRecieverID(receiverID);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("Message", messageText);
                    Log.i("ms", messageText);
                    map.put("Sender", currUserName);
                    map.put("Receiver",recieverName);
                    Log.i("name", currUserName);
////                    map.put("user", UserDetails.username);
//                    reference1.child("Sender").setValue(messageModel.getSenderName());
//                    reference1.child("SenderID").setValue(messageModel.getSenderID());
//                    reference1.child("Reciever").setValue(messageModel.getRecieverName());
//                    reference1.child("RecieverID").setValue(messageModel.getRecieverID());
//                    reference1.child("Message").setValue(messageModel.getMesssage());
//                    messageModelList.add(messageModel);
                    reference1.push().setValue(map);
//                    reference2.push().setValue(map);

                    reference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            messageModelList = new ArrayList<messageModel>();

                            for (DataSnapshot snap : dataSnapshot.getChildren())
                            {
                                messageModel model = new messageModel();

                                String Sender = snap.child("Sender").getValue(String.class);

                                String Receiver = snap.child("Receiver").getValue(String.class);

                                String message = snap.child("Message").getValue(String.class);

                                model.setSenderName(Sender);
                                Log.i("Sending to adapter", Sender);
                                model.setRecieverName(Receiver);
                                Log.i("Sending to adapter", Receiver);
                                model.setMesssage(message);
                                Log.i("Sending to adapter", message);
                                messageArea.setText("");
                                messageModelList.add(model);

                                if (messageModelList.size() != 0 ) {
                                    Log.i("lmao", "0");

                                }
                            }
                            messageView.setAdapter(new messageAdapter(messageModelList, getApplicationContext()));

                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });






//                    reference2.child("Sender").setValue(messageModel.getSenderName());
//                    reference2.child("SenderID").setValue(messageModel.getSenderID());
//                    reference2.child("Reciever").setValue(messageModel.getRecieverName());
//                    reference2.child("RecieverID").setValue(messageModel.getRecieverID());
//                    reference2.child("Message").setValue(messageModel.getMesssage());
//
                }
            }
        });


    }
}

