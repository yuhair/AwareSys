package com.example.hai.awaresys.activity;


import android.support.v4.app.Fragment;

/**
 * Created by Hai on 3/24/2016.
 */
public class ContactListActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        return new ContactListFragment();
    }
}
