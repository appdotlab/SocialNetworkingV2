package com.example.photouploader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

public class settingsActivity extends AppCompatActivity {

    ImageButton backButton;
    Button editProfileButton, logoutButton;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backButton = (ImageButton) findViewById(R.id.backButton);

        editProfileButton =(Button) findViewById(R.id.editProfileButton);
        logoutButton = (Button) findViewById(R.id.logoutButton);
        prefs = getSharedPreferences("Prefs", Context.MODE_PRIVATE);


        onClicks();

    }
    public void onClicks()
    {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(settingsActivity.this,profileInfoActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit()
                        .remove("userID")
                        .remove("name")
                        .apply();
                prefs.edit()
                        .clear();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(settingsActivity.this, loginActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
