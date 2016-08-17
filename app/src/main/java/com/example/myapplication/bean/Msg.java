package com.example.myapplication.bean;

/**
 * Created by fantasee on 2016/8/11.
 */
public class Msg {
    private int id;
    private int festivalid;
    private String content;

    public Msg(int id, int festivalid, String content) {
        this.id = id;
        this.festivalid = festivalid;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFestivalid() {
        return festivalid;
    }

    public void setFestivalid(int festivalid) {
        this.festivalid = festivalid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
