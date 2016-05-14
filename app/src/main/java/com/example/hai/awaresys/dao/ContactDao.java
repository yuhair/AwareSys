package com.example.hai.awaresys.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hai.awaresys.model.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used for operate contact table
 *
 * @author Hai Yu
 */
public class ContactDao {
    private DBOpenHelper helper;
    private SQLiteDatabase db;

    public ContactDao(Context context) {
        helper = new DBOpenHelper(context);
    }

    public ArrayList<Contact> getStoredContacts() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "select id, name, number, email from contact", null);
        while (cursor.moveToNext()) {
            contacts.add(new Contact(cursor.getString(cursor
                    .getColumnIndex("id")), cursor.getString(cursor
                    .getColumnIndex("name")), cursor.getString(cursor
                    .getColumnIndex("number")), cursor.getString(cursor
                    .getColumnIndex("email"))));
        }
        cursor.close();
        db.close();
        return contacts;
    }

    public Contact getById(String id) {
        Contact person = null;
        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "select id, name, number, email from contact= ? ", new String[]{id});
        while (cursor.moveToNext()) {
            person = new Contact(cursor.getString(cursor
                    .getColumnIndex("id")), cursor.getString(cursor
                    .getColumnIndex("name")), cursor.getString(cursor
                    .getColumnIndex("number")), cursor.getString(cursor
                    .getColumnIndex("email")));
        }
        cursor.close();
        db.close();
        return person;
    }

    /**
     * update the tuple whose id is parameter id using the info of record paramter
     *
     * @param record
     */
    public void update(Contact record) {
        boolean exist = existId(record.getId());
        if (!exist) {
            add(record);
        }
        db = helper.getWritableDatabase();
        db.execSQL(
                "update contact set name = ?, number = ?, email = ? where id = ?",
                new Object[]
                        {record.getName(), record.getNumber(), record.getEmail(), record.getId()});
        db.close();
    }

    /**
     * delete the tuple whose id equals to id paramter
     *
     * @param id
     */
    public void delete(String id) {
        db = helper.getWritableDatabase();
        db.execSQL("delete from contact where id = ? ", new String[]
                {id});
        db.close();
    }

    /**
     * judge whether there is a tuple whose id is eqauld to the id parameter
     *
     * @param id
     * @return
     */
    public boolean existId(String id) {

        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from contact where id = ?", new String[]{id});
        boolean exist = false;
        while (cursor.moveToNext()) {
            exist = true;
        }

        db.close();
        return exist;
    }


    /**
     * add a new tuple with the data stored in the record paramter
     *
     * @param record
     */
    public void add(Contact record) {
        boolean exist = existId(record.getId());

        if (exist) {
            update(record);
        } else {
            db = helper.getWritableDatabase();
            db.execSQL("insert into contact (id, name, number, email)"
                            + "values (?,?,?,?)",
                    new Object[]
                            {record.getId(), record.getName(), record.getNumber(),
                                    record.getEmail()});
            db.close();
        }


    }

    /**
     * reture all emails stored in the contact table
     *
     * @return a list of emails
     */
    public List<String> getEmails() {
        List<String> contacts = new ArrayList<String>();
        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "select email from contact", null);

        while (cursor.moveToNext()) {
            contacts.add(cursor.getString(cursor.getColumnIndex("email")));
        }
        cursor.close();
        db.close();
        return contacts;
    }

    /**
     * return all phone numbers stored in the contact table
     *
     * @return a list of numbers
     */
    public List<String> getNumbers() {
        List<String> contacts = new ArrayList<String>();
        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "select number from contact", null);
        while (cursor.moveToNext()) {
            contacts.add(cursor.getString(cursor.getColumnIndex("number")));
        }
        cursor.close();
        db.close();
        return contacts;
    }
}
