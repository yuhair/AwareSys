package com.example.hai.awaresys.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hai.awaresys.model.Contact;
import com.example.hai.awaresys.model.Emotion;

import java.util.ArrayList;

/**
 * this class is used to operate the emotion table
 *
 * @author Hai Yu
 */
public class EmotionDao {

    private DBOpenHelper helper;
    private SQLiteDatabase db;

    public EmotionDao(Context context) {
        helper = new DBOpenHelper(context);
    }

    /**
     * add a new tuple
     *
     * @param record
     */
    public void add(Emotion record) {

        db = helper.getWritableDatabase();
        db.execSQL("insert into emotion (date, time, emotion, value)"
                        + "values (?,?,?,?)",
                new Object[]
                        {record.getDate(), record.getTime(), record.getEmotion(),record.getValue()});
        db.close();

    }

    public ArrayList<Emotion> getEmotions(String date, String emotion){
        ArrayList<Emotion> res = new ArrayList<>();
        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "select id, date, time, emotion, value from emotion where " +
                        "date = ? and emotion = ?", new String[]{date, emotion});
        while (cursor.moveToNext()) {
            res.add(new Emotion(cursor.getString(cursor.getColumnIndex("id")),
                                cursor.getString(cursor.getColumnIndex("date")),
                                cursor.getString(cursor.getColumnIndex("time")),
                                cursor.getString(cursor.getColumnIndex("emotion")),
                                cursor.getString(cursor.getColumnIndex("value"))));
        }
        cursor.close();
        db.close();
        return res;

    }

    public ArrayList<Emotion> getEmotions(){
        ArrayList<Emotion> res = new ArrayList<>();
        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "select id, date, time, emotion, value from emotion", null);
        while (cursor.moveToNext()) {
            res.add(new Emotion(cursor.getString(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("date")),
                    cursor.getString(cursor.getColumnIndex("time")),
                    cursor.getString(cursor.getColumnIndex("emotion")),
                    cursor.getString(cursor.getColumnIndex("value"))));
        }
        cursor.close();
        db.close();
        return res;

    }



}
