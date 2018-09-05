package com.example.david.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.database.sqlite.SQLiteDatabase;
import android.view.KeyEvent;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import java.lang.Object;
import java.util.ArrayList;
import java.util.List;


public class Moodbar extends AppCompatActivity {

BarChart barchart;
private DiaryDatabaseHelper mHelper;
private int DATA_COUNT=5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);
        barchart = (BarChart) findViewById(R.id.chart_bar);
        barchart.setDescription("心情統計");
        barchart.fitScreen();
        //LineData data = getData(12, 100);
        mHelper = new DiaryDatabaseHelper(this, "Diary.db", null, 1);
        barchart.setData(getBarData());
        barchart.getXAxis().setDrawGridLines(false);
        barchart.getAxisLeft().setDrawGridLines(false);
        barchart.animateXY(1000, 2000);


    }
    private BarData getBarData(){
        BarDataSet dataSetA = new BarDataSet(getChartData(), "心情");

       List<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSetA); // add the datasets

        return new BarData(getLabels(), dataSets);
    }
    private List<BarEntry> getChartData(){
        final int DATA_COUNT = 5;
        SQLiteDatabase sqLiteDatabase = mHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("Diary", null, null, null, null, null, null);
        List<BarEntry> chartData = new ArrayList<>();
        cursor.moveToFirst();
        int a=0 ,b=0 ,c=0,d=0,e=0;
        if (cursor.moveToFirst()) {
            do {

                String mood = cursor.getString(cursor.getColumnIndex("mood"));
                if (mood.equals("開心") ) {
                    a++;
                } else if (mood.equals("平靜")) {
                    b++;
                } else if (mood.equals("難過")) {
                   c++;
                } else if (mood.equals("憤怒")) {
                   d++;
                } else if (mood.equals("失望")) {
                   e++;
                }


            } while (cursor.moveToNext());
        }
        cursor.close();
//        for(int i=0;i<DATA_COUNT;i++){
//            chartData.add(new BarEntry(i*5, i));
        chartData.add(new BarEntry(a, 0));
        chartData.add(new BarEntry(b, 1));
        chartData.add(new BarEntry(c, 2));
        chartData.add(new BarEntry(d, 3));
        chartData.add(new BarEntry(e, 4));
//    }
        return chartData;
    }
    //寫標題
    private List<String> getLabels(){
        List<String> chartLabels = new ArrayList<>();

            chartLabels.add("開心");
            chartLabels.add("平靜");
            chartLabels.add("難過");
            chartLabels.add("憤怒");
            chartLabels.add("失望");

        return chartLabels;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent();
        intent.setClass(Moodbar.this, HomeActivity.class);
        startActivity(intent);
        finish();
        return true;
    }





}
