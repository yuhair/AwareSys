package com.example.hai.awaresys.activity;

import android.support.v4.app.Fragment;

import java.util.UUID;

public class ContactActivity extends SingleFragmentActivity {
	@Override
    protected Fragment createFragment() {
        String contactId = getIntent().getStringExtra(ContactFragment.EXTRA_CONTACT_ID);
        return ContactFragment.newInstance(contactId);
    }
}
