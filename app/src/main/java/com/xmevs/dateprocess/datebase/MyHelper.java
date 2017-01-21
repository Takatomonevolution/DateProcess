package com.xmevs.dateprocess.datebase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MSI on 2016/12/28.
 */

public class MyHelper extends SQLiteOpenHelper {

    public static final String ID = "_id";
    public static final String TIMESLOT = "timeslot";
    public static final String SLOTNAME = "slotname";

    public MyHelper(Context context) {
        super(context, "timeslot.db", null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE timeslot(" +
                "  _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                TIMESLOT + " VARCHAR," +
                SLOTNAME + " VARCHAR" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
