package com.example.david.myapplication;

public class Diary {

    public static final String CHILD_NAME = "diary";

    private String title;
    private String name;
    private String date;
    private String mood;
    private String content;

    public Diary() {
        //重要！空的 constructor 為 Firebase Realtime Database 必須要有的。
    }

    public Diary(String content, String mood, String title, String date, String name) {
        this.title = title;
        this.name = name;
        this.mood = mood;
        this.date = date;
        this.content = content;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public String getmood() {
        return mood;
    }

    public String getTitle() {
        return title;
    }

    public String getdate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public void setmood(String mood) {
        this.mood = mood;
    }
}
