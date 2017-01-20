package com.xmevs.dateprocess.datebase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.xmevs.dateprocess.entity.Info;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MSI on 2016/12/28.
 */

public class DBManager {
    private MyHelper helper;
    private SQLiteDatabase db;

    public static final String ID = "_id";
    public static final String TIMESLOT = "timeslot";

    public DBManager(Context context) {
        helper = new MyHelper(context);
        db = helper.getWritableDatabase();
    }

    /**
     *
     * @param infos
     */
    public void add(List<Info> infos) {
        db.beginTransaction();
        try {
            for (Info info : infos) {
                db.execSQL("INSERT INTO timeslot VALUES(null, ?)", new Object[]{info.getTimeslot()});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     *
     * @param info
     */
    public void add(Info info) {
        db.beginTransaction();
        try {
                db.execSQL("INSERT INTO timeslot VALUES(null, ?)",
                                            new Object[]{
                                                    info.getTimeslot(),
                                            });
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void update(int id, Info newInfo) {
        String sql =                 "UPDATE" +
                "  `timeslot`" +
                "SET" +
                "  `" + TIMESLOT + "` = '" + newInfo.getTimeslot() + "'" +
                "WHERE" +
                " _id = " + id;
        Log.i("sql", sql);
        db.execSQL(sql);
    }

    public void delete(Info info) {
        db.execSQL("DELETE FROM `timeslot` WHERE `_id` = " + info.getId());
    }

    public void delete(int id) {
        db.execSQL("DELETE FROM `timeslot` WHERE `_id` = " + id);
    }

    public List<Info> querys() {
        String timeslot;
        ArrayList<Info> infos = new ArrayList<Info>();
        Cursor c = queryTheCursors();
        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndex(ID));
            timeslot = c.getString(c.getColumnIndex(TIMESLOT));
            Info info = new Info(id, timeslot);
            infos.add(info);
        }
        c.close();
        return infos;
    }

    private Cursor queryTheCursors() {
        Cursor c = db.rawQuery("SELECT * FROM `timeslot`", null);
        return c;
    }

    public Info query(int whereId) {
        String timeslot;
        Info info;
                Cursor c = queryTheCursor(whereId);
        if(c.moveToFirst()) {
            int id = c.getInt(c.getColumnIndex(ID));
            timeslot = c.getString(c.getColumnIndex(TIMESLOT));
            info = new Info(id, timeslot);
            c.close();
            return info;
        }
        c.close();
        info = new Info(-1, "-1");
        return info;
    }

    private Cursor queryTheCursor(int whereId) {
        Cursor c = db.rawQuery("SELECT * FROM `timeslot` WHERE `" + ID + "`=?", new String[]{whereId+""});
        return c;
    }

    public void close() {
        db.close();
    }
}
