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
import android.widget.Toast;

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

    ImageView sendButton;
    EditText messageArea;

    FirebaseDatabase database;
    DatabaseReference reference;

    List<messageModel> messageModelList;
    RecyclerView messageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent intent = getIntent();

        prefs = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        final String currentUserID = prefs.getString("userID", "N/A");
        final String currUserName = prefs.getString("name", "N/A");
        final String receiverID = intent.getStringExtra("otherID");
        final String recieverName = intent.getStringExtra("otherName");


        messageAdapter recyclerAdapter = new messageAdapter(messageModelList, getApplicationContext());
        messageView = (RecyclerView) findViewById(R.id.messageView);
        messageView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        messageView.setHasFixedSize(true);


        prefs.edit()
                .putString("recieverID", receiverID)
                .apply();

        prefs.edit()
                .putString("recieverName", recieverName)
                .apply();

        sendButton = (ImageView) findViewById(R.id.sendButton);
        messageArea = (EditText) findViewById(R.id.editMessage);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();
                if (!messageText.equals("")) {

                    sendMessage(currentUserID, receiverID, messageText);
                } else {
                    Toast.makeText(messageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                messageArea.setText("");
            }
        });




    }

    public void sendMessage(final String currID, final String ReceiverID, String msgText) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", currID);
        hashMap.put("receiver", ReceiverID);
        hashMap.put("message", msgText);
        hashMap.put("isseen", false);
        reference.child("Chats").push().setValue(hashMap);
        Log.i("curr", currID);
        readMesagges(currID, ReceiverID);
    }

    private void readMesagges(final String myid, final String userid) {
        messageModelList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageModelList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    messageModel model = new messageModel();
                    messageModel messagemodel = snapshot.getValue(messageModel.class);
                    String rID = snapshot.child("receiver").getValue(String.class);
                    Log.i("lol", rID);
                    String sID = snapshot.child("sender").getValue(String.class);
                    Log.i("lol sender",sID);

                    if (sID == myid && rID==(userid) || sID==(userid) && rID==(myid)) {
                        messageModelList.add(messagemodel);
                        Log.i("wow", "noice");
                        Log.i("wow", rID);
                        Log.i("wow", sID);
                    }

                    messageAdapter adapter = new messageAdapter(messageModelList, messageActivity.this);
                    messageView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

