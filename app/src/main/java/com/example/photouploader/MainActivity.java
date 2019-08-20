package com.example.photouploader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity { SharedPreferences prefs;

    private BottomNavigationView.OnNavigationItemSelectedListener navListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            Boolean islogout = false;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.navigation_newPost:
                    selectedFragment = new AddPostFragment();
                    break;
                case R.id.navigation_profile:
                    Log.w("Clicked","worked");
                    selectedFragment = new UserProfile();
                    break;
                case R.id.navigation_temp:
                    selectedFragment = new SearchFragment();
                    islogout = true;
                    break;
                case R.id.navigation_Search:
                    selectedFragment = new SearchFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragments_container, selectedFragment).commit();
            if(islogout == true) {
                logout();
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);

        String CU = getIntent().getStringExtra("CU");
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navListener);
        prefs = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragments_container, new HomeFragment()).commit();

    }

    public void logout(){
        prefs.edit()
                .remove("userID")
                .remove("name")
                .apply();
        startActivity(new Intent(getApplicationContext(), loginActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i("Back", "Back Pressed");
        getFragmentManager().popBackStackImmediate();
    }
}
