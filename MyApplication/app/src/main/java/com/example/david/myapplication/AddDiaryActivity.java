package com.example.david.myapplication;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.lizehao.watermelondiarynew.utils.AppManager;
//import com.lizehao.watermelondiarynew.utils.GetDate;
//import com.lizehao.watermelondiarynew.utils.StatusBarCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.trity.floatingactionbutton.FloatingActionButton;
import cc.trity.floatingactionbutton.FloatingActionsMenu;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import static android.app.PendingIntent.getActivity;

/**
 * Created by 李 on 2017/1/26.
 */
public class AddDiaryActivity extends AppCompatActivity {
    String moodtext = " ";
    int left1 = 0;
    int right1 = 0;
    int top1 = 0;
    int bot1 = 0;
    private final int MY_PERMISSIONS_CAMERA = 3;
    String addbmp = "";
    private final int IMAGE_RESULT_CODE = 1;
    private final int PICK_IMAGE = 2;

    @BindView(R.id.add_diary_tv_date)
    TextView mAddDiaryTvDate;
    @BindView(R.id.add_diary_et_title)
    EditText mAddDiaryEtTitle;
    @BindView(R.id.add_diary_et_content)
    LinedEditText mAddDiaryEtContent;
    @BindView(R.id.add_diary_fab_back)
    FloatingActionButton mAddDiaryFabBack;
    @BindView(R.id.add_diary_fab_add)
    FloatingActionButton mAddDiaryFabAdd;
    @BindView(R.id.right_labels)
    FloatingActionsMenu mRightLabels;
    @BindView(R.id.common_tv_title)
    TextView mCommonTvTitle;
    @BindView(R.id.common_title_ll)
    LinearLayout mCommonTitleLl;
    @BindView(R.id.common_iv_back)
    ImageView mCommonIvBack;
    @BindView(R.id.common_iv_test)
    ImageView mCommonIvTest;
    @BindView(R.id.imageView)
    ImageView img;


    private DiaryDatabaseHelper mHelper;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AddDiaryActivity.class);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String title, String mood, String content, String bmp1) {
        Intent intent = new Intent(context, AddDiaryActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("mood", mood);
        intent.putExtra("content", content);
        intent.putExtra("bmp1", bmp1);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        AndroidBug5497Workaround.assistActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);

        //心情
        Spinner spinner = (Spinner) findViewById(R.id.mood_spinner);
        final String[] lunch = {"開心", "平靜", "難過", "憤怒", "失望"};
        ArrayAdapter<String> lunchList = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, lunch);
        spinner.setAdapter(lunchList);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

                           public void run() {
                               InputMethodManager inputManager =
                                       (InputMethodManager) mAddDiaryEtTitle.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(mAddDiaryEtTitle, 0);
                           }

                       },
                998);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                moodtext = lunch[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //
//        AppManager.getAppManager().addActivity(this);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Intent intent = getIntent();
        mAddDiaryEtTitle.setText(intent.getStringExtra("title"));
        StatusBarCompat.compat(this, Color.parseColor("#161414"));

        mCommonTvTitle.setText("添加日記");
        mAddDiaryTvDate.setText("今天，" + GetDate.getDate());
        mAddDiaryEtContent.setText(intent.getStringExtra("content"));
        mHelper = new DiaryDatabaseHelper(this, "Diary.db", null, 1);

        img.setOnTouchListener(new View.OnTouchListener() {

            private float x, y;    // 原本圖片存在的X,Y軸位置
            private int mx, my; // 圖片被拖曳的X ,Y軸距離長度


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Log.e("View", v.toString());
                switch (event.getAction()) {          //判斷觸控的動作

                    case MotionEvent.ACTION_DOWN:// 按下圖片時
                        x = event.getX();                  //觸控的X軸位置
                        y = event.getY();                  //觸控的Y軸位置

                    case MotionEvent.ACTION_MOVE:// 移動圖片時

                        //getX()：是獲取當前控件(View)的座標

                        //getRawX()：是獲取相對顯示螢幕左上角的座標
                        mx = (int) (event.getRawX() - x);
                        my = (int) (event.getRawY() - y);
                        v.layout(mx, my, mx + v.getWidth(), my + v.getHeight());
                        left1 = v.getLeft();
                        top1 = v.getTop();
                        right1 = v.getRight();
                        bot1 = v.getRight();
                        break;


                }
                //Log.e("address", String.valueOf(mx) + "~~" + String.valueOf(my)); // 記錄目前位置
                return true;
            }
        });
    }


    @OnClick({R.id.common_iv_back, R.id.add_diary_et_title, R.id.add_diary_et_content, R.id.add_diary_fab_back, R.id.add_diary_fab_add, R.id.imageView})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.common_iv_back:
                final String titleBack2 = mAddDiaryEtTitle.getText().toString();
                final String contentBack2 = mAddDiaryEtContent.getText().toString();
                if (!titleBack2.isEmpty() || !contentBack2.isEmpty()) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setMessage("確定不保存日記就退出?").setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();

                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
