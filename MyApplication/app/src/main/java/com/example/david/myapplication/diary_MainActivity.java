package com.example.david.myapplication;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.lizehao.watermelondiarynew.R;
//import com.DiaryBean;
//import com.db.DiaryDatabaseHelper;
//import com.lizehao.watermelondiarynew.event.StartUpdateDiaryEvent;
//import com.lizehao.watermelondiarynew.utils.AppManager;
//import com.lizehao.watermelondiarynew.utils.GetDate;
//import com.lizehao.watermelondiarynew.utils.SpHelper;
//import com.lizehao.watermelondiarynew.utils.StatusBarCompat;

import com.google.firebase.database.DatabaseReference;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.david.myapplication.BaseActivity.ANONYMOUS;

public class diary_MainActivity extends BaseActivity {
    @BindView(R.id.upload)
    Button upload;
    @BindView(R.id.common_iv_back)
    ImageView mCommonIvBack;
    @BindView(R.id.common_tv_title)
    TextView mCommonTvTitle;
    @BindView(R.id.common_iv_test)
    ImageView mCommonIvTest;
    @BindView(R.id.common_title_ll)
    LinearLayout mCommonTitleLl;
    @BindView(R.id.main_iv_circle)
    ImageView mMainIvCircle;
    @BindView(R.id.main_tv_date)
    TextView mMainTvDate;
    @BindView(R.id.main_tv_content)
    TextView mMainTvContent;
    @BindView(R.id.main_tv_mood)
    TextView mMainTvMood;
    @BindView(R.id.item_ll_control)
    LinearLayout mItemLlControl;

    @BindView(R.id.main_rv_show_diary)
    RecyclerView mMainRvShowDiary;
    @BindView(R.id.main_fab_enter_edit)
    FloatingActionButton mMainFabEnterEdit;
    @BindView(R.id.main_rl_main)
    RelativeLayout mMainRlMain;
    @BindView(R.id.item_first)
    LinearLayout mItemFirst;
    @BindView(R.id.main_ll_main)
    LinearLayout mMainLlMain;
    private List<DiaryBean> mDiaryBeanList;

    private DiaryDatabaseHelper mHelper;

    private static String IS_WRITE = "true";

    private int mEditPosition = -1;

    /**
     * 标识今天是否已经写了日记
     */
    private boolean isWrite = false;
    private static TextView mTvTest;
    private DatabaseReference diaryReference;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, diary_MainActivity.class);
        context.startActivity(intent);

    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
//        AppManager.getAppManager().addActivity(this);
        getWindow().setBackgroundDrawableResource(R.drawable.background_5);
        ButterKnife.bind(this);
        StatusBarCompat.compat(this, Color.parseColor("#161414"));
        mHelper = new DiaryDatabaseHelper(this, "Diary.db", null, 1);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        EventBus.getDefault().register(this);
        SpHelper spHelper = SpHelper.getInstance(this);
        try {
            getDiaryBeanList();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        initTitle();
        if (mFirebaseAuth.getCurrentUser() != null) {
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            diaryReference = mFirebaseDatabaseReference.child(User.CHILD_NAME).child(mFirebaseUser.getUid()).child(Diary.CHILD_NAME);
            Log.d("123", "User ID:" + mFirebaseUser.getUid());
        }
        mMainRvShowDiary.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mMainRvShowDiary.setAdapter(new DiaryAdapter(this, mDiaryBeanList));
        mTvTest = new TextView(this);
        mTvTest.setText("hello world");

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = ANONYMOUS;
                String photoUrl = null;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                Date dt = new Date();
                String dts = sdf.format(dt);
                if (null != mFirebaseUser) {
                    userName = mFirebaseUser.getDisplayName();
                    if (null != mFirebaseUser.getPhotoUrl()) {
                        photoUrl = mFirebaseUser.getPhotoUrl().toString();
                    }
                }
                SQLiteDatabase sqLiteDatabase = mHelper.getWritableDatabase();
                Cursor cursor2 = sqLiteDatabase.query("Diary", null, null, null, null, null, null);
                cursor2.moveToFirst();
                if (cursor2.moveToFirst()) {
                    do {
                        SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                        String date = cursor2.getString(cursor2.getColumnIndex("date"));

                        Date datestring = null;
                        try {
                            datestring = sdf2.parse(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        date = sdf3.format(datestring);
                        String childdate =sdf4.format(datestring);
                        String title = cursor2.getString(cursor2.getColumnIndex("title"));
//                        String date = cursor2.getString(cursor2.getColumnIndex("date"));
                        String content = cursor2.getString(cursor2.getColumnIndex("content"));
                        String mood = cursor2.getString(cursor2.getColumnIndex("mood"));

                        Diary diary = new Diary(content, mood, title, date, userName);
                        diaryReference.child(childdate).push().setValue(diary);
                    } while (cursor2.moveToNext());
                }
                cursor2.close();
            }
        });
