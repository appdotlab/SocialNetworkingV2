package com.example.photouploader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.dynamic.IFragmentWrapper;
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

public class loginActivity extends AppCompatActivity
{

    //Declaration
    String token = "89042839257-01213s3kkgb417pmgm2lei0lregivc2q.apps.googleusercontent.com";
    int RC_SIGN_IN = 0;
    MaterialEditText editEmail, editPassword;
    Button loginButton, registerButton ;
    ImageButton googleauthButton;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    DatabaseReference mDatabase, currentUser, userRef;
    SharedPreferences prefs;
    boolean newUser;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //Initialize text fields

        editEmail = (MaterialEditText) findViewById(R.id.editEmail);
        editPassword = (MaterialEditText) findViewById(R.id.editPassword);

        //Initialize Buttons

        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.regIntent);
        googleauthButton = (ImageButton) findViewById(R.id.googleAuth);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize Firebase Auth

        mAuth = FirebaseAuth.getInstance();
        prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        String userID = prefs.getString("userID","nil");
        newUser = prefs.getBoolean("firstSignIn",false);

        if(userID != "nil" )
        {
            if (newUser== false) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
            else
            {
                Intent i = new Intent(loginActivity.this,profileInfoActivity.class);
                i.putExtra("bool",true);
                startActivity(i);
                finish();
            }
        }
/*
        FirebaseUser user = mAuth.getCurrentUser();
        final String email = user.getEmail();
        final String uID = user.getUid();
        Log.i("User", email + " " + uID);
        Toast.makeText(this, "Email : " + email, Toast.LENGTH_SHORT).show();
*/
        login();
        register();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(token)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        googleauthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });
    }

    private void googleSignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                final GoogleSignInAccount account = task.getResult(ApiException.class);
                userRef = mDatabase.child("Users");
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Boolean userExists = false;
                        String email = account.getEmail();
                        for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                            if(email.compareTo((String) userSnapshot.child("email").getValue()) == 0){
                                userExists = true;
                                break;
                            }
                        }
                        if(userExists == true){
                            if (account != null) firebaseAuthWithGoogle(account);
                        }
                        else{
                            Toast.makeText(loginActivity.this, "Account Doesn't Exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Log.i("Account", String.valueOf(account.getEmail()));
            } catch (ApiException e) {
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());
        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuthLogin(credential);

    }

    public void mAuthLogin(AuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d("TAG", "signInWithCredential:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {

                            Log.w("TAG", "signInWithCredential:failure", task.getException());

                            Toast.makeText(loginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateUI(FirebaseUser user) {
        Log.i("User", String.valueOf(user));
        if (user != null) {
            final String userID = user.getUid();
            currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("name");
            currentUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
                    prefs.edit()
                            .putString("name", String.valueOf(dataSnapshot.getValue()))
                            .putString("userID", userID)
                            .apply();
                    Log.i("User", "Logged In");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Error", "Error");
                }
            });

            Toast.makeText(loginActivity.this, "Logged In successfully", Toast.LENGTH_LONG).show();
            Log.i("YO", toString().valueOf(user.getMetadata().getCreationTimestamp()));
            Log.i("YO", toString().valueOf(user.getMetadata().getLastSignInTimestamp()));

//            if (user.getMetadata().getCreationTimestamp()==user.getMetadata().getLastSignInTimestamp()|| user.getMetadata().getLastSignInTimestamp()==0)
//            {
//                Intent i = new Intent(loginActivity.this,profileInfoActivity.class);
//                startActivity(i);
//                finish();
//            }
//            else
//            {
                Intent i = new Intent(loginActivity.this,MainActivity.class);
                startActivity(i);
                finish();
//            }
        }
    }



    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
    public void register()
    {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(loginActivity.this, registerActivity.class);
                startActivity(regIntent);
            }
        });
    }
    public void login()
    {
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               final String txt_email = editEmail.getText().toString();
                String txt_password = editPassword.getText().toString();

                if(TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password))
                {
                    Toast.makeText(loginActivity.this, "All fields are required", Toast.LENGTH_LONG).show();
                }
                else
                {
                    mAuth.signInWithEmailAndPassword(txt_email, txt_password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                String CU = firebaseUser.getUid();
                                prefs.edit()
                                        .putString("userID", CU)
                                        .apply();

                                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String currentUserID = prefs.getString("userID", "N/A");

                                currentUser = database.getReference().child("Users").child(currentUserID).child("name");
                                currentUser.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        prefs.edit()
                                                .putString("name", String.valueOf(dataSnapshot.getValue()))
                                                .apply();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.e("Error", "Error");
                                    }
                                });


                                Log.i("YO", toString().valueOf(firebaseUser.getMetadata().getCreationTimestamp()));
                                Log.i("YO", toString().valueOf(firebaseUser.getMetadata().getLastSignInTimestamp()));

//                                if (firebaseUser.getMetadata().getCreationTimestamp()==firebaseUser.getMetadata().getLastSignInTimestamp()|| firebaseUser.getMetadata().getLastSignInTimestamp()==0)
//                                {
//                                    Intent i = new Intent(loginActivity.this,profileInfoActivity.class);
//                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    i.putExtra("email", txt_email);
//
//                                    startActivity(i);
//                                    finish();
//                                }
//                                else
//                                {
                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("email", txt_email);

                                    startActivity(intent);
                                    finish();
//                                }
                            }
                            else
                            {
                                Toast.makeText(loginActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void logout() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }
}

