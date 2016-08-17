package com.example.myapplication.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fantasee on 2016/8/10.
 */
public class FestivalLab {
    public static FestivalLab mInstance;

    private List<Festival> mFestivals = new ArrayList<>();

    private List<Msg> mMsgs = new ArrayList<Msg>();

    private FestivalLab() {
        mFestivals.add(new Festival(1, "国庆节"));
        mFestivals.add(new Festival(2, "中秋节"));
        mFestivals.add(new Festival(3, "元旦"));
        mFestivals.add(new Festival(4, "春节"));
        mFestivals.add(new Festival(5, "端午节"));
        mFestivals.add(new Festival(6, "七夕节"));
        mFestivals.add(new Festival(7, "圣诞节"));
        mFestivals.add(new Festival(8, "儿童节"));

        mMsgs.add(new Msg(1, 1, "sssssssss"));
        mMsgs.add(new Msg(2, 1, "aaaaaaaaaaaa"));
        mMsgs.add(new Msg(3, 1, "dddddddddddddd"));
        mMsgs.add(new Msg(4, 1, "cccccccccccca"));
        mMsgs.add(new Msg(5, 1, "acacacacac"));
        mMsgs.add(new Msg(6, 1, "wrewtgreyhtujyk"));
        mMsgs.add(new Msg(7, 1, "kjlkjlkjlkjlkjl"));
        mMsgs.add(new Msg(8, 1, "gjkjgfjfgjfjfhj"));
        mMsgs.add(new Msg(9, 1, "tyutyuytutyuty"));
    }


    //返回所有数据
    public List<Festival> getFestivals() {

        return new ArrayList<Festival>(mFestivals);
    }


    public Festival getFestivalById(int resId) {
        for (Festival festival : mFestivals
                ) {
            if (festival.getId() == resId) {
                return festival;
            }
        }
        return null;
    }


    public static FestivalLab getInstance() {
        if (mInstance == null) {
            synchronized (FestivalLab.class) {
                if (mInstance == null) {
                    mInstance = new FestivalLab();
                }
            }
        }
        return mInstance;
    }


    public List<Msg> getMsgsByFestivalId(int fesId) {
        List<Msg> msgs = new ArrayList<Msg>();
        for (Msg msg : mMsgs) {
            if (msg.getFestivalid() == fesId) {
                msgs.add(msg);
            }

        }
        return msgs;
    }

    public Msg getMsgByMsgId(int msgId) {
        Msg m=null;
        for (Msg msg : mMsgs) {
            if (msg.getId() == msgId) {
                m= msg;
            }
        }
        return m;
    }


}