//        upload.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (MotionEvent.ACTION_DOWN == event.getAction()) {
//                    int count = 0;
//                    count++;
//                    long firClick = 0;
//                    long secClick = 0;
//                    if (count == 1) {
//                        firClick = System.currentTimeMillis();
//                    } else if (count == 2) {
//                        secClick = System.currentTimeMillis();
//                        if (secClick - firClick < 1000) {
////按兩下事件
////                            SQLiteDatabase db = this.getWritableDatabase();
////                            db.execSQL("DROP TABLE IF EXISTS history"); //刪除history table
//                            mHelper.onUpgrade(mHelper.getWritableDatabase(),1,2);
////                            this.onCreate(db); //在onCreate去新增
////                            db.close();
//                        }
//                        count = 0;
//                        firClick = 0;
//                        secClick = 0;
//                    }
//                }
//                return true;
//            }
//
//        });
    }

    private void initTitle() {
        mMainTvDate.setText("今天，" + GetDate.getDate());
        mCommonTvTitle.setText("日記");

        mCommonIvBack.setVisibility(View.INVISIBLE);
        mCommonIvTest.setVisibility(View.INVISIBLE);

    }

    private List<DiaryBean> getDiaryBeanList() throws ParseException {

        mDiaryBeanList = new ArrayList<>();
        List<DiaryBean> diaryList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = mHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("Diary", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                String date = cursor.getString(cursor.getColumnIndex("date"));
//                Date datestring = sdf.parse(date);
//                date = sdf2.format(datestring);
                Date datesys = new Date();

                String dateSystem = sdf2.format(datesys);
//                String dateSystem = GetDate.getDate().toString();
                if (date.equals(dateSystem)) {
                    mMainLlMain.removeView(mItemFirst);
                    break;
                }
            } while (cursor.moveToNext());
        }


        if (cursor.moveToFirst()) {
            do {
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                String date = cursor.getString(cursor.getColumnIndex("date"));
//                Date datestring = sdf.parse(date);
//                date = sdf2.format(datestring);
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String mood = cursor.getString(cursor.getColumnIndex("mood"));
                String tag = cursor.getString(cursor.getColumnIndex("tag"));
                int left1 = cursor.getInt(cursor.getColumnIndex("left1"));
                int top1 = cursor.getInt(cursor.getColumnIndex("top1"));
                int right1 = cursor.getInt(cursor.getColumnIndex("right1"));
                int bot1 = cursor.getInt(cursor.getColumnIndex("bot1"));
                String bmp1 = cursor.getString(cursor.getColumnIndex("bmp1"));
                mDiaryBeanList.add(new DiaryBean(date, title, mood, content, tag, left1, top1, right1, bot1, bmp1));
            } while (cursor.moveToNext());
        }
        cursor.close();

        for (int i = mDiaryBeanList.size() - 1; i >= 0; i--) {
            diaryList.add(mDiaryBeanList.get(i));
        }

        mDiaryBeanList = diaryList;
        return mDiaryBeanList;
    }

    @Subscribe
    public void startUpdateDiaryActivity(StartUpdateDiaryEvent event) {
        String title = mDiaryBeanList.get(event.getPosition()).getTitle();
        String content = mDiaryBeanList.get(event.getPosition()).getContent();
        String mood = mDiaryBeanList.get(event.getPosition()).getMood();
        String tag = mDiaryBeanList.get(event.getPosition()).getTag();
        int left1 = mDiaryBeanList.get(event.getPosition()).getLeft1();
        int top1 = mDiaryBeanList.get(event.getPosition()).getTop1();
        int right1 = mDiaryBeanList.get(event.getPosition()).getRight1();
        int bot1 = mDiaryBeanList.get(event.getPosition()).getBot1();
        String bmp1 = mDiaryBeanList.get(event.getPosition()).getBmp1();
        UpdateDiaryActivity.startActivity(this, title, mood, content, tag, left1, top1, right1, bot1, bmp1);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.main_fab_enter_edit)
    public void onClick() {
        AddDiaryActivity.startActivity(this);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent();
        intent.setClass(diary_MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
        return true;
    }
}


