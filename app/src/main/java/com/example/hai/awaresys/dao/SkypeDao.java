package com.example.hai.awaresys.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.hai.awaresys.model.Contact;
import com.example.hai.awaresys.model.Skype;

import java.util.ArrayList;
import java.util.List;

/**
 * this class is to operate skype table
 *
 * @author Hai Yu
 */
public class SkypeDao {
    private DBOpenHelper helper;
    private SQLiteDatabase db;

    public SkypeDao(Context context) {
        helper = new DBOpenHelper(context);
    }


    public ArrayList<Skype> getStoredSkypes() {
        ArrayList<Skype> skypes = new ArrayList<Skype>();
        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "select id, path, account from skype", null);
        while (cursor.moveToNext()) {
            skypes.add(new Skype(cursor.getString(cursor.getColumnIndex("id")),
                                 cursor.getString(cursor.getColumnIndex("path")),
                                 cursor.getString(cursor.getColumnIndex("account"))));
        }
        cursor.close();
        db.close();
        return skypes;
    }

    public String getAccount(String path) {
        String contact = null;
        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select account from skype where path = ?",
                new String[]
                        {path});
        while (cursor.moveToNext()) {
            contact = cursor.getString(cursor.getColumnIndex("account"));
        }
        cursor.close();
        db.close();
        return contact;
    }

    /**
     * judge whether the account has been set up for the picture stored in the given path
     *
     * @param path
     * @return
     */
    public boolean existAccount(String path) {

        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from skype where path = ?",
                new String[]
                        {path});
        boolean exist = false;
        while (cursor.moveToNext()) {
            exist = true;
        }

        db.close();
        return exist;
    }

    public void delete(String id) {
        db = helper.getWritableDatabase();
        db.execSQL("delete from skype where id = ? ", new String[]
                {id});
        db.close();
    }

    /**
     * update the skype account
     *
     * @param skype
     */
    public void update(Skype skype) {
        db = helper.getWritableDatabase();
        db.execSQL("update skype set account = ? where path = ?", new Object[]
                {skype.getAccount(), skype.getPath()});
        db.close();
    }

    public void updateAccount(Skype skype) {
        db = helper.getWritableDatabase();
        db.execSQL("update skype set account = ? where id = ?", new Object[]
                {skype.getAccount(), skype.getId()});
        db.close();
    }

    /**
     * add a new tuple for skype table
     *
     * @param skype
     */
    public void add(Skype skype) {


            db = helper.getWritableDatabase();
            db.execSQL("insert into skype (id, path, account) " + "values (?,?,?)",
                    new Object[]
                            {skype.getId(), skype.getPath(), skype.getAccount()});
            db.close();

    }

    /**
     * return all paths of the pics stored in the skype table
     *
     * @return
     */
    public List<String> getPathes() {
        List<String> pathes = new ArrayList<String>();
        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "select path from skype", null);

        while (cursor.moveToNext()) {
            pathes.add(cursor.getString(cursor.getColumnIndex("path")));
        }
        cursor.close();
        db.close();
        return pathes;
    }

}