//                            finish();
//                            diary_MainActivity.startActivity(AddDiaryActivity.this);
                            dialog.cancel();
                        }
                    }).show();

                } else {
                    finish();
//                    diary_MainActivity.startActivity(this);
                }
            case R.id.add_diary_et_title:
                break;
            case R.id.add_diary_et_content:
                break;
            case R.id.imageView:
                img.layout(img.getLeft(), img.getTop(), img.getRight(), img.getBottom());
                break;

            case R.id.add_diary_fab_back:
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                Date dt = new Date();
                String date = sdf.format(dt);
                String tag = String.valueOf(System.currentTimeMillis());
                String title = mAddDiaryEtTitle.getText().toString() + "";
                String mood = moodtext;
                String content = mAddDiaryEtContent.getText().toString() + "";
                String bmp1 = addbmp;
                if (title.equals("")) {
                    Toast toast = Toast.makeText(AddDiaryActivity.this, "標題不能是空白!!", Toast.LENGTH_LONG);
                    toast.show();
                }
//                else if(title.equals("") && content.equals("")) {
//                    Toast toast = Toast.makeText(AddDiaryActivity.this, "標題及內容不能是空白!!", Toast.LENGTH_LONG);
//                    toast.show();
//                }
//                else if(title.equals("")) {
//                    Toast toast = Toast.makeText(AddDiaryActivity.this, "標題不能是空白!!", Toast.LENGTH_LONG);
//                    toast.show();
//                }
                else if (content.equals("")) {
                    Toast toast = Toast.makeText(AddDiaryActivity.this, "內容不能是空白!!", Toast.LENGTH_LONG);
                    toast.show();
                } else if (!title.equals("") || !content.equals("")) {
                    SQLiteDatabase db = mHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();

                    values.put("date", date);
                    values.put("title", title);
                    values.put("mood", mood);
                    values.put("content", content);
                    values.put("tag", tag);
                    values.put("left1", left1);
                    values.put("top1", top1);
                    values.put("right1", right1);
                    values.put("bot1", bot1);
                    values.put("bmp1", bmp1);
                    values.put("uploaded", "no");

                    db.insert("Diary", null, values);
//                    diary_MainActivity.startActivity(this);
                    finish();
                }


                break;
            case R.id.add_diary_fab_add:
                sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                dt = new Date();
                final String dateBack = sdf.format(dt);
                final String titleBack = mAddDiaryEtTitle.getText().toString();
                final String moodBack = moodtext;
                final String contentBack = mAddDiaryEtContent.getText().toString();
                final String bmp1back = addbmp;


                if (!titleBack.isEmpty() || !contentBack.isEmpty()) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setMessage("確定不保存日記就退出?").setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
//                            SQLiteDatabase db = mHelper.getWritableDatabase();
//                            ContentValues values = new ContentValues();
//                            values.put("date", dateBack);
//                            values.put("title", titleBack);
//                            values.put("mood", moodBack);
//                            values.put("content", contentBack);
//                            values.put("bmp1", bmp1back);
//                            db.insert("Diary", null, values);
//                            values.clear();
                            finish();
//                            diary_MainActivity.startActivity(AddDiaryActivity.this);

                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
//                            finish();
//                            diary_MainActivity.startActivity(AddDiaryActivity.this);
                        }
                    }).show();

                } else {
                    finish();
//                    diary_MainActivity.startActivity(this);
                }
                break;
        }
    }

    //拍照
    public void click1(View v) {
        if (ContextCompat.checkSelfPermission(AddDiaryActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (!ActivityCompat.shouldShowRequestPermissionRationale(AddDiaryActivity.this,
                    Manifest.permission.CAMERA)) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, IMAGE_RESULT_CODE);
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(AddDiaryActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.

            }


        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, IMAGE_RESULT_CODE);
        }
    }
    //ehejfeje

    //從圖庫拿圖
    public void click2(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE);
    }

    public void click3(View v) {
        Intent intent = new Intent(this, DetectionActivity.class);
        startActivity(intent);
    }

    //拍照以及從圖庫拿圖
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IMAGE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    img.setImageBitmap(bitmap);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    int options = 100;
                    while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                        baos.reset();//重置baos即清空baos
                        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
                        options -= 10;//每次都减少10
                    }
                    String base64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

                    addbmp = base64;
                }
                break;

            case PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    ContentResolver cr = this.getContentResolver();
                    try {
                        Bitmap bitmap_gal = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        img.setImageBitmap(bitmap_gal);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap_gal.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        int options = 100;
                        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                            baos.reset();//重置baos即清空baos
                            bitmap_gal.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
                            options -= 10;//每次都减少10
                        }
                        String base64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                        addbmp = base64;
                    } catch (FileNotFoundException e) {
                        Log.e("Exception", e.getMessage(), e);
                    }
                }
                break;
        }
    }

    public class diarydata {
        private String diary;
        private String date;
        private int id;
        public diarydata() {
        }

        public diarydata(String diary, String date, int id) {
            this.id = id;
            this.diary = diary;
            this.date = date;
        }

        public String getDate() {
            return this.date = date;
        }

        public String getDiary() {
            return this.diary = diary;
        }

        public long getId() {
            return this.id = id;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestcode, String[] permissions, int[] grantResults) {
//        if(requestcode==MY_PERMISSIONS_CAMERA){return;}
        switch (requestcode) {
            case MY_PERMISSIONS_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, IMAGE_RESULT_CODE);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
        super.onRequestPermissionsResult(requestcode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        diary_MainActivity.startActivity(this);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        final String titleBack = mAddDiaryEtTitle.getText().toString();
        final String contentBack = mAddDiaryEtContent.getText().toString();
        if (!titleBack.isEmpty() || !contentBack.isEmpty()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("確定不保存日記就退出?").setPositiveButton("確定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();

                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
//                            finish();
//                            diary_MainActivity.startActivity(AddDiaryActivity.this);
                    dialog.cancel();
                }
            }).show();

        } else {
            finish();
        }
        return true;
    }
}











