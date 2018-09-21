package com.example.david.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

//import com.lizehao.watermelondiarynew.widget.LinedEditText;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.trity.floatingactionbutton.FloatingActionButton;
import cc.trity.floatingactionbutton.FloatingActionsMenu;

/**
 * Created by 李 on 2017/1/26.
 */

public class UpdateDiaryActivity extends AppCompatActivity {
    String moodtext="開心";
    @BindView(R.id.update_diary_tv_date)
    TextView mUpdateDiaryTvDate;
    @BindView(R.id.update_diary_et_title)
    EditText mUpdateDiaryEtTitle;
    @BindView(R.id.update_diary_et_content)
    LinedEditText mUpdateDiaryEtContent;
    @BindView(R.id.update_diary_fab_back)
    FloatingActionButton mUpdateDiaryFabBack;
    @BindView(R.id.update_diary_fab_add)
    FloatingActionButton mUpdateDiaryFabAdd;
    @BindView(R.id.update_diary_fab_delete)
    FloatingActionButton mUpdateDiaryFabDelete;
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
    @BindView(R.id.update_diary_tv_tag)
    TextView mTvTag;
    @BindView(R.id.imageView2)
    ImageView img2;
    String updatebmp1="";


    private DiaryDatabaseHelper mHelper;

    public static void startActivity(Context context, String title, String mood, String content, String tag, int left1, int top1, int right1, int bot1,String bmp1) {
        Intent intent = new Intent(context, UpdateDiaryActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("mood", mood);
        intent.putExtra("content", content);
        intent.putExtra("tag", tag);
        intent.putExtra("left1", left1);
        intent.putExtra("top1", top1);
        intent.putExtra("right1", right1);
        intent.putExtra("bot1", bot1);
        intent.putExtra("bmp1", bmp1);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_diary);
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
                       {

                           public void run()
                           {
                               InputMethodManager inputManager =
                                       (InputMethodManager)mUpdateDiaryEtTitle.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(mUpdateDiaryEtTitle, 0);
                           }

                       },
                998);
        //心情
        Spinner spinner = (Spinner)findViewById(R.id.mood_spinner);
        final String[] lunch = {"開心", "平靜", "難過", "憤怒", "失望"};
        ArrayAdapter<String> lunchList = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,lunch);
        spinner.setAdapter(lunchList);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                moodtext=lunch[i];
        }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                moodtext=lunch[1];
            }
        });
        //
//        AppManager.getAppManager().addActivity(this);
        ButterKnife.bind(this);
        mHelper = new DiaryDatabaseHelper(this, "Diary.db", null, 1);
        initTitle();
        StatusBarCompat.compat(this, Color.parseColor("#161414"));

        Intent intent = getIntent();
        mUpdateDiaryTvDate.setText("今天，" + GetDate.getDate());
        mUpdateDiaryEtTitle.setText(intent.getStringExtra("title"));
        img2.layout(321,21,3232,323);
        mUpdateDiaryEtContent.setText(intent.getStringExtra("content"));
        mTvTag.setText(intent.getStringExtra("tag"));
        //載入圖片

        updatebmp1 = intent.getStringExtra("bmp1");
        byte bytes[] = Base64.decode(updatebmp1.getBytes(), Base64.DEFAULT);
        img2.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
        //位置
        img2.layout(intent.getIntExtra("left1",0), intent.getIntExtra("top1",0),intent.getIntExtra("right1",0),intent.getIntExtra("bot1",0));

    }

    private void initTitle() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mCommonTvTitle.setText("修改日記");

    }

    @OnClick({R.id.common_iv_back, R.id.update_diary_tv_date, R.id.update_diary_et_title, R.id.update_diary_et_content, R.id.update_diary_fab_back, R.id.update_diary_fab_add, R.id.update_diary_fab_delete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.common_iv_back:
                diary_MainActivity.startActivity(this);
            case R.id.update_diary_tv_date:
                break;
            case R.id.update_diary_et_title:
                break;
            case R.id.update_diary_et_content:
                break;
            case R.id.update_diary_fab_back:
                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("確定要删除該日記嗎？").setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String title = mUpdateDiaryEtTitle.getText().toString();
                        String tag = mTvTag.getText().toString();
                        SQLiteDatabase dbDelete = mHelper.getWritableDatabase();
                        dbDelete.delete("Diary", "tag = ?", new String[]{tag});
                        diary_MainActivity.startActivity(UpdateDiaryActivity.this);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
                break;
            case R.id.update_diary_fab_add:
                SQLiteDatabase dbUpdate = mHelper.getWritableDatabase();
                ContentValues valuesUpdate = new ContentValues();
                String title = mUpdateDiaryEtTitle.getText().toString();
                String mood = moodtext;
                String content = mUpdateDiaryEtContent.getText().toString();
                String bmp1 = updatebmp1;
                valuesUpdate.put("title", title);
                valuesUpdate.put("mood", mood);
                valuesUpdate.put("content", content);
                valuesUpdate.put("bmp1", bmp1);
                dbUpdate.update("Diary", valuesUpdate, "title = ?", new String[]{title});
              //  dbUpdate.update("Diary", valuesUpdate, "mood=?", new String[]{mood});
                dbUpdate.update("Diary", valuesUpdate, "content = ?", new String[]{content});
             //   dbUpdate.update("Diary", valuesUpdate, "bmp1 = ?", new String[]{bmp1});
                valuesUpdate.clear();
                dbUpdate.close();
                diary_MainActivity.startActivity(this);
                break;
            case R.id.update_diary_fab_delete:
                diary_MainActivity.startActivity(this);

                break;
        }
    }

    @OnClick(R.id.common_tv_title)
    public void onClick() {
        Intent intent = getIntent();
        img2.layout(intent.getIntExtra("left1",0), intent.getIntExtra("top1",0),intent.getIntExtra("right1",0),intent.getIntExtra("bot1",0));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        diary_MainActivity.startActivity(this);
    }
}