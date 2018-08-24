package com.example.david.myapplication;

/**
 * Created by 李 on 2017/1/26.
 */
public class DiaryBean {
    private String date;
    private String title;
    private String content;
    private String tag;
    private String mood;
    private int  left1;
    private int  top1;
    private int  right1;
    private int  bot1;
    private String bmp1;

    public DiaryBean(String date, String title,  String mood, String content, String tag,int left1,int top1,int right1,int bot1,String bmp1) {
        this.date = date;
        this.title = title;
        this.content = content;
        this.tag = tag;
        this.mood = mood;
        this.left1 = left1;
        this.top1 = top1;
        this.right1 = right1;
        this.bot1 = bot1;
        this.bmp1 = bmp1;
    }
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }
    public int getLeft1() {
        return left1;
    }

    public void setLeft1(int left1) {
        this.left1 = left1;
    }
    public int getTop1() {return top1;}
    public void setTop1(int top1) {this.top1 = top1;}

    public int getRight1() {return right1;}
    public void setRight1(int right1) {this.right1 = right1;}

    public int getBot1() {return bot1;}
    public void setBot1(int bot1) {this.bot1 = bot1;}

    public String getBmp1() {return bmp1;}
    public void setBmp1(String bmp1) {this.bmp1 = bmp1;}

}
