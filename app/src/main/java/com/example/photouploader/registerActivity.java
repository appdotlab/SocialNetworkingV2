package com.example.photouploader;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

public class registerActivity extends AppCompatActivity
{
    String token = "89042839257-01213s3kkgb417pmgm2lei0lregivc2q.apps.googleusercontent.com";
    int RC_SIGN_IN = 0;
    GoogleSignInClient mGoogleSignInClient;
    MaterialEditText email, pass1, pass2,username;
    Button regButton, back, googleRegBtn;
    FirebaseAuth auth;
    List<userModel> names;
    DatabaseReference mDatabase, userRef,myref;
    SharedPreferences sharedpreferences;
    FirebaseAuth mAuth;
    boolean regFail = false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        email = (MaterialEditText) findViewById(R.id.emailField);
        pass1 = (MaterialEditText) findViewById(R.id.PasswordField);
        pass2 = (MaterialEditText) findViewById(R.id.repeatPasswordField);
        username = (MaterialEditText) findViewById(R.id.usernameField);
        googleRegBtn = (Button) findViewById(R.id.googleRegBtn);
        back = (Button) findViewById(R.id.backButton);
        regButton = (Button) findViewById(R.id.registerButton);
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Google Sign In Settings
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(token)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(),gso);
        google();

        onclick();
        back2login();

    }

    public void google(){
        googleRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAGsss", "firebaseAuthWithGoogle:" + acct.getId());

        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FirebaseUser user = mAuth.getCurrentUser();
        final String email = user.getEmail();
        userRef = mDatabase.child("Users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean userExists = false;
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    if(email.compareTo((String) userSnapshot.child("email").getValue()) == 0){
                        Toast.makeText(registerActivity.this, "Account Already Exists", Toast.LENGTH_SHORT).show();
                        userExists = true;
                        break;
                    }
                }

                if(userExists == false){
                    mAuthLogin(credential);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void mAuthLogin(AuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d("TAG", "signInWithCredential:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            userRef = mDatabase.child("Users").child(user.getUid());
                            userRef.child("age").setValue(50);
                            userRef.child("name").setValue(user.getDisplayName());
                            userRef.child("email").setValue(user.getEmail());
                            SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
                            prefs.edit()
                                    .putString("name", user.getDisplayName())
                                    .putString("userID", user.getUid())
                                    .apply();
                            Intent i = new Intent(registerActivity.this,loginActivity.class);
                            startActivity(i);
                            finish();
                        } else {

                            Log.w("TAG", "signInWithCredential:failure", task.getException());

                            Toast.makeText(registerActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void back2login()
    {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),loginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    public void onclick()
    {
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                myref = mDatabase.child("Users");
                myref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Boolean isAccExisting = false;
                        String txt_email = email.getText().toString();
                        String txt_pass1 = pass1.getText().toString();
                        String txt_pass2 = pass2.getText().toString();
                        final String txt_username = username.getText().toString();

                        if (dataSnapshot.getValue() == null) {
                            Toast.makeText(registerActivity.this, "Data is null", Toast.LENGTH_LONG).show();
                        }
                        for (DataSnapshot nameSnapshot : dataSnapshot.getChildren())
                        {
                            String name = nameSnapshot.child("name").getValue(String.class);
                            Log.e("User", "Username : " + txt_username + " User : " + name);
                            if(name.compareTo(txt_username) == 0){
                                Log.i("LOG", "User Exists");
                                isAccExisting = true;
                                break;
                            }
                        }
                        if(TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_pass1) || TextUtils.isEmpty(txt_pass2))
                        {
                            Toast.makeText(registerActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                        }
                        else if(!(isValidEmail(txt_email)))
                        {
                            Toast.makeText(registerActivity.this, "Enter valid Email address", Toast.LENGTH_SHORT).show();
                        }
                        else if(txt_pass1.length() < 6)
                        {
                            Toast.makeText(registerActivity.this, "Password must contain at least 6 characters", Toast.LENGTH_SHORT).show();
                        }
                        else if(!txt_pass1.equals(txt_pass2))
                        {
                            Toast.makeText(registerActivity.this, "Passwords must be the same", Toast.LENGTH_SHORT).show();
                        }
                        else if (isAccExisting == true)
                        {
                            Toast.makeText(registerActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            register(txt_email, txt_pass1, txt_username);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        });
    }
    public void register(final String email, String password, final String username)
    {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    assert firebaseUser != null;
                    String userid = firebaseUser.getUid();
                    userRef = mDatabase.child("Users").child(userid);
                    userRef.child("age").setValue(50);
                    userRef.child("name").setValue(username);
                    userRef.child("email").setValue(email);
                    Toast.makeText(registerActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
                    prefs.edit()
                            .putString("name", username)
                            .putString("userID", userid)
                            .apply();
                    Intent i = new Intent(registerActivity.this,loginActivity.class);
                    startActivity(i);
                    finish();

                }
                else
                {
                    Toast.makeText(registerActivity.this, "You can't register with email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void regFailed(int bool)
    {
        if (bool==1)
        {
            //Log.i("ERROR" , "user exists");
        }
        int number = bool;
    }


}
