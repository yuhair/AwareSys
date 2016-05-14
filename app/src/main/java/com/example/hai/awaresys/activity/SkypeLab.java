package com.example.hai.awaresys.activity;

import android.content.Context;

import com.example.hai.awaresys.dao.ContactDao;
import com.example.hai.awaresys.dao.SkypeDao;
import com.example.hai.awaresys.model.Contact;
import com.example.hai.awaresys.model.Skype;

import java.util.ArrayList;

/**
 * Created by Hai on 3/26/2016.
 */
public class SkypeLab {
    private static SkypeLab sSkypeLab;
    private ArrayList<Skype> mSkypes;
    private Context mAppContext;
    private SkypeDao skypeDao;

    private SkypeLab(Context appContext){
        mAppContext = appContext;
        skypeDao = new SkypeDao(appContext);
        mSkypes = skypeDao.getStoredSkypes();
    }

    public static SkypeLab get(Context c) {
        if (sSkypeLab == null) {
            sSkypeLab = new SkypeLab(c.getApplicationContext());
        }
        return sSkypeLab;
    }

    public void addSkype(Skype i){
        mSkypes.add(i);
    }

    public void deleteSkype(String id){
        for(int i=0; i<mSkypes.size(); i++){
            if(mSkypes.get(i).getId().equals(id)){
                mSkypes.remove(i);
            }
        }
    }

    public void deleteSkype(Skype i){
        mSkypes.remove(i);
    }


    public Skype getSkype(String id){
        for(Skype i : mSkypes){
            if(i.getId().equals(id))
                return i;
        }
        return null;
    }
    public ArrayList<Skype> getmSkypes(){
        return mSkypes;
    }
}
