package com.example.hai.awaresys.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * this class is to create a database and tables when the user first install the app
 *
 * @author Hai Yu
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DBNAME = "AwareSysDB.db";
    public static int flag = 1;

    public DBOpenHelper(Context context) {
        super(context, DBNAME, null, VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL("create table contact (id Varchar(32) primary key, "
                + "name varchar(30), " + "number varchar(30)," + "email varchar(50)) ");
        db.execSQL("create table skype (id Varchar(32) primary key, "
                + "path varchar(80), " + "account varchar(30)) ");
        db.execSQL("create table emotion (id integer primary key autoincrement, " + "date varchar(50), "
                + "time varchar(50), " + "emotion varchar(30)," + "value varchar(30)) ");
        // Insert some data for each table
        //db.execSQL("insert into contact (id, name, number, email) values ('100', 'HaiYu', '5157081693', 'yuhair@iastate.edu')");
        //db.execSQL("insert into contact (id, name, number, email) values ('200', 'ShuYang', '5155986973', 'szheng@iastate.edu')");
        //db.execSQL("insert into skype (id, path, account) values ('100', 'xxx', 'shuyang.zheng')");
        //db.execSQL("insert into skype (id, path, account) values ('101', 'xxx', 'xxx')");
        //db.execSQL("insert into skype (id, path, account) values ('102', 'xxx', 'xxx')");
        //db.execSQL("insert into preference (type, name) values ('music', 'We are the world')");
        //db.execSQL("insert into preference (type, name) values ('video', 'Freinds_01_01')");
        //db.execSQL("insert into mapping (emotion, action) values ('sad', 'smv')");// s-Skype , v-Video, m-Music
        flag = 0;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }
}
