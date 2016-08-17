package com.example.myapplication.bean;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fantasee on 2016/8/11.
 */
public class SendedMsg {
    private int id;
    private String msg;
    private String number;
    private String names;
    private String festivalName;
    private Date date;
    private String dateStr;
    private DateFormat df=new SimpleDateFormat("yy-MM-dd hh-mm-ss");


    public static final String TABLE_NAME="tb_sended_msg";
    public static final String COLUMN_MSG="msg";
    public static final String COLUMN_NUMBER="number";
    public static final String COLUMN_NAMES="names";
    public static final String COLUMN_FESTIVAL_NAME="festivalName";
    public static final String COLUMN_DATE="date";


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getFestivalName() {
        return festivalName;
    }

    public void setFestivalName(String festivalName) {
        this.festivalName = festivalName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDateStr() {

        dateStr=df.format(date);
        return dateStr;
    }

}
