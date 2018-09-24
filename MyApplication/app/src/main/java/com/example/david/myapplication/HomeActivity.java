package com.example.david.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;


public class HomeActivity extends BaseActivity {
    private FirebaseAnalytics mFirebaseAnalytics;

    private Button memo, chat, diary, entermood, Account;

    private static final int RC_SIGN_IN = 1;
    private DatabaseReference userlistReference, userReference;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private String dateSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawableResource(R.drawable.background_4_new);

        final Date datesys = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateSystem = sdf.format(datesys);

        setContentView(R.layout.activity_home);
//        singOutButton = (Button) findViewById(R.id.singoutButton);
        Account = findViewById(R.id.account);
        Account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, AccountActivity.class);
                startActivity(intent);
//                finish();
            }
        });
        chat = findViewById(R.id.chatbutton);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, ExchangeDiary.class);
                startActivity(intent);
//                finish();
            }
        });
        memo = (Button) findViewById(R.id.memo);
        memo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, memo_MainActivity.class);
                startActivity(intent);
//                finish();
            }
        });
        diary = (Button) findViewById(R.id.fuck);
        diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, diary_MainActivity.class);
                startActivity(intent);
//                finish();
            }
        });
        entermood = (Button) findViewById(R.id.entermood);
        entermood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, Moodbar.class);
                startActivity(intent);
//                finish();
            }
        });


        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

//        signInButton = (SignInButton) findViewById(R.id.googleSignIn);

        // 設定 FirebaseAuth 介面
        mAuth = FirebaseAuth.getInstance();

        // 設定 Google 登入 Client
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
    }

    public void AddtoList(View v) {
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        userlistReference = mFirebaseDatabaseReference.child("exchange").child(dateSystem).child(User.CHILD_NAME);
        mFirebaseDatabaseReference.child("exchange").child(ExchangeDiary.getSpecifiedDayBefore(dateSystem, 0)).child("end").setValue(false);
        userReference = mFirebaseDatabaseReference.child("users").child(mFirebaseUser.getUid());
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("qualified").getValue().equals(true)) {
                    userlistReference.child(mFirebaseUser.getUid()).setValue(mFirebaseUser.getUid());
                }
                mFirebaseDatabaseReference.child("exchange").child(ExchangeDiary.getSpecifiedDayBefore(dateSystem, 0)).child("end").setValue(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        userlistReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        if (userReference.toString().equals("false")) {
//            userlistReference.child(mFirebaseUser.getUid()).setValue(userReference);
//        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                //取得使用者並試登入
                firebaseAuthWithGoogle(account);
            }
        }
    }

    //登入 Firebase
    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(HomeActivity.this, "Failed", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(HomeActivity.this, "SingIn name:" + account.getDisplayName(), Toast.LENGTH_LONG).show();
                        }
                    }

                });
    }

    public void firebaseSingOut(View view) {
        // Firebase 登出
        mAuth.signOut();

        // Google 登出
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        GoogleSignIn.getClient(this, gso).signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(HomeActivity.this, "SingOut", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(HomeActivity.this)
                    .setTitle("確認視窗")
                    .setMessage("確定要結束Moodiary嗎?")
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub

                                }
                            }).show();
        }
        return true;
    }
}