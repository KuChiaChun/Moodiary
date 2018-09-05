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

//public class HomeActivity extends AppCompatActivity {
//    //google
//    private SignInButton signInButton;
//    private GoogleApiClient mGoogleApiClient;
//    private GoogleSignInOptions gso;
//    private TextView textView;
//    private static int RC_SIGN_IN = 100;
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode!=RESULT_CANCELED){
//            if(requestCode ==RC_SIGN_IN && data != null){
//                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//
//                handleSignInResult(result);
//            }else {
//                textView = findViewById(R.id.textView);
//                textView.setText("onActivityResult");
//
//            }
//        }
//    }
//
//    private void handleSignInResult(GoogleSignInResult result) {
//        Log.d("Success","handleSignInResult:"+ result.isSuccess());
//        if(result.isSuccess()){
//            GoogleSignInAccount acct = result.getSignInAccount();
//            firebaseAuthWithGoogle(acct);
//        }
//        else{
//            textView = findViewById(R.id.textView);
//            textView.setText("handleSignInResult");
//        }
//    }
//
//    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
//        Log.d("FireBase2Google","firebaseAuthWithGoogle"+acct.getId());
//
//        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()){
//                            Log.d("Success","signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            textView = findViewById(R.id.textView);
//                            textView.setText(user.getDisplayName());
////                            updateUI(user);
//                        }
//                    else{
//                            textView = findViewById(R.id.textView);
//                            textView.setText("fuck");
//                            Log.w("fail","signInWithCredential:failure",task.getException());
//                        }
//                    }
//                });
//    }
//
//    private void googleConfig(){
//
//        signInButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                signIn();
//            }
//        });
//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken("1:87146277394:android:b0a15ac72cc44abf")
//                .requestEmail()
//                .build();
//        mGoogleApiClient = new  GoogleApiClient.Builder(this).addApi(Auth.GOOGLE_SIGN_IN_API,gso)
//                .build();
//    }
//
//    private void signIn() {
//        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//        startActivityForResult(signInIntent,RC_SIGN_IN);
//    }
//
//    private Button login;
//    private Button signUp;
//
//
//    private FirebaseAuth mAuth;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser user = mAuth.getCurrentUser();
//        if(user == null){
//            setContentView(R.layout.activity_home);
//            login = (Button) findViewById(R.id.login);
//            signUp = (Button) findViewById(R.id.sign_up);
//
//            login.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent();
//                    intent.setClass(HomeActivity.this, LoginActivity.class);
//                    startActivity(intent);
//
//                }
//            });
//            signUp.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent();
//                    intent.setClass(HomeActivity.this, SignUpActivity.class);
//                    startActivity(intent);
//                }
//            });
//        } else{
//            Intent intent = new Intent();
//            intent.setClass(HomeActivity.this, MainActivity.class);
//            startActivity(intent);
//
//        }
//        setContentView(R.layout.activity_home);
//        signInButton = findViewById(R.id.googleSignIn);
//        googleConfig();
//
//    }
//
//}

public class HomeActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;

    private SignInButton signInButton;
    private Button singOutButton,memo,chat,fuck,entermood,Account;

    private static final int RC_SIGN_IN = 1;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawableResource(R.drawable.background_4_new);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_home);
//        singOutButton = (Button) findViewById(R.id.singoutButton);
        Account = findViewById(R.id.account);
        Account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, AccountActivity.class);
                startActivity(intent);
                finish();
            }
        });
        chat = findViewById(R.id.chatbutton);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, ExchangeDiary.class);
                startActivity(intent);
                finish();
            }
        });
        memo = (Button)findViewById(R.id.memo);
        memo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, memo_MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        fuck = (Button)findViewById(R.id.fuck);
        fuck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, diary_MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        entermood = (Button)findViewById(R.id.entermood);
        entermood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, Moodbar.class);
                startActivity(intent);
                finish();
            }
        });
//        signInButton = (SignInButton) findViewById(R.id.googleSignIn);

        // 設定 FirebaseAuth 介面
        mAuth = FirebaseAuth.getInstance();

        // 設定 Google 登入 Client
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(HomeActivity.this, "Google", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
//        signInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//                startActivityForResult(signInIntent, RC_SIGN_IN);
//            }
//        });

//        singOutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                firebaseSingOut(v);
//            }
//        });
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