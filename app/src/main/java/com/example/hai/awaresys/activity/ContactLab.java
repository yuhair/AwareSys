package com.example.hai.awaresys.activity;

import android.content.Context;

import com.example.hai.awaresys.dao.ContactDao;
import com.example.hai.awaresys.model.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hai on 3/24/2016.
 */
public class ContactLab {
    private static ContactLab sContactLab;
    private ArrayList<Contact> mContacts;
    private Context mAppContext;
    private ContactDao contactDao;

    private ContactLab(Context appContext){
        mAppContext = appContext;
        contactDao = new ContactDao(appContext);
        mContacts = contactDao.getStoredContacts();
    }

    public static ContactLab get(Context c) {
        if (sContactLab == null) {
            sContactLab = new ContactLab(c.getApplicationContext());
        }
        return sContactLab;
    }

    public void addContact(Contact c){
        mContacts.add(c);
    }

    public void deleteContact(String id){
        for(int i=0; i<mContacts.size(); i++){
            if(mContacts.get(i).getId().equals(id)){
                mContacts.remove(i);
            }
        }
    }

    public void deleteContact(Contact c){
        mContacts.remove(c);
    }


    public Contact getContact(String id){
        for(Contact c : mContacts){
            if(c.getId().equals(id))
                return c;
        }
        return null;
    }
    public ArrayList<Contact> getmContacts(){
        return mContacts;
    }
}
