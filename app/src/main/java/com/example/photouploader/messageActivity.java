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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class messageActivity extends AppCompatActivity {
    SharedPreferences prefs;

    TextView RecieverName;
    ImageButton sendButton,backButton;
    EditText messageArea;
    CircleImageView RecieverDP;

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
        final String dpLink = intent.getStringExtra("otherDP");
        RecieverName =(TextView) findViewById(R.id.userText);
        RecieverDP = (CircleImageView) findViewById(R.id.userDP);
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

        sendButton = (ImageButton) findViewById(R.id.sendButton);
        messageArea = (EditText) findViewById(R.id.editMessage);
        backButton = (ImageButton) findViewById(R.id.backButton);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        RecieverName.setText(recieverName);
        Picasso.get().load(dpLink).into(RecieverDP);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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

        reference = database.getReference("Users").child(currentUserID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                userModel user = new userModel();
                String userID = dataSnapshot.getKey();

                user.setUserID(userID);
                Log.i("wth", user.getUserID());
                Log.i("current", currentUserID);
                Log.i("receiver", receiverID);
                Log.i("Sent", "Works?");

                readMesagges(currentUserID,receiverID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
    }

    private void readMesagges(final String myid, final String userid) {
        messageModelList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageModelList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    messageModel messagemodel = snapshot.getValue(messageModel.class);
                    Log.i("MSGsss", "Receiver ID : " + messagemodel.getReceiver());
                    Log.i("MSGsss", "User ID : " + userid);
                    Log.i("MSGsss", "Sender ID : " + messagemodel.getSender());
                    Log.i("MSGsss", "My ID : " + myid);
                    if (((messagemodel.getSender().compareTo(myid) == 0) && (messagemodel.getReceiver().compareTo(userid) == 0)) || ((messagemodel.getSender().compareTo(userid) == 0) && (messagemodel.getReceiver().compareTo(myid) == 0)))
                    {
                        messageModelList.add(messagemodel);
                        Log.i("MSGsss", "setToAdapter");

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

