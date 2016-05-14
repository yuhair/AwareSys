package com.example.hai.awaresys.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.hai.awaresys.R;
import com.example.hai.awaresys.dao.ContactDao;
import com.example.hai.awaresys.model.Contact;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Hai on 3/24/2016.
 */
public class ContactPagerActivity extends AppCompatActivity{
    ViewPager mViewPager;
    private ContactDao contactDao = new ContactDao(this);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        final ArrayList<Contact> contacts = contactDao.getStoredContacts();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public int getCount() {
                return contacts.size();
            }
            @Override
            public Fragment getItem(int pos) {
                String contactId =  contacts.get(pos).getId();
                return ContactFragment.newInstance(contactId);
            }
        });

        String contactId = getIntent().getStringExtra(ContactFragment.EXTRA_CONTACT_ID);
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getId().equals(contactId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
