package com.example.myapplication.bean;

import java.util.Date;

/**
 * Created by fantasee on 2016/8/10.
 */
public class Festival {

    private int id;
    private String name;
    private String desc;
    private Date date;

    public Festival(int  id, String name) {
        this.id=id;
        this.name=name;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {

        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
